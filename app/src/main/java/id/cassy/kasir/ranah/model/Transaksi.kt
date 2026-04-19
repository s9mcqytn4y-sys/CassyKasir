package id.cassy.kasir.ranah.model

/**
 * Representasi dokumen transaksi final setelah pembayaran.
 *
 * @property id Nomor referensi unik transaksi (misal: "TRX-20241020-001").
 * @property daftarItemKeranjang List [ItemKeranjang] yang dibeli.
 * @property uangDibayar Nominal uang yang diterima dari pelanggan.
 * @property potongan Nilai diskon yang dikurangkan dari total.
 * @property biayaLayanan Biaya tambahan (service charge).
 * @property pajak Nilai pajak (PPN/PB1).
 * @property waktuTransaksiEpochMili Timestamp saat transaksi dicatat dalam milidetik.
 * @property catatan Pesan tambahan untuk transaksi secara keseluruhan.
 */
data class Transaksi(
    val id: String,
    val daftarItemKeranjang: List<ItemKeranjang>,
    val uangDibayar: Long,
    val potongan: Long = 0,
    val biayaLayanan: Long = 0,
    val pajak: Long = 0,
    val waktuTransaksiEpochMili: Long,
    val catatan: String? = null,
)
