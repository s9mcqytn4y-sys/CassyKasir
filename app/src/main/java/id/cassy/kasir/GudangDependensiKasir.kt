package id.cassy.kasir

import android.content.Context
import androidx.room.Room
import id.cassy.kasir.data.jaringan.konfigurasi.KonfigurasiJaringanKasir
import id.cassy.kasir.data.jaringan.konfigurasi.PenyediaJaringanKasir
import id.cassy.kasir.data.jaringan.layanan.LayananProdukJaringan
import id.cassy.kasir.data.lokal.basisdata.BasisDataCassyKasir
import id.cassy.kasir.data.lokal.basisdata.MigrasiBasisDataKasir
import id.cassy.kasir.data.lokal.repositori.RepositoriTransaksi
import id.cassy.kasir.data.repositori.RepositoriProdukLokalRemote
import id.cassy.kasir.ranah.kasuspenggunaan.AmatiProdukBerdasarkanIdentitas
import id.cassy.kasir.ranah.kasuspenggunaan.AmatiRiwayatTransaksi
import id.cassy.kasir.ranah.kasuspenggunaan.AmatiTransaksiBerdasarkanId
import id.cassy.kasir.ranah.kasuspenggunaan.MuatKatalogProduk
import id.cassy.kasir.ranah.kasuspenggunaan.SelesaikanCheckoutLokalKasir
import id.cassy.kasir.ranah.repositori.RepositoriProduk

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
                MigrasiBasisDataKasir.DARI_2_KE_3,
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
     * Repositori produk yang menggabungkan sumber data lokal dan jaringan.
     */
    private val repositoriProduk: RepositoriProduk by lazy {
        RepositoriProdukLokalRemote(
            basisData = basisData,
            layananJaringan = layananProdukJaringan,
        )
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
     * Kasus penggunaan untuk memuat katalog produk dengan skema local-first.
     */
    val muatKatalogProduk: MuatKatalogProduk by lazy {
        MuatKatalogProduk(repositoriProduk)
    }

    /**
     * Kasus penggunaan untuk mengamati detail produk dari sumber lokal.
     */
    val amatiProdukBerdasarkanIdentitas: AmatiProdukBerdasarkanIdentitas by lazy {
        AmatiProdukBerdasarkanIdentitas(repositoriProduk)
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
