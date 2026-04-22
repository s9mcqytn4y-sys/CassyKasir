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
 * Mengimplementasikan pola Unidirectional Data Flow (UDF) dengan mengekspos
 * [StateFlow] tunggal yang berisi seluruh status antarmuka.
 */
class LayarUtamaKasirViewModel : ViewModel() {

    private val daftarProdukPenuh: List<Produk> = KatalogProdukContoh.daftarAwal()

    private val _modelTampilan = MutableStateFlow(
        ModelTampilanLayarUtamaKasir(
            statusBeranda = StatusBerandaKasir(
                namaAplikasi = "Cassy Kasir",
                sloganAplikasi = "Solusi Digital UMKM Modern",
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
     * Titik masuk tunggal untuk memproses semua interaksi pengguna dari UI.
     *
     * @param aksi Objek [AksiLayarUtamaKasir] yang mewakili niat pengguna.
     */
    fun tanganiAksi(aksi: AksiLayarUtamaKasir) {
        when (aksi) {
            is AksiLayarUtamaKasir.UbahKataKunciPencarian -> perbaruiPencarian(aksi.kataKunciBaru)
            AksiLayarUtamaKasir.UbahVisibilitasRingkasanPembayaran -> toggleVisibilitasPembayaran()
        }
    }

    private fun perbaruiPencarian(kataKunci: String) {
        val pencarianBersih = kataKunci.trim()

        // Optimasi: Hindari proses filter jika kata kunci sama dengan state saat ini
        if (_modelTampilan.value.kataKunciPencarian == kataKunci) return

        val hasilFilter = if (pencarianBersih.isBlank()) {
            daftarProdukPenuh
        } else {
            daftarProdukPenuh.filter { it.nama.contains(pencarianBersih, ignoreCase = true) }
        }

        _modelTampilan.update { it.copy(
            kataKunciPencarian = kataKunci,
            daftarProdukTersaring = hasilFilter
        )}
    }

    private fun toggleVisibilitasPembayaran() {
        _modelTampilan.update { it.copy(
            apakahRingkasanPembayaranTampil = !it.apakahRingkasanPembayaranTampil
        )}
    }
}
