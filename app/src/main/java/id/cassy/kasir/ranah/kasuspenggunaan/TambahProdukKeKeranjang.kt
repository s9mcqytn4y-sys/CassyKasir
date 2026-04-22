package id.cassy.kasir.ranah.kasuspenggunaan

import id.cassy.kasir.ranah.model.ItemKeranjang
import id.cassy.kasir.ranah.model.Produk

/**
 * Menambahkan satu produk ke keranjang.
 *
 * Jika produk sudah ada di keranjang, jumlahnya ditambah satu.
 * Jika belum ada, item baru dibuat.
 * Jika stok sudah habis, data keranjang dikembalikan apa adanya.
 */
class TambahProdukKeKeranjang {

    operator fun invoke(
        daftarItemKeranjang: List<ItemKeranjang>,
        produk: Produk,
    ): List<ItemKeranjang> {
        if (!produk.aktif || produk.stokTersedia <= 0) {
            return daftarItemKeranjang
        }

        val daftarBaru = daftarItemKeranjang.toMutableList()

        val indeksItemLama = daftarBaru.indexOfFirst { itemKeranjang ->
            itemKeranjang.produk.id == produk.id
        }

        if (indeksItemLama >= 0) {
            val itemLama = daftarBaru[indeksItemLama]

            if (itemLama.jumlah >= produk.stokTersedia) {
                return daftarItemKeranjang
            }

            daftarBaru[indeksItemLama] = itemLama.copy(
                jumlah = itemLama.jumlah + 1,
            )
        } else {
            daftarBaru.add(
                ItemKeranjang(
                    produk = produk,
                    jumlah = 1,
                ),
            )
        }

        return daftarBaru
    }
}
