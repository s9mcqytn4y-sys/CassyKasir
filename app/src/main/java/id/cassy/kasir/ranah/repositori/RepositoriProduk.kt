package id.cassy.kasir.ranah.repositori

import id.cassy.kasir.ranah.model.HasilOperasiJaringan
import id.cassy.kasir.ranah.model.Produk
import kotlinx.coroutines.flow.Flow

/**
 * Kontrak repositori untuk mengelola data produk.
 * Mendefinisikan operasi baca/tulis yang diperlukan oleh use case.
 */
interface RepositoriProduk {

    /**
     * Mengamati seluruh produk dari penyimpanan lokal secara reaktif.
     */
    fun amatiSemuaProduk(): Flow<List<Produk>>

    /**
     * Mencari produk berdasarkan kata kunci di lokal.
     */
    fun cariProdukLokal(kataKunci: String): Flow<List<Produk>>

    /**
     * Memperbarui katalog lokal dengan mengambil data terbaru dari jaringan.
     * @return Hasil operasi jaringan yang berisi status keberhasilan atau kegagalan.
     */
    suspend fun sinkronkanKatalog(): HasilOperasiJaringan<Unit>
}
