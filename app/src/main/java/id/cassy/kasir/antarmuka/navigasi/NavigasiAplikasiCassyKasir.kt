package id.cassy.kasir.antarmuka.navigasi

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import id.cassy.kasir.antarmuka.detail.LayarDetailProduk
import id.cassy.kasir.antarmuka.detail.LayarDetailProdukViewModel
import id.cassy.kasir.antarmuka.riwayat.LayarRiwayatTransaksi
import id.cassy.kasir.antarmuka.utama.LayarUtamaKasir
import id.cassy.kasir.antarmuka.utama.LayarUtamaKasirViewModel

/**
 * Root navigasi aplikasi CassyKasir.
 *
 * File ini hanya bertanggung jawab pada:
 * - pemetaan destination
 * - pembuatan ViewModel per layar
 * - perpindahan antar layar
 */
@Composable
fun NavigasiAplikasiCassyKasir() {
    val pengendaliNavigasi = rememberNavController()

    NavHost(
        navController = pengendaliNavigasi,
        startDestination = TujuanNavigasiKasir.KasirUtama.rute,
    ) {
        composable(
            route = TujuanNavigasiKasir.KasirUtama.rute,
        ) {
            val layarUtamaKasirViewModel: LayarUtamaKasirViewModel = viewModel()
            val modelTampilan = layarUtamaKasirViewModel.modelTampilan.collectAsStateWithLifecycle()

            LayarUtamaKasir(
                modelTampilan = modelTampilan.value,
                saatAksiDikirim = layarUtamaKasirViewModel::tanganiAksi,
                alurEfek = layarUtamaKasirViewModel.efek,
                saatBukaRiwayatTransaksi = {
                    pengendaliNavigasi.bukaRiwayatTransaksi()
                },
                saatBukaDetailProduk = { produkId ->
                    pengendaliNavigasi.bukaDetailProduk(produkId)
                },
            )
        }

        composable(
            route = TujuanNavigasiKasir.RiwayatTransaksi.rute,
        ) {
            LayarRiwayatTransaksi(
                saatKembali = {
                    pengendaliNavigasi.navigateUp()
                },
            )
        }

        composable(
            route = TujuanNavigasiKasir.DetailProduk.rute,
            arguments = listOf(
                navArgument(TujuanNavigasiKasir.DetailProduk.namaArgumenProdukId) {
                    type = NavType.StringType
                },
            ),
        ) {
            val layarDetailProdukViewModel: LayarDetailProdukViewModel = viewModel()
            val modelTampilan = layarDetailProdukViewModel.modelTampilan.collectAsStateWithLifecycle()

            LayarDetailProduk(
                modelTampilan = modelTampilan.value,
                saatKembali = {
                    pengendaliNavigasi.navigateUp()
                },
                saatCobaLagi = layarDetailProdukViewModel::cobaLagi,
            )
        }
    }
}
