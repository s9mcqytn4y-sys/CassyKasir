package id.cassy.kasir.antarmuka.utama

import androidx.compose.runtime.Immutable
import id.cassy.kasir.ranah.model.ItemKeranjang
import id.cassy.kasir.ranah.model.Produk

/**
 * Representasi seluruh status UI untuk Layar Utama Kasir.
 *
 * Model ini menjadi satu sumber kebenaran untuk seluruh tampilan layar.
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
        judul = "Keranjang kosong",
        deskripsi = "Mulai transaksi dengan memilih produk.",
        jumlahItem = "0 item",
    ),
    val ringkasanPembayaran: RingkasanPembayaranKasir = RingkasanPembayaranKasir(
        subtotal = "Rp0",
        potongan = "Rp0",
        pajak = "Rp0",
        totalAkhir = "Rp0",
        labelAksiUtama = "Pilih Produk",
        aksiUtamaAktif = false,
    ),
    val kataKunciPencarian: String = "",
    val apakahRingkasanPembayaranTampil: Boolean = true,
)
