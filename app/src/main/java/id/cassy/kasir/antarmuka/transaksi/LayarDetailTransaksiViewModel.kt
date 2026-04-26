package id.cassy.kasir.antarmuka.transaksi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.cassy.kasir.antarmuka.navigasi.TujuanNavigasiKasir
import id.cassy.kasir.ranah.kasuspenggunaan.AmatiTransaksiBerdasarkanId
import id.cassy.kasir.ranah.fungsi.hitungKembalian
import id.cassy.kasir.ranah.fungsi.hitungTotalTransaksi
import id.cassy.kasir.ranah.model.Transaksi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
 * Pengelola status untuk layar rincian transaksi tunggal.
 *
 * Pada tahap ini detail transaksi sudah reaktif terhadap perubahan Room
 * dan membaca data melalui use case, bukan langsung ke repository.
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class LayarDetailTransaksiViewModel(
    private val amatiTransaksiBerdasarkanId: AmatiTransaksiBerdasarkanId,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val transaksiId: String = checkNotNull(
        savedStateHandle[TujuanNavigasiKasir.DetailTransaksi.namaArgumenTransaksiId],
    ) {
        "Argumen transaksiId wajib tersedia pada layar detail transaksi."
    }

    private val _nomorPermintaanMuatUlang = MutableStateFlow(0)

    /**
     * Aliran status tampilan detail yang diobservasi oleh UI.
     */
    val modelTampilan: StateFlow<ModelTampilanDetailTransaksi> =
        _nomorPermintaanMuatUlang
            .flatMapLatest {
                flow {
                    emit(
                        ModelTampilanDetailTransaksi(
                            judulLayar = "Detail Transaksi",
                            statusMuat = StatusMuatDetailTransaksi.Memuat,
                        ),
                    )

                    emitAll(
                        amatiTransaksiBerdasarkanId(transaksiId).map { transaksi ->
                            transaksi.keModelTampilanDetailTransaksi(transaksiId)
                        },
                    )
                }.catch {
                    emit(
                        ModelTampilanDetailTransaksi(
                            judulLayar = "Detail Transaksi",
                            statusMuat = StatusMuatDetailTransaksi.Gagal(
                                judul = "Gagal memuat detail transaksi",
                                deskripsi = "Terjadi gangguan saat membaca transaksi. Silakan coba lagi.",
                            ),
                        ),
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ModelTampilanDetailTransaksi(
                    judulLayar = "Detail Transaksi",
                    statusMuat = StatusMuatDetailTransaksi.Memuat,
                ),
            )

    fun muatUlang() {
        _nomorPermintaanMuatUlang.update { nomorLama ->
            nomorLama + 1
        }
    }
}

private fun Transaksi?.keModelTampilanDetailTransaksi(
    transaksiId: String,
): ModelTampilanDetailTransaksi {
    if (this == null) {
        return ModelTampilanDetailTransaksi(
            judulLayar = "Detail Transaksi",
            statusMuat = StatusMuatDetailTransaksi.Kosong(
                judul = "Transaksi tidak ditemukan",
                deskripsi = "Data untuk ID $transaksiId tidak ada di database lokal.",
            ),
        )
    }

    val subtotal = hitungSubtotal()
    val totalAkhir = hitungTotalAkhir()
    val kembalian = hitungKembalian(
        uangDibayar = uangDibayar,
        totalTransaksi = totalAkhir,
    )

    return ModelTampilanDetailTransaksi(
        judulLayar = "Detail Transaksi",
        statusMuat = StatusMuatDetailTransaksi.Berhasil(
            transaksiId = id,
            labelWaktu = waktuTransaksiEpochMili.sebagaiLabelWaktuTransaksi(),
            labelJumlahItem = "${hitungJumlahItem()} item",
            labelSubtotal = subtotal.sebagaiRupiahSederhana(),
            labelPotongan = potongan.sebagaiRupiahSederhana(),
            labelBiayaLayanan = biayaLayanan.sebagaiRupiahSederhana(),
            labelPajak = pajak.sebagaiRupiahSederhana(),
            labelTotalAkhir = totalAkhir.sebagaiRupiahSederhana(),
            labelUangDibayar = uangDibayar.sebagaiRupiahSederhana(),
            labelKembalian = kembalian.sebagaiRupiahSederhana(),
            daftarItem = daftarItemKeranjang.map { itemKeranjang ->
                ItemTampilanDetailTransaksi(
                    namaProduk = itemKeranjang.produk.nama,
                    labelJumlahKaliHarga = "${itemKeranjang.jumlah} x ${itemKeranjang.produk.harga.sebagaiRupiahSederhana()}",
                    labelSubtotalItem = (itemKeranjang.produk.harga * itemKeranjang.jumlah)
                        .sebagaiRupiahSederhana(),
                )
            },
            catatan = catatan,
        ),
    )
}

private fun Transaksi.hitungJumlahItem(): Int {
    return daftarItemKeranjang.sumOf { itemKeranjang ->
        itemKeranjang.jumlah
    }
}

private fun Transaksi.hitungSubtotal(): Long {
    return daftarItemKeranjang.sumOf { itemKeranjang ->
        itemKeranjang.produk.harga * itemKeranjang.jumlah
    }
}

private fun Transaksi.hitungTotalAkhir(): Long {
    return hitungTotalTransaksi(
        daftarItemKeranjang = daftarItemKeranjang,
        potongan = potongan,
        biayaLayanan = biayaLayanan,
        pajak = pajak,
    )
}

private fun Long.sebagaiLabelWaktuTransaksi(): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}

private fun Long.sebagaiRupiahSederhana(): String {
    return "Rp$this"
}
