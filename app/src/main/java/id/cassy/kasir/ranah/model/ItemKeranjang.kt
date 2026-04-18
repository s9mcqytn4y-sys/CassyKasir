package id.cassy.kasir.ranah.model

data class ItemKeranjang(
    val produk: Produk,
    val jumlah: Int,
    val catatan: String? = null
)