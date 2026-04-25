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
import androidx.compose.material3.Button
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
import id.cassy.kasir.antarmuka.komponen.StatusKosongSederhana

/**
 * Layar riwayat transaksi yang menampilkan daftar aktivitas penjualan yang telah selesai.
 *
 * Layar ini bersifat stateless, menerima data via [modelTampilan] dan mengirimkan
 * aksi pengguna melalui berbagai callback.
 *
 * @param modelTampilan Status data yang akan ditampilkan di layar.
 * @param saatKembali Callback untuk navigasi balik ke layar sebelumnya.
 * @param saatBukaDetailTransaksi Callback untuk membuka detail satu transaksi berdasarkan ID.
 * @param saatCobaMuatUlang Callback untuk memicu pemuatan ulang data jika terjadi kegagalan.
 * @param modifier Modifikasi tata letak opsional.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayarRiwayatTransaksi(
    modelTampilan: ModelTampilanRiwayatTransaksi,
    saatKembali: () -> Unit,
    saatBukaDetailTransaksi: (String) -> Unit,
    saatCobaMuatUlang: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = modelTampilan.judulLayar,
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
        when (val statusMuat = modelTampilan.statusMuat) {
            StatusMuatRiwayatTransaksi.Memuat -> {
                KontenMemuatRiwayat(
                    paddingDalam = paddingDalam,
                )
            }

            is StatusMuatRiwayatTransaksi.Berhasil -> {
                KontenRiwayatBerhasil(
                    paddingDalam = paddingDalam,
                    statusMuat = statusMuat,
                    saatBukaDetailTransaksi = saatBukaDetailTransaksi,
                )
            }

            is StatusMuatRiwayatTransaksi.Kosong -> {
                KontenRiwayatKosong(
                    paddingDalam = paddingDalam,
                    judul = statusMuat.judul,
                    deskripsi = statusMuat.deskripsi,
                )
            }

            is StatusMuatRiwayatTransaksi.Gagal -> {
                KontenRiwayatGagal(
                    paddingDalam = paddingDalam,
                    judul = statusMuat.judul,
                    deskripsi = statusMuat.deskripsi,
                    saatCobaMuatUlang = saatCobaMuatUlang,
                )
            }
        }
    }
}

/**
 * Konten yang ditampilkan saat data riwayat transaksi sedang dimuat.
 *
 * @param paddingDalam Padding dari Scaffold.
 * @param modifier Modifikasi tata letak.
 */
@Composable
private fun KontenMemuatRiwayat(
    paddingDalam: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingDalam)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Memuat riwayat transaksi...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

/**
 * Konten yang ditampilkan saat data riwayat transaksi berhasil dimuat.
 *
 * @param paddingDalam Padding dari Scaffold.
 * @param statusMuat Data transaksi yang berhasil dimuat.
 * @param saatBukaDetailTransaksi Callback untuk membuka detail transaksi.
 * @param modifier Modifikasi tata letak.
 */
@Composable
private fun KontenRiwayatBerhasil(
    paddingDalam: PaddingValues,
    statusMuat: StatusMuatRiwayatTransaksi.Berhasil,
    saatBukaDetailTransaksi: (String) -> Unit,
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
                    text = statusMuat.judulBagian,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = statusMuat.deskripsiBagian,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        items(
            items = statusMuat.daftarRingkasanTransaksi,
            key = { ringkasan -> ringkasan.transaksiId },
        ) { ringkasan ->
            KartuRingkasanTransaksiRiwayat(
                ringkasan = ringkasan,
                saatBukaDetailTransaksi = {
                    saatBukaDetailTransaksi(ringkasan.transaksiId)
                },
            )
        }
    }
}

/**
 * Komponen kartu untuk menampilkan ringkasan satu transaksi dalam daftar.
 *
 * @param ringkasan Data ringkasan transaksi.
 * @param saatBukaDetailTransaksi Callback saat kartu diklik atau tombol detail ditekan.
 * @param modifier Modifikasi tata letak.
 */
@Composable
private fun KartuRingkasanTransaksiRiwayat(
    ringkasan: RingkasanTransaksiRiwayat,
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
                text = ringkasan.transaksiId,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = ringkasan.labelWaktu,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = ringkasan.labelJumlahItem,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = ringkasan.labelTotalAkhir,
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

/**
 * Konten yang ditampilkan saat daftar riwayat transaksi kosong.
 *
 * @param paddingDalam Padding dari Scaffold.
 * @param judul Pesan judul kosong.
 * @param deskripsi Pesan deskripsi kosong.
 * @param modifier Modifikasi tata letak.
 */
@Composable
private fun KontenRiwayatKosong(
    paddingDalam: PaddingValues,
    judul: String,
    deskripsi: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingDalam)
            .padding(16.dp),
    ) {
        StatusKosongSederhana(
            judul = judul,
            deskripsi = deskripsi,
        )
    }
}

/**
 * Konten yang ditampilkan saat terjadi kegagalan dalam memuat riwayat transaksi.
 *
 * @param paddingDalam Padding dari Scaffold.
 * @param judul Pesan judul kegagalan.
 * @param deskripsi Pesan rincian kegagalan.
 * @param saatCobaMuatUlang Callback untuk mencoba memuat data kembali.
 * @param modifier Modifikasi tata letak.
 */
@Composable
private fun KontenRiwayatGagal(
    paddingDalam: PaddingValues,
    judul: String,
    deskripsi: String,
    saatCobaMuatUlang: () -> Unit,
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
            judul = judul,
            deskripsi = deskripsi,
        )

        Button(
            onClick = saatCobaMuatUlang,
        ) {
            Text(
                text = "Coba Lagi",
            )
        }
    }
}
