package id.cassy.kasir.antarmuka.utama

import androidx.compose.runtime.Immutable
import id.cassy.kasir.ranah.model.Produk

/**
 * Representasi status UI (State) tunggal untuk Layar Utama Kasir.
 *
 * Menggunakan anotasi @Immutable untuk optimasi rekomposisi pada Jetpack Compose.
 * Seluruh properti bersifat immutable (val) untuk mendukung pola Unidirectional Data Flow (UDF).
 *
 * @property statusBeranda Informasi identitas dan statistik ringkas aplikasi.
 * @property daftarProdukTersaring Daftar produk yang ditampilkan setelah melalui filter pencarian.
 * @property statusKeranjang Status visual keranjang belanja (saat ini statis/kosong).
 * @property ringkasanPembayaran Detail kalkulasi biaya transaksi.
 * @property kataKunciPencarian Teks aktif yang ada pada kolom pencarian.
 * @property apakahRingkasanPembayaranTampil Status visibilitas panel detail pembayaran.
 */
@Immutable
data class ModelTampilanLayarUtamaKasir(
    val statusBeranda: StatusBerandaKasir = StatusBerandaKasir(
        namaAplikasi = "Cassy Kasir",
        sloganAplikasi = "Solusi Kasir UMKM",
        jumlahProdukTersedia = 0,
        jumlahItemKeranjang = 0,
        totalBelanjaSementara = "Rp0",
        statusSinkronisasi = "Tersimpan Lokal",
    ),
    val daftarProdukTersaring: List<Produk> = emptyList(),
    val statusKeranjang: StatusKeranjangStatis = StatusKeranjangStatis(
        judul = "Keranjang Kosong",
        deskripsi = "Mulai transaksi dengan memilih produk.",
        jumlahItem = "0 item",
    ),
    val ringkasanPembayaran: RingkasanPembayaranStatis = RingkasanPembayaranStatis(
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
