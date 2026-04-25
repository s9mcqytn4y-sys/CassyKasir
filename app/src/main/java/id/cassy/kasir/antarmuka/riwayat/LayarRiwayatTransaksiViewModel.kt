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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
 * Pengelola logika dan status untuk Layar Riwayat Transaksi.
 *
 * Pada tahap ini:
 * - daftar transaksi tetap diambil dari Flow Room,
 * - filter dilakukan di ViewModel,
 * - dan loading tidak lagi bergantung pada delay buatan.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LayarRiwayatTransaksiViewModel(
    private val repositori: RepositoriTransaksi,
) : ViewModel() {

    private val _nomorPermintaanMuatUlang = MutableStateFlow(0)
    private val _kataKunciPencarian = MutableStateFlow("")

    private val statusSumberData: StateFlow<StatusSumberDataRiwayat> =
        _nomorPermintaanMuatUlang
            .flatMapLatest {
                flow {
                    emit(StatusSumberDataRiwayat.Memuat)

                    emitAll(
                        repositori.ambilSemuaTransaksi().map { daftarTransaksi ->
                            StatusSumberDataRiwayat.Berhasil(
                                daftarTransaksi = daftarTransaksi,
                            )
                        },
                    )
                }.catch {
                    emit(
                        StatusSumberDataRiwayat.Gagal(
                            pesan = "Terjadi gangguan saat membaca daftar transaksi. Silakan coba lagi.",
                        ),
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = StatusSumberDataRiwayat.Memuat,
            )

    /**
     * Aliran status tampilan yang dapat diobservasi oleh UI.
     */
    val modelTampilan: StateFlow<ModelTampilanRiwayatTransaksi> =
        combine(
            _kataKunciPencarian,
            statusSumberData,
        ) { kataKunciPencarian, statusSumber ->
            bentukModelTampilan(
                kataKunciPencarian = kataKunciPencarian,
                statusSumber = statusSumber,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ModelTampilanRiwayatTransaksi(
                judulLayar = "Riwayat Transaksi",
                kataKunciPencarian = "",
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

    /**
     * Memperbarui kata kunci pencarian riwayat transaksi.
     */
    fun perbaruiKataKunciPencarian(
        kataKunciBaru: String,
    ) {
        _kataKunciPencarian.value = kataKunciBaru
    }

    private fun bentukModelTampilan(
        kataKunciPencarian: String,
        statusSumber: StatusSumberDataRiwayat,
    ): ModelTampilanRiwayatTransaksi {
        return when (statusSumber) {
            StatusSumberDataRiwayat.Memuat -> {
                ModelTampilanRiwayatTransaksi(
                    judulLayar = "Riwayat Transaksi",
                    kataKunciPencarian = kataKunciPencarian,
                    statusMuat = StatusMuatRiwayatTransaksi.Memuat,
                )
            }

            is StatusSumberDataRiwayat.Gagal -> {
                ModelTampilanRiwayatTransaksi(
                    judulLayar = "Riwayat Transaksi",
                    kataKunciPencarian = kataKunciPencarian,
                    statusMuat = StatusMuatRiwayatTransaksi.Gagal(
                        judul = "Gagal memuat riwayat transaksi",
                        deskripsi = statusSumber.pesan,
                    ),
                )
            }

            is StatusSumberDataRiwayat.Berhasil -> {
                val kataKunciNormal = kataKunciPencarian.trim()
                val daftarTersaring = statusSumber.daftarTransaksi.filter { transaksi ->
                    transaksi.cocokDenganKataKunci(
                        kataKunci = kataKunciNormal,
                    )
                }

                if (daftarTersaring.isEmpty()) {
                    ModelTampilanRiwayatTransaksi(
                        judulLayar = "Riwayat Transaksi",
                        kataKunciPencarian = kataKunciPencarian,
                        statusMuat = StatusMuatRiwayatTransaksi.Kosong(
                            judul = if (kataKunciNormal.isBlank()) {
                                "Belum ada riwayat transaksi"
                            } else {
                                "Transaksi tidak ditemukan"
                            },
                            deskripsi = if (kataKunciNormal.isBlank()) {
                                "Riwayat transaksi akan tampil di sini setelah data transaksi mulai disimpan."
                            } else {
                                "Coba gunakan kata kunci lain yang lebih umum atau cek kembali id transaksi."
                            },
                        ),
                    )
                } else {
                    ModelTampilanRiwayatTransaksi(
                        judulLayar = "Riwayat Transaksi",
                        kataKunciPencarian = kataKunciPencarian,
                        statusMuat = StatusMuatRiwayatTransaksi.Berhasil(
                            judulBagian = "Riwayat penjualan",
                            deskripsiBagian = if (kataKunciNormal.isBlank()) {
                                "Data diambil dari database lokal."
                            } else {
                                "Menampilkan hasil yang sesuai dengan pencarian aktif."
                            },
                            labelJumlahHasil = "${daftarTersaring.size} transaksi ditemukan",
                            daftarRingkasanTransaksi = daftarTersaring.map { transaksi ->
                                transaksi.keRingkasanTransaksiRiwayat()
                            },
                        ),
                    )
                }
            }
        }
    }
}

private sealed interface StatusSumberDataRiwayat {
    data object Memuat : StatusSumberDataRiwayat

    data class Berhasil(
        val daftarTransaksi: List<Transaksi>,
    ) : StatusSumberDataRiwayat

    data class Gagal(
        val pesan: String,
    ) : StatusSumberDataRiwayat
}

private fun Transaksi.cocokDenganKataKunci(
    kataKunci: String,
): Boolean {
    if (kataKunci.isBlank()) return true

    return id.contains(kataKunci, ignoreCase = true) ||
        catatan?.contains(kataKunci, ignoreCase = true) == true ||
        daftarItemKeranjang.any { itemKeranjang ->
            itemKeranjang.produk.nama.contains(kataKunci, ignoreCase = true) ||
                itemKeranjang.catatan?.contains(kataKunci, ignoreCase = true) == true
        }
}

private fun Transaksi.keRingkasanTransaksiRiwayat(): RingkasanTransaksiRiwayat {
    return RingkasanTransaksiRiwayat(
        transaksiId = id,
        labelWaktu = waktuTransaksiEpochMili.sebagaiLabelWaktuTransaksi(),
        labelJumlahItem = "${hitungJumlahItem()} item",
        labelTotalAkhir = hitungTotalAkhir().sebagaiRupiahSederhana(),
        ringkasanItem = bentukRingkasanItem(),
    )
}

private fun Transaksi.bentukRingkasanItem(): String {
    val daftarNamaProduk = daftarItemKeranjang.map { itemKeranjang ->
        itemKeranjang.produk.nama
    }

    if (daftarNamaProduk.isEmpty()) {
        return "Tanpa item"
    }

    val duaProdukPertama = daftarNamaProduk.take(2).joinToString(separator = ", ")
    val sisaProduk = daftarNamaProduk.size - 2

    return if (sisaProduk > 0) {
        "$duaProdukPertama +$sisaProduk lainnya"
    } else {
        duaProdukPertama
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
