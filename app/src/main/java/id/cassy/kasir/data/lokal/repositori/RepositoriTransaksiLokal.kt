package id.cassy.kasir.data.lokal.repositori

import id.cassy.kasir.data.lokal.basisdata.BasisDataCassyKasir
import id.cassy.kasir.data.lokal.pemetaan.keDomain
import id.cassy.kasir.data.lokal.pemetaan.keLokal
import id.cassy.kasir.data.lokal.relasi.TransaksiDenganItemLokal
import id.cassy.kasir.ranah.model.Transaksi
import id.cassy.kasir.ranah.repositori.RepositoriTransaksi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementasi repository transaksi berbasis Room.
 *
 * Class ini berada di layer data karena mengetahui detail database lokal.
 * Layer ranah hanya mengenal kontrak [RepositoriTransaksi].
 *
 * @property basisData Instance database lokal Cassy Kasir.
 */
class RepositoriTransaksiLokal(
    private val basisData: BasisDataCassyKasir,
) : RepositoriTransaksi {

    private val aksesDataTransaksi = basisData.aksesDataTransaksiLokal()

    /**
     * Menyimpan transaksi baru beserta seluruh item terkait ke database secara atomik.
     *
     * @param transaksi Objek domain transaksi yang akan disimpan.
     */
    override suspend fun simpanTransaksi(
        transaksi: Transaksi,
    ) {
        val entitasTransaksi = transaksi.keLokal()
        val daftarEntitasItem = transaksi.daftarItemKeranjang.map { itemKeranjang ->
            itemKeranjang.keLokal(transaksi.id)
        }

        aksesDataTransaksi.simpanTransaksiDenganItem(entitasTransaksi, daftarEntitasItem)
    }

    /**
     * Mengamati seluruh riwayat transaksi dari Room.
     *
     * @return Aliran daftar transaksi domain.
     */
    override fun amatiSemuaTransaksi(): Flow<List<Transaksi>> {
        return aksesDataTransaksi.amatiSemuaTransaksi().map { daftarLokal: List<TransaksiDenganItemLokal> ->
            daftarLokal.map { transaksiLokal ->
                transaksiLokal.keDomain()
            }
        }
    }

    /**
     * Mengamati satu transaksi berdasarkan identitas.
     *
     * @param identitasTransaksi Identitas unik transaksi.
     * @return Aliran transaksi domain, atau null jika tidak ditemukan.
     */
    override fun amatiTransaksiBerdasarkanIdentitas(
        identitasTransaksi: String,
    ): Flow<Transaksi?> {
        return aksesDataTransaksi.amatiTransaksiBerdasarkanId(identitasTransaksi).map { transaksiLokal ->
            transaksiLokal?.keDomain()
        }
    }

    /**
     * Mengambil rincian transaksi tunggal berdasarkan identitas unik.
     *
     * @param identitasTransaksi Identitas unik transaksi.
     * @return Objek domain transaksi jika ditemukan, null jika tidak.
     */
    override suspend fun ambilTransaksiBerdasarkanIdentitas(
        identitasTransaksi: String,
    ): Transaksi? {
        return aksesDataTransaksi.ambilTransaksiBerdasarkanId(identitasTransaksi)?.keDomain()
    }
}
