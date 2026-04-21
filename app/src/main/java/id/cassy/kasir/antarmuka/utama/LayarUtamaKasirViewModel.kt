package id.cassy.kasir.antarmuka.utama

import androidx.lifecycle.ViewModel
import id.cassy.kasir.ranah.contoh.KatalogProdukContoh
import id.cassy.kasir.ranah.model.Produk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel untuk mengelola status dan logika bisnis pada Layar Utama Kasir.
 *
 * ViewModel ini mengimplementasikan pola Unidirectional Data Flow (UDF)
 * dengan mengekspos satu StateFlow tunggal yang berisi seluruh status UI.
 */
class LayarUtamaKasirViewModel : ViewModel() {

    private val daftarProdukPenuh: List<Produk> = KatalogProdukContoh.daftarAwal()

    private val _modelTampilan = MutableStateFlow(
        ModelTampilanLayarUtamaKasir(
            statusBeranda = StatusBerandaKasir(
                namaAplikasi = "Cassy Kasir",
                sloganAplikasi = "Kasir Cepat untuk Usaha Hebat",
                jumlahProdukTersedia = daftarProdukPenuh.size,
                jumlahItemKeranjang = 0,
                totalBelanjaSementara = "Rp0",
                statusSinkronisasi = "Tersimpan Lokal",
            ),
            daftarProdukTersaring = daftarProdukPenuh,
        ),
    )

    /**
     * Aliran status UI yang bersifat read-only untuk dikonsumsi oleh lapisan antarmuka.
     */
    val modelTampilan: StateFlow<ModelTampilanLayarUtamaKasir> = _modelTampilan.asStateFlow()

    /**
     * Memperbarui kata kunci pencarian dan menyaring daftar produk.
     *
     * @param kataKunciBaru String yang dimasukkan pengguna pada kolom pencarian.
     */
    fun ubahKataKunciPencarian(kataKunciBaru: String) {
        val kataKunciBersih = kataKunciBaru.trim()

        val daftarProdukTersaringBaru = if (kataKunciBersih.isBlank()) {
            daftarProdukPenuh
        } else {
            daftarProdukPenuh.filter { produk ->
                produk.nama.contains(kataKunciBersih, ignoreCase = true)
            }
        }

        _modelTampilan.update { modelSaatIni ->
            modelSaatIni.copy(
                kataKunciPencarian = kataKunciBaru,
                daftarProdukTersaring = daftarProdukTersaringBaru,
            )
        }
    }

    /**
     * Mengatur visibilitas panel ringkasan pembayaran.
     */
    fun ubahVisibilitasRingkasanPembayaran() {
        _modelTampilan.update { modelSaatIni ->
            modelSaatIni.copy(
                apakahRingkasanPembayaranTampil = !modelSaatIni.apakahRingkasanPembayaranTampil,
            )
        }
    }
}
