package id.cassy.kasir

import android.content.Context
import androidx.room.Room
import id.cassy.kasir.data.lokal.basisdata.BasisDataCassyKasir
import id.cassy.kasir.data.lokal.repositori.RepositoriTransaksi

/**
 * Kontainer dependensi manual (Service Locator) untuk aplikasi Cassy Kasir.
 * Mengelola siklus hidup singleton database dan repositori.
 */
class GudangDependensiKasir(konteks: Context) {

    /**
     * Singleton instance untuk database Room.
     */
    private val basisData: BasisDataCassyKasir by lazy {
        Room.databaseBuilder(
            konteks.applicationContext,
            BasisDataCassyKasir::class.java,
            "kasir.db"
        ).build()
    }

    /**
     * Singleton instance untuk repositori transaksi.
     */
    val repositoriTransaksi: RepositoriTransaksi by lazy {
        RepositoriTransaksi(basisData)
    }
}
