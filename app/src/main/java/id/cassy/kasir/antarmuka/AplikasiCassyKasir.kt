package id.cassy.kasir.antarmuka

import androidx.compose.foundation.layout.fillMaxSize
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
 * Titik masuk utama untuk antarmuka aplikasi Cassy Kasir.
 *
 * Fungsi ini mengatur integrasi antara ViewModel dan layar utama,
 * serta menerapkan tema dasar aplikasi.
 *
 * @param layarUtamaKasirViewModel Instance ViewModel untuk mengelola status layar utama.
 */
@Composable
fun AplikasiCassyKasir(
    layarUtamaKasirViewModel: LayarUtamaKasirViewModel = viewModel(),
) {
    // Mengumpulkan status dari ViewModel dengan kesadaran akan siklus hidup (lifecycle-aware)
    val modelTampilan by layarUtamaKasirViewModel.modelTampilan.collectAsStateWithLifecycle()

    TemaCassyKasir(
        gunakanWarnaDinamis = false,
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            LayarUtamaKasir(
                modelTampilan = modelTampilan,
                saatKataKunciPencarianBerubah = layarUtamaKasirViewModel::ubahKataKunciPencarian,
                saatUbahVisibilitasRingkasanPembayaran = layarUtamaKasirViewModel::ubahVisibilitasRingkasanPembayaran,
            )
        }
    }
}
