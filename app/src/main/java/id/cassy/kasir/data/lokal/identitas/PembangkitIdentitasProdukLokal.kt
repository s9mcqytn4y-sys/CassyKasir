package id.cassy.kasir.data.lokal.identitas

import java.util.Locale
import java.util.UUID

/**
 * Pembangkit identitas produk lokal.
 *
 * Format identitas dibuat cukup mudah dibaca manusia, tetapi tetap diberi
 * potongan acak agar aman dari bentrok saat produk dengan nama serupa dibuat.
 *
 * Contoh:
 * produk-kopi-susu-gula-aren-a7k2m9
 */
object PembangkitIdentitasProdukLokal {

    private const val awalanIdentitasProduk = "produk"
    private const val panjangKodeUnik = 6

    /**
     * Membuat identitas produk baru dari nama produk.
     *
     * @param namaProduk Nama produk yang diinput kasir atau pemilik toko.
     * @return Identitas produk yang stabil untuk disimpan sebagai primary key.
     */
    fun buatIdentitasBaru(
        namaProduk: String,
    ): String {
        val slugNamaProduk = namaProduk
            .trim()
            .lowercase(Locale.ROOT)
            .gantiKarakterSelainHurufDanAngkaMenjadiStrip()
            .rapikanStripBerulang()
            .ifBlank {
                "tanpa-nama"
            }

        val kodeUnik = UUID.randomUUID()
            .toString()
            .filter { karakter -> karakter.isLetterOrDigit() }
            .take(panjangKodeUnik)
            .lowercase(Locale.ROOT)

        return "$awalanIdentitasProduk-$slugNamaProduk-$kodeUnik"
    }

    private fun String.gantiKarakterSelainHurufDanAngkaMenjadiStrip(): String {
        return replace(
            regex = Regex("[^a-z0-9]+"),
            replacement = "-",
        )
    }

    private fun String.rapikanStripBerulang(): String {
        return trim('-')
            .replace(
                regex = Regex("-+"),
                replacement = "-",
            )
    }
}
