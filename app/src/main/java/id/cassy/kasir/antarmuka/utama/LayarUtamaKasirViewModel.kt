package id.cassy.kasir.antarmuka.utama

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.cassy.kasir.ranah.fungsi.sebagaiRupiah
import id.cassy.kasir.ranah.kasuspenggunaan.BentukModelTampilanLayarUtamaKasir
import id.cassy.kasir.ranah.kasuspenggunaan.HapusProdukDariKeranjang
import id.cassy.kasir.ranah.kasuspenggunaan.KurangiProdukDiKeranjang
import id.cassy.kasir.ranah.kasuspenggunaan.MuatKatalogProduk
import id.cassy.kasir.ranah.kasuspenggunaan.SelesaikanCheckoutLokalKasir
import id.cassy.kasir.ranah.kasuspenggunaan.TambahProdukKeKeranjang
import id.cassy.kasir.ranah.model.Produk
import id.cassy.kasir.ranah.model.StatusSinkronisasi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Pengelola status layar utama kasir dengan pola alur data satu arah (UDF).
 * Mengatur keranjang belanja, proses checkout ke penyimpanan lokal,
 * serta fitur pencarian produk reaktif dengan debounce untuk performa optimal.
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class LayarUtamaKasirViewModel(
    private val muatKatalogProduk: MuatKatalogProduk,
    private val selesaikanCheckoutLokalKasirUseCase: SelesaikanCheckoutLokalKasir,
) : ViewModel() {

    private val daftarProdukPenuh = muatKatalogProduk.eksekusi()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    private val tambahProdukKeKeranjangUseCase = TambahProdukKeKeranjang()
    private val kurangiProdukDiKeranjangUseCase = KurangiProdukDiKeranjang()
    private val hapusProdukDariKeranjangUseCase = HapusProdukDariKeranjang()
    private val bentukModelTampilanUseCase = BentukModelTampilanLayarUtamaKasir()

    private val _statusTransaksi = MutableStateFlow(
        StatusTransaksiLayarUtamaKasir(),
    )

    private val _statusElemenLayar = MutableStateFlow(
        StatusElemenLayarUtamaKasir(),
    )

    private val _efek = MutableSharedFlow<EfekLayarUtamaKasir>(
        extraBufferCapacity = 1,
    )
    val efek: SharedFlow<EfekLayarUtamaKasir> = _efek.asSharedFlow()

    private val _kataKunciPencarian = MutableStateFlow("")

    private val kataKunciPencarianEfektif =
        _kataKunciPencarian
            .debounce(250)
            .map { kataKunci ->
                kataKunci.trim()
            }
            .distinctUntilChanged()

    /**
     * Aliran status UI publik yang dirender oleh Compose.
     */
    val modelTampilan = combine(
        daftarProdukPenuh,
        _statusTransaksi,
        _statusElemenLayar,
        _kataKunciPencarian,
        kataKunciPencarianEfektif,
    ) { produk, statusTransaksi, statusElemenLayar, kataKunciMentah, kataKunciEfektif ->
        bentukModelTampilanUseCase(
            daftarProdukPenuh = produk,
            statusTransaksi = statusTransaksi,
            statusElemenLayar = statusElemenLayar,
            kataKunciMentah = kataKunciMentah,
            kataKunciEfektif = kataKunciEfektif,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = bentukModelTampilanUseCase(
            daftarProdukPenuh = emptyList(),
            statusTransaksi = StatusTransaksiLayarUtamaKasir(),
            statusElemenLayar = StatusElemenLayarUtamaKasir(),
            kataKunciMentah = "",
            kataKunciEfektif = "",
        ),
    )

    /**
     * Titik masuk tunggal untuk semua aksi yang dipicu oleh UI.
     */
    fun tanganiAksi(aksi: AksiLayarUtamaKasir) {
        when (aksi) {
            is AksiLayarUtamaKasir.UbahKataKunciPencarian -> perbaruiPencarian(aksi.kataKunciBaru)
            AksiLayarUtamaKasir.UbahVisibilitasRingkasanPembayaran -> alihkanVisibilitasPembayaran()
            is AksiLayarUtamaKasir.TambahProdukKeKeranjang -> tambahProdukKeKeranjang(aksi.produkId)
            is AksiLayarUtamaKasir.KurangiProdukDiKeranjang -> kurangiProdukDiKeranjang(aksi.produkId)
            is AksiLayarUtamaKasir.HapusProdukDariKeranjang -> hapusProdukDariKeranjang(aksi.produkId)
            AksiLayarUtamaKasir.CobaCheckout -> cobaCheckout()
            AksiLayarUtamaKasir.BatalkanKonfirmasiCheckout -> batalkanKonfirmasiCheckout()
            AksiLayarUtamaKasir.KonfirmasiCheckout -> konfirmasiCheckout()
            AksiLayarUtamaKasir.TutupStatusHasilCheckout -> tutupStatusHasilCheckout()
            AksiLayarUtamaKasir.ResetPencarian -> resetPencarian()
        }
    }

    private fun perbaruiPencarian(kataKunciBaru: String) {
        _kataKunciPencarian.value = kataKunciBaru
    }

    private fun resetPencarian() {
        _kataKunciPencarian.value = ""
    }

    private fun alihkanVisibilitasPembayaran() {
        _statusElemenLayar.update { statusLama ->
            statusLama.copy(
                apakahRingkasanPembayaranTampil = !statusLama.apakahRingkasanPembayaranTampil,
            )
        }
    }

    private fun tambahProdukKeKeranjang(produkId: String) {
        val produk = daftarProdukPenuh.value.firstOrNull { it.id == produkId } ?: return

        var apakahStokPenuh = false

        _statusTransaksi.update { statusLama ->
            val daftarLama = statusLama.daftarItemKeranjang
            val daftarBaru = tambahProdukKeKeranjangUseCase(
                daftarItemKeranjang = daftarLama,
                produk = produk,
            )

            apakahStokPenuh = daftarBaru == daftarLama

            statusLama.copy(
                daftarItemKeranjang = daftarBaru,
                statusSinkronisasi = StatusSinkronisasi.SinkronLokal,
            )
        }

        _statusElemenLayar.update { statusLama ->
            statusLama.copy(
                statusHasilCheckout = StatusHasilCheckoutKasir(),
            )
        }

        if (apakahStokPenuh) {
            kirimPesanSingkat("Stok produk sudah mencapai batas maksimum.")
        }
    }

    private fun kurangiProdukDiKeranjang(produkId: String) {
        _statusTransaksi.update { statusLama ->
            statusLama.copy(
                daftarItemKeranjang = kurangiProdukDiKeranjangUseCase(
                    daftarItemKeranjang = statusLama.daftarItemKeranjang,
                    produkId = produkId,
                ),
                statusSinkronisasi = StatusSinkronisasi.SinkronLokal,
            )
        }

        resetStatusHasil()
    }

    private fun hapusProdukDariKeranjang(produkId: String) {
        _statusTransaksi.update { statusLama ->
            statusLama.copy(
                daftarItemKeranjang = hapusProdukDariKeranjangUseCase(
                    daftarItemKeranjang = statusLama.daftarItemKeranjang,
                    produkId = produkId,
                ),
                statusSinkronisasi = StatusSinkronisasi.SinkronLokal,
            )
        }

        resetStatusHasil()
    }

    private fun cobaCheckout() {
        if (_statusTransaksi.value.daftarItemKeranjang.isEmpty()) {
            kirimPesanSingkat("Keranjang masih kosong. Yuk, tambah produk!")
            return
        }

        _statusElemenLayar.update { statusLama ->
            statusLama.copy(
                apakahDialogKonfirmasiCheckoutTampil = true,
                statusHasilCheckout = StatusHasilCheckoutKasir(),
            )
        }
    }

    private fun batalkanKonfirmasiCheckout() {
        _statusElemenLayar.update { statusLama ->
            statusLama.copy(
                apakahDialogKonfirmasiCheckoutTampil = false,
            )
        }
    }

    private fun konfirmasiCheckout() {
        val daftarKeranjangSaatIni = _statusTransaksi.value.daftarItemKeranjang
        if (daftarKeranjangSaatIni.isEmpty()) {
            kirimPesanSingkat("Keranjang masih kosong. Yuk, tambah produk!")
            return
        }

        viewModelScope.launch {
            try {
                val hasilCheckout = selesaikanCheckoutLokalKasirUseCase.eksekusi(
                    daftarItemKeranjang = daftarKeranjangSaatIni,
                )

                _statusTransaksi.update {
                    StatusTransaksiLayarUtamaKasir(
                        daftarItemKeranjang = hasilCheckout.daftarItemKeranjangBaru,
                        statusSinkronisasi = hasilCheckout.statusSinkronisasiBaru,
                    )
                }

                _statusElemenLayar.update { statusLama ->
                    statusLama.copy(
                        apakahDialogKonfirmasiCheckoutTampil = false,
                        statusHasilCheckout = StatusHasilCheckoutKasir(
                            apakahTampil = true,
                            judul = "Transaksi Berhasil",
                            deskripsi = "Sebanyak ${hasilCheckout.jumlahItemCheckout} item dengan total ${hasilCheckout.totalCheckout.sebagaiRupiah()} telah diproses.",
                        ),
                    )
                }
            } catch (_: Exception) {
                _statusElemenLayar.update { statusLama ->
                    statusLama.copy(
                        apakahDialogKonfirmasiCheckoutTampil = false,
                    )
                }

                kirimPesanSingkat("Transaksi belum tersimpan. Coba lagi.")
            }
        }
    }

    private fun tutupStatusHasilCheckout() {
        _statusElemenLayar.update { statusLama ->
            statusLama.copy(
                statusHasilCheckout = StatusHasilCheckoutKasir(),
            )
        }
    }

    private fun resetStatusHasil() {
        _statusElemenLayar.update { statusLama ->
            statusLama.copy(
                statusHasilCheckout = StatusHasilCheckoutKasir(),
            )
        }
    }

    fun tampilkanPesanOperasional(
        pesan: String,
    ) {
        if (pesan.isBlank()) return
        kirimPesanSingkat(pesan)
    }

    private fun kirimPesanSingkat(pesan: String) {
        _efek.tryEmit(
            EfekLayarUtamaKasir.TampilkanPesanSingkat(
                pesan = pesan,
            ),
        )
    }
}

