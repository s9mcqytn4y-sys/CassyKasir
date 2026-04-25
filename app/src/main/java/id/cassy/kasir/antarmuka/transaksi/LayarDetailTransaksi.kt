package id.cassy.kasir.antarmuka.transaksi

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.cassy.kasir.antarmuka.komponen.StatusKosongSederhana
import id.cassy.kasir.ranah.contoh.RiwayatTransaksiContoh
import id.cassy.kasir.ranah.model.Transaksi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Layar detail transaksi yang menampilkan rincian lengkap dari sebuah nota penjualan.
 *
 * Pada scope ini layar masih menggunakan data contoh statis.
 * Tujuan utamanya adalah membangun route detail transaksi
 * dengan kontrak argumen yang sehat.
 *
 * @param transaksiId ID unik transaksi yang ingin ditampilkan.
 * @param saatKembali Callback untuk navigasi balik.
 * @param modifier Modifikasi tata letak opsional.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayarDetailTransaksi(
    transaksiId: String,
    saatKembali: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val transaksi = remember(transaksiId) {
        RiwayatTransaksiContoh.temukanBerdasarkanId(transaksiId)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Transaksi",
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
        if (transaksi == null) {
            KontenTransaksiTidakDitemukan(
                paddingDalam = paddingDalam,
                transaksiId = transaksiId,
            )
        } else {
            KontenDetailTransaksi(
                paddingDalam = paddingDalam,
                transaksi = transaksi,
            )
        }
    }
}

/**
 * Merender isi detail transaksi saat data berhasil ditemukan.
 */
@Composable
private fun KontenDetailTransaksi(
    paddingDalam: PaddingValues,
    transaksi: Transaksi,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
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
                    text = transaksi.id,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = formatWaktuTransaksi(transaksi.waktuTransaksiEpochMili),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            KartuRingkasanTransaksi(
                transaksi = transaksi,
            )
        }

        item {
            Text(
                text = "Daftar item",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        items(
            items = transaksi.daftarItemKeranjang,
            key = { itemKeranjang -> itemKeranjang.produk.id },
        ) { itemKeranjang ->
            Card(
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
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = itemKeranjang.produk.nama,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = hitungSubtotalItem(itemKeranjang.produk.harga, itemKeranjang.jumlah)
                                .sebagaiRupiahSederhana(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    Text(
                        text = "${itemKeranjang.jumlah} x ${itemKeranjang.produk.harga.sebagaiRupiahSederhana()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        transaksi.catatan?.takeIf { it.isNotBlank() }?.let { catatan ->
            item {
                Card(
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
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Catatan transaksi",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = catatan,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Kartu ringkasan finansial transaksi.
 */
@Composable
private fun KartuRingkasanTransaksi(
    transaksi: Transaksi,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
            BarisRingkasan(
                label = "Jumlah item",
                nilai = "${hitungJumlahItemTransaksi(transaksi)} item",
            )
            BarisRingkasan(
                label = "Subtotal",
                nilai = hitungSubtotalTransaksi(transaksi).sebagaiRupiahSederhana(),
            )
            BarisRingkasan(
                label = "Potongan",
                nilai = transaksi.potongan.sebagaiRupiahSederhana(),
            )
            BarisRingkasan(
                label = "Biaya layanan",
                nilai = transaksi.biayaLayanan.sebagaiRupiahSederhana(),
            )
            BarisRingkasan(
                label = "Pajak",
                nilai = transaksi.pajak.sebagaiRupiahSederhana(),
            )

            HorizontalDivider()

            BarisRingkasan(
                label = "Total akhir",
                nilai = hitungTotalTransaksiContoh(transaksi).sebagaiRupiahSederhana(),
                tonjolkan = true,
            )
            BarisRingkasan(
                label = "Uang dibayar",
                nilai = transaksi.uangDibayar.sebagaiRupiahSederhana(),
                tonjolkan = true,
            )
        }
    }
}

@Composable
private fun BarisRingkasan(
    label: String,
    nilai: String,
    modifier: Modifier = Modifier,
    tonjolkan: Boolean = false,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = if (tonjolkan) {
                MaterialTheme.typography.titleMedium
            } else {
                MaterialTheme.typography.bodyLarge
            },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = nilai,
            style = if (tonjolkan) {
                MaterialTheme.typography.titleMedium
            } else {
                MaterialTheme.typography.bodyLarge
            },
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun KontenTransaksiTidakDitemukan(
    paddingDalam: PaddingValues,
    transaksiId: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingDalam)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        StatusKosongSederhana(
            judul = "Transaksi tidak ditemukan",
            deskripsi = "Transaksi dengan id $transaksiId belum tersedia pada data contoh saat ini.",
        )
    }
}

private fun hitungJumlahItemTransaksi(
    transaksi: Transaksi,
): Int {
    return transaksi.daftarItemKeranjang.sumOf { itemKeranjang ->
        itemKeranjang.jumlah
    }
}

private fun hitungSubtotalTransaksi(
    transaksi: Transaksi,
): Long {
    return transaksi.daftarItemKeranjang.sumOf { itemKeranjang ->
        itemKeranjang.produk.harga * itemKeranjang.jumlah
    }
}

private fun hitungTotalTransaksiContoh(
    transaksi: Transaksi,
): Long {
    return (
        hitungSubtotalTransaksi(transaksi) -
            transaksi.potongan +
            transaksi.biayaLayanan +
            transaksi.pajak
        ).coerceAtLeast(0)
}

private fun hitungSubtotalItem(
    hargaProduk: Long,
    jumlah: Int,
): Long {
    return hargaProduk * jumlah
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
