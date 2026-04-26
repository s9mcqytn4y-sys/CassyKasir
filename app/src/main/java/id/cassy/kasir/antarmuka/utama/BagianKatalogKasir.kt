package id.cassy.kasir.antarmuka.utama

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import id.cassy.kasir.ranah.model.Produk

/**
 * Bagian UI pencarian produk yang menggabungkan bidang input dan tombol reset.
 *
 * @param nilaiPencarian Teks pencarian saat ini.
 * @param saatNilaiPencarianBerubah Callback saat teks pencarian berubah.
 * @param jumlahHasil Jumlah produk yang ditemukan berdasarkan pencarian.
 * @param tampilkanAksiResetPencarian Apakah tombol reset pencarian harus ditampilkan.
 * @param saatResetPencarian Callback saat tombol reset diklik.
 * @param modifier Modifier untuk kustomisasi tata letak.
 */
@Composable
internal fun BagianPencarianProdukKasir(
    nilaiPencarian: String,
    saatNilaiPencarianBerubah: (String) -> Unit,
    jumlahHasil: Int,
    tampilkanAksiResetPencarian: Boolean,
    saatResetPencarian: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BidangPencarianProdukKasir(
            nilaiPencarian = nilaiPencarian,
            saatNilaiPencarianBerubah = saatNilaiPencarianBerubah,
            jumlahHasil = jumlahHasil,
        )

        if (tampilkanAksiResetPencarian) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(
                    onClick = saatResetPencarian,
                    modifier = Modifier.heightIn(min = 48.dp),
                ) {
                    Text(
                        text = "Reset pencarian",
                    )
                }
            }
        }
    }
}

/**
 * Input teks pencarian untuk menyaring katalog produk secara waktu nyata.
 *
 * @param nilaiPencarian Teks pencarian saat ini.
 * @param saatNilaiPencarianBerubah Callback saat teks pencarian berubah.
 * @param jumlahHasil Jumlah produk yang ditemukan untuk ditampilkan di teks pembantu.
 * @param modifier Modifier untuk kustomisasi tata letak.
 */
@Composable
internal fun BidangPencarianProdukKasir(
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
 * Komponen judul teks standar untuk memisahkan setiap bagian informasi pada layar.
 *
 * @param judul Teks judul yang akan ditampilkan.
 * @param modifier Modifier untuk kustomisasi tata letak.
 */
@Composable
internal fun JudulBagianKasir(
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
 * Kartu informasi produk tunggal dalam daftar katalog.
 *
 * @param produk Data produk yang akan ditampilkan.
 * @param saatTambahProduk Callback saat tombol tambah diklik.
 * @param saatBukaDetailProduk Callback saat tombol detail diklik.
 * @param modifier Modifier untuk kustomisasi tata letak.
 */
@Composable
internal fun KartuProdukKasir(
    produk: Produk,
    saatTambahProduk: () -> Unit,
    saatBukaDetailProduk: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val produkBisaDipilih = produk.aktif && produk.stokTersedia > 0

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        text = "Rp${produk.harga}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                LencanaStokKasir(
                    stokTersedia = produk.stokTersedia,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = saatBukaDetailProduk,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp),
                ) {
                    Text(
                        text = "Detail",
                    )
                }

                Button(
                    onClick = saatTambahProduk,
                    enabled = produkBisaDipilih,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp),
                ) {
                    Text(
                        text = "Tambah",
                    )
                }
            }
        }
    }
}

/**
 * Lencana (Badge) kecil untuk menunjukkan jumlah stok produk yang tersedia.
 *
 * @param stokTersedia Jumlah stok yang tersedia.
 * @param modifier Modifier untuk kustomisasi tata letak.
 */
@Composable
internal fun LencanaStokKasir(
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
