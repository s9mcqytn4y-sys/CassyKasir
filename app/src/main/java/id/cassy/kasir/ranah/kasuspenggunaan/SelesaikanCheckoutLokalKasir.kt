package id.cassy.kasir.ranah.kasuspenggunaan

import id.cassy.kasir.ranah.identitas.PembangkitIdentitasTransaksi
import id.cassy.kasir.ranah.fungsi.hitungJumlahItem
import id.cassy.kasir.ranah.fungsi.hitungTotalTransaksiUang
import id.cassy.kasir.ranah.fungsi.sanitasiDaftarItemKeranjangUntukCheckout
import id.cassy.kasir.ranah.fungsi.validasiCheckout
import id.cassy.kasir.ranah.fungsi.validasiDaftarItemCheckout
import id.cassy.kasir.ranah.model.HasilValidasiCheckout
import id.cassy.kasir.ranah.model.ItemKeranjang
import id.cassy.kasir.ranah.model.StatusSinkronisasi
import id.cassy.kasir.ranah.model.Transaksi
import id.cassy.kasir.ranah.repositori.RepositoriTransaksi

/**
 * Hasil penyelesaian checkout lokal.
 */
data class HasilCheckoutLokalKasir(
    val daftarItemKeranjangBaru: List<ItemKeranjang>,
    val statusSinkronisasiBaru: StatusSinkronisasi,
    val jumlahItemCheckout: Int,
    val totalCheckout: Long,
)

/**
 * Kasus penggunaan untuk menyelesaikan checkout secara lokal.
 *
 * Use case ini menjadi pagar domain sebelum transaksi disimpan ke Room.
 */
class SelesaikanCheckoutLokalKasir(
    private val repositori: RepositoriTransaksi,
) {

    suspend fun eksekusi(
        daftarItemKeranjang: List<ItemKeranjang>,
    ): HasilCheckoutLokalKasir {
        val daftarItemKeranjangBersih = daftarItemKeranjang
            .sanitasiDaftarItemKeranjangUntukCheckout()

        validasiDaftarItemCheckout(
            daftarItemKeranjang = daftarItemKeranjangBersih,
        ).pastikanSah()

        val totalCheckout = hitungTotalTransaksiUang(
            daftarItemKeranjang = daftarItemKeranjangBersih,
        )

        validasiCheckout(
            daftarItemKeranjang = daftarItemKeranjangBersih,
            uangDibayar = totalCheckout,
        ).pastikanSah()

        val jumlahItemCheckout = daftarItemKeranjangBersih.hitungJumlahItem()

        val transaksi = Transaksi(
            id = PembangkitIdentitasTransaksi.buatIdentitasBaru(),
            daftarItemKeranjang = daftarItemKeranjangBersih,
            // Untuk tahap sekarang, checkout tunai dianggap memakai uang pas karena
            // layar belum memiliki input nominal pembayaran.
            uangDibayar = totalCheckout.nilaiRupiah,
            potongan = 0,
            biayaLayanan = 0,
            pajak = 0,
            waktuTransaksiEpochMili = System.currentTimeMillis(),
            catatan = null,
        )

        repositori.simpanTransaksiDanKurangiStok(transaksi)

        return HasilCheckoutLokalKasir(
            daftarItemKeranjangBaru = emptyList(),
            statusSinkronisasiBaru = StatusSinkronisasi.SinkronLokal,
            jumlahItemCheckout = jumlahItemCheckout,
            totalCheckout = totalCheckout.nilaiRupiah,
        )
    }
}

/**
 * Mengubah hasil validasi tidak sah menjadi exception domain yang jelas.
 *
 * ViewModel menangkap [IllegalArgumentException] agar pesan validasi bisa
 * ditampilkan sebagai pesan singkat kepada kasir.
 */
private fun HasilValidasiCheckout.pastikanSah() {
    if (this is HasilValidasiCheckout.TidakSah) {
        throw IllegalArgumentException(pesan)
    }
}
