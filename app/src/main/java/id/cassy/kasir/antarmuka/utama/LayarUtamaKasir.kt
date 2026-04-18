package id.cassy.kasir.antarmuka.utama

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class StatusBerandaKasir(
    val namaAplikasi: String,
    val sloganAplikasi: String,
    val jumlahProdukTersedia: Int,
    val jumlahItemKeranjang: Int,
    val totalBelanjaSementara: String,
    val statusSinkronisasi: String,
)

@Composable
fun LayarUtamaKasir(
    statusBeranda: StatusBerandaKasir,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = statusBeranda.namaAplikasi,
            style = MaterialTheme.typography.headlineMedium,
        )

        Text(
            text = statusBeranda.sloganAplikasi,
            style = MaterialTheme.typography.bodyLarge,
        )

        KartuRingkasanKasir(
            judul = "Ringkasan kasir",
            isi = {
                KolomInformasiKasir(
                    label = "Produk tersedia",
                    nilai = statusBeranda.jumlahProdukTersedia.toString(),
                )
                KolomInformasiKasir(
                    label = "Item Keranjang",
                    nilai = statusBeranda.jumlahItemKeranjang.toString(),
                )
                KolomInformasiKasir(
                    label = "Total Belanja Sementara",
                    nilai = statusBeranda.totalBelanjaSementara,
                )
                KolomInformasiKasir(
                    label = "Sinkronisasi",
                    nilai = statusBeranda.statusSinkronisasi,
                )
            }
        )
    }
}

@Composable
fun KartuRingkasanKasir(
    judul: String,
    isi: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = judul,
                style = MaterialTheme.typography.titleMedium,
            )
            isi()
        }
    }
}

@Composable
fun KolomInformasiKasir(
    label: String,
    nilai: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = nilai,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
