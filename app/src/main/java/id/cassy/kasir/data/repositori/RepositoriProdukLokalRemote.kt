package id.cassy.kasir.data.repositori

import id.cassy.kasir.data.jaringan.layanan.LayananProdukJaringan
import id.cassy.kasir.data.jaringan.pemetaan.keDomain
import id.cassy.kasir.data.lokal.basisdata.BasisDataCassyKasir
import id.cassy.kasir.data.lokal.pemetaan.keDomain
import id.cassy.kasir.data.lokal.pemetaan.keLokal
import id.cassy.kasir.ranah.model.HasilOperasiJaringan
import id.cassy.kasir.ranah.model.Produk
import id.cassy.kasir.ranah.repositori.RepositoriProduk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Implementasi repositori produk yang menggabungkan data lokal (Room) dan remote (Retrofit).
 * Mengutamakan data lokal untuk tampilan (Local-first).
 */
class RepositoriProdukLokalRemote(
    private val basisData: BasisDataCassyKasir,
    private val layananJaringan: LayananProdukJaringan,
) : RepositoriProduk {

    private val dao = basisData.aksesDataProdukLokal()

    override fun amatiSemuaProduk(): Flow<List<Produk>> {
        return dao.amatiSemuaProduk().map { daftarLokal ->
            daftarLokal.map { it.keDomain() }
        }
    }

    override fun cariProdukLokal(kataKunci: String): Flow<List<Produk>> {
        return dao.cariProduk(kataKunci).map { daftarLokal ->
            daftarLokal.map { it.keDomain() }
        }
    }

    override suspend fun sinkronkanKatalog(): HasilOperasiJaringan<Unit> {
        return try {
            val responsJaringan = layananJaringan.ambilDaftarProduk()
            val daftarProdukDomain = responsJaringan.keDomain()

            // Simpan ke database lokal
            dao.simpanBanyakProduk(daftarProdukDomain.map { it.keLokal() })

            HasilOperasiJaringan.Berhasil(Unit)
        } catch (e: IOException) {
            HasilOperasiJaringan.GagalJaringan("Koneksi internet bermasalah")
        } catch (e: Exception) {
            HasilOperasiJaringan.GagalServer(500, e.message ?: "Gagal mengambil data dari server")
        }
    }
}
