package id.cassy.kasir.data.lokal.identitas

import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pengujian unit untuk pembangkit identitas produk lokal.
 */
class PengujianPembangkitIdentitasProdukLokal {

    @Test
    fun identitasProdukMemakaiAwalanProdukDanSlugNama() {
        val identitasProduk = PembangkitIdentitasProdukLokal.buatIdentitasBaru(
            namaProduk = "Kopi Susu Gula Aren",
        )

        assertTrue(
            identitasProduk.startsWith("produk-kopi-susu-gula-aren-"),
        )
    }

    @Test
    fun identitasProdukMembersihkanKarakterTidakAman() {
        val identitasProduk = PembangkitIdentitasProdukLokal.buatIdentitasBaru(
            namaProduk = "  Es Teh!!! 330ml  ",
        )

        assertTrue(
            identitasProduk.startsWith("produk-es-teh-330ml-"),
        )
    }

    @Test
    fun identitasProdukKosongMemakaiNamaFallback() {
        val identitasProduk = PembangkitIdentitasProdukLokal.buatIdentitasBaru(
            namaProduk = "   ",
        )

        assertTrue(
            identitasProduk.startsWith("produk-tanpa-nama-"),
        )
    }

    @Test
    fun namaProdukSamaTetapMenghasilkanIdentitasBerbeda() {
        val identitasPertama = PembangkitIdentitasProdukLokal.buatIdentitasBaru(
            namaProduk = "Kopi Susu",
        )

        val identitasKedua = PembangkitIdentitasProdukLokal.buatIdentitasBaru(
            namaProduk = "Kopi Susu",
        )

        assertNotEquals(identitasPertama, identitasKedua)
    }
}
