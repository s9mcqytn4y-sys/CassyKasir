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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.cassy.kasir.antarmuka.komponen.StatusKosongSederhana
import id.cassy.kasir.antarmuka.tema.TemaCassyKasir
import id.cassy.kasir.ranah.contoh.KatalogProdukContoh
import id.cassy.kasir.ranah.model.Produk

/**
 * Model UI ringan untuk ringkasan beranda kasir.
 *
 * @property namaAplikasi Nama aplikasi yang ditampilkan.
 * @property sloganAplikasi Slogan aplikasi yang ditampilkan.
 * @property jumlahProdukTersedia Total produk dalam katalog.
 * @property jumlahItemKeranjang Jumlah item unik di keranjang.
 * @property totalBelanjaSementara Total nilai belanja dalam format mata uang.
 * @property statusSinkronisasi Status penyimpanan data (misal: "Tersimpan Lokal").
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
 * Model UI statis untuk panel keranjang kosong.
 *
 * @property judul Judul pesan kosong.
 * @property deskripsi Penjelasan tambahan saat keranjang kosong.
 * @property jumlahItem Label jumlah item (misal: "0 item").
 */
data class StatusKeranjangStatis(
    val judul: String,
    val deskripsi: String,
    val jumlahItem: String,
)

/**
 * Model UI statis untuk ringkasan pembayaran.
 *
 * @property subtotal Nilai total sebelum potongan.
 * @property potongan Nilai diskon atau potongan harga.
 * @property pajak Nilai pajak yang dikenakan.
 * @property totalAkhir Total biaya yang harus dibayar.
 * @property labelAksiUtama Teks pada tombol konfirmasi pembayaran.
 * @property aksiUtamaAktif Status apakah tombol pembayaran bisa diklik.
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
 * Komponen utama yang menyusun seluruh tampilan Layar Kasir.
 *
 * Fungsi ini bersifat stateless dan mendukung tata letak responsif (Ponsel & Tablet).
 *
 * @param modelTampilan Objek state tunggal yang berisi seluruh data UI.
 * @param saatKataKunciPencarianBerubah Callback saat pengguna mengetik di kolom pencarian.
 * @param saatUbahVisibilitasRingkasanPembayaran Callback untuk beralih tampilan ringkasan.
 * @param modifier Modifier untuk menyesuaikan tata letak komponen root.
 */
@Composable
fun LayarUtamaKasir(
    modelTampilan: ModelTampilanLayarUtamaKasir,
    saatKataKunciPencarianBerubah: (String) -> Unit,
    saatUbahVisibilitasRingkasanPembayaran: () -> Unit,
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
                    saatKataKunciPencarianBerubah = saatKataKunciPencarianBerubah,
                    saatUbahVisibilitasRingkasanPembayaran = saatUbahVisibilitasRingkasanPembayaran,
                )
            } else {
                TataLetakPonselKasir(
                    modelTampilan = modelTampilan,
                    saatKataKunciPencarianBerubah = saatKataKunciPencarianBerubah,
                    saatUbahVisibilitasRingkasanPembayaran = saatUbahVisibilitasRingkasanPembayaran,
                )
            }
        }
    }
}

/**
 * Tata letak untuk layar dengan lebar terbatas (Ponsel).
 */
@Composable
fun TataLetakPonselKasir(
    modelTampilan: ModelTampilanLayarUtamaKasir,
    saatKataKunciPencarianBerubah: (String) -> Unit,
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
            PanelKeranjangKosongKasir(
                statusKeranjang = modelTampilan.statusKeranjang,
            )
        }

        item {
            BagianRingkasanPembayaranKasir(
                ringkasanPembayaran = modelTampilan.ringkasanPembayaran,
                apakahRingkasanPembayaranTampil = modelTampilan.apakahRingkasanPembayaranTampil,
                saatUbahVisibilitasRingkasanPembayaran = saatUbahVisibilitasRingkasanPembayaran,
            )
        }

        item {
            BidangPencarianProdukKasir(
                nilaiPencarian = modelTampilan.kataKunciPencarian,
                saatNilaiPencarianBerubah = saatKataKunciPencarianBerubah,
                jumlahHasil = modelTampilan.daftarProdukTersaring.size,
            )
        }

        item {
            JudulBagianKasir(
                judul = "Katalog produk",
            )
        }

        if (modelTampilan.daftarProdukTersaring.isEmpty()) {
            item {
                StatusKosongSederhana(
                    judul = "Produk tidak ditemukan",
                    deskripsi = "Coba gunakan kata kunci lain untuk mencari produk.",
                )
            }
        } else {
            items(
                items = modelTampilan.daftarProdukTersaring,
                key = { produk -> produk.id },
            ) { produk ->
                KartuProdukKasir(
                    produk = produk,
                )
            }
        }
    }
}

/**
 * Tata letak kolom ganda untuk layar lebar (Tablet).
 */
@Composable
fun TataLetakTabletKasir(
    modelTampilan: ModelTampilanLayarUtamaKasir,
    saatKataKunciPencarianBerubah: (String) -> Unit,
    saatUbahVisibilitasRingkasanPembayaran: () -> Unit,
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
                    saatNilaiPencarianBerubah = saatKataKunciPencarianBerubah,
                    jumlahHasil = modelTampilan.daftarProdukTersaring.size,
                )
            }

            item {
                JudulBagianKasir(
                    judul = "Katalog produk",
                )
            }

            if (modelTampilan.daftarProdukTersaring.isEmpty()) {
                item {
                    StatusKosongSederhana(
                        judul = "Produk tidak ditemukan",
                        deskripsi = "Coba gunakan kata kunci lain untuk mencari produk.",
                    )
                }
            } else {
                items(
                    items = modelTampilan.daftarProdukTersaring,
                    key = { produk -> produk.id },
                ) { produk ->
                    KartuProdukKasir(
                        produk = produk,
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
            RingkasanKasir(
                statusBeranda = modelTampilan.statusBeranda,
            )

            PanelKeranjangKosongKasir(
                statusKeranjang = modelTampilan.statusKeranjang,
            )

            BagianRingkasanPembayaranKasir(
                ringkasanPembayaran = modelTampilan.ringkasanPembayaran,
                apakahRingkasanPembayaranTampil = modelTampilan.apakahRingkasanPembayaranTampil,
                saatUbahVisibilitasRingkasanPembayaran = saatUbahVisibilitasRingkasanPembayaran,
            )
        }
    }
}

/**
 * Header utama layar kasir yang berisi identitas aplikasi.
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
 * Judul teks untuk memisahkan bagian-bagian pada layar.
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
 * Komponen input untuk pencarian katalog produk.
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
        label = {
            Text(text = "Cari produk")
        },
        placeholder = {
            Text(text = "Contoh: kopi, teh, roti")
        },
        supportingText = {
            Text(text = "Hasil ditemukan: $jumlahHasil")
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
        ),
    )
}

/**
 * Menampilkan ringkasan statistik operasional kasir.
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
 * Kartu statistik individual untuk menampilkan satu metrik.
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
 * Bagian ringkasan biaya transaksi dengan kontrol visibilitas.
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
            JudulBagianKasir(
                judul = "Ringkasan pembayaran",
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
            )
        }
    }
}

/**
 * Panel detail biaya transaksi.
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
                Text(
                    text = ringkasanPembayaran.labelAksiUtama,
                )
            }
        }
    }
}

/**
 * Baris informasi tunggal dalam panel ringkasan pembayaran.
 */
@Composable
fun BarisRingkasanPembayaranKasir(
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
 * Menampilkan item produk individual dalam katalog.
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
 * Menampilkan status stok produk.
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
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 8.dp,
            ),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

/**
 * Memformat nilai numerik ke format mata uang Rupiah sederhana.
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
        modeGelap = false,
        gunakanWarnaDinamis = false,
    ) {
        LayarUtamaKasir(
            modelTampilan = ModelTampilanLayarUtamaKasir(
                statusBeranda = StatusBerandaKasir(
                    namaAplikasi = "Cassy Kasir",
                    sloganAplikasi = "Kasir Cepat untuk Usaha Hebat",
                    jumlahProdukTersedia = daftarProduk.size,
                    jumlahItemKeranjang = 0,
                    totalBelanjaSementara = "Rp0",
                    statusSinkronisasi = "Tersimpan Lokal",
                ),
                daftarProdukTersaring = daftarProduk,
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
                kataKunciPencarian = "",
                apakahRingkasanPembayaranTampil = true,
            ),
            saatKataKunciPencarianBerubah = {},
            saatUbahVisibilitasRingkasanPembayaran = {},
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
                    sloganAplikasi = "Kasir Cepat untuk Usaha Hebat",
                    jumlahProdukTersedia = daftarProduk.size,
                    jumlahItemKeranjang = 0,
                    totalBelanjaSementara = "Rp0",
                    statusSinkronisasi = "Tersimpan Lokal",
                ),
                daftarProdukTersaring = daftarProduk,
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
                kataKunciPencarian = "",
                apakahRingkasanPembayaranTampil = true,
            ),
            saatKataKunciPencarianBerubah = {},
            saatUbahVisibilitasRingkasanPembayaran = {},
        )
    }
}
