package id.cassy.kasir.antarmuka.utama

import androidx.compose.runtime.Immutable
import id.cassy.kasir.ranah.model.ItemKeranjang
import id.cassy.kasir.ranah.model.Produk

/**
 * Status ringkasan informasi aplikasi dan metrik cepat di beranda.
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
 */
@Immutable
data class StatusKeranjangKasir(
    val judul: String,
    val deskripsi: String,
    val jumlahItem: String,
)

/**
 * Representasi status rincian biaya dan tombol aksi pembayaran.
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
