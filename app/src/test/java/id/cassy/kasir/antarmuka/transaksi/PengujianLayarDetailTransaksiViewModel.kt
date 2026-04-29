package id.cassy.kasir.antarmuka.transaksi

import androidx.lifecycle.SavedStateHandle
import id.cassy.kasir.ranah.kasuspenggunaan.AmatiTransaksiBerdasarkanIdentitas
import id.cassy.kasir.ranah.model.ItemKeranjang
import id.cassy.kasir.ranah.model.Produk
import id.cassy.kasir.ranah.model.Transaksi
import id.cassy.kasir.ranah.repositori.RepositoriTransaksi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
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

@OptIn(ExperimentalCoroutinesApi::class)
class PengujianLayarDetailTransaksiViewModel {

    private val dispatcher = StandardTestDispatcher()
    private val testScope = TestScope(dispatcher)
    private val repositoriPalsu = RepositoriTransaksiPalsu()
    private val amatiTransaksiBerdasarkanIdentitas = AmatiTransaksiBerdasarkanIdentitas(repositoriPalsu)

    private lateinit var viewModel: LayarDetailTransaksiViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buatViewModel(transaksiId: String) {
        val savedStateHandle = SavedStateHandle(mapOf("identitasTransaksi" to transaksiId))
        try {
            viewModel = LayarDetailTransaksiViewModel(
                savedStateHandle = savedStateHandle,
                amatiTransaksiBerdasarkanIdentitas = amatiTransaksiBerdasarkanIdentitas
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @Test
    fun berhasilMemuatDetailTransaksi() = testScope.runTest {
        val transaksiId = "TRX-123"
        buatViewModel(transaksiId)

        // Start collection
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.modelTampilan.collect()
        }

        val transaksi = Transaksi(
            id = transaksiId,
            daftarItemKeranjang = listOf(
                ItemKeranjang(
                    produk = Produk(id = "P1", nama = "Kopi", harga = 10_000L, stokTersedia = 10),
                    jumlah = 2
                )
            ),
            uangDibayar = 20_000L,
            waktuTransaksiEpochMili = System.currentTimeMillis()
        )

        repositoriPalsu.emit(transaksi)
        advanceUntilIdle()

        val statusMuat = viewModel.modelTampilan.value.statusMuat
        assertTrue("Status seharusnya Berhasil, tapi: $statusMuat", statusMuat is StatusMuatDetailTransaksi.Berhasil)
        val berhasil = statusMuat as StatusMuatDetailTransaksi.Berhasil
        assertEquals(transaksiId, berhasil.transaksiId)
        assertEquals(1, berhasil.daftarItem.size)
        assertEquals("Kopi", berhasil.daftarItem.first().namaProduk)

        job.cancel()
    }

    @Test
    fun transaksiTidakDitemukan() = testScope.runTest {
        val transaksiId = "TRX-404"
        buatViewModel(transaksiId)

        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.modelTampilan.collect()
        }

        // Emit null to simulate not found
        repositoriPalsu.emit(null)
        advanceUntilIdle()

        val statusMuat = viewModel.modelTampilan.value.statusMuat
        assertTrue("Status seharusnya Kosong, tapi: $statusMuat", statusMuat is StatusMuatDetailTransaksi.Kosong)

        job.cancel()
    }

    private class RepositoriTransaksiPalsu : RepositoriTransaksi {
        private val flow = MutableSharedFlow<Transaksi?>(replay = 1)

        suspend fun emit(transaksi: Transaksi?) {
            flow.emit(transaksi)
        }

        override fun amatiSemuaTransaksi(): Flow<List<Transaksi>> = throw NotImplementedError()

        override fun amatiTransaksiBerdasarkanIdentitas(identitasTransaksi: String): Flow<Transaksi?> {
            return flow
        }

        override suspend fun simpanTransaksi(transaksi: Transaksi) = throw NotImplementedError()

        override suspend fun simpanTransaksiDanKurangiStok(transaksi: Transaksi) = throw NotImplementedError()

        override suspend fun ambilTransaksiBerdasarkanIdentitas(identitasTransaksi: String): Transaksi? = throw NotImplementedError()
    }
}
