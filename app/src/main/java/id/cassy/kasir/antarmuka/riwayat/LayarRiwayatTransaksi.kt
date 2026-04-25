package id.cassy.kasir.antarmuka.riwayat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.cassy.kasir.ranah.contoh.RiwayatTransaksiContoh
import id.cassy.kasir.ranah.model.Transaksi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Layar riwayat transaksi yang menampilkan daftar aktivitas penjualan yang telah selesai.
 *
 * Pada scope ini data masih memakai contoh statis agar alur route
 * ke detail transaksi bisa dibangun dengan rapi sebelum Room masuk.
 *
 * @param saatKembali Callback untuk navigasi balik ke layar sebelumnya.
 * @param saatBukaDetailTransaksi Callback untuk membuka detail satu transaksi.
 * @param modifier Modifikasi tata letak opsional.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayarRiwayatTransaksi(
    saatKembali: () -> Unit,
    saatBukaDetailTransaksi: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val daftarRiwayat = RiwayatTransaksiContoh.daftarAwal()

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingDalam),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Riwayat penjualan",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "Mode awal memakai data contoh agar alur detail transaksi bisa dibangun dulu.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            items(
                items = daftarRiwayat,
                key = { transaksi -> transaksi.id },
            ) { transaksi ->
                KartuRiwayatTransaksiAwal(
                    transaksi = transaksi,
                    saatBukaDetailTransaksi = {
                        saatBukaDetailTransaksi(transaksi.id)
                    },
                )
            }
        }
    }
}

/**
 * Kartu informasi singkat transaksi untuk ditampilkan dalam daftar riwayat.
 */
@Composable
private fun KartuRiwayatTransaksiAwal(
    transaksi: Transaksi,
    saatBukaDetailTransaksi: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = saatBukaDetailTransaksi,
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.14f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = transaksi.id,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = formatWaktuTransaksi(transaksi.waktuTransaksiEpochMili),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${hitungJumlahItemTransaksi(transaksi)} item",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = hitungTotalTransaksiContoh(transaksi).sebagaiRupiahSederhana(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            OutlinedButton(
                onClick = saatBukaDetailTransaksi,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
            ) {
                Text(
                    text = "Lihat Detail Transaksi",
                )
            }
        }
    }
}

private fun hitungJumlahItemTransaksi(
    transaksi: Transaksi,
): Int {
    return transaksi.daftarItemKeranjang.sumOf { itemKeranjang ->
        itemKeranjang.jumlah
    }
}

private fun hitungTotalTransaksiContoh(
    transaksi: Transaksi,
): Long {
    val subtotal = transaksi.daftarItemKeranjang.sumOf { itemKeranjang ->
        itemKeranjang.produk.harga * itemKeranjang.jumlah
    }

    return (subtotal - transaksi.potongan + transaksi.biayaLayanan + transaksi.pajak)
        .coerceAtLeast(0)
}

private fun formatWaktuTransaksi(
    waktuEpochMili: Long,
): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
    return Instant.ofEpochMilli(waktuEpochMili)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}

private fun Long.sebagaiRupiahSederhana(): String {
    return "Rp$this"
}
