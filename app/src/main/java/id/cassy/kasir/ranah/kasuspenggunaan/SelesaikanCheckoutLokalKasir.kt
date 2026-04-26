package id.cassy.kasir.ranah.kasuspenggunaan

import id.cassy.kasir.data.lokal.identitas.PembangkitIdentitasTransaksiLokal
import id.cassy.kasir.ranah.fungsi.hitungJumlahItem
import id.cassy.kasir.ranah.fungsi.hitungTotalTransaksi
import id.cassy.kasir.ranah.fungsi.transaksiSiapDiproses
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
        val totalCheckout = hitungTotalTransaksi(
            daftarItemKeranjang = daftarItemKeranjang,
            potongan = 0,
            biayaLayanan = 0,
            pajak = 0,
        )

        require(
            transaksiSiapDiproses(
                daftarItemKeranjang = daftarItemKeranjang,
                uangDibayar = totalCheckout,
                potongan = 0,
                biayaLayanan = 0,
                pajak = 0,
            ),
        ) {
            "Transaksi belum valid untuk disimpan."
        }

        val jumlahItemCheckout = daftarItemKeranjang.hitungJumlahItem()

        val transaksi = Transaksi(
            id = PembangkitIdentitasTransaksiLokal.buatIdentitasBaru(),
            daftarItemKeranjang = daftarItemKeranjang,
            uangDibayar = totalCheckout,
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
            totalCheckout = totalCheckout,
        )
    }
}
