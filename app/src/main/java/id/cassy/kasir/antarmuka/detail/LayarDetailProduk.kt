package id.cassy.kasir.antarmuka.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
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
import id.cassy.kasir.antarmuka.komponen.StatusGagalSederhana
import id.cassy.kasir.antarmuka.komponen.StatusKosongSederhana

/**
 * Layar detail produk yang bersifat stateless.
 *
 * Layar ini hanya merender status (state) yang sudah dibentuk oleh ViewModel.
 * Pemisahan ini memastikan UI tetap sederhana dan mudah diuji tanpa bergantung
 * langsung pada logika navigasi atau pengambilan data.
 *
 * @param modelTampilan Status UI yang akan ditampilkan pada layar.
 * @param saatKembali Callback yang dipicu saat pengguna menekan tombol kembali.
 * @param saatCobaLagi Callback yang dipicu saat pengguna menekan tombol coba lagi pada status gagal.
 * @param modifier Modifikasi tata letak opsional.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayarDetailProduk(
    modelTampilan: ModelTampilanDetailProduk,
    saatKembali: () -> Unit,
    saatCobaLagi: () -> Unit,
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
        when {
            modelTampilan.sedangMemuat -> {
                KontenMemuatDetailProduk(
                    paddingDalam = paddingDalam,
                )
            }

            modelTampilan.pesanKesalahan != null -> {
                KontenGagalMemuatDetailProduk(
                    paddingDalam = paddingDalam,
                    pesan = modelTampilan.pesanKesalahan,
                    saatCobaLagi = saatCobaLagi,
                )
            }

            modelTampilan.apakahProdukDitemukan -> {
                KontenDetailProduk(
                    paddingDalam = paddingDalam,
                    namaProduk = modelTampilan.namaProduk,
                    hargaProduk = modelTampilan.hargaProduk,
                    stokTersedia = modelTampilan.stokTersedia,
                    deskripsiProduk = modelTampilan.deskripsiProduk,
                )
            }

            else -> {
                KontenProdukTidakDitemukan(
                    paddingDalam = paddingDalam,
                    judulStatusKosong = modelTampilan.judulStatusKosong,
                    deskripsiStatusKosong = modelTampilan.deskripsiStatusKosong,
                )
            }
        }
    }
}

/**
 * Merender indikator atau pesan saat detail produk sedang dalam proses pemuatan.
 *
 * @param paddingDalam Jarak aman dari kerangka Scaffold.
 * @param modifier Modifikasi tata letak opsional.
 */
@Composable
private fun KontenMemuatDetailProduk(
    paddingDalam: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingDalam),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Merender tampilan status gagal jika terjadi kesalahan saat memuat data.
 *
 * @param paddingDalam Jarak aman dari kerangka Scaffold.
 * @param pesan Pesan kesalahan yang akan ditampilkan.
 * @param saatCobaLagi Callback untuk memicu pemuatan ulang.
 * @param modifier Modifikasi tata letak opsional.
 */
@Composable
private fun KontenGagalMemuatDetailProduk(
    paddingDalam: PaddingValues,
    pesan: String,
    saatCobaLagi: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingDalam)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        StatusGagalSederhana(
            pesan = pesan,
            saatCobaLagi = saatCobaLagi,
        )
    }
}

/**
 * Merender informasi lengkap produk ketika data berhasil ditemukan.
 *
 * @param paddingDalam Jarak aman dari kerangka Scaffold.
 * @param namaProduk Nama produk yang akan ditampilkan.
 * @param hargaProduk Harga produk dalam format teks.
 * @param stokTersedia Jumlah stok yang tersedia saat ini.
 * @param deskripsiProduk Penjelasan atau detail tambahan mengenai produk.
 * @param modifier Modifikasi tata letak opsional.
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
 * Merender tampilan status kosong jika produk tidak ditemukan di sumber data.
 *
 * @param paddingDalam Jarak aman dari kerangka Scaffold.
 * @param judulStatusKosong Judul pesan kesalahan.
 * @param deskripsiStatusKosong Penjelasan mengapa produk tidak ditemukan.
 * @param modifier Modifikasi tata letak opsional.
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
