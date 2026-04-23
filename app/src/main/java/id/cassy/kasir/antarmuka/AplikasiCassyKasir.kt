package id.cassy.kasir.antarmuka

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import id.cassy.kasir.antarmuka.tema.TemaCassyKasir
import id.cassy.kasir.antarmuka.utama.LayarUtamaKasir
import id.cassy.kasir.antarmuka.utama.LayarUtamaKasirViewModel

/**
 * Komposabel akar aplikasi yang mengatur inisialisasi ViewModel dan tema.
 *
 * Fungsi ini bertanggung jawab untuk:
 * - Menghubungkan [LayarUtamaKasirViewModel] dengan antarmuka.
 * - Mengamati perubahan status (state) secara lifecycle-aware.
 * - Menerapkan tema dasar aplikasi [TemaCassyKasir].
 *
 * @param layarUtamaKasirViewModel Sumber data dan logika bisnis untuk layar utama.
 */
@Composable
fun AplikasiCassyKasir(
    layarUtamaKasirViewModel: LayarUtamaKasirViewModel = viewModel(),
) {
    val modelTampilan by layarUtamaKasirViewModel.modelTampilan.collectAsStateWithLifecycle()

    TemaCassyKasir(
        gunakanWarnaDinamis = false,
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            LayarUtamaKasir(
                modelTampilan = modelTampilan,
                saatAksiDikirim = layarUtamaKasirViewModel::tanganiAksi,
                alurEfek = layarUtamaKasirViewModel.efek,
            )
        }
    }
}
