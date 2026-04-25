package id.cassy.kasir.data.lokal.basisdata

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Kumpulan migrasi database lokal CassyKasir.
 *
 * Scope ini memperbaiki perbedaan schema saat index baru ditambahkan
 * ke tabel transaksi lokal dan item transaksi lokal.
 */
object MigrasiBasisDataKasir {

    /**
     * Migrasi dari versi 1 ke versi 2.
     *
     * Perubahan:
     * - menambahkan index untuk urutan waktu transaksi
     * - menambahkan index untuk produkId pada item transaksi
     */
    val DARI_1_KE_2: Migration = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_transaksi_lokal_waktuTransaksiEpochMili
                ON transaksi_lokal(waktuTransaksiEpochMili)
                """.trimIndent(),
            )

            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_item_transaksi_lokal_produkId
                ON item_transaksi_lokal(produkId)
                """.trimIndent(),
            )
        }
    }
}
