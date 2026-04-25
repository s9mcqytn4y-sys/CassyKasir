package id.cassy.kasir.data.lokal.entitas

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entitas database untuk menyimpan data utama sebuah transaksi.
 *
 * @property id ID unik transaksi (UUID).
 * @property uangDibayar Jumlah uang tunai yang diterima dari pelanggan.
 * @property potongan Total diskon atau potongan harga.
 * @property biayaLayanan Biaya tambahan layanan.
 * @property pajak Total pajak transaksi.
 * @property waktuTransaksiEpochMili Stempel waktu saat transaksi terjadi (Unix Epoch).
 * @property catatan Catatan tambahan untuk transaksi ini.
 */
@Entity(
    tableName = "transaksi_lokal",
)
data class EntitasTransaksiLokal(
    @PrimaryKey
    val id: String,
    val uangDibayar: Long,
    val potongan: Long,
    val biayaLayanan: Long,
    val pajak: Long,
    val waktuTransaksiEpochMili: Long,
    val catatan: String?,
)
