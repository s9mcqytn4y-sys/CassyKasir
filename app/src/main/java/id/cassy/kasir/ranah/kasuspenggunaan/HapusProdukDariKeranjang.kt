package id.cassy.kasir.ranah.kasuspenggunaan

import id.cassy.kasir.ranah.model.ItemKeranjang

/**
 * Menghapus satu item sepenuhnya dari keranjang berdasarkan id produk.
 */
class HapusProdukDariKeranjang {

    /**
     * Menjalankan penghapusan item dari daftar keranjang.
     *
     * @param daftarItemKeranjang Daftar item saat ini.
     * @param produkId ID produk yang akan dihapus.
     * @return Daftar item baru setelah produk dihapus.
     */
    operator fun invoke(
        daftarItemKeranjang: List<ItemKeranjang>,
        produkId: String,
    ): List<ItemKeranjang> {
        return daftarItemKeranjang.filterNot { itemKeranjang ->
            itemKeranjang.produk.id == produkId
        }
    }
}
