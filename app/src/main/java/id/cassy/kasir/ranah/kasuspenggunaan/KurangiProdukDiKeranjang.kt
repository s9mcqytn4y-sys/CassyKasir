package id.cassy.kasir.ranah.kasuspenggunaan

import id.cassy.kasir.ranah.model.ItemKeranjang

/**
 * Mengurangi jumlah item di keranjang satu per satu.
 *
 * Jika jumlah item masih lebih dari satu, jumlah akan dikurangi.
 * Jika jumlah sudah satu, data dikembalikan apa adanya.
 * Penghapusan total tetap ditangani oleh use case khusus hapus item.
 */
class KurangiProdukDiKeranjang {

    operator fun invoke(
        daftarItemKeranjang: List<ItemKeranjang>,
        produkId: String,
    ): List<ItemKeranjang> {
        val daftarBaru = daftarItemKeranjang.toMutableList()

        val indeksItem = daftarBaru.indexOfFirst { itemKeranjang ->
            itemKeranjang.produk.id == produkId
        }

        if (indeksItem < 0) {
            return daftarItemKeranjang
        }

        val itemLama = daftarBaru[indeksItem]

        if (itemLama.jumlah <= 1) {
            return daftarItemKeranjang
        }

        daftarBaru[indeksItem] = itemLama.copy(
            jumlah = itemLama.jumlah - 1,
        )

        return daftarBaru
    }
}
