package id.cassy.kasir.data.lokal.basisdata

import androidx.room.Database
import androidx.room.RoomDatabase
import id.cassy.kasir.data.lokal.dao.AksesDataProdukLokal
import id.cassy.kasir.data.lokal.dao.AksesDataTransaksiLokal
import id.cassy.kasir.data.lokal.entitas.EntitasItemTransaksiLokal
import id.cassy.kasir.data.lokal.entitas.EntitasProdukLokal
import id.cassy.kasir.data.lokal.entitas.EntitasTransaksiLokal

/**
 * Titik masuk utama database Room aplikasi Cassy Kasir.
 */
@Database(
    entities = [
        EntitasTransaksiLokal::class,
        EntitasItemTransaksiLokal::class,
        EntitasProdukLokal::class,
    ],
    version = 3,
    exportSchema = true,
)
abstract class BasisDataCassyKasir : RoomDatabase() {
    /**
     * Menyediakan akses ke operasi data transaksi.
     */
    abstract fun aksesDataTransaksiLokal(): AksesDataTransaksiLokal

    /**
     * Menyediakan akses ke operasi data produk.
     */
    abstract fun aksesDataProdukLokal(): AksesDataProdukLokal
}
