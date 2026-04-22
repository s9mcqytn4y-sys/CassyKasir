package id.cassy.kasir.antarmuka.utama

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
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
 */
data class StatusKeranjangStatis(
    val judul: String,
    val deskripsi: String,
    val jumlahItem: String,
)

/**
 * Model UI statis untuk ringkasan pembayaran.
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
 * Komponen utama Layar Kasir yang bersifat stateless.
 *
 * Mengikuti prinsip Hoisting State, di mana status dikelola oleh ViewModel
 * dan diteruskan ke sini sebagai model tampilan yang immutable.
 *
 * @param modelTampilan Objek state tunggal yang mewakili kondisi UI.
 * @param saatAksiDikirim Callback untuk mengirimkan interaksi pengguna kembali ke ViewModel.
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

        item { RingkasanKasir(statusBeranda = modelTampilan.statusBeranda) }

        item { PanelKeranjangKosongKasir(statusKeranjang = modelTampilan.statusKeranjang) }

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
                saatNilaiPencarianBerubah = {
                    saatAksiDikirim(AksiLayarUtamaKasir.UbahKataKunciPencarian(it))
                },
                jumlahHasil = modelTampilan.daftarProdukTersaring.size,
            )
        }

        item { JudulBagianKasir(judul = "Katalog Produk") }

        if (modelTampilan.daftarProdukTersaring.isEmpty()) {
            item {
                StatusKosongSederhana(
                    judul = "Produk Tidak Ditemukan",
                    deskripsi = "Coba gunakan kata kunci lain.",
                )
            }
        } else {
            items(
                items = modelTampilan.daftarProdukTersaring,
                key = { it.id },
            ) { produk ->
                KartuProdukKasir(produk = produk)
            }
        }
    }
}

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
                    saatNilaiPencarianBerubah = {
                        saatAksiDikirim(AksiLayarUtamaKasir.UbahKataKunciPencarian(it))
                    },
                    jumlahHasil = modelTampilan.daftarProdukTersaring.size,
                )
            }

            item { JudulBagianKasir(judul = "Katalog Produk") }

            if (modelTampilan.daftarProdukTersaring.isEmpty()) {
                item {
                    StatusKosongSederhana(
                        judul = "Produk Tidak Ditemukan",
                        deskripsi = "Coba gunakan kata kunci lain.",
                    )
                }
            } else {
                items(
                    items = modelTampilan.daftarProdukTersaring,
                    key = { it.id },
                ) { produk ->
                    KartuProdukKasir(produk = produk)
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
            PanelKeranjangKosongKasir(statusKeranjang = modelTampilan.statusKeranjang)
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

@Composable
private fun HeaderBerandaKasir(
    namaAplikasi: String,
    sloganAplikasi: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
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

@Composable
private fun JudulBagianKasir(judul: String) {
    Text(
        text = judul,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
private fun BidangPencarianProdukKasir(
    nilaiPencarian: String,
    saatNilaiPencarianBerubah: (String) -> Unit,
    jumlahHasil: Int,
) {
    OutlinedTextField(
        value = nilaiPencarian,
        onValueChange = saatNilaiPencarianBerubah,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Cari Produk") },
        placeholder = { Text("Contoh: Kopi, Roti") },
        supportingText = { Text("Ditemukan: $jumlahHasil produk") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    )
}

@Composable
private fun RingkasanKasir(statusBeranda: StatusBerandaKasir) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KartuStatistikKasir("Produk", statusBeranda.jumlahProdukTersedia.toString(), Modifier.weight(1f))
            KartuStatistikKasir("Item", statusBeranda.jumlahItemKeranjang.toString(), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KartuStatistikKasir("Total", statusBeranda.totalBelanjaSementara, Modifier.weight(1f))
            KartuStatistikKasir("Status", statusBeranda.statusSinkronisasi, Modifier.weight(1f))
        }
    }
}

@Composable
private fun KartuStatistikKasir(judul: String, nilai: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = judul, style = MaterialTheme.typography.labelMedium)
            Text(text = nilai, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun PanelKeranjangKosongKasir(statusKeranjang: StatusKeranjangStatis) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        JudulBagianKasir("Keranjang")
        StatusKosongSederhana(statusKeranjang.judul, statusKeranjang.deskripsi)
        Text(
            text = statusKeranjang.jumlahItem,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun BagianRingkasanPembayaranKasir(
    ringkasanPembayaran: RingkasanPembayaranStatis,
    apakahRingkasanPembayaranTampil: Boolean,
    saatUbahVisibilitasRingkasanPembayaran: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            JudulBagianKasir("Pembayaran")
            TextButton(onClick = saatUbahVisibilitasRingkasanPembayaran) {
                Text(if (apakahRingkasanPembayaranTampil) "Sembunyikan" else "Tampilkan")
            }
        }
        if (apakahRingkasanPembayaranTampil) {
            PanelRingkasanPembayaranKasir(ringkasanPembayaran)
        }
    }
}

@Composable
private fun PanelRingkasanPembayaranKasir(ringkasanPembayaran: RingkasanPembayaranStatis) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BarisRingkasan("Subtotal", ringkasanPembayaran.subtotal)
            BarisRingkasan("Potongan", ringkasanPembayaran.potongan)
            BarisRingkasan("Pajak", ringkasanPembayaran.pajak)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            BarisRingkasan("Total", ringkasanPembayaran.totalAkhir, true)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = ringkasanPembayaran.aksiUtamaAktif
            ) {
                Text(ringkasanPembayaran.labelAksiUtama)
            }
        }
    }
}

@Composable
private fun BarisRingkasan(label: String, nilai: String, tonjolkan: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = if (tonjolkan) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium)
        Text(nilai, style = if (tonjolkan) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun KartuProdukKasir(produk: Produk) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(produk.nama, style = MaterialTheme.typography.titleMedium)
                Text(produk.harga.sebagaiRupiahSederhana(), style = MaterialTheme.typography.bodyMedium)
            }
            LencanaStok(produk.stokTersedia)
        }
    }
}

@Composable
private fun LencanaStok(stok: Int) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(
            "Stok $stok",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

private fun Long.sebagaiRupiahSederhana(): String = "Rp$this"

@Preview(widthDp = 1280, heightDp = 800)
@Composable
private fun PreviewTablet() {
    TemaCassyKasir(false) {
        LayarUtamaKasir(ModelTampilanLayarUtamaKasir(), {})
    }
}
