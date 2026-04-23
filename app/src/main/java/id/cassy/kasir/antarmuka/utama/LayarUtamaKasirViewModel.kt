package id.cassy.kasir.antarmuka.utama

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.cassy.kasir.ranah.contoh.KatalogProdukContoh
import id.cassy.kasir.ranah.kasuspenggunaan.BentukModelTampilanLayarUtamaKasir
import id.cassy.kasir.ranah.kasuspenggunaan.HapusProdukDariKeranjang
import id.cassy.kasir.ranah.kasuspenggunaan.KurangiProdukDiKeranjang
import id.cassy.kasir.ranah.kasuspenggunaan.SelesaikanCheckoutLokalKasir
import id.cassy.kasir.ranah.kasuspenggunaan.TambahProdukKeKeranjang
import id.cassy.kasir.ranah.model.ItemKeranjang
import id.cassy.kasir.ranah.model.Produk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
 * Pengelola status layar utama kasir dengan pola Unidirectional Data Flow (UDF).
 *
 * ViewModel ini mengelola aliran status secara atomik untuk:
 * - Keranjang belanja.
 * - Kata kunci pencarian.
 * - Status panel pembayaran.
 * - Status sinkronisasi lokal.
 * - Status dialog konfirmasi checkout.
 * - Status hasil checkout.
 */
class LayarUtamaKasirViewModel : ViewModel() {

    private val daftarProdukPenuh: List<Produk> = KatalogProdukContoh.daftarAwal()

    private val tambahProdukKeKeranjangUseCase = TambahProdukKeKeranjang()
    private val kurangiProdukDiKeranjangUseCase = KurangiProdukDiKeranjang()
    private val hapusProdukDariKeranjangUseCase = HapusProdukDariKeranjang()
    private val selesaikanCheckoutLokalKasirUseCase = SelesaikanCheckoutLokalKasir()
    private val bentukModelTampilanUseCase = BentukModelTampilanLayarUtamaKasir()

    private val _daftarItemKeranjang = MutableStateFlow<List<ItemKeranjang>>(emptyList())
    private val _kataKunciPencarian = MutableStateFlow("")
    private val _apakahRingkasanTampil = MutableStateFlow(true)
    private val _statusSinkronisasi = MutableStateFlow("Tersimpan Lokal")
    private val _apakahDialogKonfirmasiCheckoutTampil = MutableStateFlow(false)
    private val _statusHasilCheckout = MutableStateFlow(StatusHasilCheckoutKasir())

    /**
     * Aliran status UI tunggal.
     */
    @Suppress("UNCHECKED_CAST")
    val modelTampilan: StateFlow<ModelTampilanLayarUtamaKasir> = combine(
        _daftarItemKeranjang,
        _kataKunciPencarian,
        _apakahRingkasanTampil,
        _statusSinkronisasi,
        _apakahDialogKonfirmasiCheckoutTampil,
        _statusHasilCheckout,
    ) { params ->
        bentukModelTampilanUseCase(
            daftarProdukPenuh = daftarProdukPenuh,
            daftarItemKeranjang = params[0] as List<ItemKeranjang>,
            kataKunciPencarian = params[1] as String,
            apakahRingkasanPembayaranTampil = params[2] as Boolean,
            statusSinkronisasi = params[3] as String,
            apakahDialogKonfirmasiCheckoutTampil = params[4] as Boolean,
            statusHasilCheckout = params[5] as StatusHasilCheckoutKasir,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = bentukModelTampilanUseCase(
            daftarProdukPenuh = daftarProdukPenuh,
            daftarItemKeranjang = emptyList(),
            kataKunciPencarian = "",
            apakahRingkasanPembayaranTampil = true,
            statusSinkronisasi = "Tersimpan Lokal",
            apakahDialogKonfirmasiCheckoutTampil = false,
            statusHasilCheckout = StatusHasilCheckoutKasir(),
        ),
    )

    /**
     * Memproses aksi yang datang dari UI.
     */
    fun tanganiAksi(aksi: AksiLayarUtamaKasir) {
        when (aksi) {
            is AksiLayarUtamaKasir.UbahKataKunciPencarian -> {
                perbaruiPencarian(aksi.kataKunciBaru)
            }

            AksiLayarUtamaKasir.UbahVisibilitasRingkasanPembayaran -> {
                alihkanVisibilitasPembayaran()
            }

            is AksiLayarUtamaKasir.TambahProdukKeKeranjang -> {
                tambahProdukKeKeranjang(aksi.produkId)
            }

            is AksiLayarUtamaKasir.KurangiProdukDiKeranjang -> {
                kurangiProdukDiKeranjang(aksi.produkId)
            }

            is AksiLayarUtamaKasir.HapusProdukDariKeranjang -> {
                hapusProdukDariKeranjang(aksi.produkId)
            }

            AksiLayarUtamaKasir.CobaCheckout -> {
                cobaCheckout()
            }

            AksiLayarUtamaKasir.BatalkanKonfirmasiCheckout -> {
                batalkanKonfirmasiCheckout()
            }

            AksiLayarUtamaKasir.KonfirmasiCheckout -> {
                konfirmasiCheckout()
            }

            AksiLayarUtamaKasir.TutupStatusHasilCheckout -> {
                tutupStatusHasilCheckout()
            }
        }
    }

    private fun perbaruiPencarian(kataKunciBaru: String) {
        if (_kataKunciPencarian.value == kataKunciBaru) return
        _kataKunciPencarian.value = kataKunciBaru
    }

    private fun alihkanVisibilitasPembayaran() {
        _apakahRingkasanTampil.update { nilaiLama -> !nilaiLama }
    }

    private fun tambahProdukKeKeranjang(produkId: String) {
        val produk = daftarProdukPenuh.firstOrNull { it.id == produkId } ?: return

        _daftarItemKeranjang.update { daftarLama ->
            tambahProdukKeKeranjangUseCase(
                daftarItemKeranjang = daftarLama,
                produk = produk,
            )
        }

        _statusSinkronisasi.value = "Tersimpan Lokal"
        _statusHasilCheckout.value = StatusHasilCheckoutKasir()
    }

    private fun kurangiProdukDiKeranjang(produkId: String) {
        _daftarItemKeranjang.update { daftarLama ->
            kurangiProdukDiKeranjangUseCase(
                daftarItemKeranjang = daftarLama,
                produkId = produkId,
            )
        }

        _statusSinkronisasi.value = "Tersimpan Lokal"
        _statusHasilCheckout.value = StatusHasilCheckoutKasir()
    }

    private fun hapusProdukDariKeranjang(produkId: String) {
        _daftarItemKeranjang.update { daftarLama ->
            hapusProdukDariKeranjangUseCase(
                daftarItemKeranjang = daftarLama,
                produkId = produkId,
            )
        }

        _statusSinkronisasi.value = "Tersimpan Lokal"
        _statusHasilCheckout.value = StatusHasilCheckoutKasir()
    }

    private fun cobaCheckout() {
        if (_daftarItemKeranjang.value.isEmpty()) return

        _apakahDialogKonfirmasiCheckoutTampil.value = true
        _statusHasilCheckout.value = StatusHasilCheckoutKasir()
    }

    private fun batalkanKonfirmasiCheckout() {
        _apakahDialogKonfirmasiCheckoutTampil.value = false
    }

    private fun konfirmasiCheckout() {
        val daftarKeranjangSaatIni = _daftarItemKeranjang.value
        if (daftarKeranjangSaatIni.isEmpty()) return

        val hasilCheckout = selesaikanCheckoutLokalKasirUseCase(daftarKeranjangSaatIni)

        _daftarItemKeranjang.value = hasilCheckout.daftarItemKeranjangBaru
        _statusSinkronisasi.value = hasilCheckout.statusSinkronisasiBaru
        _apakahDialogKonfirmasiCheckoutTampil.value = false
        _statusHasilCheckout.value = StatusHasilCheckoutKasir(
            apakahTampil = true,
            judul = "Transaksi berhasil",
            deskripsi = "${hasilCheckout.jumlahItemCheckout} item dengan total ${hasilCheckout.totalCheckout.sebagaiRupiahSederhana()} siap disimpan ke riwayat lokal pada scope data berikutnya.",
        )
    }

    private fun tutupStatusHasilCheckout() {
        _statusHasilCheckout.value = StatusHasilCheckoutKasir()
    }
}

private fun Long.sebagaiRupiahSederhana(): String {
    return "Rp$this"
}
