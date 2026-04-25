package id.cassy.kasir.antarmuka.detail

import androidx.compose.runtime.Immutable

/**
 * Representasi status UI untuk layar detail produk.
 *
 * Model ini menjadi satu sumber kebenaran bagi layar detail produk.
 */
@Immutable
data class ModelTampilanDetailProduk(
    val sedangMemuat: Boolean = true,
    val pesanKesalahan: String? = null,
    val produkId: String = "",
    val judulLayar: String = "Detail Produk",
    val namaProduk: String = "",
    val hargaProduk: String = "Rp0",
    val stokTersedia: Int = 0,
    val deskripsiProduk: String = "",
    val apakahProdukDitemukan: Boolean = false,
    val judulStatusKosong: String = "Produk tidak ditemukan",
    val deskripsiStatusKosong: String = "Produk yang dipilih tidak tersedia.",
)
