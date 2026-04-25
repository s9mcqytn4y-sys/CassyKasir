package id.cassy.kasir.antarmuka.utama

import androidx.compose.runtime.Immutable
import id.cassy.kasir.ranah.model.ItemKeranjang
import id.cassy.kasir.ranah.model.Produk

/**
 * Status ringkasan informasi aplikasi dan metrik cepat di beranda.
 *
 * @property namaAplikasi Nama brand aplikasi.
 * @property sloganAplikasi Pesan pemasaran aplikasi.
 * @property jumlahProdukTersedia Total produk dalam katalog.
 * @property jumlahItemKeranjang Total kuantitas item di keranjang.
 * @property totalBelanjaSementara Estimasi nilai belanja saat ini.
 * @property statusSinkronisasi Keterangan status penyimpanan data.
 */
@Immutable
data class StatusBerandaKasir(
    val namaAplikasi: String,
    val sloganAplikasi: String,
    val jumlahProdukTersedia: Int,
    val jumlahItemKeranjang: Int,
    val totalBelanjaSementara: String,
    val statusSinkronisasi: String,
)

/**
 * Representasi status visual panel keranjang belanja.
 *
 * @property judul Teks judul panel keranjang.
 * @property deskripsi Pesan informatif di panel keranjang.
 * @property jumlahItem Label jumlah item saat ini.
 */
@Immutable
data class StatusKeranjangKasir(
    val judul: String,
    val deskripsi: String,
    val jumlahItem: String,
)

/**
 * Representasi status rincian biaya dan tombol aksi pembayaran.
 *
 * @property subtotal Nilai kotor sebelum potongan/pajak.
 * @property potongan Nilai diskon.
 * @property pajak Nilai pajak.
 * @property totalAkhir Nilai bersih yang harus dibayar.
 * @property labelAksiUtama Teks pada tombol bayar/pilih.
 * @property aksiUtamaAktif Status apakah tombol dapat diklik.
 */
@Immutable
data class RingkasanPembayaranKasir(
    val subtotal: String,
    val potongan: String,
    val pajak: String,
    val totalAkhir: String,
    val labelAksiUtama: String,
    val aksiUtamaAktif: Boolean,
)

/**
 * Status dialog konfirmasi checkout.
 *
 * @property apakahTampil Status visibilitas dialog.
 * @property judul Teks judul dialog.
 * @property deskripsi Pesan rincian dalam dialog.
 * @property labelKonfirmasi Teks tombol eksekusi bayar.
 */
@Immutable
data class StatusKonfirmasiCheckoutKasir(
    val apakahTampil: Boolean = false,
    val judul: String = "Konfirmasi pembayaran",
    val deskripsi: String = "",
    val labelKonfirmasi: String = "Bayar sekarang",
)

/**
 * Status hasil checkout yang ditampilkan sebagai banner.
 *
 * @property apakahTampil Status visibilitas banner.
 * @property judul Teks judul banner.
 * @property deskripsi Pesan sukses atau gagal.
 */
@Immutable
data class StatusHasilCheckoutKasir(
    val apakahTampil: Boolean = false,
    val judul: String = "",
    val deskripsi: String = "",
)

/**
 * Representasi seluruh status UI untuk Layar Utama Kasir.
 *
 * Model ini menjadi satu sumber kebenaran (single source of truth) untuk antarmuka.
 */
@Immutable
data class ModelTampilanLayarUtamaKasir(
    val statusBeranda: StatusBerandaKasir = StatusBerandaKasir(
        namaAplikasi = "Cassy Kasir",
        sloganAplikasi = "Solusi Digital UMKM Modern",
        jumlahProdukTersedia = 0,
        jumlahItemKeranjang = 0,
        totalBelanjaSementara = "Rp0",
        statusSinkronisasi = "Tersimpan Lokal",
    ),
    val daftarProdukTersaring: List<Produk> = emptyList(),
    val daftarItemKeranjang: List<ItemKeranjang> = emptyList(),
    val statusKeranjang: StatusKeranjangKasir = StatusKeranjangKasir(
        judul = "Keranjang masih kosong",
        deskripsi = "Yuk, mulai transaksi dengan memilih produk favorit!",
        jumlahItem = "0 item",
    ),
    val ringkasanPembayaran: RingkasanPembayaranKasir = RingkasanPembayaranKasir(
        subtotal = "Rp0",
        potongan = "Rp0",
        pajak = "Rp0",
        totalAkhir = "Rp0",
        labelAksiUtama = "Pilih produk",
        aksiUtamaAktif = false,
    ),
    val statusKonfirmasiCheckout: StatusKonfirmasiCheckoutKasir = StatusKonfirmasiCheckoutKasir(),
    val statusHasilCheckout: StatusHasilCheckoutKasir = StatusHasilCheckoutKasir(),
    val kataKunciPencarian: String = "",
    val apakahRingkasanPembayaranTampil: Boolean = true,
)
