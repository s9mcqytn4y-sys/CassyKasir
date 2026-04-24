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
 * Layar placeholder untuk riwayat transaksi.
 *
 * Pada scope ini layar masih statis.
 * Data riwayat asli akan kita bangun saat masuk scope data lokal.
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
 * Konten kosong untuk layar riwayat transaksi.
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
