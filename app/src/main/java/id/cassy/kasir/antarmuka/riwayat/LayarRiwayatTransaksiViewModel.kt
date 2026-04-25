package id.cassy.kasir.antarmuka.riwayat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.cassy.kasir.ranah.contoh.RiwayatTransaksiContoh
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
 * Pengelola logika dan status untuk Layar Riwayat Transaksi.
 *
 * ViewModel ini bertanggung jawab menjembatani data riwayat (saat ini masih dari [RiwayatTransaksiContoh])
 * ke dalam bentuk [ModelTampilanRiwayatTransaksi] yang siap dikonsumsi oleh UI.
 */
class LayarRiwayatTransaksiViewModel : ViewModel() {

    private val _modelTampilan = MutableStateFlow(ModelTampilanRiwayatTransaksi())

    /**
     * Aliran status tampilan yang dapat diobservasi oleh UI.
     */
    val modelTampilan: StateFlow<ModelTampilanRiwayatTransaksi> = _modelTampilan.asStateFlow()

    init {
        muatRiwayatTransaksi()
    }

    /**
     * Memperbarui daftar riwayat transaksi dari sumber data.
     */
    fun muatUlang() {
        muatRiwayatTransaksi()
    }

    private fun muatRiwayatTransaksi() {
        viewModelScope.launch {
            _modelTampilan.update { statusLama ->
                statusLama.copy(
                    statusMuat = StatusMuatRiwayatTransaksi.Memuat,
                )
            }

            try {
                // Simulasi pemuatan data (nantinya diganti dengan repository/Room)
                val daftarTransaksi = RiwayatTransaksiContoh.daftarAwal()

                _modelTampilan.value = if (daftarTransaksi.isEmpty()) {
                    ModelTampilanRiwayatTransaksi(
                        judulLayar = "Riwayat Transaksi",
                        statusMuat = StatusMuatRiwayatTransaksi.Kosong(
                            judul = "Belum ada riwayat transaksi",
                            deskripsi = "Riwayat transaksi akan tampil di sini setelah data transaksi mulai disimpan.",
                        ),
                    )
                } else {
                    ModelTampilanRiwayatTransaksi(
                        judulLayar = "Riwayat Transaksi",
                        statusMuat = StatusMuatRiwayatTransaksi.Berhasil(
                            judulBagian = "Riwayat penjualan",
                            deskripsiBagian = "Masih memakai data contoh agar nanti transisi ke Room tetap ringan.",
                            daftarRingkasanTransaksi = daftarTransaksi.map { transaksi ->
                                RingkasanTransaksiRiwayat(
                                    transaksiId = transaksi.id,
                                    labelWaktu = transaksi.waktuTransaksiEpochMili
                                        .sebagaiLabelWaktuTransaksi(),
                                    labelJumlahItem = "${transaksi.hitungJumlahItem()} item",
                                    labelTotalAkhir = transaksi.hitungTotalAkhir().sebagaiRupiahSederhana(),
                                )
                            },
                        ),
                    )
                }
            } catch (_: Exception) {
                _modelTampilan.value = ModelTampilanRiwayatTransaksi(
                    judulLayar = "Riwayat Transaksi",
                    statusMuat = StatusMuatRiwayatTransaksi.Gagal(
                        judul = "Gagal memuat riwayat transaksi",
                        deskripsi = "Terjadi gangguan saat membaca daftar transaksi. Silakan coba lagi.",
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
