package id.cassy.kasir.ranah.model

data class Produk(
    val id: String,
    val nama: String,
    val harga: Long,
    val stokTersedia: Int,
    val kodePindai: String? = null,
    val aktif: Boolean = true

)