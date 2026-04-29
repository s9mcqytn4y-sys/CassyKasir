package id.cassy.kasir.antarmuka.riwayat

import id.cassy.kasir.ranah.kasuspenggunaan.AmatiRiwayatTransaksi
import id.cassy.kasir.ranah.model.ItemKeranjang
import id.cassy.kasir.ranah.model.Produk
import id.cassy.kasir.ranah.model.Transaksi
import id.cassy.kasir.ranah.repositori.RepositoriTransaksi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pengujian unit untuk [LayarRiwayatTransaksiViewModel].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PengujianLayarRiwayatTransaksiViewModel {

    private val dispatcher = StandardTestDispatcher()
    private val testScope = kotlinx.coroutines.test.TestScope(dispatcher)
    private val repositoriPalsu = RepositoriTransaksiPalsu()
    private val useCase = AmatiRiwayatTransaksi(repositoriPalsu)
    private lateinit var viewModel: LayarRiwayatTransaksiViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = LayarRiwayatTransaksiViewModel(useCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun awalMuatMenampilkanStatusMemuat() = runTest {
        val statusMuat = viewModel.modelTampilan.value.statusMuat
        assertTrue(statusMuat is StatusMuatRiwayatTransaksi.Memuat)
    }

    @Test
    fun berhasilMemuatDaftarTransaksi() = testScope.runTest {
        // Start collection in the background to keep the StateFlow active
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.modelTampilan.collect()
        }

        val daftarTransaksi = listOf(
            Transaksi(
                id = "TRX-1",
                daftarItemKeranjang = listOf(
                    ItemKeranjang(
                        produk = Produk(id = "P1", nama = "Kopi", harga = 10_000L, stokTersedia = 10),
                        jumlah = 1
                    )
                ),
                uangDibayar = 10_000L,
                waktuTransaksiEpochMili = System.currentTimeMillis()
            )
        )
        repositoriPalsu.emit(daftarTransaksi)
        advanceUntilIdle()

        val statusMuat = viewModel.modelTampilan.value.statusMuat
        if (statusMuat !is StatusMuatRiwayatTransaksi.Berhasil) {
            val pesan = when (statusMuat) {
                is StatusMuatRiwayatTransaksi.Gagal -> "Status Gagal: ${statusMuat.deskripsi}"
                is StatusMuatRiwayatTransaksi.Kosong -> "Status Kosong: ${statusMuat.deskripsi}"
                StatusMuatRiwayatTransaksi.Memuat -> "Status Memuat"
            }
            job.cancel()
            throw AssertionError("Seharusnya Status Berhasil, tapi: $pesan")
        }
        val berhasil = statusMuat as StatusMuatRiwayatTransaksi.Berhasil
        assertEquals(1, berhasil.daftarRingkasanTransaksi.size)
        assertEquals("TRX-1", berhasil.daftarRingkasanTransaksi.first().transaksiId)
        job.cancel()
    }

    @Test
    fun pencarianBerhasilMenyaringTransaksi() = testScope.runTest {
        // Start collection in the background to keep the StateFlow active
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.modelTampilan.collect()
        }

        val daftarTransaksi = listOf(
            Transaksi(
                id = "TRX-KOPI",
                daftarItemKeranjang = listOf(
                    ItemKeranjang(
                        produk = Produk(id = "P1", nama = "Kopi", harga = 10_000L, stokTersedia = 10),
                        jumlah = 1
                    )
                ),
                uangDibayar = 10_000L,
                waktuTransaksiEpochMili = System.currentTimeMillis()
            ),
            Transaksi(
                id = "TRX-TEH",
                daftarItemKeranjang = listOf(
                    ItemKeranjang(
                        produk = Produk(id = "P2", nama = "Teh", harga = 5_000L, stokTersedia = 10),
                        jumlah = 1
                    )
                ),
                uangDibayar = 5_000L,
                waktuTransaksiEpochMili = System.currentTimeMillis()
            )
        )
        repositoriPalsu.emit(daftarTransaksi)
        advanceUntilIdle()

        viewModel.perbaruiKataKunciPencarian("Kopi")
        advanceUntilIdle() // Menunggu debounce 250ms

        val statusMuat = viewModel.modelTampilan.value.statusMuat
        assertTrue("Status seharusnya Berhasil, tapi: $statusMuat", statusMuat is StatusMuatRiwayatTransaksi.Berhasil)
        val berhasil = statusMuat as StatusMuatRiwayatTransaksi.Berhasil
        assertEquals(1, berhasil.daftarRingkasanTransaksi.size)
        assertEquals("TRX-KOPI", berhasil.daftarRingkasanTransaksi.first().transaksiId)
        job.cancel()
    }

    private class RepositoriTransaksiPalsu : RepositoriTransaksi {
        private val flow = MutableStateFlow<List<Transaksi>>(emptyList())

        fun emit(daftar: List<Transaksi>) {
            flow.value = daftar
        }

        override fun amatiSemuaTransaksi(): Flow<List<Transaksi>> = flow

        override suspend fun simpanTransaksi(transaksi: Transaksi) {}
        override suspend fun simpanTransaksiDanKurangiStok(transaksi: Transaksi) {}
        override fun amatiTransaksiBerdasarkanIdentitas(identitasTransaksi: String): Flow<Transaksi?> = throw NotImplementedError()
        override suspend fun ambilTransaksiBerdasarkanIdentitas(identitasTransaksi: String): Transaksi? = throw NotImplementedError()
    }
}
