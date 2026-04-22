package id.cassy.kasir.ranah.kasuspenggunaan

import id.cassy.kasir.ranah.model.ItemKeranjang

/**
 * Menghapus satu item sepenuhnya dari keranjang berdasarkan id produk.
 */
class HapusProdukDariKeranjang {

    operator fun invoke(
        daftarItemKeranjang: List<ItemKeranjang>,
        produkId: String,
    ): List<ItemKeranjang> {
        return daftarItemKeranjang.filterNot { itemKeranjang ->
            itemKeranjang.produk.id == produkId
        }
    }
}
