package id.cassy.kasir.data.lokal.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.cassy.kasir.data.lokal.entitas.EntitasProdukLokal
import kotlinx.coroutines.flow.Flow

/**
 * Kontrak akses data untuk entitas produk di database lokal.
 */
@Dao
interface AksesDataProdukLokal {

    /**
     * Mengambil seluruh produk yang tersimpan di lokal.
     */
    @Query("SELECT * FROM produk")
    fun amatiSemuaProduk(): Flow<List<EntitasProdukLokal>>

    /**
     * Mencari produk berdasarkan kata kunci di nama atau deskripsi.
     */
    @Query("SELECT * FROM produk WHERE nama LIKE '%' || :kataKunci || '%' OR deskripsi LIKE '%' || :kataKunci || '%'")
    fun cariProduk(kataKunci: String): Flow<List<EntitasProdukLokal>>

    /**
     * Menyimpan banyak produk sekaligus (misal: setelah sinkronisasi).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun simpanBanyakProduk(daftarProduk: List<EntitasProdukLokal>)

    /**
     * Menghapus semua produk dari tabel lokal.
     */
    @Query("DELETE FROM produk")
    suspend fun hapusSemuaProduk()
}
