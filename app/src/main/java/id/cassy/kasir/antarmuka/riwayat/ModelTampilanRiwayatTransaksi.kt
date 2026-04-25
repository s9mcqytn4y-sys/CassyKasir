package id.cassy.kasir.antarmuka.riwayat

import androidx.compose.runtime.Immutable

/**
 * Representasi status data untuk layar riwayat transaksi.
 *
 * @property judulLayar Judul yang akan ditampilkan di bilah atas.
 * @property kataKunciPencarian Isi kata kunci pencarian aktif.
 * @property petunjukPencarian Bantuan teks pada kolom pencarian.
 * @property statusMuat Kondisi pemuatan data riwayat.
 */
@Immutable
data class ModelTampilanRiwayatTransaksi(
    val judulLayar: String = "Riwayat Transaksi",
    val kataKunciPencarian: String = "",
    val petunjukPencarian: String = "Cari id transaksi, catatan, atau nama produk",
    val statusMuat: StatusMuatRiwayatTransaksi = StatusMuatRiwayatTransaksi.Memuat,
)

/**
 * Definisi status pemuatan data riwayat transaksi.
 */
@Immutable
sealed interface StatusMuatRiwayatTransaksi {

    /**
     * Sedang dalam proses mengambil data.
     */
    @Immutable
    data object Memuat : StatusMuatRiwayatTransaksi

    /**
     * Berhasil mengambil daftar transaksi.
     *
     * @property judulBagian Label untuk daftar riwayat.
     * @property deskripsiBagian Penjelasan singkat tentang data yang tampil.
     * @property labelJumlahHasil Ringkasan jumlah hasil saat ini.
     * @property daftarRingkasanTransaksi List data ringkasan untuk tiap baris riwayat.
     */
    @Immutable
    data class Berhasil(
        val judulBagian: String,
        val deskripsiBagian: String,
        val labelJumlahHasil: String,
        val daftarRingkasanTransaksi: List<RingkasanTransaksiRiwayat>,
    ) : StatusMuatRiwayatTransaksi

    /**
     * Berhasil mengambil data tapi daftar kosong.
     *
     * @property judul Pesan utama saat kosong.
     * @property deskripsi Saran aksi saat kosong.
     */
    @Immutable
    data class Kosong(
        val judul: String,
        val deskripsi: String,
    ) : StatusMuatRiwayatTransaksi

    /**
     * Terjadi kesalahan saat memproses data.
     *
     * @property judul Pesan kegagalan utama.
     * @property deskripsi Rincian kesalahan atau saran perbaikan.
     */
    @Immutable
    data class Gagal(
        val judul: String,
        val deskripsi: String,
    ) : StatusMuatRiwayatTransaksi
}

/**
 * Model ringkas untuk satu baris item dalam riwayat.
 *
 * @property transaksiId ID unik transaksi.
 * @property labelWaktu Waktu transaksi terformat manusiawi.
 * @property labelJumlahItem Deskripsi jumlah item.
 * @property labelTotalAkhir Nilai total akhir terformat rupiah.
 * @property ringkasanItem Ringkasan isi transaksi untuk membantu pencarian visual cepat.
 */
@Immutable
data class RingkasanTransaksiRiwayat(
    val transaksiId: String,
    val labelWaktu: String,
    val labelJumlahItem: String,
    val labelTotalAkhir: String,
    val ringkasanItem: String,
)
