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
import androidx.compose.ui.text.font.FontWeight
import id.cassy.kasir.antarmuka.komponen.StatusKosongSederhana
import id.cassy.kasir.antarmuka.tema.TemaCassyKasir
import id.cassy.kasir.ranah.contoh.KatalogProdukContoh
import id.cassy.kasir.ranah.fungsi.hitungSubTotal
import id.cassy.kasir.ranah.model.ItemKeranjang
import id.cassy.kasir.ranah.model.Produk

/**
 * Representasi status ringkasan beranda kasir.
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
 */
data class StatusKeranjangKasir(
    val judul: String,
    val deskripsi: String,
    val jumlahItem: String,
)

/**
 * Representasi status rincian biaya pembayaran.
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
 * Mengimplementasikan pola stateless UI dengan tata letak adaptif (Responsif).
 *
 * @param modelTampilan Status UI tunggal dari ViewModel.
 * @param saatAksiDikirim Callback untuk mengirimkan interaksi pengguna.
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
    ) { paddingSistem ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingSistem),
        ) {
            val apakahTablet = maxWidth >= 840.dp

            if (apakahTablet) {
                TataLetakTablet(modelTampilan, saatAksiDikirim)
            } else {
                TataLetakPonsel(modelTampilan, saatAksiDikirim)
            }
        }
    }
}

@Composable
private fun TataLetakPonsel(
    modelTampilan: ModelTampilanLayarUtamaKasir,
    saatAksiDikirim: (AksiLayarUtamaKasir) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { HeaderAplikasi(modelTampilan.statusBeranda) }
        item { RingkasanBeranda(modelTampilan.statusBeranda) }
        item {
            PanelKeranjang(
                modelTampilan.daftarItemKeranjang,
                modelTampilan.statusKeranjang
            )
        }
        item {
            PanelPembayaran(
                modelTampilan.ringkasanPembayaran,
                modelTampilan.apakahRingkasanPembayaranTampil,
                saatUbahTampilan = { saatAksiDikirim(AksiLayarUtamaKasir.UbahVisibilitasRingkasanPembayaran) }
            )
        }
        item {
            KotakPencarian(
                modelTampilan.kataKunciPencarian,
                modelTampilan.daftarProdukTersaring.size,
                saatBerubah = { saatAksiDikirim(AksiLayarUtamaKasir.UbahKataKunciPencarian(it)) }
            )
        }
        item { JudulBagian("Katalog Produk") }

        if (modelTampilan.daftarProdukTersaring.isEmpty()) {
            item { StatusKosong() }
        } else {
            items(modelTampilan.daftarProdukTersaring, key = { it.id }) { produk ->
                KartuProduk(produk) {
                    saatAksiDikirim(AksiLayarUtamaKasir.TambahProdukKeKeranjang(produk.id))
                }
            }
        }
    }
}

@Composable
private fun TataLetakTablet(
    modelTampilan: ModelTampilanLayarUtamaKasir,
    saatAksiDikirim: (AksiLayarUtamaKasir) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Kolom Kiri: Katalog
        LazyColumn(
            modifier = Modifier.weight(1.2f).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { HeaderAplikasi(modelTampilan.statusBeranda) }
            item {
                KotakPencarian(
                    modelTampilan.kataKunciPencarian,
                    modelTampilan.daftarProdukTersaring.size,
                    saatBerubah = { saatAksiDikirim(AksiLayarUtamaKasir.UbahKataKunciPencarian(it)) }
                )
            }
            item { JudulBagian("Katalog Produk") }

            if (modelTampilan.daftarProdukTersaring.isEmpty()) {
                item { StatusKosong() }
            } else {
                items(modelTampilan.daftarProdukTersaring, key = { it.id }) { produk ->
                    KartuProduk(produk) {
                        saatAksiDikirim(AksiLayarUtamaKasir.TambahProdukKeKeranjang(produk.id))
                    }
                }
            }
        }

        // Kolom Kanan: Detail Transaksi
        Column(
            modifier = Modifier.weight(0.8f).fillMaxHeight().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RingkasanBeranda(modelTampilan.statusBeranda)
            PanelKeranjang(modelTampilan.daftarItemKeranjang, modelTampilan.statusKeranjang)
            PanelPembayaran(
                modelTampilan.ringkasanPembayaran,
                modelTampilan.apakahRingkasanPembayaranTampil,
                saatUbahTampilan = { saatAksiDikirim(AksiLayarUtamaKasir.UbahVisibilitasRingkasanPembayaran) }
            )
        }
    }
}


@Composable
private fun HeaderAplikasi(status: StatusBerandaKasir) {
    Column {
        Text(status.namaAplikasi, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            status.sloganAplikasi,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun JudulBagian(teks: String) {
    Text(teks, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun KotakPencarian(
    nilai: String,
    hasil: Int,
    saatBerubah: (String) -> Unit
) {
    OutlinedTextField(
        value = nilai,
        onValueChange = saatBerubah,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Cari Produk") },
        placeholder = { Text("Nama atau Barcode...") },
        supportingText = { Text("Ditemukan $hasil produk") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
    )
}

@Composable
private fun StatusKosong() {
    StatusKosongSederhana(
        judul = "Produk Tidak Ditemukan",
        deskripsi = "Silakan gunakan kata kunci pencarian lainnya."
    )
}

/**
 * Komponen grid statistik ringkas (produk, item, total).
 */
@Composable
private fun RingkasanBeranda(
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
private fun PanelKeranjang(
    daftarItemKeranjang: List<ItemKeranjang>,
    statusKeranjang: StatusKeranjangKasir,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        JudulBagian(teks = "Keranjang")

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
private fun PanelPembayaran(
    ringkasanPembayaran: RingkasanPembayaranKasir,
    apakahRingkasanPembayaranTampil: Boolean,
    saatUbahTampilan: () -> Unit,
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
            JudulBagian(teks = "Pembayaran")
            TextButton(onClick = saatUbahTampilan) {
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
private fun KartuProduk(
    produk: Produk,
    modifier: Modifier = Modifier,
    saatKlikProduk: () -> Unit,
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
