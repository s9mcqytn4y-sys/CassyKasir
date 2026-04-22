package id.cassy.kasir.ranah.kasuspenggunaan

import id.cassy.kasir.antarmuka.utama.ModelTampilanLayarUtamaKasir
import id.cassy.kasir.antarmuka.utama.RingkasanPembayaranKasir
import id.cassy.kasir.antarmuka.utama.StatusBerandaKasir
import id.cassy.kasir.antarmuka.utama.StatusKeranjangKasir
import id.cassy.kasir.ranah.fungsi.cariProduk
import id.cassy.kasir.ranah.fungsi.hitungJumlahItem
import id.cassy.kasir.ranah.fungsi.hitungSubtotalKeranjang
import id.cassy.kasir.ranah.fungsi.hitungTotalTransaksi
import id.cassy.kasir.ranah.model.ItemKeranjang
import id.cassy.kasir.ranah.model.Produk

/**
 * Membentuk satu model tampilan utuh untuk layar utama kasir
 * dari data produk, keranjang, pencarian, dan status panel pembayaran.
 */
class BentukModelTampilanLayarUtamaKasir {

    operator fun invoke(
        daftarProdukPenuh: List<Produk>,
        daftarItemKeranjang: List<ItemKeranjang>,
        kataKunciPencarian: String,
        apakahRingkasanPembayaranTampil: Boolean,
    ): ModelTampilanLayarUtamaKasir {
        val daftarProdukTersaring = daftarProdukPenuh.cariProduk(kataKunciPencarian)
        val jumlahItem = daftarItemKeranjang.hitungJumlahItem()
        val subtotal = daftarItemKeranjang.hitungSubtotalKeranjang()
        val total = hitungTotalTransaksi(
            daftarItemKeranjang = daftarItemKeranjang,
            potongan = 0,
            biayaLayanan = 0,
            pajak = 0,
        )

        return ModelTampilanLayarUtamaKasir(
            statusBeranda = StatusBerandaKasir(
                namaAplikasi = "Cassy Kasir",
                sloganAplikasi = "Solusi Digital UMKM Modern",
                jumlahProdukTersedia = daftarProdukPenuh.size,
                jumlahItemKeranjang = jumlahItem,
                totalBelanjaSementara = subtotal.sebagaiRupiahSederhana(),
                statusSinkronisasi = "Tersimpan Lokal",
            ),
            daftarProdukTersaring = daftarProdukTersaring,
            daftarItemKeranjang = daftarItemKeranjang,
            statusKeranjang = StatusKeranjangKasir(
                judul = if (daftarItemKeranjang.isEmpty()) {
                    "Keranjang kosong"
                } else {
                    "Keranjang aktif"
                },
                deskripsi = if (daftarItemKeranjang.isEmpty()) {
                    "Mulai transaksi dengan memilih produk."
                } else {
                    "Atur jumlah item sebelum lanjut ke pembayaran."
                },
                jumlahItem = "$jumlahItem item",
            ),
            ringkasanPembayaran = RingkasanPembayaranKasir(
                subtotal = subtotal.sebagaiRupiahSederhana(),
                potongan = "Rp0",
                pajak = "Rp0",
                totalAkhir = total.sebagaiRupiahSederhana(),
                labelAksiUtama = if (jumlahItem > 0) {
                    "Lanjut Pembayaran"
                } else {
                    "Pilih Produk"
                },
                aksiUtamaAktif = jumlahItem > 0,
            ),
            kataKunciPencarian = kataKunciPencarian,
            apakahRingkasanPembayaranTampil = apakahRingkasanPembayaranTampil,
        )
    }

    private fun Long.sebagaiRupiahSederhana(): String {
        return "Rp$this"
    }
}
