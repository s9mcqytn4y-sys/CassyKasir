package id.cassy.kasir.ranah.kasuspenggunaan

import id.cassy.kasir.data.lokal.identitas.PembangkitIdentitasTransaksiLokal
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
            id = PembangkitIdentitasTransaksiLokal.buatIdentitasBaru(),
            daftarItemKeranjang = daftarItemKeranjangBersih,
            uangDibayar = totalCheckout.nilaiRupiah,
            potongan = 0,
            biayaLayanan = 0,
            pajak = 0,
            waktuTransaksiEpochMili = System.currentTimeMillis(),
            catatan = null,
        )

        repositori.simpanTransaksi(transaksi)

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
 * Untuk tahap sekarang, ViewModel masih menangkap Exception dan menampilkan
 * pesan umum. Pesan spesifik ini disiapkan agar Scope berikutnya bisa
 * menampilkan alasan validasi yang lebih tepat kepada kasir.
 */
private fun HasilValidasiCheckout.pastikanSah() {
    if (this is HasilValidasiCheckout.TidakSah) {
        throw IllegalArgumentException(pesan)
    }
}
