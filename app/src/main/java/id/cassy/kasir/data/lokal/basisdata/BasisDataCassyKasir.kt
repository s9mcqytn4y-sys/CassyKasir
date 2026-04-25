package id.cassy.kasir.data.lokal.basisdata

import androidx.room.Database
import androidx.room.RoomDatabase
import id.cassy.kasir.data.lokal.dao.AksesDataTransaksiLokal
import id.cassy.kasir.data.lokal.entitas.EntitasItemTransaksiLokal
import id.cassy.kasir.data.lokal.entitas.EntitasTransaksiLokal

/**
 * Titik masuk utama database Room aplikasi Cassy Kasir.
 */
@Database(
    entities = [
        EntitasTransaksiLokal::class,
        EntitasItemTransaksiLokal::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class BasisDataCassyKasir : RoomDatabase() {
    /**
     * Menyediakan akses ke operasi data transaksi.
     */
    abstract fun aksesDataTransaksiLokal(): AksesDataTransaksiLokal
}
