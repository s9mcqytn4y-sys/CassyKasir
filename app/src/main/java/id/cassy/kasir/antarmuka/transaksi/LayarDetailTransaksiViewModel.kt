package id.cassy.kasir.antarmuka.transaksi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.cassy.kasir.antarmuka.navigasi.TujuanNavigasiKasir
import id.cassy.kasir.ranah.contoh.RiwayatTransaksiContoh
import id.cassy.kasir.ranah.fungsi.hitungKembalian
import id.cassy.kasir.ranah.fungsi.hitungTotalTransaksi
import id.cassy.kasir.ranah.model.Transaksi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Pengelola logika dan status untuk Layar Detail Transaksi.
 *
 * ViewModel ini mengambil [transaksiId] dari [SavedStateHandle] dan memuat data rinciannya
 * untuk dipetakan ke dalam [ModelTampilanDetailTransaksi].
 */
class LayarDetailTransaksiViewModel(
    statusTersimpan: SavedStateHandle,
) : ViewModel() {

    private val transaksiId: String = checkNotNull(
        statusTersimpan.get<String>(
            TujuanNavigasiKasir.DetailTransaksi.namaArgumenTransaksiId,
        ),
    ) {
        "Argumen transaksiId wajib tersedia pada layar detail transaksi."
    }

    private val _modelTampilan = MutableStateFlow(ModelTampilanDetailTransaksi())

    /**
     * Aliran status tampilan yang dapat diobservasi oleh UI.
     */
    val modelTampilan: StateFlow<ModelTampilanDetailTransaksi> = _modelTampilan.asStateFlow()

    init {
        muatDetailTransaksi()
    }

    /**
     * Mencoba memuat ulang data rincian transaksi.
     */
    fun muatUlang() {
        muatDetailTransaksi()
    }

    private fun muatDetailTransaksi() {
        viewModelScope.launch {
            _modelTampilan.update { statusLama ->
                statusLama.copy(
                    statusMuat = StatusMuatDetailTransaksi.Memuat,
                )
            }

            try {
                // Simulasi pemuatan data (nantinya diganti dengan repository/Room)
                val transaksi = RiwayatTransaksiContoh.temukanBerdasarkanId(transaksiId)

                _modelTampilan.value = if (transaksi == null) {
                    ModelTampilanDetailTransaksi(
                        judulLayar = "Detail Transaksi",
                        statusMuat = StatusMuatDetailTransaksi.Kosong(
                            judul = "Transaksi tidak ditemukan",
                            deskripsi = "Transaksi dengan id $transaksiId belum tersedia pada data contoh saat ini.",
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
