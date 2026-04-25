package id.cassy.kasir.antarmuka.riwayat

import androidx.compose.runtime.Immutable

/**
 * Representasi status data untuk layar riwayat transaksi.
 *
 * @property judulLayar Judul yang akan ditampilkan di bilah atas.
 * @property statusMuat Kondisi pemuatan data riwayat.
 */
@Immutable
data class ModelTampilanRiwayatTransaksi(
    val judulLayar: String = "Riwayat Transaksi",
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
     * @property daftarRingkasanTransaksi List data ringkasan untuk tiap baris riwayat.
     */
    @Immutable
    data class Berhasil(
        val judulBagian: String,
        val deskripsiBagian: String,
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
 * @property labelJumlahItem Deskripsi jumlah item (misal: "3 item").
 * @property labelTotalAkhir Nilai total akhir terformat rupiah.
 */
@Immutable
data class RingkasanTransaksiRiwayat(
    val transaksiId: String,
    val labelWaktu: String,
    val labelJumlahItem: String,
    val labelTotalAkhir: String,
)
