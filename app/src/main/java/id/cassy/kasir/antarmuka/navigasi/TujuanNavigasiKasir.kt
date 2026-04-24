package id.cassy.kasir.antarmuka.navigasi

import androidx.navigation.NavHostController

/**
 * Representasi tujuan navigasi aplikasi CassyKasir.
 *
 * Semua route disimpan terpusat di sini agar tidak ada string liar
 * yang tersebar di banyak file.
 */
sealed interface TujuanNavigasiKasir {

    /**
     * Route mentah yang dipakai oleh Navigation Compose.
     */
    val rute: String

    /**
     * Tujuan layar utama kasir.
     */
    data object KasirUtama : TujuanNavigasiKasir {
        override val rute: String = "kasir_utama"
    }

    /**
     * Tujuan layar riwayat transaksi.
     */
    data object RiwayatTransaksi : TujuanNavigasiKasir {
        override val rute: String = "riwayat_transaksi"
    }
}

/**
 * Membuka layar utama kasir.
 *
 * Helper ini mencegah penulisan string route mentah berulang-ulang.
 */
fun NavHostController.bukaKasirUtama() {
    navigate(TujuanNavigasiKasir.KasirUtama.rute) {
        launchSingleTop = true
    }
}

/**
 * Membuka layar riwayat transaksi.
 *
 * `launchSingleTop` dipakai agar tombol yang diketuk berulang
 * tidak menumpuk layar yang sama berkali-kali di back stack.
 */
fun NavHostController.bukaRiwayatTransaksi() {
    navigate(TujuanNavigasiKasir.RiwayatTransaksi.rute) {
        launchSingleTop = true
    }
}
