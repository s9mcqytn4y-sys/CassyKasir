package id.cassy.kasir.ranah.kasuspenggunaan

import id.cassy.kasir.antarmuka.utama.ModelTampilanLayarUtamaKasir
import id.cassy.kasir.antarmuka.utama.RingkasanPembayaranKasir
import id.cassy.kasir.antarmuka.utama.StatusBerandaKasir
import id.cassy.kasir.antarmuka.utama.StatusElemenLayarUtamaKasir
import id.cassy.kasir.antarmuka.utama.StatusKeranjangKasir
import id.cassy.kasir.antarmuka.utama.StatusKonfirmasiCheckoutKasir
import id.cassy.kasir.antarmuka.utama.StatusTransaksiLayarUtamaKasir
import id.cassy.kasir.ranah.fungsi.cariProduk
import id.cassy.kasir.ranah.fungsi.hitungJumlahItem
import id.cassy.kasir.ranah.fungsi.hitungSubtotalKeranjang
import id.cassy.kasir.ranah.fungsi.hitungTotalTransaksi
import id.cassy.kasir.ranah.model.Produk

/**
 * Kasus penggunaan untuk mentransformasikan data domain dan status internal menjadi model tampilan (UI State).
 *
 * Mengikuti pola Unidirectional Data Flow (UDF) dengan memastikan seluruh logika pembentukan
 * status antarmuka terpusat di sini, sehingga ViewModel tetap bersih dan fokus pada manajemen aliran data.
 */
class BentukModelTampilanLayarUtamaKasir {

    /**
     * Membentuk [ModelTampilanLayarUtamaKasir] berdasarkan status terkini.
     *
     * @param daftarProdukPenuh Koleksi seluruh produk yang tersedia di sistem.
     * @param statusTransaksi Data inti transaksi saat ini (keranjang dan sinkronisasi).
     * @param statusElemenLayar Status elemen visual dan interaksi pada layar.
     * @return Model tampilan yang siap dikonsumsi oleh komponen Jetpack Compose.
     */
    operator fun invoke(
        daftarProdukPenuh: List<Produk>,
        statusTransaksi: StatusTransaksiLayarUtamaKasir,
        statusElemenLayar: StatusElemenLayarUtamaKasir,
    ): ModelTampilanLayarUtamaKasir {
        val daftarItemKeranjang = statusTransaksi.daftarItemKeranjang
        val kataKunciPencarian = statusElemenLayar.kataKunciPencarian

        // Optimasi: Gunakan fungsi ekstensi untuk logika pencarian dan perhitungan
        val daftarProdukTersaring = daftarProdukPenuh.cariProduk(kataKunciPencarian)
        val jumlahItem = daftarItemKeranjang.hitungJumlahItem()
        val subtotal = daftarItemKeranjang.hitungSubtotalKeranjang()

        // Menghitung total transaksi dengan parameter default (potongan/pajak bisa dikembangkan di masa depan)
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
                subtotal = subtotal.sebagaiRupiahSederhana(),
                potongan = "Rp0",
                pajak = "Rp0",
                totalAkhir = total.sebagaiRupiahSederhana(),
                labelAksiUtama = if (jumlahItem > 0) "Bayar sekarang" else "Pilih produk",
                aksiUtamaAktif = jumlahItem > 0,
            ),
            statusKonfirmasiCheckout = StatusKonfirmasiCheckoutKasir(
                apakahTampil = statusElemenLayar.apakahDialogKonfirmasiCheckoutTampil && jumlahItem > 0,
                judul = "Konfirmasi pembayaran",
                deskripsi = "Bayar $jumlahItem item dengan total ${total.sebagaiRupiahSederhana()} sekarang?",
                labelKonfirmasi = "Bayar sekarang",
            ),
            statusHasilCheckout = statusElemenLayar.statusHasilCheckout,
            kataKunciPencarian = kataKunciPencarian,
            apakahRingkasanPembayaranTampil = statusElemenLayar.apakahRingkasanPembayaranTampil,
        )
    }
}

/**
 * Memformat nilai Long menjadi string representasi Rupiah sederhana.
 */
private fun Long.sebagaiRupiahSederhana(): String {
    return "Rp$this"
}
