package id.cassy.kasir.antarmuka.utama

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Bagian pembungkus panel ringkasan pembayaran.
 *
 * @param ringkasanPembayaran Data rincian biaya pembayaran.
 * @param apakahRingkasanPembayaranTampil Apakah rincian pembayaran sedang ditampilkan.
 * @param saatUbahVisibilitasRingkasanPembayaran Callback untuk beralih tampilan rincian pembayaran.
 * @param saatCheckout Callback saat tombol checkout diklik.
 * @param modifier Modifier untuk kustomisasi tata letak.
 */
@Composable
internal fun BagianRingkasanPembayaranKasir(
    ringkasanPembayaran: RingkasanPembayaranKasir,
    apakahRingkasanPembayaranTampil: Boolean,
    saatUbahVisibilitasRingkasanPembayaran: () -> Unit,
    saatCheckout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            JudulBagianKasir(
                judul = "Pembayaran",
            )

            TextButton(
                onClick = saatUbahVisibilitasRingkasanPembayaran,
            ) {
                Text(
                    text = if (apakahRingkasanPembayaranTampil) {
                        "Sembunyikan"
                    } else {
                        "Tampilkan"
                    },
                )
            }
        }

        if (apakahRingkasanPembayaranTampil) {
            PanelRingkasanPembayaranKasir(
                ringkasanPembayaran = ringkasanPembayaran,
                saatCheckout = saatCheckout,
            )
        }
    }
}

/**
 * Panel detail pembayaran.
 *
 * @param ringkasanPembayaran Data rincian biaya pembayaran.
 * @param saatCheckout Callback saat tombol checkout diklik.
 * @param modifier Modifier untuk kustomisasi tata letak.
 */
@Composable
internal fun PanelRingkasanPembayaranKasir(
    ringkasanPembayaran: RingkasanPembayaranKasir,
    saatCheckout: () -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            BarisRingkasanPembayaranKasir(
                label = "Subtotal",
                nilai = ringkasanPembayaran.subtotal,
            )
            BarisRingkasanPembayaranKasir(
                label = "Potongan",
                nilai = ringkasanPembayaran.potongan,
            )
            BarisRingkasanPembayaranKasir(
                label = "Pajak",
                nilai = ringkasanPembayaran.pajak,
            )

            HorizontalDivider()

            BarisRingkasanPembayaranKasir(
                label = "Total",
                nilai = ringkasanPembayaran.totalAkhir,
                tonjolkan = true,
            )

            Button(
                onClick = saatCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                enabled = ringkasanPembayaran.aksiUtamaAktif,
            ) {
                Text(
                    text = ringkasanPembayaran.labelAksiUtama,
                )
            }
        }
    }
}

/**
 * Satu baris informasi pembayaran.
 *
 * @param label Teks label biaya (misal: Subtotal).
 * @param nilai Teks nilai biaya yang diformat.
 * @param modifier Modifier untuk kustomisasi tata letak.
 * @param tonjolkan Apakah teks harus ditampilkan lebih tebal/besar.
 */
@Composable
internal fun BarisRingkasanPembayaranKasir(
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
                MaterialTheme.typography.titleLarge
            } else {
                MaterialTheme.typography.bodyLarge
            },
            color = if (tonjolkan) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        )

        Text(
            text = nilai,
            style = if (tonjolkan) {
                MaterialTheme.typography.titleLarge
            } else {
                MaterialTheme.typography.bodyLarge
            },
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

/**
 * Menampilkan pesan konfirmasi sebelum transaksi diproses.
 *
 * @param statusKonfirmasiCheckout Data status dialog konfirmasi.
 * @param saatBatalkan Callback saat tombol batal atau area luar dialog diklik.
 * @param saatKonfirmasi Callback saat tombol konfirmasi bayar diklik.
 */
@Composable
internal fun DialogKonfirmasiCheckoutKasir(
    statusKonfirmasiCheckout: StatusKonfirmasiCheckoutKasir,
    saatBatalkan: () -> Unit,
    saatKonfirmasi: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = saatBatalkan,
        title = {
            Text(
                text = statusKonfirmasiCheckout.judul,
            )
        },
        text = {
            Text(
                text = statusKonfirmasiCheckout.deskripsi,
            )
        },
        confirmButton = {
            Button(
                onClick = saatKonfirmasi,
            ) {
                Text(
                    text = statusKonfirmasiCheckout.labelKonfirmasi,
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = saatBatalkan,
            ) {
                Text(
                    text = "Batal",
                )
            }
        },
    )
}
