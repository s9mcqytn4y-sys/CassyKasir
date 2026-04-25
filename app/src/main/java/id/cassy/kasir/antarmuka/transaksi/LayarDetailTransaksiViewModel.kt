package id.cassy.kasir.antarmuka.transaksi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.cassy.kasir.antarmuka.navigasi.TujuanNavigasiKasir
import id.cassy.kasir.data.lokal.repositori.RepositoriTransaksi
import id.cassy.kasir.ranah.model.Transaksi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import id.cassy.kasir.ranah.fungsi.hitungTotalTransaksi
import id.cassy.kasir.ranah.fungsi.hitungKembalian
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Pengelola status untuk layar rincian transaksi tunggal.
 * Membaca [transaksiId] dari [SavedStateHandle] untuk memuat data spesifik dari database.
 *
 * @property repositori Sumber data transaksi lokal.
 * @property savedStateHandle Pengelola status state untuk navigasi dan pemulihan data.
 */
class LayarDetailTransaksiViewModel(
    private val repositori: RepositoriTransaksi,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val transaksiId: String? = savedStateHandle[TujuanNavigasiKasir.DetailTransaksi.namaArgumenTransaksiId]

    private val _modelTampilan = MutableStateFlow(ModelTampilanDetailTransaksi())

    /**
     * Aliran status tampilan detail yang diobservasi oleh UI.
     */
    val modelTampilan: StateFlow<ModelTampilanDetailTransaksi> = _modelTampilan.asStateFlow()

    init {
        muatDetailTransaksi()
    }

    /**
     * Memuat ulang data detail transaksi dari repositori.
     */
    fun muatUlang() {
        muatDetailTransaksi()
    }

    private fun muatDetailTransaksi() {
        val id = transaksiId
        if (id == null) {
            _modelTampilan.value = ModelTampilanDetailTransaksi(
                statusMuat = StatusMuatDetailTransaksi.Gagal(
                    judul = "Data transaksi tidak ditemukan",
                    deskripsi = "ID transaksi tidak valid atau tidak tersedia.",
                ),
            )
            return
        }

        viewModelScope.launch {
            _modelTampilan.update { it.copy(statusMuat = StatusMuatDetailTransaksi.Memuat) }
            try {
                val transaksi = repositori.ambilTransaksiBerdasarkanId(id)

                if (transaksi == null) {
                    _modelTampilan.value = ModelTampilanDetailTransaksi(
                        statusMuat = StatusMuatDetailTransaksi.Gagal(
                            judul = "Transaksi tidak ditemukan",
                            deskripsi = "Data untuk ID $id tidak ada di database lokal.",
                        ),
                    )
                } else {
                    val subtotal = transaksi.hitungSubtotal()
                    val totalAkhir = transaksi.hitungTotalAkhir()
                    val kembalian = hitungKembalian(
                        uangDibayar = transaksi.uangDibayar,
                        totalTransaksi = totalAkhir,
                    )

                    ModelTampilanDetailTransaksi(
                        judulLayar = "Detail Transaksi",
                        statusMuat = StatusMuatDetailTransaksi.Berhasil(
                            transaksiId = transaksi.id,
                            labelWaktu = transaksi.waktuTransaksiEpochMili.sebagaiLabelWaktuTransaksi(),
                            labelJumlahItem = "${transaksi.hitungJumlahItem()} item",
                            labelSubtotal = subtotal.sebagaiRupiahSederhana(),
                            labelPotongan = transaksi.potongan.sebagaiRupiahSederhana(),
                            labelBiayaLayanan = transaksi.biayaLayanan.sebagaiRupiahSederhana(),
                            labelPajak = transaksi.pajak.sebagaiRupiahSederhana(),
                            labelTotalAkhir = totalAkhir.sebagaiRupiahSederhana(),
                            labelUangDibayar = transaksi.uangDibayar.sebagaiRupiahSederhana(),
                            labelKembalian = kembalian.sebagaiRupiahSederhana(),
                            daftarItem = transaksi.daftarItemKeranjang.map { itemKeranjang ->
                                ItemTampilanDetailTransaksi(
                                    namaProduk = itemKeranjang.produk.nama,
                                    labelJumlahKaliHarga = "${itemKeranjang.jumlah} x ${itemKeranjang.produk.harga.sebagaiRupiahSederhana()}",
                                    labelSubtotalItem = (itemKeranjang.produk.harga * itemKeranjang.jumlah)
                                        .sebagaiRupiahSederhana(),
                                )
                            },
                            catatan = transaksi.catatan,
                        ),
                    )
                }
            } catch (_: Exception) {
                _modelTampilan.value = ModelTampilanDetailTransaksi(
                    judulLayar = "Detail Transaksi",
                    statusMuat = StatusMuatDetailTransaksi.Gagal(
                        judul = "Gagal memuat detail transaksi",
                        deskripsi = "Terjadi gangguan saat membaca transaksi. Silakan coba lagi.",
                    ),
                )
            }
        }
    }
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
