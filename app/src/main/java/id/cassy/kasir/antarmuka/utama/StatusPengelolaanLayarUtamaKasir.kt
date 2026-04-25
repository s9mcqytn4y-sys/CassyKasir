package id.cassy.kasir.antarmuka.utama

import androidx.compose.runtime.Immutable
import id.cassy.kasir.ranah.model.ItemKeranjang

/**
 * Kelompok status yang merepresentasikan data transaksi aktif di kasir.
 *
 * Status ini bersifat atomik dan dikelola sebagai bagian dari status internal ViewModel.
 * Digunakan untuk melacak item yang dipilih pelanggan dan status persistensi data.
 *
 * @property daftarItemKeranjang Daftar produk yang telah ditambahkan ke keranjang belanja.
 * @property statusSinkronisasi Label teks yang menunjukkan status penyimpanan data (misal: "Tersimpan Lokal").
 */
@Immutable
data class StatusTransaksiLayarUtamaKasir(
    val daftarItemKeranjang: List<ItemKeranjang> = emptyList(),
    val statusSinkronisasi: String = "Tersimpan Lokal",
)

/**
 * Kelompok status yang merepresentasikan elemen visual dan interaksi pada layar utama.
 *
 * Memisahkan status UI dari data transaksi membantu dalam mengurangi re-komposisi yang tidak perlu
 * dan mempermudah pengelolaan status sementara seperti visibilitas panel.
 *
 * @property apakahRingkasanPembayaranTampil Menentukan apakah panel detail biaya ditampilkan atau disembunyikan.
 * @property apakahDialogKonfirmasiCheckoutTampil Menentukan visibilitas dialog finalisasi transaksi.
 * @property statusHasilCheckout Pesan dan judul hasil akhir setelah aksi checkout berhasil dilakukan.
 */
@Immutable
data class StatusElemenLayarUtamaKasir(
    val apakahRingkasanPembayaranTampil: Boolean = true,
    val apakahDialogKonfirmasiCheckoutTampil: Boolean = false,
    val statusHasilCheckout: StatusHasilCheckoutKasir = StatusHasilCheckoutKasir(),
)
