package id.cassy.kasir.antarmuka.utama

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.cassy.kasir.antarmuka.tema.TemaCassyKasir
import id.cassy.kasir.ranah.contoh.KatalogProdukContoh
import id.cassy.kasir.ranah.model.Produk

/**
 * Model data untuk merepresentasikan status ringkasan di beranda kasir.
 */
data class StatusBerandaKasir(
    val namaAplikasi: String,
    val sloganAplikasi: String,
    val jumlahProdukTersedia: Int,
    val jumlahItemKeranjang: Int,
    val totalBelanjaSementara: String,
    val statusSinkronisasi: String,
)

/**
 * Komponen Layar Utama yang menampilkan daftar produk dan ringkasan transaksi.
 * Mendukung penyesuaian otomatis terhadap status bar sistem (Edge-to-Edge).
 */
@Composable
fun LayarUtamaKasir(
    statusBeranda: StatusBerandaKasir,
    daftarProduk: List<Produk>,
    modifier: Modifier = Modifier,
) {
    // Mengambil insets status bar untuk menghindari konten terpotong di bagian atas
    val paddingStatusSistem = WindowInsets.statusBars.asPaddingValues()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp + paddingStatusSistem.calculateTopPadding(),
            end = 16.dp,
            bottom = 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Bagian Header Aplikasi
        item {
            HeaderBerandaKasir(
                namaAplikasi = statusBeranda.namaAplikasi,
                sloganAplikasi = statusBeranda.sloganAplikasi,
            )
        }

        // Bagian Ringkasan Statistik
        item {
            RingkasanKasir(
                statusBeranda = statusBeranda,
            )
        }

        // Label Katalog
        item {
            Text(
                text = "Katalog produk",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        // Daftar Produk Interaktif
        items(
            items = daftarProduk,
            key = { produk -> produk.id },
        ) { produk ->
            KartuProdukKasir(
                produk = produk,
            )
        }
    }
}

/**
 * Menampilkan identitas aplikasi dan slogan di bagian atas layar.
 */
@Composable
fun HeaderBerandaKasir(
    namaAplikasi: String,
    sloganAplikasi: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
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
}

/**
 * Menampilkan baris statistik ringkasan dalam bentuk kartu (grid sederhana).
 */
@Composable
fun RingkasanKasir(
    statusBeranda: StatusBerandaKasir,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Baris Pertama: Stok dan Keranjang
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            KartuStatistikKasir(
                judul = "Produk tersedia",
                nilai = statusBeranda.jumlahProdukTersedia.toString(),
                modifier = Modifier.weight(1f),
            )
            KartuStatistikKasir(
                judul = "Item keranjang",
                nilai = statusBeranda.jumlahItemKeranjang.toString(),
                modifier = Modifier.weight(1f),
            )
        }

        // Baris Kedua: Nominal dan Sinkronisasi
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            KartuStatistikKasir(
                judul = "Total sementara",
                nilai = statusBeranda.totalBelanjaSementara,
                modifier = Modifier.weight(1f),
            )
            KartuStatistikKasir(
                judul = "Sinkronisasi",
                nilai = statusBeranda.statusSinkronisasi,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * Komponen kartu individu untuk menampilkan data statistik tunggal.
 */
@Composable
fun KartuStatistikKasir(
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
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

/**
 * Menampilkan informasi produk individu dalam daftar katalog.
 */
@Composable
fun KartuProdukKasir(
    produk: Produk,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = produk.nama,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = produk.harga.sebagaiRupiahSederhana(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Label indikator stok
            LencanaStokKasir(
                stokTersedia = produk.stokTersedia,
            )
        }
    }
}

/**
 * Lencana kecil untuk menunjukkan sisa stok produk.
 */
@Composable
fun LencanaStokKasir(
    stokTersedia: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.small,
    ) {
        Text(
            text = "Stok $stokTersedia",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

/**
 * Ekstensi pembantu untuk format mata uang sederhana.
 */
private fun Long.sebagaiRupiahSederhana(): String {
    return "Rp$this"
}

// --- Preview Area ---

@Preview(
    name = "Beranda tablet terang",
    showBackground = true,
    widthDp = 1280,
    heightDp = 800,
)
@Composable
private fun PreviewBerandaTabletTerang() {
    val daftarProduk = KatalogProdukContoh.daftarAwal()
    val statusBeranda = StatusBerandaKasir(
        namaAplikasi = "Cassy Kasir",
        sloganAplikasi = "Kasir Cepat untuk Usaha Hebat",
        jumlahProdukTersedia = daftarProduk.size,
        jumlahItemKeranjang = 0,
        totalBelanjaSementara = "Rp0",
        statusSinkronisasi = "Belum ada sinkronisasi",
    )

    TemaCassyKasir(
        modeGelap = false,
    ) {
        LayarUtamaKasir(
            statusBeranda = statusBeranda,
            daftarProduk = daftarProduk,
        )
    }
}
