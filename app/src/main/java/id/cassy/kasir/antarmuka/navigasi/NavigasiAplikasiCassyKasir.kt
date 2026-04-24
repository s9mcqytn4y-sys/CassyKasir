package id.cassy.kasir.antarmuka.navigasi

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import id.cassy.kasir.antarmuka.riwayat.LayarRiwayatTransaksi
import id.cassy.kasir.antarmuka.utama.LayarUtamaKasir
import id.cassy.kasir.antarmuka.utama.LayarUtamaKasirViewModel

/**
 * Root navigasi aplikasi CassyKasir.
 *
 * File ini bertugas menghubungkan tujuan navigasi dengan composable layar.
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
    }
}
