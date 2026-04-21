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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.cassy.kasir.antarmuka.komponen.StatusKosongSederhana
import id.cassy.kasir.antarmuka.tema.TemaCassyKasir
import id.cassy.kasir.ranah.contoh.KatalogProdukContoh
import id.cassy.kasir.ranah.model.Produk

/**
 * Representasi status data untuk tampilan beranda kasir.
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
 * Status statis untuk menampilkan kondisi keranjang belanja.
 */
data class StatusKeranjangStatis(
    val judul: String,
    val deskripsi: String,
    val jumlahItem: String,
)

/**
 * Data ringkasan pembayaran yang ditampilkan pada panel pembayaran.
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
 * Komposabel utama untuk Layar Kasir.
 * Mengatur logika pencarian, visibilitas ringkasan, dan memilih tata letak berdasarkan ukuran layar.
 */
@Composable
fun LayarUtamaKasir(
    statusBeranda: StatusBerandaKasir,
    daftarProduk: List<Produk>,
    statusKeranjang: StatusKeranjangStatis,
    ringkasanPembayaran: RingkasanPembayaranStatis,
    modifier: Modifier = Modifier,
) {
    // State untuk menyimpan input pencarian pengguna
    var kataKunciPencarian by rememberSaveable { mutableStateOf("") }

    // State untuk mengatur apakah panel ringkasan pembayaran terbuka atau tertutup
    var apakahRingkasanPembayaranTampil by remember { mutableStateOf(true) }

    // Filter daftar produk secara reaktif berdasarkan kata kunci
    val daftarProdukTersaring = remember(daftarProduk, kataKunciPencarian) {
        val kataKunciBersih = kataKunciPencarian.trim()
        if (kataKunciBersih.isBlank()) {
            daftarProduk
        } else {
            daftarProduk.filter { produk ->
                produk.nama.contains(kataKunciBersih, ignoreCase = true)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { paddingKerangka ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingKerangka),
        ) {
            // Tentukan tata letak berdasarkan lebar layar (Tablet vs Ponsel)
            val gunakanDuaPanel = maxWidth >= 840.dp

            if (gunakanDuaPanel) {
                TataLetakTabletKasir(
                    statusBeranda = statusBeranda,
                    daftarProdukTersaring = daftarProdukTersaring,
                    statusKeranjang = statusKeranjang,
                    ringkasanPembayaran = ringkasanPembayaran,
                    kataKunciPencarian = kataKunciPencarian,
                    saatKataKunciPencarianBerubah = { kataKunciPencarian = it },
                    apakahRingkasanPembayaranTampil = apakahRingkasanPembayaranTampil,
                    saatUbahVisibilitasRingkasanPembayaran = {
                        apakahRingkasanPembayaranTampil = !apakahRingkasanPembayaranTampil
                    },
                )
            } else {
                TataLetakPonselKasir(
                    statusBeranda = statusBeranda,
                    daftarProdukTersaring = daftarProdukTersaring,
                    statusKeranjang = statusKeranjang,
                    ringkasanPembayaran = ringkasanPembayaran,
                    kataKunciPencarian = kataKunciPencarian,
                    saatKataKunciPencarianBerubah = { kataKunciPencarian = it },
                    apakahRingkasanPembayaranTampil = apakahRingkasanPembayaranTampil,
                    saatUbahVisibilitasRingkasanPembayaran = {
                        apakahRingkasanPembayaranTampil = !apakahRingkasanPembayaranTampil
                    },
                )
            }
        }
    }
}

/**
 * Tata letak kolom tunggal yang dioptimalkan untuk perangkat layar kecil (ponsel).
 */
@Composable
fun TataLetakPonselKasir(
    statusBeranda: StatusBerandaKasir,
    daftarProdukTersaring: List<Produk>,
    statusKeranjang: StatusKeranjangStatis,
    ringkasanPembayaran: RingkasanPembayaranStatis,
    kataKunciPencarian: String,
    saatKataKunciPencarianBerubah: (String) -> Unit,
    apakahRingkasanPembayaranTampil: Boolean,
    saatUbahVisibilitasRingkasanPembayaran: () -> Unit,
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
            BagianRingkasanPembayaranKasir(
                ringkasanPembayaran = ringkasanPembayaran,
                apakahRingkasanPembayaranTampil = apakahRingkasanPembayaranTampil,
                saatUbahVisibilitasRingkasanPembayaran = saatUbahVisibilitasRingkasanPembayaran,
            )
        }

        item {
            BidangPencarianProdukKasir(
                nilaiPencarian = kataKunciPencarian,
                saatNilaiPencarianBerubah = saatKataKunciPencarianBerubah,
                jumlahHasil = daftarProdukTersaring.size,
            )
        }

        item {
            JudulBagianKasir(judul = "Katalog produk")
        }

        if (daftarProdukTersaring.isEmpty()) {
            item {
                StatusKosongSederhana(
                    judul = "Produk tidak ditemukan",
                    deskripsi = "Coba gunakan kata kunci lain untuk mencari produk.",
                )
            }
        } else {
            items(
                items = daftarProdukTersaring,
                key = { produk -> produk.id },
            ) { produk ->
                KartuProdukKasir(produk = produk)
            }
        }
    }
}

/**
 * Tata letak dua panel (katalog di kiri, ringkasan di kanan) untuk layar lebar (tablet).
 */
@Composable
fun TataLetakTabletKasir(
    statusBeranda: StatusBerandaKasir,
    daftarProdukTersaring: List<Produk>,
    statusKeranjang: StatusKeranjangStatis,
    ringkasanPembayaran: RingkasanPembayaranStatis,
    kataKunciPencarian: String,
    saatKataKunciPencarianBerubah: (String) -> Unit,
    apakahRingkasanPembayaranTampil: Boolean,
    saatUbahVisibilitasRingkasanPembayaran: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Sisi Kiri: Katalog dan Pencarian
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
                BidangPencarianProdukKasir(
                    nilaiPencarian = kataKunciPencarian,
                    saatNilaiPencarianBerubah = saatKataKunciPencarianBerubah,
                    jumlahHasil = daftarProdukTersaring.size,
                )
            }

            item {
                JudulBagianKasir(judul = "Katalog produk")
            }

            if (daftarProdukTersaring.isEmpty()) {
                item {
                    StatusKosongSederhana(
                        judul = "Produk tidak ditemukan",
                        deskripsi = "Coba gunakan kata kunci lain untuk mencari produk.",
                    )
                }
            } else {
                items(
                    items = daftarProdukTersaring,
                    key = { produk -> produk.id },
                ) { produk ->
                    KartuProdukKasir(produk = produk)
                }
            }
        }

        // Sisi Kanan: Statistik dan Ringkasan (Dapat discroll jika konten penuh)
        Column(
            modifier = Modifier
                .weight(0.95f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            RingkasanKasir(statusBeranda = statusBeranda)

            PanelKeranjangKosongKasir(statusKeranjang = statusKeranjang)

            BagianRingkasanPembayaranKasir(
                ringkasanPembayaran = ringkasanPembayaran,
                apakahRingkasanPembayaranTampil = apakahRingkasanPembayaranTampil,
                saatUbahVisibilitasRingkasanPembayaran = saatUbahVisibilitasRingkasanPembayaran,
            )
        }
    }
}

/**
 * Header yang menampilkan identitas aplikasi di bagian atas layar.
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
 * Komponen teks standar untuk judul setiap bagian di aplikasi.
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
 * Bidang input pencarian untuk menyaring daftar produk.
 */
@Composable
fun BidangPencarianProdukKasir(
    nilaiPencarian: String,
    saatNilaiPencarianBerubah: (String) -> Unit,
    jumlahHasil: Int,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = nilaiPencarian,
        onValueChange = saatNilaiPencarianBerubah,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text(text = "Cari produk") },
        placeholder = { Text(text = "Contoh: kopi, teh, roti") },
        supportingText = { Text(text = "Hasil ditemukan: $jumlahHasil") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    )
}

/**
 * Menampilkan kartu statistik ringkas seperti jumlah produk dan total sementara.
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
 * Kartu individual untuk menampilkan informasi statistik tunggal.
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
 * Menampilkan pesan ketika keranjang masih kosong.
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
        JudulBagianKasir(judul = "Keranjang")

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
 * Bagian pembungkus untuk ringkasan pembayaran yang menyertakan tombol toggle tampilkan/sembunyikan.
 */
@Composable
fun BagianRingkasanPembayaranKasir(
    ringkasanPembayaran: RingkasanPembayaranStatis,
    apakahRingkasanPembayaranTampil: Boolean,
    saatUbahVisibilitasRingkasanPembayaran: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            JudulBagianKasir(judul = "Ringkasan pembayaran")

            TextButton(onClick = saatUbahVisibilitasRingkasanPembayaran) {
                Text(
                    text = if (apakahRingkasanPembayaranTampil) "Sembunyikan" else "Tampilkan",
                )
            }
        }

        if (apakahRingkasanPembayaranTampil) {
            PanelRingkasanPembayaranKasir(ringkasanPembayaran = ringkasanPembayaran)
        }
    }
}

/**
 * Panel kartu yang berisi rincian harga, potongan, dan tombol aksi utama.
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
                Text(text = ringkasanPembayaran.labelAksiUtama)
            }
        }
    }
}

/**
 * Baris informasi harga dalam panel ringkasan.
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
            style = if (tonjolkan) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            color = if (tonjolkan) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = nilai,
            style = if (tonjolkan) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

/**
 * Kartu untuk menampilkan informasi singkat sebuah produk di dalam katalog.
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

            LencanaStokKasir(stokTersedia = produk.stokTersedia)
        }
    }
}

/**
 * Label penanda stok produk yang tersedia.
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
 * Fungsi ekstensi internal untuk format harga sederhana.
 */
private fun Long.sebagaiRupiahSederhana(): String {
    return "Rp$this"
}

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
        gunakanWarnaDinamis = false,
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
        gunakanWarnaDinamis = false,
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
