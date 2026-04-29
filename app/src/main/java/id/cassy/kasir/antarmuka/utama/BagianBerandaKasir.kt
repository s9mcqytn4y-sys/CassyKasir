package id.cassy.kasir.antarmuka.utama

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import id.cassy.kasir.ranah.model.StatusSinkronisasi

/**
 * Komponen identitas visual aplikasi yang menampilkan nama dan slogan.
 *
 * @param namaAplikasi Nama aplikasi yang akan ditampilkan.
 * @param sloganAplikasi Slogan atau deskripsi singkat aplikasi.
 * @param saatBukaRiwayatTransaksi Callback saat tombol riwayat diklik.
 * @param saatBukaKelolaProduk Callback saat tombol kelola produk diklik.
 * @param modifier Modifier untuk kustomisasi tata letak.
 */
@Composable
internal fun HeaderBerandaKasir(
    namaAplikasi: String,
    sloganAplikasi: String,
    saatBukaRiwayatTransaksi: () -> Unit,
    saatBukaKelolaProduk: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = namaAplikasi,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = sloganAplikasi,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        TextButton(
            onClick = saatBukaKelolaProduk,
            modifier = Modifier.heightIn(min = 48.dp),
        ) {
            Text(
                text = "Kelola",
            )
        }

        TextButton(
            onClick = saatBukaRiwayatTransaksi,
            modifier = Modifier.heightIn(min = 48.dp),
        ) {
            Text(
                text = "Riwayat",
            )
        }
    }
}

/**
 * Komponen ringkasan metrik performa kasir dalam bentuk kartu statistik.
 *
 * @param statusBeranda Data status beranda yang berisi metrik ringkasan.
 * @param modifier Modifier untuk kustomisasi tata letak.
 */
@Composable
internal fun RingkasanKasir(
    statusBeranda: StatusBerandaKasir,
    saatSinkronkanKatalogProduk: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            KartuStatistikKasir(
                judul = "Produk",
                nilai = statusBeranda.jumlahProdukTersedia.toString(),
                modifier = Modifier.weight(1f),
            )
            KartuStatistikKasir(
                judul = "Item",
                nilai = statusBeranda.jumlahItemKeranjang.toString(),
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            KartuStatistikKasir(
                judul = "Total",
                nilai = statusBeranda.totalBelanjaSementara,
                modifier = Modifier.weight(1f),
            )
            KartuStatusKatalogKasir(
                statusSinkronisasi = statusBeranda.statusSinkronisasi,
                labelAksiSinkronisasi = statusBeranda.labelAksiSinkronisasi,
                aksiSinkronisasiAktif = statusBeranda.aksiSinkronisasiAktif,
                saatSinkronkanKatalogProduk = saatSinkronkanKatalogProduk,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * Kartu statistik kecil untuk metrik tunggal.
 *
 * @param judul Label atau judul statistik.
 * @param nilai Nilai numerik atau teks statistik.
 * @param modifier Modifier untuk kustomisasi tata letak.
 */
@Composable
internal fun KartuStatistikKasir(
    judul: String,
    nilai: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = judul,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = nilai,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

/**
 * Kartu khusus untuk menampilkan status sinkronisasi data local-first.
 *
 * @param statusSinkronisasi Status sinkronisasi saat ini.
 * @param modifier Modifier untuk kustomisasi tata letak.
 */
@Composable
internal fun KartuStatusKatalogKasir(
    statusSinkronisasi: StatusSinkronisasi,
    labelAksiSinkronisasi: String,
    aksiSinkronisasiAktif: Boolean,
    saatSinkronkanKatalogProduk: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (judul, warnaLatar) = when (statusSinkronisasi) {
        StatusSinkronisasi.BelumPernah -> "Belum Sinkron" to MaterialTheme.colorScheme.errorContainer
        StatusSinkronisasi.SinkronLokal -> "Tersimpan Lokal" to MaterialTheme.colorScheme.secondaryContainer
        StatusSinkronisasi.SedangSinkron -> "Menyinkronkan..." to MaterialTheme.colorScheme.tertiaryContainer
        StatusSinkronisasi.Berhasil -> "Terbaru" to MaterialTheme.colorScheme.primaryContainer
        is StatusSinkronisasi.Gagal -> "Gagal" to MaterialTheme.colorScheme.errorContainer
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = warnaLatar,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (statusSinkronisasi is StatusSinkronisasi.SedangSinkron) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        strokeCap = StrokeCap.Round,
                    )
                }
            }

            Text(
                text = judul,
                style = MaterialTheme.typography.titleLarge,
            )

            TextButton(
                onClick = saatSinkronkanKatalogProduk,
                enabled = aksiSinkronisasiAktif,
                modifier = Modifier.heightIn(min = 48.dp),
            ) {
                Text(
                    text = labelAksiSinkronisasi,
                )
            }
        }
    }
}

/**
 * Banner informasi hasil akhir dari proses checkout.
 *
 * @param statusHasilCheckout Data hasil checkout yang berisi pesan sukses/gagal.
 * @param saatTutup Callback saat tombol tutup diklik.
 * @param modifier Modifier untuk kustomisasi tata letak.
 */
@Composable
internal fun KartuHasilCheckoutKasir(
    statusHasilCheckout: StatusHasilCheckoutKasir,
    saatTutup: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = statusHasilCheckout.judul,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )

                TextButton(
                    onClick = saatTutup,
                ) {
                    Text(
                        text = "Tutup",
                    )
                }
            }

            Text(
                text = statusHasilCheckout.deskripsi,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}
