package id.cassy.kasir.ranah.fungsi

import id.cassy.kasir.ranah.model.ItemKeranjang
import id.cassy.kasir.ranah.model.Produk
import java.text.NumberFormat
import java.util.Locale

/**
 * Menghitung nilai subtotal untuk satu baris item keranjang.
 */
fun ItemKeranjang.hitungSubTotal(): Long {
    return produk.harga * jumlah
}

/**
 * Menghitung total seluruh jumlah item dalam keranjang.
 */
fun List<ItemKeranjang>.hitungJumlahItem(): Int {
    return sumOf { it.jumlah }
}

/**
 * Menghitung total nilai uang seluruh item dalam keranjang sebelum potongan,
 * pajak, atau biaya layanan.
 */
fun List<ItemKeranjang>.hitungSubtotalKeranjang(): Long {
    return sumOf { itemKeranjang ->
        itemKeranjang.hitungSubTotal()
    }
}

/**
 * Menyaring daftar produk berdasarkan nama produk atau kode pindai.
 */
fun List<Produk>.cariProduk(kataKunci: String): List<Produk> {
    val kataKunciBersih = kataKunci.trim()

    if (kataKunciBersih.isBlank()) {
        return this
    }

    return filter { produk ->
        produk.nama.contains(kataKunciBersih, ignoreCase = true) ||
            produk.kodePindai?.contains(kataKunciBersih, ignoreCase = true) == true
    }
}

/**
 * Memastikan nilai teks tidak kosong untuk tampilan.
 */
fun String?.ambilAtauStrip(): String {
    return if (isNullOrBlank()) "-" else this
}

/**
 * Mengubah nilai uang berbasis Long menjadi format Rupiah.
 *
 * Nilai uang tetap disimpan sebagai Long agar aman untuk transaksi kasir.
 */
fun Long.sebagaiRupiah(): String {
    val pembuatFormatRupiah = NumberFormat.getCurrencyInstance(
        Locale.forLanguageTag("id-ID"),
    )

    pembuatFormatRupiah.maximumFractionDigits = 0

    return pembuatFormatRupiah.format(this)
}
