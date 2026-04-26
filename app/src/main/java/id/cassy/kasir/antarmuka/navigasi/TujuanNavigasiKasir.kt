package id.cassy.kasir.antarmuka.navigasi

import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

/**
 * Kontrak tujuan navigasi type-safe untuk Cassy Kasir.
 *
 * Setiap tujuan layar direpresentasikan sebagai tipe Kotlin, bukan string route.
 * Ini membuat argumen navigasi lebih aman saat compile-time dan lebih mudah
 * dirawat ketika jumlah layar bertambah.
 */
sealed interface TujuanNavigasiKasir {

    /**
     * Tujuan layar utama kasir.
     */
    @Serializable
    data object KasirUtama : TujuanNavigasiKasir

    /**
     * Tujuan layar riwayat transaksi.
     */
    @Serializable
    data object RiwayatTransaksi : TujuanNavigasiKasir

    /**
     * Tujuan layar detail produk.
     *
     * @property identitasProduk Identitas unik produk yang akan dibuka.
     */
    @Serializable
    data class DetailProduk(
        val identitasProduk: String,
    ) : TujuanNavigasiKasir

    /**
     * Tujuan layar detail transaksi.
     *
     * @property identitasTransaksi Identitas unik transaksi yang akan dibuka.
     */
    @Serializable
    data class DetailTransaksi(
        val identitasTransaksi: String,
    ) : TujuanNavigasiKasir
}

/**
 * Membuka layar utama kasir.
 */
fun NavHostController.bukaKasirUtama() {
    navigate(TujuanNavigasiKasir.KasirUtama) {
        launchSingleTop = true
    }
}

/**
 * Membuka layar riwayat transaksi.
 */
fun NavHostController.bukaRiwayatTransaksi() {
    navigate(TujuanNavigasiKasir.RiwayatTransaksi) {
        launchSingleTop = true
    }
}

/**
 * Membuka layar detail produk berdasarkan identitas produk.
 */
fun NavHostController.bukaDetailProduk(
    identitasProduk: String,
) {
    navigate(
        TujuanNavigasiKasir.DetailProduk(
            identitasProduk = identitasProduk,
        ),
    ) {
        launchSingleTop = true
    }
}

/**
 * Membuka layar detail transaksi berdasarkan identitas transaksi.
 */
fun NavHostController.bukaDetailTransaksi(
    identitasTransaksi: String,
) {
    navigate(
        TujuanNavigasiKasir.DetailTransaksi(
            identitasTransaksi = identitasTransaksi,
        ),
    ) {
        launchSingleTop = true
    }
}
