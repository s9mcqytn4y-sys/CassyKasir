package id.cassy.kasir.ranah.model

/**
 * Preferensi toko yang dipakai sebagai pengaturan dasar Cassy Kasir.
 *
 * Data ini cocok disimpan di DataStore karena ukurannya kecil dan tidak
 * membutuhkan relasi database.
 *
 * @property namaToko Nama resmi toko/warung yang akan muncul di nota dan layar utama.
 * @property alamatToko Alamat fisik atau informasi lokasi toko.
 * @property basisPoinPajakDefault Persentase pajak default dalam basis poin (contoh: 1100 untuk 11%).
 * @property biayaLayananDefault Biaya layanan tetap yang dibebankan per transaksi.
 */
data class PreferensiToko(
    val namaToko: String = "Cassy Kasir",
    val alamatToko: String = "",
    val basisPoinPajakDefault: Int = 0,
    val biayaLayananDefault: Long = 0L,
) {
    init {
        require(namaToko.isNotBlank()) {
            "Nama toko wajib diisi."
        }

        require(basisPoinPajakDefault >= 0) {
            "Pajak default tidak boleh negatif."
        }

        require(biayaLayananDefault >= 0L) {
            "Biaya layanan default tidak boleh negatif."
        }
    }
}
