package id.cassy.kasir.antarmuka.detail

import androidx.compose.runtime.Immutable

/**
 * Representasi status pemuatan layar detail produk.
 *
 * Setiap status bersifat saling eksklusif agar UI tidak perlu
 * menebak kondisi layar dari kombinasi banyak boolean.
 */
@Immutable
sealed interface StatusMuatDetailProduk {

    /**
     * Status saat detail produk sedang dimuat.
     */
    @Immutable
    data object Memuat : StatusMuatDetailProduk

    /**
     * Status saat detail produk berhasil dimuat.
     */
    @Immutable
    data class Berhasil(
        val namaProduk: String,
        val hargaProduk: String,
        val stokTersedia: Int,
        val deskripsiProduk: String,
        val labelAksiTambah: String,
        val aksiTambahAktif: Boolean,
    ) : StatusMuatDetailProduk

    /**
     * Status saat data produk tidak ditemukan.
     */
    @Immutable
    data class Kosong(
        val judul: String,
        val deskripsi: String,
    ) : StatusMuatDetailProduk

    /**
     * Status saat terjadi kegagalan saat memuat data.
     */
    @Immutable
    data class Gagal(
        val judul: String,
        val deskripsi: String,
    ) : StatusMuatDetailProduk
}
