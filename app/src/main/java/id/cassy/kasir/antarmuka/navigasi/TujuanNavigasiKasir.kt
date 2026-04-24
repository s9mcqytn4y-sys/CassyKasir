package id.cassy.kasir.antarmuka.navigasi

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

/**
 * Representasi kontrak tujuan navigasi dalam aplikasi CassyKasir.
 *
 * Menggunakan pendekatan berbasis rute (route-based) untuk mendefinisikan
 * hierarki navigasi dan argumen yang diperlukan antar layar.
 */
sealed interface TujuanNavigasiKasir {

    /**
     * Route mentah yang dipakai Navigation Compose.
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

    /**
     * Tujuan layar detail produk yang memerlukan identitas unik produk.
     */
    data object DetailProduk : TujuanNavigasiKasir {
        /** Nama kunci argumen untuk ID produk. */
        const val namaArgumenProdukId: String = "produkId"

        override val rute: String = "detail_produk/{$namaArgumenProdukId}"

        /**
         * Membentuk string rute yang valid dengan menyisipkan [produkId].
         */
        fun buatRute(produkId: String): String {
            return "detail_produk/$produkId"
        }
    }
}

/**
 * Membuka layar utama kasir.
 */
fun NavHostController.bukaKasirUtama() {
    navigate(TujuanNavigasiKasir.KasirUtama.rute) {
        launchSingleTop = true
    }
}

/**
 * Membuka layar riwayat transaksi.
 */
fun NavHostController.bukaRiwayatTransaksi() {
    navigate(TujuanNavigasiKasir.RiwayatTransaksi.rute) {
        launchSingleTop = true
    }
}

/**
 * Membuka layar detail produk berdasarkan id produk.
 */
fun NavHostController.bukaDetailProduk(produkId: String) {
    navigate(TujuanNavigasiKasir.DetailProduk.buatRute(produkId)) {
        launchSingleTop = true
    }
}

/**
 * Mengekstrak argumen [produkId] dari [NavBackStackEntry] secara aman.
 *
 * @throws IllegalArgumentException Jika argumen tidak ditemukan dalam entri navigasi.
 */
fun NavBackStackEntry.ambilProdukIdDetailProduk(): String {
    return requireNotNull(
        arguments?.getString(TujuanNavigasiKasir.DetailProduk.namaArgumenProdukId),
    ) {
        "Argumen produkId wajib tersedia untuk layar detail produk."
    }
}
