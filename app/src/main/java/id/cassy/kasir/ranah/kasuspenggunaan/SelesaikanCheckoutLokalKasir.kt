package id.cassy.kasir.ranah.kasuspenggunaan

import androidx.compose.runtime.Immutable
import id.cassy.kasir.ranah.fungsi.hitungJumlahItem
import id.cassy.kasir.ranah.fungsi.hitungTotalTransaksi
import id.cassy.kasir.ranah.model.ItemKeranjang

/**
 * Hasil penyelesaian checkout statis lokal.
 */
@Immutable
data class HasilCheckoutLokalKasir(
    val daftarItemKeranjangBaru: List<ItemKeranjang>,
    val statusSinkronisasiBaru: String,
    val jumlahItemCheckout: Int,
    val totalCheckout: Long,
)

/**
 * Menyelesaikan checkout lokal secara statis.
 *
 * Pada scope ini, checkout hanya:
 * - menghitung total transaksi akhir,
 * - menyiapkan hasil informasi transaksi,
 * - dan mengosongkan keranjang.
 *
 * Belum ada penyimpanan Room, pencetakan struk, atau sinkronisasi server.
 */
class SelesaikanCheckoutLokalKasir {

    operator fun invoke(
        daftarItemKeranjang: List<ItemKeranjang>,
    ): HasilCheckoutLokalKasir {
        val jumlahItemCheckout = daftarItemKeranjang.hitungJumlahItem()
        val totalCheckout = hitungTotalTransaksi(
            daftarItemKeranjang = daftarItemKeranjang,
            potongan = 0,
            biayaLayanan = 0,
            pajak = 0,
        )

        return HasilCheckoutLokalKasir(
            daftarItemKeranjangBaru = emptyList(),
            statusSinkronisasiBaru = "Transaksi lokal selesai",
            jumlahItemCheckout = jumlahItemCheckout,
            totalCheckout = totalCheckout,
        )
    }
}
