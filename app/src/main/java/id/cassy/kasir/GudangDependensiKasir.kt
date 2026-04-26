package id.cassy.kasir

import android.content.Context
import androidx.room.Room
import id.cassy.kasir.data.jaringan.konfigurasi.KonfigurasiJaringanKasir
import id.cassy.kasir.data.jaringan.konfigurasi.PenyediaJaringanKasir
import id.cassy.kasir.data.jaringan.layanan.LayananProdukJaringan
import id.cassy.kasir.data.lokal.basisdata.BasisDataCassyKasir
import id.cassy.kasir.data.lokal.basisdata.MigrasiBasisDataKasir
import id.cassy.kasir.data.lokal.repositori.RepositoriTransaksi
import id.cassy.kasir.ranah.kasuspenggunaan.AmatiRiwayatTransaksi
import id.cassy.kasir.ranah.kasuspenggunaan.AmatiTransaksiBerdasarkanId
import id.cassy.kasir.ranah.kasuspenggunaan.SelesaikanCheckoutLokalKasir

/**
 * Kontainer dependensi manual (Service Locator) untuk aplikasi Cassy Kasir.
 * Mengelola siklus hidup singleton basis data, repositori, kasus penggunaan, dan layanan jaringan.
 *
 * @param konteks Konteks aplikasi untuk inisialisasi Room dan sumber daya lainnya.
 */
class GudangDependensiKasir(
    konteks: Context,
) {

    /**
     * Instansi tunggal (singleton) untuk basis data Room.
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
     * Repositori transaksi sebagai sumber data tunggal untuk transaksi lokal.
     */
    private val repositoriTransaksi: RepositoriTransaksi by lazy {
        RepositoriTransaksi(basisData)
    }

    /**
     * Layanan API produk untuk komunikasi jaringan.
     *
     * Layanan ini disiapkan sebagai fondasi awal untuk transisi ke local-first.
     * Belum dikonsumsi oleh ViewModel untuk menjaga stabilitas migrasi bertahap.
     */
    val layananProdukJaringan: LayananProdukJaringan by lazy {
        PenyediaJaringanKasir.buatLayananProdukJaringan(
            alamatDasarApi = KonfigurasiJaringanKasir.alamatDasarApi,
            modeDebug = BuildConfig.DEBUG,
        )
    }

    /**
     * Kasus penggunaan untuk menyelesaikan proses checkout di tingkat lokal.
     */
    val selesaikanCheckoutLokalKasir: SelesaikanCheckoutLokalKasir by lazy {
        SelesaikanCheckoutLokalKasir(repositoriTransaksi)
    }

    /**
     * Kasus penggunaan untuk mengamati aliran data riwayat transaksi.
     */
    val amatiRiwayatTransaksi: AmatiRiwayatTransaksi by lazy {
        AmatiRiwayatTransaksi(repositoriTransaksi)
    }

    /**
     * Kasus penggunaan untuk mengamati detail transaksi tunggal berdasarkan ID.
     */
    val amatiTransaksiBerdasarkanId: AmatiTransaksiBerdasarkanId by lazy {
        AmatiTransaksiBerdasarkanId(repositoriTransaksi)
    }
}
