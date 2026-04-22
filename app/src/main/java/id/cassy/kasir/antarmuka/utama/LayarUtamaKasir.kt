package id.cassy.kasir.antarmuka.utama

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.cassy.kasir.antarmuka.komponen.StatusKosongSederhana
import id.cassy.kasir.antarmuka.tema.TemaCassyKasir
import id.cassy.kasir.ranah.contoh.KatalogProdukContoh
import id.cassy.kasir.ranah.fungsi.hitungSubTotal
import id.cassy.kasir.ranah.model.ItemKeranjang
import id.cassy.kasir.ranah.model.Produk

/**
 * Representasi status ringkasan beranda kasir.
 *
 * Digunakan untuk menampilkan informasi statis dan dinamis pada bagian atas aplikasi.
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
 * Representasi status visual panel keranjang.
 *
 * Menampung informasi judul, deskripsi, dan total item untuk memudahkan perubahan UI.
 */
data class StatusKeranjangKasir(
    val judul: String,
    val deskripsi: String,
    val jumlahItem: String,
)

/**
 * Representasi status rincian biaya pembayaran.
 *
 * Menghindari perhitungan langsung di UI dengan menyediakan string yang sudah diformat.
 */
data class RingkasanPembayaranKasir(
    val subtotal: String,
    val potongan: String,
    val pajak: String,
    val totalAkhir: String,
    val labelAksiUtama: String,
    val aksiUtamaAktif: Boolean,
)

/**
 * Komposabel utama Layar Kasir.
 *
 * Fungsi ini bertindak sebagai entri poin UI yang bersifat stateless. Menggunakan
 * [BoxWithConstraints] untuk menentukan tata letak adaptif berdasarkan lebar layar.
 *
 * @param modelTampilan Objek status tunggal yang mewakili kondisi UI saat ini.
 * @param saatAksiDikirim Callback untuk mengirimkan interaksi pengguna kembali ke pengelola status.
 * @param modifier Modifier untuk menyesuaikan tata letak eksternal.
 */
@Composable
fun LayarUtamaKasir(
    modelTampilan: ModelTampilanLayarUtamaKasir,
    saatAksiDikirim: (AksiLayarUtamaKasir) -> Unit,
    modifier: Modifier = Modifier,
) {
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
            val gunakanDuaPanel = maxWidth >= 840.dp

            if (gunakanDuaPanel) {
                TataLetakTabletKasir(
                    modelTampilan = modelTampilan,
                    saatAksiDikirim = saatAksiDikirim,
                )
            } else {
                TataLetakPonselKasir(
                    modelTampilan = modelTampilan,
                    saatAksiDikirim = saatAksiDikirim,
                )
            }
        }
    }
}

/**
 * Tata letak layar untuk perangkat dengan lebar terbatas (ponsel).
 * Menampilkan konten dalam satu kolom gulir tunggal.
 */
@Composable
private fun TataLetakPonselKasir(
    modelTampilan: ModelTampilanLayarUtamaKasir,
    saatAksiDikirim: (AksiLayarUtamaKasir) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            HeaderBerandaKasir(
                namaAplikasi = modelTampilan.statusBeranda.namaAplikasi,
                sloganAplikasi = modelTampilan.statusBeranda.sloganAplikasi,
            )
        }

        item {
            RingkasanKasir(
                statusBeranda = modelTampilan.statusBeranda,
            )
        }

        item {
            PanelKeranjangKasir(
                daftarItemKeranjang = modelTampilan.daftarItemKeranjang,
                statusKeranjang = modelTampilan.statusKeranjang,
            )
        }

        item {
            BagianRingkasanPembayaranKasir(
                ringkasanPembayaran = modelTampilan.ringkasanPembayaran,
                apakahRingkasanPembayaranTampil = modelTampilan.apakahRingkasanPembayaranTampil,
                saatUbahVisibilitasRingkasanPembayaran = {
                    saatAksiDikirim(AksiLayarUtamaKasir.UbahVisibilitasRingkasanPembayaran)
                },
            )
        }

        item {
            BidangPencarianProdukKasir(
                nilaiPencarian = modelTampilan.kataKunciPencarian,
                saatNilaiPencarianBerubah = { kataKunciBaru ->
                    saatAksiDikirim(AksiLayarUtamaKasir.UbahKataKunciPencarian(kataKunciBaru))
                },
                jumlahHasil = modelTampilan.daftarProdukTersaring.size,
            )
        }

        item {
            JudulBagianKasir(judul = "Katalog produk")
        }

        if (modelTampilan.daftarProdukTersaring.isEmpty()) {
            item {
                StatusKosongSederhana(
                    judul = "Produk tidak ditemukan",
                    deskripsi = "Coba gunakan kata kunci lain.",
                )
            }
        } else {
            items(
                items = modelTampilan.daftarProdukTersaring,
                key = { produk -> produk.id },
            ) { produk ->
                KartuProdukKasir(
                    produk = produk,
                    saatKlikProduk = {
                        saatAksiDikirim(AksiLayarUtamaKasir.TambahProdukKeKeranjang(produk.id))
                    },
                )
            }
        }
    }
}

/**
 * Tata letak layar untuk perangkat layar lebar (tablet).
 * Membagi layar menjadi dua bagian: Katalog (kiri) dan Ringkasan/Keranjang (kanan).
 */
@Composable
private fun TataLetakTabletKasir(
    modelTampilan: ModelTampilanLayarUtamaKasir,
    saatAksiDikirim: (AksiLayarUtamaKasir) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1.35f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                HeaderBerandaKasir(
                    namaAplikasi = modelTampilan.statusBeranda.namaAplikasi,
                    sloganAplikasi = modelTampilan.statusBeranda.sloganAplikasi,
                )
            }

            item {
                BidangPencarianProdukKasir(
                    nilaiPencarian = modelTampilan.kataKunciPencarian,
                    saatNilaiPencarianBerubah = { kataKunciBaru ->
                        saatAksiDikirim(AksiLayarUtamaKasir.UbahKataKunciPencarian(kataKunciBaru))
                    },
                    jumlahHasil = modelTampilan.daftarProdukTersaring.size,
                )
            }

            item {
                JudulBagianKasir(judul = "Katalog produk")
            }

            if (modelTampilan.daftarProdukTersaring.isEmpty()) {
                item {
                    StatusKosongSederhana(
                        judul = "Produk tidak ditemukan",
                        deskripsi = "Coba gunakan kata kunci lain.",
                    )
                }
            } else {
                items(
                    items = modelTampilan.daftarProdukTersaring,
                    key = { produk -> produk.id },
                ) { produk ->
                    KartuProdukKasir(
                        produk = produk,
                        saatKlikProduk = {
                            saatAksiDikirim(AksiLayarUtamaKasir.TambahProdukKeKeranjang(produk.id))
                        },
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(0.95f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            RingkasanKasir(statusBeranda = modelTampilan.statusBeranda)
            PanelKeranjangKasir(
                daftarItemKeranjang = modelTampilan.daftarItemKeranjang,
                statusKeranjang = modelTampilan.statusKeranjang,
            )
            BagianRingkasanPembayaranKasir(
                ringkasanPembayaran = modelTampilan.ringkasanPembayaran,
                apakahRingkasanPembayaranTampil = modelTampilan.apakahRingkasanPembayaranTampil,
                saatUbahVisibilitasRingkasanPembayaran = {
                    saatAksiDikirim(AksiLayarUtamaKasir.UbahVisibilitasRingkasanPembayaran)
                },
            )
        }
    }
}

/**
 * Komponen identitas aplikasi pada bagian atas layar.
 */
@Composable
private fun HeaderBerandaKasir(
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
 * Komponen judul teks standar untuk setiap bagian layar.
 */
@Composable
private fun JudulBagianKasir(
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
 * Input teks untuk pencarian katalog produk.
 */
@Composable
private fun BidangPencarianProdukKasir(
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
        label = { Text("Cari produk") },
        placeholder = { Text("Contoh: kopi, teh, roti") },
        supportingText = { Text("Ditemukan: $jumlahHasil produk") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    )
}

/**
 * Komponen grid statistik ringkas (produk, item, total).
 */
@Composable
private fun RingkasanKasir(
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
            KartuStatistikKasir(
                judul = "Status",
                nilai = statusBeranda.statusSinkronisasi,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * Kartu individual untuk menampilkan satu metrik statistik.
 */
@Composable
private fun KartuStatistikKasir(
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
 * Panel yang menampilkan daftar produk yang telah masuk ke keranjang.
 */
@Composable
private fun PanelKeranjangKasir(
    daftarItemKeranjang: List<ItemKeranjang>,
    statusKeranjang: StatusKeranjangKasir,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        JudulBagianKasir(judul = "Keranjang")

        if (daftarItemKeranjang.isEmpty()) {
            StatusKosongSederhana(
                judul = statusKeranjang.judul,
                deskripsi = statusKeranjang.deskripsi,
            )
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.14f)),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    daftarItemKeranjang.forEach { BarisItemKeranjangKasir(it) }
                }
            }
        }

        Text(
            text = statusKeranjang.jumlahItem,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Komponen satu baris produk di dalam panel keranjang.
 */
@Composable
private fun BarisItemKeranjangKasir(
    itemKeranjang: ItemKeranjang,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = itemKeranjang.produk.nama,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "${itemKeranjang.jumlah} x ${itemKeranjang.produk.harga.sebagaiRupiahSederhana()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Text(
            text = itemKeranjang.hitungSubTotal().sebagaiRupiahSederhana(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

/**
 * Bagian pembungkus panel ringkasan pembayaran dengan tombol alih visibilitas.
 */
@Composable
private fun BagianRingkasanPembayaranKasir(
    ringkasanPembayaran: RingkasanPembayaranKasir,
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
            JudulBagianKasir(judul = "Pembayaran")
            TextButton(onClick = saatUbahVisibilitasRingkasanPembayaran) {
                Text(if (apakahRingkasanPembayaranTampil) "Sembunyikan" else "Tampilkan")
            }
        }

        if (apakahRingkasanPembayaranTampil) {
            PanelRingkasanPembayaranKasir(ringkasanPembayaran)
        }
    }
}

/**
 * Panel kartu yang merinci subtotal, potongan, pajak, dan total akhir.
 */
@Composable
private fun PanelRingkasanPembayaranKasir(
    ringkasanPembayaran: RingkasanPembayaranKasir,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.14f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BarisRingkasanPembayaranKasir("Subtotal", ringkasanPembayaran.subtotal)
            BarisRingkasanPembayaranKasir("Potongan", ringkasanPembayaran.potongan)
            BarisRingkasanPembayaranKasir("Pajak", ringkasanPembayaran.pajak)
            HorizontalDivider()
            BarisRingkasanPembayaranKasir("Total", ringkasanPembayaran.totalAkhir, tonjolkan = true)
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = ringkasanPembayaran.aksiUtamaAktif,
            ) {
                Text(ringkasanPembayaran.labelAksiUtama)
            }
        }
    }
}

/**
 * Komponen satu baris informasi biaya (misal: Subtotal: Rp...).
 */
@Composable
private fun BarisRingkasanPembayaranKasir(
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
 * Kartu produk di dalam katalog yang dapat diklik untuk menambahkan ke keranjang.
 */
@Composable
private fun KartuProdukKasir(
    produk: Produk,
    saatKlikProduk: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val produkBisaDipilih = produk.aktif && produk.stokTersedia > 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = produkBisaDipilih, onClick = saatKlikProduk),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.14f)),
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
                Text(
                    text = if (produkBisaDipilih) "Ketuk untuk tambah" else "Produk tidak tersedia",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            LencanaStokKasir(stokTersedia = produk.stokTersedia)
        }
    }
}

/**
 * Lencana kecil untuk menunjukkan jumlah stok produk yang tersisa.
 */
@Composable
private fun LencanaStokKasir(
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

private fun Long.sebagaiRupiahSederhana(): String = "Rp$this"

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
        gunakanWarnaDinamis = false,
    ) {
        LayarUtamaKasir(
            modelTampilan = ModelTampilanLayarUtamaKasir(
                statusBeranda = StatusBerandaKasir(
                    namaAplikasi = "Cassy Kasir",
                    sloganAplikasi = "Solusi Digital UMKM Modern",
                    jumlahProdukTersedia = daftarProduk.size,
                    jumlahItemKeranjang = 3,
                    totalBelanjaSementara = "Rp30000",
                    statusSinkronisasi = "Tersimpan Lokal",
                ),
                daftarProdukTersaring = daftarProduk,
                daftarItemKeranjang = listOf(
                    ItemKeranjang(produk = daftarProduk[0], jumlah = 1),
                    ItemKeranjang(produk = daftarProduk[1], jumlah = 2),
                ),
                statusKeranjang = StatusKeranjangKasir(
                    judul = "Keranjang aktif",
                    deskripsi = "Periksa item sebelum lanjut ke pembayaran.",
                    jumlahItem = "3 item",
                ),
                ringkasanPembayaran = RingkasanPembayaranKasir(
                    subtotal = "Rp30000",
                    potongan = "Rp0",
                    pajak = "Rp0",
                    totalAkhir = "Rp30000",
                    labelAksiUtama = "Lanjut Pembayaran",
                    aksiUtamaAktif = true,
                ),
                kataKunciPencarian = "",
                apakahRingkasanPembayaranTampil = true,
            ),
            saatAksiDikirim = {},
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
        gunakanWarnaDinamis = false,
    ) {
        LayarUtamaKasir(
            modelTampilan = ModelTampilanLayarUtamaKasir(
                statusBeranda = StatusBerandaKasir(
                    namaAplikasi = "Cassy Kasir",
                    sloganAplikasi = "Solusi Digital UMKM Modern",
                    jumlahProdukTersedia = daftarProduk.size,
                    jumlahItemKeranjang = 0,
                    totalBelanjaSementara = "Rp0",
                    statusSinkronisasi = "Tersimpan Lokal",
                ),
                daftarProdukTersaring = daftarProduk,
                daftarItemKeranjang = emptyList(),
                statusKeranjang = StatusKeranjangKasir(
                    judul = "Keranjang kosong",
                    deskripsi = "Mulai transaksi dengan memilih produk.",
                    jumlahItem = "0 item",
                ),
                ringkasanPembayaran = RingkasanPembayaranKasir(
                    subtotal = "Rp0",
                    potongan = "Rp0",
                    pajak = "Rp0",
                    totalAkhir = "Rp0",
                    labelAksiUtama = "Pilih Produk",
                    aksiUtamaAktif = false,
                ),
                kataKunciPencarian = "",
                apakahRingkasanPembayaranTampil = true,
            ),
            saatAksiDikirim = {},
        )
    }
}
