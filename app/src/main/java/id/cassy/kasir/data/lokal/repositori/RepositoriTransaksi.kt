package id.cassy.kasir.data.lokal.repositori

import id.cassy.kasir.data.lokal.basisdata.BasisDataCassyKasir
import id.cassy.kasir.data.lokal.pemetaan.keDomain
import id.cassy.kasir.data.lokal.pemetaan.keLokal
import id.cassy.kasir.data.lokal.relasi.TransaksiDenganItemLokal
import id.cassy.kasir.ranah.model.Transaksi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repositori untuk mengelola persistensi data transaksi.
 * Menghubungkan logika bisnis (ranah) dengan database lokal (Room).
 *
 * @property basisData Instance database lokal Cassy Kasir.
 */
class RepositoriTransaksi(
    private val basisData: BasisDataCassyKasir,
) {
    private val dao = basisData.aksesDataTransaksiLokal()

    /**
     * Menyimpan transaksi baru beserta seluruh item terkait ke database secara atomik.
     *
     * @param transaksi Objek domain transaksi yang akan disimpan.
     */
    suspend fun simpanTransaksi(transaksi: Transaksi) {
        val entitasTransaksi = transaksi.keLokal()
        val daftarEntitasItem = transaksi.daftarItemKeranjang.map { it.keLokal(transaksi.id) }
        
        dao.simpanTransaksiDenganItem(entitasTransaksi, daftarEntitasItem)
    }

    /**
     * Mengambil aliran (Flow) dari seluruh riwayat transaksi.
     * Data akan diperbarui secara otomatis setiap ada perubahan di database.
     *
     * @return Aliran daftar objek domain Transaksi.
     */
    fun ambilSemuaTransaksi(): Flow<List<Transaksi>> {
        return dao.amatiSemuaTransaksi().map { daftarLokal: List<TransaksiDenganItemLokal> ->
            daftarLokal.map { item: TransaksiDenganItemLokal -> item.keDomain() }
        }
    }

    /**
     * Mengamati satu transaksi berdasarkan ID.
     *
     * Dipakai untuk layar detail transaksi yang perlu tetap sinkron
     * dengan perubahan data lokal.
     *
     * @param id ID unik transaksi.
     * @return Aliran objek domain Transaksi atau null jika tidak ditemukan.
     */
    fun amatiTransaksiBerdasarkanId(
        id: String,
    ): Flow<Transaksi?> {
        return dao.amatiTransaksiBerdasarkanId(id).map { transaksiLokal ->
            transaksiLokal?.keDomain()
        }
    }

    /**
     * Mengambil rincian transaksi tunggal berdasarkan ID unik.
     *
     * @param id ID unik transaksi (UUID).
     * @return Objek domain Transaksi jika ditemukan, null jika tidak.
     */
    suspend fun ambilTransaksiBerdasarkanId(id: String): Transaksi? {
        return dao.ambilTransaksiBerdasarkanId(id)?.keDomain()
    }
}
