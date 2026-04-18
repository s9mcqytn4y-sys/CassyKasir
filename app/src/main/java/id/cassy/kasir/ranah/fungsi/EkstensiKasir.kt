package id.cassy.kasir.ranah.fungsi

import id.cassy.kasir.ranah.model.ItemKeranjang
import id.cassy.kasir.ranah.model.Produk

fun ItemKeranjang.hitungSubTotal(): Long {
    return produk.harga * jumlah
}

fun List<ItemKeranjang>.hitungJumlahItem(): Int {
    return sumOf { it.jumlah }
}

fun List<ItemKeranjang>.hitungSubtotalKeranjang(): Long {
    return sumOf {
        it.hitungSubTotal()
    }
}

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

fun String?.ambilAtauStrip(): String {
    return if (isNullOrBlank()) "-" else this
}
