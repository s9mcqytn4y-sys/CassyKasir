package id.cassy.kasir.ranah.kasuspenggunaan

import androidx.compose.runtime.Immutable
import id.cassy.kasir.ranah.fungsi.hitungJumlahItem
import id.cassy.kasir.ranah.fungsi.hitungTotalTransaksi
import id.cassy.kasir.ranah.model.ItemKeranjang
import id.cassy.kasir.ranah.model.StatusSinkronisasi

import id.cassy.kasir.data.lokal.repositori.RepositoriTransaksi
import id.cassy.kasir.data.lokal.identitas.PembangkitIdTransaksiLokal
import id.cassy.kasir.ranah.model.Transaksi

/**
 * Hasil penyelesaian checkout statis lokal.
 *
 * @property daftarItemKeranjangBaru Daftar item keranjang setelah checkout (biasanya kosong).
 * @property statusSinkronisasiBaru Objek status penyimpanan transaksi.
 * @property jumlahItemCheckout Total kuantitas item yang berhasil diproses.
 * @property totalCheckout Nilai moneter total dari transaksi.
 */
@Immutable
data class HasilCheckoutLokalKasir(
    val daftarItemKeranjangBaru: List<ItemKeranjang>,
    val statusSinkronisasiBaru: StatusSinkronisasi,
    val jumlahItemCheckout: Int,
    val totalCheckout: Long,
)

/**
 * Kasus Penggunaan untuk menyelesaikan proses checkout secara lokal.
 * Bertanggung jawab membuat ID transaksi, menghitung total, dan menyimpan ke [RepositoriTransaksi].
 *
 * @property repositori Sumber persistensi data transaksi.
 */
class SelesaikanCheckoutLokalKasir(
    private val repositori: RepositoriTransaksi,
) {

    /**
     * Memproses penyelesaian transaksi lokal.
     *
     * @param daftarItemKeranjang Daftar item yang akan di-checkout.
     * @return Objek hasil checkout berisi ringkasan transaksi.
     */
    suspend fun eksekusi(
        daftarItemKeranjang: List<ItemKeranjang>,
    ): HasilCheckoutLokalKasir {
        val jumlahItemCheckout = daftarItemKeranjang.hitungJumlahItem()
        val totalCheckout = hitungTotalTransaksi(
            daftarItemKeranjang = daftarItemKeranjang,
            potongan = 0,
            biayaLayanan = 0,
            pajak = 0,
        )

        // Membuat objek domain Transaksi
        val transaksi = Transaksi(
            id = PembangkitIdTransaksiLokal.buatIdBaru(),
            daftarItemKeranjang = daftarItemKeranjang,
            uangDibayar = totalCheckout,
            potongan = 0,
            biayaLayanan = 0,
            pajak = 0,
            waktuTransaksiEpochMili = System.currentTimeMillis(),
            catatan = null,
        )

        // Simpan ke database melalui repositori
        repositori.simpanTransaksi(transaksi)

        return HasilCheckoutLokalKasir(
            daftarItemKeranjangBaru = emptyList(),
            statusSinkronisasiBaru = StatusSinkronisasi.SinkronLokal,
            jumlahItemCheckout = jumlahItemCheckout,
            totalCheckout = totalCheckout,
        )
    }
}
