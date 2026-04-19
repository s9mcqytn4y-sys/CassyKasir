package id.cassy.kasir.antarmuka.utama

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.cassy.kasir.antarmuka.komponen.StatusKosongSederhana
import id.cassy.kasir.antarmuka.tema.TemaCassyKasir
import id.cassy.kasir.ranah.contoh.KatalogProdukContoh
import id.cassy.kasir.ranah.model.Produk

/**
 * Representasi data status beranda untuk dashboard kasir.
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
 * Representasi data untuk status keranjang belanja statis.
 */
data class StatusKeranjangStatis(
    val judul: String,
    val deskripsi: String,
    val jumlahItem: String,
)

/**
 * Representasi data untuk rincian pembayaran akhir.
 */
data class RingkasanPembayaranStatis(
    val subtotal: String,
    val potongan: String,
    val pajak: String,
    val totalAkhir: String,
    val labelAksiUtama: String,
    val aksiUtamaAktif: Boolean,
)

/**
 * Layar utama antarmuka kasir yang bersifat adaptif.
 * Mampu menyesuaikan tata letak antara ponsel (panel tunggal) dan tablet (panel ganda).
 */
@Composable
fun LayarUtamaKasir(
    statusBeranda: StatusBerandaKasir,
    daftarProduk: List<Produk>,
    statusKeranjang: StatusKeranjangStatis,
    ringkasanPembayaran: RingkasanPembayaranStatis,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing, // Menghindari konten tertutup sistem UI
    ) { paddingKerangka ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingKerangka),
        ) {
            // Menggunakan ambang batas 840dp untuk menentukan mode tablet sesuai standar Android
            val gunakanDuaPanel = maxWidth >= 840.dp

            if (gunakanDuaPanel) {
                TataLetakTabletKasir(
                    statusBeranda = statusBeranda,
                    daftarProduk = daftarProduk,
                    statusKeranjang = statusKeranjang,
                    ringkasanPembayaran = ringkasanPembayaran,
                )
            } else {
                TataLetakPonselKasir(
                    statusBeranda = statusBeranda,
                    daftarProduk = daftarProduk,
                    statusKeranjang = statusKeranjang,
                    ringkasanPembayaran = ringkasanPembayaran,
                )
            }
        }
    }
}

/**
 * Tata letak khusus untuk perangkat dengan layar kecil (Ponsel).
 * Menampilkan konten dalam satu kolom gulir linear.
 */
@Composable
fun TataLetakPonselKasir(
    statusBeranda: StatusBerandaKasir,
    daftarProduk: List<Produk>,
    statusKeranjang: StatusKeranjangStatis,
    ringkasanPembayaran: RingkasanPembayaranStatis,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            HeaderBerandaKasir(
                namaAplikasi = statusBeranda.namaAplikasi,
                sloganAplikasi = statusBeranda.sloganAplikasi,
            )
        }

        item {
            RingkasanKasir(
                statusBeranda = statusBeranda,
            )
        }

        item {
            PanelKeranjangKosongKasir(
                statusKeranjang = statusKeranjang,
            )
        }

        item {
            PanelRingkasanPembayaranKasir(
                ringkasanPembayaran = ringkasanPembayaran,
            )
        }

        item {
            JudulBagianKasir(
                judul = "Katalog produk",
            )
        }

        items(
            items = daftarProduk,
            key = { it.id },
        ) { produk ->
            KartuProdukKasir(
                produk = produk,
            )
        }
    }
}

/**
 * Tata letak khusus untuk perangkat dengan layar lebar (Tablet/Desktop).
 * Membagi layar menjadi dua bagian: Katalog (Kiri) dan Dashboard Transaksi (Kanan).
 */
@Composable
fun TataLetakTabletKasir(
    statusBeranda: StatusBerandaKasir,
    daftarProduk: List<Produk>,
    statusKeranjang: StatusKeranjangStatis,
    ringkasanPembayaran: RingkasanPembayaranStatis,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Panel Kiri: Katalog Produk (Scrollable)
        LazyColumn(
            modifier = Modifier
                .weight(1.35f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                HeaderBerandaKasir(
                    namaAplikasi = statusBeranda.namaAplikasi,
                    sloganAplikasi = statusBeranda.sloganAplikasi,
                )
            }

            item {
                JudulBagianKasir(
                    judul = "Katalog produk",
                )
            }

            items(
                items = daftarProduk,
                key = { produk -> produk.id },
            ) { produk ->
                KartuProdukKasir(
                    produk = produk,
                )
            }
        }

        // Panel Kanan: Statistik dan Ringkasan (Scrollable)
        Column(
            modifier = Modifier
                .weight(0.95f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            RingkasanKasir(
                statusBeranda = statusBeranda,
            )

            PanelKeranjangKosongKasir(
                statusKeranjang = statusKeranjang,
            )

            PanelRingkasanPembayaranKasir(
                ringkasanPembayaran = ringkasanPembayaran,
            )
        }
    }
}

/**
 * Komponen Header yang menampilkan identitas aplikasi.
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
 * Teks label untuk memisahkan antar bagian informasi di aplikasi.
 */
@Composable
fun JudulBagianKasir(
    judul: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = judul,
        modifier = modifier,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

/**
 * Kumpulan kartu statistik untuk memantau performa transaksi secara cepat.
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
 * Kartu informasi tunggal yang menampilkan label dan nilai data.
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
 * Panel yang menampilkan status keranjang belanja.
 */
@Composable
fun PanelKeranjangKosongKasir(
    statusKeranjang: StatusKeranjangStatis,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        JudulBagianKasir(
            judul = "Keranjang",
        )

        StatusKosongSederhana(
            judul = statusKeranjang.judul,
            deskripsi = statusKeranjang.deskripsi,
        )

        Text(
            text = statusKeranjang.jumlahItem,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Kartu rincian biaya yang berisi subtotal, pajak, dan tombol aksi utama.
 */
@Composable
fun PanelRingkasanPembayaranKasir(
    ringkasanPembayaran: RingkasanPembayaranStatis,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            JudulBagianKasir(
                judul = "Ringkasan pembayaran",
            )

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
                label = "Total akhir",
                nilai = ringkasanPembayaran.totalAkhir,
                tonjolkan = true,
            )

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
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
 * Baris informasi tunggal dalam rincian pembayaran (Label di kiri, Nilai di kanan).
 */
@Composable
fun BarisRingkasanPembayaranKasir(
    modifier: Modifier = Modifier,
    label: String,
    nilai: String,
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
            color = if (tonjolkan) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
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

/**
 * Komponen kartu untuk menampilkan informasi produk di katalog.
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
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.14f),
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

            LencanaStokKasir(
                stokTersedia = produk.stokTersedia,
            )
        }
    }
}

/**
 * Label penanda jumlah stok yang tersedia untuk suatu produk.
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
 * Ekstensi sederhana untuk memformat nilai Long ke format Rupiah dasar.
 */
private fun Long.sebagaiRupiahSederhana(): String {
    return "Rp$this"
}

// --- Area Pratinjau (Preview) ---

@Preview(
    name = "Workspace tablet terang",
    showBackground = true,
    widthDp = 1280,
    heightDp = 800,
)
@Composable
private fun PreviewWorkspaceTabletTerang() {
    val daftarProduk = KatalogProdukContoh.daftarAwal()

    TemaCassyKasir(
        modeGelap = false,
    ) {
        LayarUtamaKasir(
            statusBeranda = StatusBerandaKasir(
                namaAplikasi = "Cassy Kasir",
                sloganAplikasi = "Kasir Cepat untuk Usaha Hebat",
                jumlahProdukTersedia = daftarProduk.size,
                jumlahItemKeranjang = 0,
                totalBelanjaSementara = "Rp0",
                statusSinkronisasi = "Belum ada sinkronisasi",
            ),
            daftarProduk = daftarProduk,
            statusKeranjang = StatusKeranjangStatis(
                judul = "Keranjang masih kosong",
                deskripsi = "Pilih produk dari katalog untuk mulai transaksi.",
                jumlahItem = "0 item",
            ),
            ringkasanPembayaran = RingkasanPembayaranStatis(
                subtotal = "Rp0",
                potongan = "Rp0",
                pajak = "Rp0",
                totalAkhir = "Rp0",
                labelAksiUtama = "Pilih produk dulu",
                aksiUtamaAktif = false,
            ),
        )
    }
}

@Preview(
    name = "Workspace ponsel gelap",
    showBackground = true,
    widthDp = 411,
    heightDp = 891,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewWorkspacePonselGelap() {
    val daftarProduk = KatalogProdukContoh.daftarAwal()

    TemaCassyKasir(
        modeGelap = true,
    ) {
        LayarUtamaKasir(
            statusBeranda = StatusBerandaKasir(
                namaAplikasi = "Cassy Kasir",
                sloganAplikasi = "Kasir Cepat untuk Usaha Hebat",
                jumlahProdukTersedia = daftarProduk.size,
                jumlahItemKeranjang = 0,
                totalBelanjaSementara = "Rp0",
                statusSinkronisasi = "Belum ada sinkronisasi",
            ),
            daftarProduk = daftarProduk,
            statusKeranjang = StatusKeranjangStatis(
                judul = "Keranjang masih kosong",
                deskripsi = "Pilih produk dari katalog untuk mulai transaksi.",
                jumlahItem = "0 item",
            ),
            ringkasanPembayaran = RingkasanPembayaranStatis(
                subtotal = "Rp0",
                potongan = "Rp0",
                pajak = "Rp0",
                totalAkhir = "Rp0",
                labelAksiUtama = "Pilih produk dulu",
                aksiUtamaAktif = false,
            ),
        )
    }
}
