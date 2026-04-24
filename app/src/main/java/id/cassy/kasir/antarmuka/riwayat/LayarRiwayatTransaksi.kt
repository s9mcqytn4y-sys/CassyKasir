package id.cassy.kasir.antarmuka.riwayat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.cassy.kasir.antarmuka.komponen.StatusKosongSederhana

/**
 * Layar riwayat transaksi yang menampilkan daftar aktivitas penjualan yang telah selesai.
 *
 * Saat ini layar ini masih bersifat statis (placeholder) dan akan dihubungkan
 * ke database lokal pada tahap pengembangan selanjutnya.
 *
 * @param saatKembali Callback untuk navigasi balik ke layar sebelumnya.
 * @param modifier Modifikasi tata letak opsional.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayarRiwayatTransaksi(
    saatKembali: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Riwayat Transaksi",
                    )
                },
                navigationIcon = {
                    TextButton(
                        onClick = saatKembali,
                    ) {
                        Text(
                            text = "Kembali",
                        )
                    }
                },
            )
        },
    ) { paddingDalam ->
        KontenRiwayatTransaksiKosong(
            paddingDalam = paddingDalam,
        )
    }
}

/**
 * Merender tampilan status kosong untuk layar riwayat transaksi.
 *
 * @param paddingDalam Jarak aman dari kerangka Scaffold.
 * @param modifier Modifikasi tata letak opsional.
 */
@Composable
private fun KontenRiwayatTransaksiKosong(
    paddingDalam: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingDalam)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Riwayat penjualan",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )

        StatusKosongSederhana(
            judul = "Belum ada riwayat transaksi",
            deskripsi = "Riwayat transaksi akan tampil di sini setelah scope data lokal selesai dibangun.",
        )
    }
}
