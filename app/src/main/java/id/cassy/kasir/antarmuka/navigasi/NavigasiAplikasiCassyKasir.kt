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
 * Tanggung jawab file ini:
 * - membuat NavController
 * - menentukan start destination
 * - menghubungkan setiap destination ke composable yang sesuai
 */
@Composable
fun NavigasiAplikasiCassyKasir() {
    val pengendaliNavigasi = rememberNavController()

    NavHost(
        navController = pengendaliNavigasi,
        startDestination = RuteNavigasiKasir.kasirUtama,
    ) {
        composable(
            route = RuteNavigasiKasir.kasirUtama,
        ) {
            val layarUtamaKasirViewModel: LayarUtamaKasirViewModel = viewModel()
            val modelTampilan = layarUtamaKasirViewModel.modelTampilan.collectAsStateWithLifecycle()

            LayarUtamaKasir(
                modelTampilan = modelTampilan.value,
                saatAksiDikirim = layarUtamaKasirViewModel::tanganiAksi,
                alurEfek = layarUtamaKasirViewModel.efek,
                saatBukaRiwayatTransaksi = {
                    pengendaliNavigasi.navigate(
                        RuteNavigasiKasir.riwayatTransaksi,
                    )
                },
            )
        }

        composable(
            route = RuteNavigasiKasir.riwayatTransaksi,
        ) {
            LayarRiwayatTransaksi(
                saatKembali = {
                    pengendaliNavigasi.navigateUp()
                },
            )
        }
    }
}
