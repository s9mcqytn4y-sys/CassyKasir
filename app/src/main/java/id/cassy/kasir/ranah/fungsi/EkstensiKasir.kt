package id.cassy.kasir.ranah.fungsi

import id.cassy.kasir.ranah.model.ItemKeranjang
import id.cassy.kasir.ranah.model.Produk

/**
 * Fungsi-fungsi pembantu (*extension functions*) untuk operasi pada model kasir.
 */

/**
 * Menghitung nilai subtotal untuk satu baris item keranjang.
 *
 * @return Hasil perkalian harga produk dengan jumlahnya.
 */
fun ItemKeranjang.hitungSubTotal(): Long {
    return produk.harga * jumlah
}

/**
 * Menghitung total seluruh kuantitas item dalam keranjang.
 *
 * @return Jumlah seluruh unit yang dibeli.
 */
fun List<ItemKeranjang>.hitungJumlahItem(): Int {
    return sumOf { it.jumlah }
}

/**
 * Menghitung total nilai uang seluruh item dalam keranjang (sebelum potongan/pajak).
 *
 * @return Total harga belanjaan.
 */
fun List<ItemKeranjang>.hitungSubtotalKeranjang(): Long {
    return sumOf {
        it.hitungSubTotal()
    }
}

/**
 * Menyaring daftar produk berdasarkan kriteria teks.
 *
 * @param kataKunci Teks pencarian yang dicocokkan dengan nama atau barcode.
 * @return List [Produk] yang sesuai kriteria.
 */
fun List<Produk>.cariProduk(kataKunci: String): List<Produk> {
    val kataKunciBersih = kataKunci.trim()

    if (kataKunciBersih.isBlank()) {
        return this
    }

    return filter {
        it.nama.contains(kataKunciBersih, ignoreCase = true) ||
                it.kodePindai?.contains(kataKunciBersih, ignoreCase = true) == true
    }
}

/**
 * Memastikan nilai String tidak kosong atau null untuk tampilan UI.
 *
 * @return Teks asli jika tersedia, atau "-" jika kosong/null.
 */
fun String?.ambilAtauStrip(): String {
    return if (isNullOrBlank()) "-" else this
}
