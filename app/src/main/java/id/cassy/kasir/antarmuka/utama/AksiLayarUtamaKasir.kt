package id.cassy.kasir.antarmuka.utama

import androidx.compose.runtime.Immutable

/**
 * Representasi aksi dari antarmuka ke pengelola status layar utama kasir.
 *
 * Seluruh interaksi pengguna dikirim sebagai aksi agar alur data tetap satu arah.
 */
@Immutable
sealed interface AksiLayarUtamaKasir {

    /**
     * Aksi saat pengguna mengubah isi kolom pencarian produk.
     */
    data class UbahKataKunciPencarian(
        val kataKunciBaru: String,
    ) : AksiLayarUtamaKasir

    /**
     * Aksi saat pengguna ingin menampilkan atau menyembunyikan
     * panel pembayaran.
     */
    data object UbahVisibilitasRingkasanPembayaran : AksiLayarUtamaKasir

    /**
     * Aksi saat pengguna mengetuk satu produk pada katalog
     * untuk menambahkannya ke keranjang.
     */
    data class TambahProdukKeKeranjang(
        val produkId: String,
    ) : AksiLayarUtamaKasir
}
