package id.cassy.kasir.antarmuka.riwayat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.cassy.kasir.ranah.kasuspenggunaan.AmatiRiwayatTransaksi
import id.cassy.kasir.ranah.fungsi.hitungTotalTransaksi
import id.cassy.kasir.ranah.model.Transaksi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class LayarRiwayatTransaksiViewModel(
    private val amatiRiwayatTransaksi: AmatiRiwayatTransaksi,
) : ViewModel() {

    private val _nomorPermintaanMuatUlang = MutableStateFlow(0)
    private val _kataKunciPencarian = MutableStateFlow("")

    private val kataKunciPencarianEfektif =
        _kataKunciPencarian
            .debounce(250)
            .map { kataKunci ->
                kataKunci.trim()
            }
            .distinctUntilChanged()

    private val statusSumberData: StateFlow<StatusSumberDataRiwayat> =
        _nomorPermintaanMuatUlang
            .flatMapLatest {
                flow {
                    emit(StatusSumberDataRiwayat.Memuat)

                    emitAll(
                        amatiRiwayatTransaksi().map { daftarTransaksi ->
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

    val modelTampilan: StateFlow<ModelTampilanRiwayatTransaksi> =
        combine(
            _kataKunciPencarian,
            kataKunciPencarianEfektif,
            statusSumberData,
        ) { kataKunciMentah, kataKunciEfektif, statusSumber ->
            bentukModelTampilan(
                kataKunciMentah = kataKunciMentah,
                kataKunciEfektif = kataKunciEfektif,
                statusSumber = statusSumber,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ModelTampilanRiwayatTransaksi(
                judulLayar = "Riwayat Transaksi",
                kataKunciPencarian = "",
                tampilkanAksiResetPencarian = false,
                statusMuat = StatusMuatRiwayatTransaksi.Memuat,
            ),
        )

    init {
        muatUlang()
    }

    fun muatUlang() {
        _nomorPermintaanMuatUlang.update { nomorLama ->
            nomorLama + 1
        }
    }

    fun perbaruiKataKunciPencarian(
        kataKunciBaru: String,
    ) {
        _kataKunciPencarian.value = kataKunciBaru
    }

    fun resetPencarian() {
        _kataKunciPencarian.value = ""
    }

    private fun bentukModelTampilan(
        kataKunciMentah: String,
        kataKunciEfektif: String,
        statusSumber: StatusSumberDataRiwayat,
    ): ModelTampilanRiwayatTransaksi {
        return when (statusSumber) {
            StatusSumberDataRiwayat.Memuat -> {
                ModelTampilanRiwayatTransaksi(
                    judulLayar = "Riwayat Transaksi",
                    kataKunciPencarian = kataKunciMentah,
                    tampilkanAksiResetPencarian = kataKunciMentah.isNotBlank(),
                    statusMuat = StatusMuatRiwayatTransaksi.Memuat,
                )
            }

            is StatusSumberDataRiwayat.Gagal -> {
                ModelTampilanRiwayatTransaksi(
                    judulLayar = "Riwayat Transaksi",
                    kataKunciPencarian = kataKunciMentah,
                    tampilkanAksiResetPencarian = kataKunciMentah.isNotBlank(),
                    statusMuat = StatusMuatRiwayatTransaksi.Gagal(
                        judul = "Gagal memuat riwayat transaksi",
                        deskripsi = statusSumber.pesan,
                    ),
                )
            }

            is StatusSumberDataRiwayat.Berhasil -> {
                val daftarTersaring = statusSumber.daftarTransaksi.filter { transaksi ->
                    transaksi.cocokDenganKataKunci(
                        kataKunci = kataKunciEfektif,
                    )
                }

                if (daftarTersaring.isEmpty()) {
                    ModelTampilanRiwayatTransaksi(
                        judulLayar = "Riwayat Transaksi",
                        kataKunciPencarian = kataKunciMentah,
                        tampilkanAksiResetPencarian = kataKunciMentah.isNotBlank(),
                        statusMuat = StatusMuatRiwayatTransaksi.Kosong(
                            judul = if (kataKunciEfektif.isBlank()) {
                                "Belum ada riwayat transaksi"
                            } else {
                                "Transaksi tidak ditemukan"
                            },
                            deskripsi = if (kataKunciEfektif.isBlank()) {
                                "Riwayat transaksi akan tampil di sini setelah data transaksi mulai disimpan."
                            } else {
                                "Coba reset pencarian atau gunakan kata kunci yang lebih umum."
                            },
                        ),
                    )
                } else {
                    ModelTampilanRiwayatTransaksi(
                        judulLayar = "Riwayat Transaksi",
                        kataKunciPencarian = kataKunciMentah,
                        tampilkanAksiResetPencarian = kataKunciMentah.isNotBlank(),
                        statusMuat = StatusMuatRiwayatTransaksi.Berhasil(
                            judulBagian = "Riwayat penjualan",
                            deskripsiBagian = if (kataKunciEfektif.isBlank()) {
                                "Data diambil dari database lokal."
                            } else {
                                "Menampilkan hasil yang sesuai dengan pencarian aktif."
                            },
                            labelJumlahHasil = "${daftarTersaring.size} transaksi ditemukan",
                            labelKataKunciAktif = kataKunciEfektif.takeIf { it.isNotBlank() }?.let { kataKunci ->
                                "Pencarian aktif: \"$kataKunci\""
                            },
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
