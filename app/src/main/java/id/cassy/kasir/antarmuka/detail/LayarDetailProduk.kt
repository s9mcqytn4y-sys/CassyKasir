package id.cassy.kasir.antarmuka.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.cassy.kasir.antarmuka.komponen.StatusKosongSederhana

/**
 * Layar detail produk.
 *
 * Layar ini hanya merender status yang sudah dibentuk oleh ViewModel.
 * Ia tidak membaca argumen navigasi secara langsung.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayarDetailProduk(
    modelTampilan: ModelTampilanDetailProduk,
    saatKembali: () -> Unit,
    saatCobaMuatUlang: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = modelTampilan.judulLayar,
                    )
                },
                navigationIcon = {
                    TextButton(
                        onClick = saatKembali,
                    ) {
                        Text(
                            text = "Kembali",
                        )
                    }
                },
            )
        },
    ) { paddingDalam ->
        when (val statusMuat = modelTampilan.statusMuat) {
            StatusMuatDetailProduk.Memuat -> {
                KontenMemuatDetailProduk(
                    paddingDalam = paddingDalam,
                )
            }

            is StatusMuatDetailProduk.Berhasil -> {
                KontenDetailProduk(
                    paddingDalam = paddingDalam,
                    namaProduk = statusMuat.namaProduk,
                    hargaProduk = statusMuat.hargaProduk,
                    stokTersedia = statusMuat.stokTersedia,
                    deskripsiProduk = statusMuat.deskripsiProduk,
                )
            }

            is StatusMuatDetailProduk.Kosong -> {
                KontenProdukTidakDitemukan(
                    paddingDalam = paddingDalam,
                    judulStatusKosong = statusMuat.judul,
                    deskripsiStatusKosong = statusMuat.deskripsi,
                )
            }

            is StatusMuatDetailProduk.Gagal -> {
                KontenGagalMemuatDetailProduk(
                    paddingDalam = paddingDalam,
                    judulStatusGagal = statusMuat.judul,
                    deskripsiStatusGagal = statusMuat.deskripsi,
                    saatCobaMuatUlang = saatCobaMuatUlang,
                )
            }
        }
    }
}

/**
 * Konten saat detail produk sedang dimuat.
 */
@Composable
private fun KontenMemuatDetailProduk(
    paddingDalam: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingDalam)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
        Text(
            text = "Memuat detail produk...",
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

/**
 * Konten ketika produk berhasil ditemukan.
 */
@Composable
private fun KontenDetailProduk(
    paddingDalam: PaddingValues,
    namaProduk: String,
    hargaProduk: String,
    stokTersedia: Int,
    deskripsiProduk: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingDalam)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = namaProduk,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = "Harga: $hargaProduk",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = "Stok tersedia: $stokTersedia",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = deskripsiProduk,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

/**
 * Konten ketika produk tidak ditemukan.
 */
@Composable
private fun KontenProdukTidakDitemukan(
    paddingDalam: PaddingValues,
    judulStatusKosong: String,
    deskripsiStatusKosong: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingDalam)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        StatusKosongSederhana(
            judul = judulStatusKosong,
            deskripsi = deskripsiStatusKosong,
        )
    }
}

/**
 * Konten ketika terjadi kegagalan memuat detail produk.
 */
@Composable
private fun KontenGagalMemuatDetailProduk(
    paddingDalam: PaddingValues,
    judulStatusGagal: String,
    deskripsiStatusGagal: String,
    saatCobaMuatUlang: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingDalam)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        StatusKosongSederhana(
            judul = judulStatusGagal,
            deskripsi = deskripsiStatusGagal,
        )

        Button(
            onClick = saatCobaMuatUlang,
        ) {
            Text(
                text = "Coba Lagi",
            )
        }
    }
}
