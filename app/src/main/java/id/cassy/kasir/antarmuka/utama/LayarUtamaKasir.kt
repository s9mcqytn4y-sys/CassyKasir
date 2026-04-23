package id.cassy.kasir.antarmuka.utama

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow

/**
 * Komposabel utama layar kasir.
 */
@Composable
fun LayarUtamaKasir(
    modelTampilan: ModelTampilanLayarUtamaKasir,
    saatAksiDikirim: (AksiLayarUtamaKasir) -> Unit,
    alurEfek: Flow<EfekLayarUtamaKasir> = emptyFlow(),
    modifier: Modifier = Modifier,
) {
    val statusHostSnackbar = remember { SnackbarHostState() }

    LaunchedEffect(alurEfek) {
        alurEfek.collectLatest { efek ->
            when (efek) {
                is EfekLayarUtamaKasir.TampilkanPesanSingkat -> {
                    statusHostSnackbar.showSnackbar(
                        message = efek.pesan,
                    )
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = {
            SnackbarHost(
                hostState = statusHostSnackbar,
            )
        },
    ) { paddingKerangka ->
        if (modelTampilan.statusKonfirmasiCheckout.apakahTampil) {
            DialogKonfirmasiCheckoutKasir(
                statusKonfirmasiCheckout = modelTampilan.statusKonfirmasiCheckout,
                saatBatalkan = {
                    saatAksiDikirim(AksiLayarUtamaKasir.BatalkanKonfirmasiCheckout)
                },
                saatKonfirmasi = {
                    saatAksiDikirim(AksiLayarUtamaKasir.KonfirmasiCheckout)
                },
            )
        }

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
 * Tata letak layar untuk perangkat dengan lebar terbatas.
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
            BidangPencarianProdukKasir(
                nilaiPencarian = modelTampilan.kataKunciPencarian,
                saatNilaiPencarianBerubah = { kataKunciBaru ->
                    saatAksiDikirim(
                        AksiLayarUtamaKasir.UbahKataKunciPencarian(
                            kataKunciBaru = kataKunciBaru,
                        ),
                    )
                },
                jumlahHasil = modelTampilan.daftarProdukTersaring.size,
            )
        }

        item {
            RingkasanKasir(
                statusBeranda = modelTampilan.statusBeranda,
            )
        }

        if (modelTampilan.statusHasilCheckout.apakahTampil) {
            item {
                KartuHasilCheckoutKasir(
                    statusHasilCheckout = modelTampilan.statusHasilCheckout,
                    saatTutup = {
                        saatAksiDikirim(AksiLayarUtamaKasir.TutupStatusHasilCheckout)
                    },
                )
            }
        }

        item {
            PanelKeranjangKasir(
                daftarItemKeranjang = modelTampilan.daftarItemKeranjang,
                statusKeranjang = modelTampilan.statusKeranjang,
                saatTambahProduk = { produkId ->
                    saatAksiDikirim(
                        AksiLayarUtamaKasir.TambahProdukKeKeranjang(
                            produkId = produkId,
                        ),
                    )
                },
                saatKurangiProduk = { produkId ->
                    saatAksiDikirim(
                        AksiLayarUtamaKasir.KurangiProdukDiKeranjang(
                            produkId = produkId,
                        ),
                    )
                },
                saatHapusProduk = { produkId ->
                    saatAksiDikirim(
                        AksiLayarUtamaKasir.HapusProdukDariKeranjang(
                            produkId = produkId,
                        ),
                    )
                },
            )
        }

        item {
            BagianRingkasanPembayaranKasir(
                ringkasanPembayaran = modelTampilan.ringkasanPembayaran,
                apakahRingkasanPembayaranTampil = modelTampilan.apakahRingkasanPembayaranTampil,
                saatUbahVisibilitasRingkasanPembayaran = {
                    saatAksiDikirim(
                        AksiLayarUtamaKasir.UbahVisibilitasRingkasanPembayaran,
                    )
                },
                saatCheckout = {
                    saatAksiDikirim(AksiLayarUtamaKasir.CobaCheckout)
                },
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
                    deskripsi = "Coba gunakan kata kunci lain.",
                )
            }
        } else {
            items(
                items = modelTampilan.daftarProdukTersaring,
                key = { produk -> produk.id },
                contentType = { "KartuProduk" },
            ) { produk ->
                KartuProdukKasir(
                    produk = produk,
                    saatKlikProduk = {
                        saatAksiDikirim(
                            AksiLayarUtamaKasir.TambahProdukKeKeranjang(
                                produkId = produk.id,
                            ),
                        )
                    },
                )
            }
        }
    }
}

/**
 * Tata letak layar untuk perangkat layar lebar.
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
                        saatAksiDikirim(
                            AksiLayarUtamaKasir.UbahKataKunciPencarian(
                                kataKunciBaru = kataKunciBaru,
                            ),
                        )
                    },
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
                        deskripsi = "Coba gunakan kata kunci lain.",
                    )
                }
            } else {
                items(
                    items = modelTampilan.daftarProdukTersaring,
                    key = { produk -> produk.id },
                    contentType = { "KartuProduk" },
                ) { produk ->
                    KartuProdukKasir(
                        produk = produk,
                        saatKlikProduk = {
                            saatAksiDikirim(
                                AksiLayarUtamaKasir.TambahProdukKeKeranjang(
                                    produkId = produk.id,
                                ),
                            )
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
            RingkasanKasir(
                statusBeranda = modelTampilan.statusBeranda,
            )

            if (modelTampilan.statusHasilCheckout.apakahTampil) {
                KartuHasilCheckoutKasir(
                    statusHasilCheckout = modelTampilan.statusHasilCheckout,
                    saatTutup = {
                        saatAksiDikirim(AksiLayarUtamaKasir.TutupStatusHasilCheckout)
                    },
                )
            }

            PanelKeranjangKasir(
                daftarItemKeranjang = modelTampilan.daftarItemKeranjang,
                statusKeranjang = modelTampilan.statusKeranjang,
                saatTambahProduk = { produkId ->
                    saatAksiDikirim(
                        AksiLayarUtamaKasir.TambahProdukKeKeranjang(
                            produkId = produkId,
                        ),
                    )
                },
                saatKurangiProduk = { produkId ->
                    saatAksiDikirim(
                        AksiLayarUtamaKasir.KurangiProdukDiKeranjang(
                            produkId = produkId,
                        ),
                    )
                },
                saatHapusProduk = { produkId ->
                    saatAksiDikirim(
                        AksiLayarUtamaKasir.HapusProdukDariKeranjang(
                            produkId = produkId,
                        ),
                    )
                },
            )

            BagianRingkasanPembayaranKasir(
                ringkasanPembayaran = modelTampilan.ringkasanPembayaran,
                apakahRingkasanPembayaranTampil = modelTampilan.apakahRingkasanPembayaranTampil,
                saatUbahVisibilitasRingkasanPembayaran = {
                    saatAksiDikirim(
                        AksiLayarUtamaKasir.UbahVisibilitasRingkasanPembayaran,
                    )
                },
                saatCheckout = {
                    saatAksiDikirim(AksiLayarUtamaKasir.CobaCheckout)
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
        label = {
            Text(text = "Cari produk")
        },
        placeholder = {
            Text(text = "Contoh: kopi, teh, roti")
        },
        supportingText = {
            Text(text = "Ditemukan: $jumlahHasil produk")
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
        ),
    )
}

/**
 * Komponen ringkasan metrik kasir.
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
 * Kartu statistik kecil.
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
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

/**
 * Banner status hasil checkout.
 */
@Composable
private fun KartuHasilCheckoutKasir(
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

/**
 * Panel yang menampilkan daftar item dalam keranjang.
 */
@Composable
private fun PanelKeranjangKasir(
    daftarItemKeranjang: List<ItemKeranjang>,
    statusKeranjang: StatusKeranjangKasir,
    saatTambahProduk: (String) -> Unit,
    saatKurangiProduk: (String) -> Unit,
    saatHapusProduk: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        JudulBagianKasir(
            judul = "Keranjang",
        )

        if (daftarItemKeranjang.isEmpty()) {
            StatusKosongSederhana(
                judul = statusKeranjang.judul,
                deskripsi = statusKeranjang.deskripsi,
            )
        } else {
            Card(
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
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    daftarItemKeranjang.forEachIndexed { indeks, itemKeranjang ->
                        BarisItemKeranjangKasir(
                            itemKeranjang = itemKeranjang,
                            saatTambahProduk = {
                                saatTambahProduk(itemKeranjang.produk.id)
                            },
                            saatKurangiProduk = {
                                saatKurangiProduk(itemKeranjang.produk.id)
                            },
                            saatHapusProduk = {
                                saatHapusProduk(itemKeranjang.produk.id)
                            },
                        )

                        if (indeks < daftarItemKeranjang.lastIndex) {
                            HorizontalDivider()
                        }
                    }
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
 * Satu baris item di keranjang.
 */
@Composable
private fun BarisItemKeranjangKasir(
    itemKeranjang: ItemKeranjang,
    saatTambahProduk: () -> Unit,
    saatKurangiProduk: () -> Unit,
    saatHapusProduk: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stokSudahPenuh = itemKeranjang.jumlah >= itemKeranjang.produk.stokTersedia

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = itemKeranjang.produk.nama,
                    style = MaterialTheme.typography.titleMedium,
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
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
                onClick = saatKurangiProduk,
                enabled = itemKeranjang.jumlah > 1,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp),
            ) {
                Text(
                    text = "Kurangi",
                )
            }

            FilledTonalButton(
                onClick = saatTambahProduk,
                enabled = !stokSudahPenuh,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp),
            ) {
                Text(
                    text = "Tambah",
                )
            }

            OutlinedButton(
                onClick = saatHapusProduk,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp),
            ) {
                Text(
                    text = "Hapus",
                )
            }
        }

        if (stokSudahPenuh) {
            Text(
                text = "Jumlah item sudah mencapai stok tersedia.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Bagian pembungkus panel ringkasan pembayaran.
 */
@Composable
private fun BagianRingkasanPembayaranKasir(
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
 */
@Composable
private fun PanelRingkasanPembayaranKasir(
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
 * Dialog konfirmasi checkout.
 */
@Composable
private fun DialogKonfirmasiCheckoutKasir(
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

/**
 * Kartu produk di katalog.
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
            .clickable(
                enabled = produkBisaDipilih,
                onClick = saatKlikProduk,
            ),
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
                Text(
                    text = if (produkBisaDipilih) {
                        "Ketuk untuk tambah"
                    } else {
                        "Produk tidak tersedia"
                    },
                    style = MaterialTheme.typography.labelMedium,
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
 * Lencana kecil untuk menunjukkan jumlah stok produk.
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
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 8.dp,
            ),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

private fun Long.sebagaiRupiahSederhana(): String {
    return "Rp$this"
}

@Preview(
    name = "Workspace tablet terang dialog checkout",
    showBackground = true,
    widthDp = 1280,
    heightDp = 800,
)
@Composable
private fun PreviewWorkspaceTabletTerangDialogCheckout() {
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
                    jumlahItemKeranjang = 4,
                    totalBelanjaSementara = "Rp59000",
                    statusSinkronisasi = "Tersimpan Lokal",
                ),
                daftarProdukTersaring = daftarProduk,
                daftarItemKeranjang = listOf(
                    ItemKeranjang(
                        produk = daftarProduk[0],
                        jumlah = 2,
                    ),
                    ItemKeranjang(
                        produk = daftarProduk[1],
                        jumlah = 1,
                    ),
                    ItemKeranjang(
                        produk = daftarProduk[2],
                        jumlah = 1,
                    ),
                ),
                statusKeranjang = StatusKeranjangKasir(
                    judul = "Keranjang aktif",
                    deskripsi = "Atur jumlah item sebelum lanjut ke pembayaran.",
                    jumlahItem = "4 item",
                ),
                ringkasanPembayaran = RingkasanPembayaranKasir(
                    subtotal = "Rp59000",
                    potongan = "Rp0",
                    pajak = "Rp0",
                    totalAkhir = "Rp59000",
                    labelAksiUtama = "Bayar sekarang",
                    aksiUtamaAktif = true,
                ),
                statusKonfirmasiCheckout = StatusKonfirmasiCheckoutKasir(
                    apakahTampil = true,
                    judul = "Konfirmasi pembayaran",
                    deskripsi = "Bayar 4 item dengan total Rp59000 sekarang?",
                    labelKonfirmasi = "Bayar sekarang",
                ),
                kataKunciPencarian = "",
                apakahRingkasanPembayaranTampil = true,
            ),
            saatAksiDikirim = {},
        )
    }
}

@Preview(
    name = "Workspace ponsel gelap hasil checkout",
    showBackground = true,
    widthDp = 411,
    heightDp = 891,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewWorkspacePonselGelapHasilCheckout() {
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
                    statusSinkronisasi = "Transaksi lokal selesai",
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
                    labelAksiUtama = "Pilih produk",
                    aksiUtamaAktif = false,
                ),
                statusHasilCheckout = StatusHasilCheckoutKasir(
                    apakahTampil = true,
                    judul = "Transaksi berhasil",
                    deskripsi = "4 item dengan total Rp59000 siap disimpan ke riwayat lokal pada scope data berikutnya.",
                ),
                kataKunciPencarian = "",
                apakahRingkasanPembayaranTampil = true,
            ),
            saatAksiDikirim = {},
        )
    }
}
