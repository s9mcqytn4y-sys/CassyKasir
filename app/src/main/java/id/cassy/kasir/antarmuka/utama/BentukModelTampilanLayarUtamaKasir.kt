package id.cassy.kasir.antarmuka.utama

import id.cassy.kasir.ranah.fungsi.cariProduk
import id.cassy.kasir.ranah.fungsi.sebagaiRupiah
import id.cassy.kasir.ranah.kasuspenggunaan.HasilHitungTotalBelanja
import id.cassy.kasir.ranah.kasuspenggunaan.HitungTotalBelanja
import id.cassy.kasir.ranah.kasuspenggunaan.RingkasanTotalBelanja
import id.cassy.kasir.ranah.model.Produk
import id.cassy.kasir.ranah.model.RincianBiayaTransaksi
import id.cassy.kasir.ranah.model.Uang

/**
 * Pembentuk model tampilan untuk layar utama kasir.
 *
 * File ini berada di layer antarmuka karena menghasilkan state presentasi
 * yang langsung dipakai oleh Compose.
 */
class BentukModelTampilanLayarUtamaKasir {
    private val hitungTotalBelanja = HitungTotalBelanja()

    /**
     * Membentuk [ModelTampilanLayarUtamaKasir] berdasarkan status terkini.
     *
     * @param daftarProdukPenuh Koleksi seluruh produk yang tersedia di sistem.
     * @param statusTransaksi Data inti transaksi saat ini (keranjang dan sinkronisasi).
     * @param statusElemenLayar Status elemen visual dan interaksi pada layar.
     * @param kataKunciMentah Nilai teks asli dari input pencarian sebelum di-debounce.
     * @param kataKunciEfektif Nilai teks pencarian setelah di-debounce untuk memfilter katalog.
     * @return Model tampilan yang siap dikonsumsi oleh komponen Jetpack Compose.
     */
    operator fun invoke(
        daftarProdukPenuh: List<Produk>,
        statusTransaksi: StatusTransaksiLayarUtamaKasir,
        statusElemenLayar: StatusElemenLayarUtamaKasir,
        kataKunciMentah: String,
        kataKunciEfektif: String,
    ): ModelTampilanLayarUtamaKasir {
        val daftarItemKeranjang = statusTransaksi.daftarItemKeranjang

        val daftarProdukTersaring = daftarProdukPenuh.cariProduk(kataKunciEfektif)
        val hasilHitungTotalBelanja = hitungTotalBelanja(
            daftarItemKeranjang = daftarItemKeranjang,
        )
        val ringkasanTotalBelanja = when (hasilHitungTotalBelanja) {
            is HasilHitungTotalBelanja.Berhasil -> hasilHitungTotalBelanja.ringkasanTotalBelanja
            is HasilHitungTotalBelanja.Gagal -> RingkasanTotalBelanja(
                daftarItemKeranjangBersih = emptyList(),
                jumlahItem = 0,
                rincianBiayaTransaksi = RincianBiayaTransaksi(
                    subtotal = Uang.Nol,
                ),
                totalAkhir = Uang.Nol,
                kembalian = Uang.Nol,
            )
        }
        val jumlahItem = ringkasanTotalBelanja.jumlahItem
        val rincianBiayaTransaksi = ringkasanTotalBelanja.rincianBiayaTransaksi
        val totalAkhir = ringkasanTotalBelanja.totalAkhir

        return ModelTampilanLayarUtamaKasir(
            statusBeranda = StatusBerandaKasir(
                namaAplikasi = "Cassy Kasir",
                sloganAplikasi = "Solusi Digital UMKM Modern",
                jumlahProdukTersedia = daftarProdukPenuh.size,
                jumlahItemKeranjang = jumlahItem,
                totalBelanjaSementara = rincianBiayaTransaksi.subtotal.sebagaiRupiah(),
                statusSinkronisasi = statusTransaksi.statusSinkronisasi,
            ),
            daftarProdukTersaring = daftarProdukTersaring,
            daftarItemKeranjang = daftarItemKeranjang,
            statusKeranjang = StatusKeranjangKasir(
                judul = if (daftarItemKeranjang.isEmpty()) "Keranjang masih kosong" else "Keranjang aktif",
                deskripsi = if (daftarItemKeranjang.isEmpty()) {
                    "Yuk, mulai transaksi dengan memilih produk favorit!"
                } else {
                    "Atur jumlah item sebelum lanjut ke pembayaran."
                },
                jumlahItem = "$jumlahItem item",
            ),
            ringkasanPembayaran = RingkasanPembayaranKasir(
                subtotal = rincianBiayaTransaksi.subtotal.sebagaiRupiah(),
                potongan = rincianBiayaTransaksi.potongan.sebagaiRupiah(),
                pajak = rincianBiayaTransaksi.pajak.sebagaiRupiah(),
                totalAkhir = totalAkhir.sebagaiRupiah(),
                labelAksiUtama = if (jumlahItem > 0) "Bayar sekarang" else "Pilih produk",
                aksiUtamaAktif = jumlahItem > 0,
            ),
            statusKonfirmasiCheckout = StatusKonfirmasiCheckoutKasir(
                apakahTampil = statusElemenLayar.apakahDialogKonfirmasiCheckoutTampil && jumlahItem > 0,
                judul = "Konfirmasi pembayaran",
                deskripsi = "Bayar $jumlahItem item dengan total ${totalAkhir.sebagaiRupiah()} sekarang?",
                labelKonfirmasi = "Bayar sekarang",
            ),
            statusHasilCheckout = statusElemenLayar.statusHasilCheckout,
            kataKunciPencarian = kataKunciMentah,
            tampilkanAksiResetPencarian = kataKunciMentah.isNotBlank(),
            apakahRingkasanPembayaranTampil = statusElemenLayar.apakahRingkasanPembayaranTampil,
        )
    }
}
