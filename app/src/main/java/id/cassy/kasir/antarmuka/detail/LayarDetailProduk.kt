package id.cassy.kasir.antarmuka.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.cassy.kasir.antarmuka.komponen.StatusKosongSederhana
import id.cassy.kasir.ranah.contoh.KatalogProdukContoh

/**
 * Komposabel layar detail produk yang menampilkan informasi mendalam mengenai satu produk.
 *
 * Layar ini mendukung navigasi berargumen untuk memuat data berdasarkan [produkId].
 * Saat ini data masih dimuat dari [KatalogProdukContoh] untuk keperluan purwarupa.
 *
 * @param produkId Identitas unik produk yang ingin ditampilkan.
 * @param saatKembali Callback untuk menutup layar detail dan kembali ke layar sebelumnya.
 * @param modifier Modifikasi tata letak opsional.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayarDetailProduk(
    produkId: String,
    saatKembali: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val produk = remember(produkId) {
        KatalogProdukContoh.daftarAwal().firstOrNull { itemProduk ->
            itemProduk.id == produkId
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Produk",
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
        if (produk == null) {
            KontenProdukTidakDitemukan(
                paddingDalam = paddingDalam,
                produkId = produkId,
            )
        } else {
            KontenDetailProduk(
                paddingDalam = paddingDalam,
                namaProduk = produk.nama,
                hargaProduk = "Rp${produk.harga}",
                stokTersedia = produk.stokTersedia,
                deskripsiProduk = produk.deskripsi.ifBlank {
                    "Produk ini belum memiliki deskripsi tambahan."
                },
            )
        }
    }
}

/**
 * Merender konten utama ketika informasi produk tersedia.
 *
 * @param paddingDalam Jarak aman dari kerangka Scaffold.
 * @param namaProduk Label nama produk yang ditampilkan sebagai judul.
 * @param hargaProduk Nilai harga produk dalam format teks rupiah.
 * @param stokTersedia Jumlah sisa stok yang dapat dijual.
 * @param deskripsiProduk Penjelasan tambahan mengenai produk.
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
 * Merender status kosong atau pesan kesalahan ketika [produkId] tidak ditemukan di katalog.
 *
 * @param paddingDalam Jarak aman dari kerangka Scaffold.
 * @param produkId ID produk yang gagal ditemukan.
 * @param modifier Modifikasi tata letak opsional.
 */
@Composable
private fun KontenProdukTidakDitemukan(
    paddingDalam: PaddingValues,
    produkId: String,
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
            judul = "Produk tidak ditemukan",
            deskripsi = "Produk dengan id $produkId tidak berhasil ditemukan dari katalog contoh.",
        )
    }
}
