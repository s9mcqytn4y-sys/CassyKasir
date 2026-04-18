package id.cassy.kasir.ranah.model

data class Transaksi(
    val id: String,
    val daftarItemKeranjang: List<ItemKeranjang>,
    val uangDibayar: Long,
    val potongan: Long = 0,
    val biayaLayanan: Long = 0,
    val pajak: Long = 0,
    val waktuTransaksiEpochMili: Long,
    val catatan: String? = null
)