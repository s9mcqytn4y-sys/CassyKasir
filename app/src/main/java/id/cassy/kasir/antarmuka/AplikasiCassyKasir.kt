package id.cassy.kasir.antarmuka

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import id.cassy.kasir.antarmuka.tema.TemaCassyKasir
import id.cassy.kasir.antarmuka.utama.LayarUtamaKasir
import id.cassy.kasir.antarmuka.utama.LayarUtamaKasirViewModel

/**
 * Root composable aplikasi.
 *
 * Tanggung jawab file ini sederhana:
 * - mengambil ViewModel
 * - membaca state layar secara lifecycle-aware
 * - meneruskan state dan aksi ke layar utama
 */
@Composable
fun AplikasiCassyKasir(
    layarUtamaKasirViewModel: LayarUtamaKasirViewModel = viewModel(),
) {
    val modelTampilan = layarUtamaKasirViewModel.modelTampilan.collectAsStateWithLifecycle()

    TemaCassyKasir(
        gunakanWarnaDinamis = false,
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            LayarUtamaKasir(
                modelTampilan = modelTampilan.value,
                saatAksiDikirim = layarUtamaKasirViewModel::tanganiAksi,
            )
        }
    }
}
