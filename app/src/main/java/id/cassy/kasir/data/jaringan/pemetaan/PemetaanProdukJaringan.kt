package id.cassy.kasir.data.jaringan.pemetaan

import id.cassy.kasir.data.jaringan.model.ResponsProdukJaringan
import id.cassy.kasir.ranah.model.Produk

/**
 * Mengubah model respons jaringan menjadi model domain.
 */
fun ResponsProdukJaringan.keDomain(): Produk {
    return Produk(
        id = id,
        nama = nama,
        harga = harga,
        stokTersedia = stokTersedia,
        kodePindai = kodePindai,
        deskripsi = deskripsi.orEmpty(),
        aktif = aktif,
    )
}

fun List<ResponsProdukJaringan>.keDomain(): List<Produk> {
    return map { responsProduk ->
        responsProduk.keDomain()
    }
}
