package id.cassy.kasir.antarmuka.utama

import id.cassy.kasir.ranah.model.Produk

/**
 * Model data yang mewakili seluruh status UI pada Layar Utama Kasir.
 *
 * Mengikuti pola Unidirectional Data Flow (UDF), di mana seluruh status tampilan
 * digabungkan dalam satu objek state tunggal yang bersifat immutable.
 *
 * @property statusBeranda Informasi ringkasan di bagian atas layar.
 * @property daftarProdukTersaring Koleksi produk yang telah disaring berdasarkan pencarian.
 * @property statusKeranjang Informasi status keranjang belanja saat ini.
 * @property ringkasanPembayaran Detail biaya dan total transaksi.
 * @property kataKunciPencarian Teks yang dimasukkan pengguna di kolom pencarian.
 * @property apakahRingkasanPembayaranTampil Status visibilitas panel detail pembayaran.
 */
data class ModelTampilanLayarUtamaKasir(
    val statusBeranda: StatusBerandaKasir = StatusBerandaKasir(
        namaAplikasi = "Cassy Kasir",
        sloganAplikasi = "Kasir Cepat untuk Usaha Hebat",
        jumlahProdukTersedia = 0,
        jumlahItemKeranjang = 0,
        totalBelanjaSementara = "Rp0",
        statusSinkronisasi = "Tersimpan Lokal",
    ),
    val daftarProdukTersaring: List<Produk> = emptyList(),
    val statusKeranjang: StatusKeranjangStatis = StatusKeranjangStatis(
        judul = "Keranjang masih kosong",
        deskripsi = "Pilih produk dari katalog untuk mulai transaksi.",
        jumlahItem = "0 item",
    ),
    val ringkasanPembayaran: RingkasanPembayaranStatis = RingkasanPembayaranStatis(
        subtotal = "Rp0",
        potongan = "Rp0",
        pajak = "Rp0",
        totalAkhir = "Rp0",
        labelAksiUtama = "Pilih produk dulu",
        aksiUtamaAktif = false,
    ),
    val kataKunciPencarian: String = "",
    val apakahRingkasanPembayaranTampil: Boolean = true,
)
