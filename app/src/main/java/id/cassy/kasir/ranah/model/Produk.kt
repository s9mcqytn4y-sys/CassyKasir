package id.cassy.kasir.ranah.model

/**
 * Representasi data untuk produk atau item yang dijual.
 *
 * @property id Identitas unik produk (misal: UUID atau slug).
 * @property nama Nama tampilan produk yang muncul di katalog.
 * @property harga Nilai jual produk dalam satuan mata uang terkecil (Long).
 * @property stokTersedia Kuantitas barang yang masih dapat dijual.
 * @property kodePindai Barcode atau kode SKU untuk pencarian cepat (opsional).
 * @property deskripsi Informasi tambahan tentang produk.
 * @property aktif Status apakah produk ini dapat ditampilkan dan dijual.
 */
data class Produk(
    val id: String,
    val nama: String,
    val harga: Long,
    val stokTersedia: Int,
    val kodePindai: String? = null,
    val deskripsi: String = "",
    val aktif: Boolean = true,
)
