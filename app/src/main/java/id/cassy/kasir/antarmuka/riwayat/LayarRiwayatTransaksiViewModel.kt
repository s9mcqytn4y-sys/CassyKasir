package id.cassy.kasir.antarmuka.riwayat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.cassy.kasir.data.lokal.repositori.RepositoriTransaksi
import id.cassy.kasir.ranah.fungsi.hitungTotalTransaksi
import id.cassy.kasir.ranah.model.Transaksi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
 * Pengelola logika dan status untuk Layar Riwayat Transaksi.
 *
 * Pada tahap ini riwayat transaksi dibangun dari aliran data Room secara reaktif,
 * tanpa delay buatan untuk loading.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LayarRiwayatTransaksiViewModel(
    private val repositori: RepositoriTransaksi,
) : ViewModel() {

    private val _nomorPermintaanMuatUlang = MutableStateFlow(0)

    /**
     * Aliran status tampilan yang dapat diobservasi oleh UI.
     */
    val modelTampilan: StateFlow<ModelTampilanRiwayatTransaksi> =
        _nomorPermintaanMuatUlang
            .flatMapLatest {
                flow {
                    emit(
                        ModelTampilanRiwayatTransaksi(
                            judulLayar = "Riwayat Transaksi",
                            statusMuat = StatusMuatRiwayatTransaksi.Memuat,
                        ),
                    )

                    emitAll(
                        repositori.ambilSemuaTransaksi().map { daftarTransaksi ->
                            if (daftarTransaksi.isEmpty()) {
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
                                        deskripsiBagian = "Data diambil dari database lokal.",
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
                        },
                    )
                }.catch {
                    emit(
                        ModelTampilanRiwayatTransaksi(
                            judulLayar = "Riwayat Transaksi",
                            statusMuat = StatusMuatRiwayatTransaksi.Gagal(
                                judul = "Gagal memuat riwayat transaksi",
                                deskripsi = "Terjadi gangguan saat membaca daftar transaksi. Silakan coba lagi.",
                            ),
                        ),
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ModelTampilanRiwayatTransaksi(
                    judulLayar = "Riwayat Transaksi",
                    statusMuat = StatusMuatRiwayatTransaksi.Memuat,
                ),
            )

    init {
        muatUlang()
    }

    /**
     * Meminta layar untuk berlangganan ulang ke aliran data riwayat transaksi.
     */
    fun muatUlang() {
        _nomorPermintaanMuatUlang.update { nomorLama ->
            nomorLama + 1
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
