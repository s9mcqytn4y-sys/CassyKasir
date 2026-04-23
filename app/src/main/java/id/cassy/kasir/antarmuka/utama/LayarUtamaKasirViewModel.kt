package id.cassy.kasir.antarmuka.utama

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.cassy.kasir.ranah.contoh.KatalogProdukContoh
import id.cassy.kasir.ranah.kasuspenggunaan.BentukModelTampilanLayarUtamaKasir
import id.cassy.kasir.ranah.kasuspenggunaan.HapusProdukDariKeranjang
import id.cassy.kasir.ranah.kasuspenggunaan.KurangiProdukDiKeranjang
import id.cassy.kasir.ranah.kasuspenggunaan.SelesaikanCheckoutLokalKasir
import id.cassy.kasir.ranah.kasuspenggunaan.TambahProdukKeKeranjang
import id.cassy.kasir.ranah.model.Produk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
 * Pengelola status (ViewModel) untuk layar utama kasir menggunakan pola Unidirectional Data Flow (UDF).
 *
 * ViewModel ini bertanggung jawab untuk mengelola aliran data antara lapisan domain dan antarmuka.
 * Status internal dipisahkan menjadi [StatusTransaksiLayarUtamaKasir] untuk data bisnis
 * dan [StatusElemenLayarUtamaKasir] untuk status UI murni guna meningkatkan keterbacaan dan pemeliharaan.
 *
 * @property daftarProdukPenuh Sumber data produk mentah dari katalog.
 */
class LayarUtamaKasirViewModel : ViewModel() {

    private val daftarProdukPenuh: List<Produk> = KatalogProdukContoh.daftarAwal()

    // Kasus penggunaan (Use Cases)
    private val tambahProdukKeKeranjangUseCase = TambahProdukKeKeranjang()
    private val kurangiProdukDiKeranjangUseCase = KurangiProdukDiKeranjang()
    private val hapusProdukDariKeranjangUseCase = HapusProdukDariKeranjang()
    private val selesaikanCheckoutLokalKasirUseCase = SelesaikanCheckoutLokalKasir()
    private val bentukModelTampilanUseCase = BentukModelTampilanLayarUtamaKasir()

    // Status internal yang dikemas (Encapsulated internal states)
    private val _statusTransaksi = MutableStateFlow(StatusTransaksiLayarUtamaKasir())
    private val _statusElemenLayar = MutableStateFlow(StatusElemenLayarUtamaKasir())

    /**
     * Aliran status UI tunggal (StateFlow) yang dikonsumsi oleh layar utama.
     * Menggabungkan data transaksi dan elemen visual menggunakan operator [combine].
     */
    val modelTampilan: StateFlow<ModelTampilanLayarUtamaKasir> = combine(
        _statusTransaksi,
        _statusElemenLayar,
    ) { statusTransaksi, statusElemenLayar ->
        bentukModelTampilanUseCase(
            daftarProdukPenuh = daftarProdukPenuh,
            statusTransaksi = statusTransaksi,
            statusElemenLayar = statusElemenLayar,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = bentukModelTampilanUseCase(
            daftarProdukPenuh = daftarProdukPenuh,
            statusTransaksi = StatusTransaksiLayarUtamaKasir(),
            statusElemenLayar = StatusElemenLayarUtamaKasir(),
        ),
    )

    /**
     * Titik masuk tunggal untuk semua interaksi pengguna dari UI.
     *
     * @param aksi Representasi dari interaksi pengguna (Event).
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
        }
    }

    private fun perbaruiPencarian(kataKunciBaru: String) {
        _statusElemenLayar.update { it.copy(kataKunciPencarian = kataKunciBaru) }
    }

    private fun alihkanVisibilitasPembayaran() {
        _statusElemenLayar.update { it.copy(apakahRingkasanPembayaranTampil = !it.apakahRingkasanPembayaranTampil) }
    }

    private fun tambahProdukKeKeranjang(produkId: String) {
        val produk = daftarProdukPenuh.firstOrNull { it.id == produkId } ?: return

        _statusTransaksi.update { statusLama ->
            statusLama.copy(
                daftarItemKeranjang = tambahProdukKeKeranjangUseCase(
                    daftarItemKeranjang = statusLama.daftarItemKeranjang,
                    produk = produk,
                ),
                statusSinkronisasi = "Tersimpan Lokal",
            )
        }
        resetStatusHasil()
    }

    private fun kurangiProdukDiKeranjang(produkId: String) {
        _statusTransaksi.update { statusLama ->
            statusLama.copy(
                daftarItemKeranjang = kurangiProdukDiKeranjangUseCase(
                    daftarItemKeranjang = statusLama.daftarItemKeranjang,
                    produkId = produkId,
                ),
                statusSinkronisasi = "Tersimpan Lokal",
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
                statusSinkronisasi = "Tersimpan Lokal",
            )
        }
        resetStatusHasil()
    }

    private fun cobaCheckout() {
        if (_statusTransaksi.value.daftarItemKeranjang.isEmpty()) return

        _statusElemenLayar.update {
            it.copy(
                apakahDialogKonfirmasiCheckoutTampil = true,
                statusHasilCheckout = StatusHasilCheckoutKasir(),
            )
        }
    }

    private fun batalkanKonfirmasiCheckout() {
        _statusElemenLayar.update { it.copy(apakahDialogKonfirmasiCheckoutTampil = false) }
    }

    private fun konfirmasiCheckout() {
        val daftarKeranjangSaatIni = _statusTransaksi.value.daftarItemKeranjang
        if (daftarKeranjangSaatIni.isEmpty()) return

        val hasilCheckout = selesaikanCheckoutLokalKasirUseCase(daftarKeranjangSaatIni)

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
                    deskripsi = "Sebanyak ${hasilCheckout.jumlahItemCheckout} item dengan total ${hasilCheckout.totalCheckout.sebagaiRupiahSederhana()} telah diproses secara lokal.",
                ),
            )
        }
    }

    private fun tutupStatusHasilCheckout() {
        _statusElemenLayar.update { it.copy(statusHasilCheckout = StatusHasilCheckoutKasir()) }
    }

    private fun resetStatusHasil() {
        _statusElemenLayar.update { it.copy(statusHasilCheckout = StatusHasilCheckoutKasir()) }
    }
}

/**
 * Memformat nilai Long menjadi string representasi Rupiah sederhana.
 */
private fun Long.sebagaiRupiahSederhana(): String {
    return "Rp$this"
}
