package id.cassy.kasir.antarmuka.utama

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.cassy.kasir.ranah.contoh.KatalogProdukContoh
import id.cassy.kasir.ranah.kasuspenggunaan.BentukModelTampilanLayarUtamaKasir
import id.cassy.kasir.ranah.kasuspenggunaan.HapusProdukDariKeranjang
import id.cassy.kasir.ranah.kasuspenggunaan.KurangiProdukDiKeranjang
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
 * ViewModel ini mengelola aliran status (StateFlow) secara atomik untuk:
 * - Sinkronisasi data keranjang belanja melalui use cases.
 * - Penyaringan katalog produk berdasarkan input pengguna.
 * - Penggabungan state atomik menjadi model tampilan tunggal yang efisien.
 */
class LayarUtamaKasirViewModel : ViewModel() {

    private val daftarProdukPenuh: List<Produk> = KatalogProdukContoh.daftarAwal()

    // Kasus penggunaan (Use Cases)
    private val tambahProdukKeKeranjangUseCase = TambahProdukKeKeranjang()
    private val kurangiProdukDiKeranjangUseCase = KurangiProdukDiKeranjang()
    private val hapusProdukDariKeranjangUseCase = HapusProdukDariKeranjang()
    private val bentukModelTampilanUseCase = BentukModelTampilanLayarUtamaKasir()

    // Status atomik internal yang bersifat privat
    private val _daftarItemKeranjang = MutableStateFlow<List<ItemKeranjang>>(emptyList())
    private val _kataKunciPencarian = MutableStateFlow("")
    private val _apakahRingkasanTampil = MutableStateFlow(true)

    /**
     * Aliran status UI tunggal yang menggabungkan seluruh status internal.
     * Menggunakan [combine] untuk memastikan re-kalkulasi hanya terjadi saat data berubah.
     */
    val modelTampilan: StateFlow<ModelTampilanLayarUtamaKasir> = combine(
        _daftarItemKeranjang,
        _kataKunciPencarian,
        _apakahRingkasanTampil
    ) { keranjang, kataKunci, tampil ->
        bentukModelTampilanUseCase(
            daftarProdukPenuh = daftarProdukPenuh,
            daftarItemKeranjang = keranjang,
            kataKunciPencarian = kataKunci,
            apakahRingkasanPembayaranTampil = tampil
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = bentukModelTampilanUseCase(
            daftarProdukPenuh = daftarProdukPenuh,
            daftarItemKeranjang = emptyList(),
            kataKunciPencarian = "",
            apakahRingkasanPembayaranTampil = true
        )
    )

    /**
     * Menangani interaksi pengguna (Aksi) dari antarmuka.
     *
     * @param aksi Objek [AksiLayarUtamaKasir] yang mewakili interaksi pengguna.
     */
    fun tanganiAksi(aksi: AksiLayarUtamaKasir) {
        when (aksi) {
            is AksiLayarUtamaKasir.UbahKataKunciPencarian -> {
                _kataKunciPencarian.value = aksi.kataKunciBaru
            }

            AksiLayarUtamaKasir.UbahVisibilitasRingkasanPembayaran -> {
                _apakahRingkasanTampil.update { !it }
            }

            is AksiLayarUtamaKasir.TambahProdukKeKeranjang -> {
                tambahKeKeranjang(aksi.produkId)
            }

            is AksiLayarUtamaKasir.KurangiProdukDiKeranjang -> {
                kurangiDiKeranjang(aksi.produkId)
            }

            is AksiLayarUtamaKasir.HapusProdukDariKeranjang -> {
                hapusDariKeranjang(aksi.produkId)
            }
        }
    }

    private fun tambahKeKeranjang(produkId: String) {
        val produkTarget = daftarProdukPenuh.firstOrNull { it.id == produkId } ?: return
        _daftarItemKeranjang.update { daftarLama ->
            tambahProdukKeKeranjangUseCase(daftarLama, produkTarget)
        }
    }

    private fun kurangiDiKeranjang(produkId: String) {
        _daftarItemKeranjang.update { daftarLama ->
            kurangiProdukDiKeranjangUseCase(daftarLama, produkId)
        }
    }

    private fun hapusDariKeranjang(produkId: String) {
        _daftarItemKeranjang.update { daftarLama ->
            hapusProdukDariKeranjangUseCase(daftarLama, produkId)
        }
    }
}
