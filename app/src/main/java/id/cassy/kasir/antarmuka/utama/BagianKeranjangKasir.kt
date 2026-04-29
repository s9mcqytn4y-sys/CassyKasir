package id.cassy.kasir.antarmuka.utama

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import id.cassy.kasir.antarmuka.komponen.StatusKosongSederhana
import id.cassy.kasir.ranah.fungsi.hitungSubTotal
import id.cassy.kasir.ranah.fungsi.sebagaiRupiah
import id.cassy.kasir.ranah.model.ItemKeranjang

/**
 * Panel yang menampilkan daftar item belanja saat ini dalam keranjang.
 *
 * @param daftarItemKeranjang Daftar item yang ada di keranjang.
 * @param statusKeranjang Status visual panel keranjang.
 * @param saatTambahProduk Callback untuk menambah jumlah produk berdasarkan ID.
 * @param saatKurangiProduk Callback untuk mengurangi jumlah produk berdasarkan ID.
 * @param saatHapusProduk Callback untuk menghapus produk dari keranjang berdasarkan ID.
 * @param modifier Modifier untuk kustomisasi tata letak.
 */
@Composable
internal fun PanelKeranjangKasir(
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
 * Satu baris informasi item di dalam keranjang belanja.
 *
 * @param itemKeranjang Data item keranjang yang akan ditampilkan.
 * @param saatTambahProduk Callback saat tombol tambah diklik.
 * @param saatKurangiProduk Callback saat tombol kurangi diklik.
 * @param saatHapusProduk Callback saat tombol hapus diklik.
 * @param modifier Modifier untuk kustomisasi tata letak.
 */
@Composable
internal fun BarisItemKeranjangKasir(
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
                    text = "${itemKeranjang.jumlah} x ${itemKeranjang.produk.harga.sebagaiRupiah()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = itemKeranjang.hitungSubTotal().sebagaiRupiah(),
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
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Kurangi jumlah",
                )
            }

            FilledTonalButton(
                onClick = saatTambahProduk,
                enabled = !stokSudahPenuh,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah jumlah",
                )
            }

            OutlinedButton(
                onClick = saatHapusProduk,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus dari keranjang",
                    tint = MaterialTheme.colorScheme.error,
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
