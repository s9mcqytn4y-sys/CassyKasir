package id.cassy.kasir

import android.content.Context
import androidx.room.Room
import id.cassy.kasir.data.lokal.basisdata.BasisDataCassyKasir
import id.cassy.kasir.data.lokal.basisdata.MigrasiBasisDataKasir
import id.cassy.kasir.data.lokal.repositori.RepositoriTransaksi
import id.cassy.kasir.ranah.kasuspenggunaan.AmatiRiwayatTransaksi
import id.cassy.kasir.ranah.kasuspenggunaan.AmatiTransaksiBerdasarkanId
import id.cassy.kasir.ranah.kasuspenggunaan.SelesaikanCheckoutLokalKasir

/**
 * Kontainer dependensi manual (Service Locator) untuk aplikasi Cassy Kasir.
 * Mengelola siklus hidup singleton database, repository, dan use case.
 */
class GudangDependensiKasir(
    konteks: Context,
) {

    /**
     * Singleton instance untuk database Room.
     */
    private val basisData: BasisDataCassyKasir by lazy {
        Room.databaseBuilder(
            konteks.applicationContext,
            BasisDataCassyKasir::class.java,
            "kasir.db",
        )
            .addMigrations(
                MigrasiBasisDataKasir.DARI_1_KE_2,
            )
            .build()
    }

    /**
     * Repository transaksi sebagai sumber data transaksi lokal.
     */
    private val repositoriTransaksi: RepositoriTransaksi by lazy {
        RepositoriTransaksi(basisData)
    }

    /**
     * Use case untuk menyelesaikan checkout lokal.
     */
    val selesaikanCheckoutLokalKasir: SelesaikanCheckoutLokalKasir by lazy {
        SelesaikanCheckoutLokalKasir(repositoriTransaksi)
    }

    /**
     * Use case untuk mengamati daftar riwayat transaksi.
     */
    val amatiRiwayatTransaksi: AmatiRiwayatTransaksi by lazy {
        AmatiRiwayatTransaksi(repositoriTransaksi)
    }

    /**
     * Use case untuk mengamati detail transaksi berdasarkan ID.
     */
    val amatiTransaksiBerdasarkanId: AmatiTransaksiBerdasarkanId by lazy {
        AmatiTransaksiBerdasarkanId(repositoriTransaksi)
    }
}
