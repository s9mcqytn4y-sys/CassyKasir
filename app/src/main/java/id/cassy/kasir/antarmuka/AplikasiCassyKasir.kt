package id.cassy.kasir.antarmuka

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import id.cassy.kasir.antarmuka.tema.TemaCassyKasir
import id.cassy.kasir.antarmuka.utama.LayarUtamaKasir
import id.cassy.kasir.antarmuka.utama.RingkasanPembayaranStatis
import id.cassy.kasir.antarmuka.utama.StatusBerandaKasir
import id.cassy.kasir.antarmuka.utama.StatusKeranjangStatis
import id.cassy.kasir.antarmuka.utama.UtamaViewModel

/**
 * Komponen tingkat atas (Root Composable) untuk aplikasi Cassy Kasir.
 * Berfungsi sebagai orchestrator yang mengatur tema, state global awal,
 * dan navigasi utama aplikasi.
 */
@Composable
fun AplikasiCassyKasir(
    viewModel: UtamaViewModel = viewModel(),
) {
    // Mengamati status UI dari ViewModel secara reaktif
    val uiState by viewModel.uiState.collectAsState()

    // Transformasi status bisnis ke status tampilan (UI State Mapping)
    val statusBeranda = StatusBerandaKasir(
        namaAplikasi = "Cassy Kasir",
        sloganAplikasi = "Kasir Cepat untuk Usaha Hebat",
        jumlahProdukTersedia = uiState.daftarProduk.size,
        jumlahItemKeranjang = uiState.jumlahItem,
        totalBelanjaSementara = "Rp${uiState.subtotal}",
        statusSinkronisasi = "Tersimpan Lokal",
    )

    val statusKeranjang = if (uiState.keranjang.isEmpty()) {
        StatusKeranjangStatis(
            judul = "Keranjang masih kosong",
            deskripsi = "Pilih produk dari katalog untuk mulai transaksi.",
            jumlahItem = "0 item",
        )
    } else {
        StatusKeranjangStatis(
            judul = "Keranjang Belanja",
            deskripsi = "${uiState.keranjang.size} jenis produk terpilih",
            jumlahItem = "${uiState.jumlahItem} item",
        )
    }

    val ringkasanPembayaran = RingkasanPembayaranStatis(
        subtotal = "Rp${uiState.subtotal}",
        potongan = "Rp${uiState.potongan}",
        pajak = "Rp${uiState.pajak}",
        totalAkhir = "Rp${uiState.totalAkhir}",
        labelAksiUtama = if (uiState.keranjang.isEmpty()) "Pilih produk dulu" else "Proses Bayar",
        aksiUtamaAktif = uiState.keranjang.isNotEmpty(),
    )

    // Penerapan tema aplikasi dengan konfigurasi Material 3
    TemaCassyKasir(
        gunakanWarnaDinamis = false,
    ) {
        // Kontainer utama dengan dukungan warna permukaan sesuai tema
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Memuat layar utama kasir dengan menyuntikkan state dari ViewModel
            LayarUtamaKasir(
                statusBeranda = statusBeranda,
                daftarProduk = uiState.daftarProduk,
                statusKeranjang = statusKeranjang,
                ringkasanPembayaran = ringkasanPembayaran,
            )
        }
    }
}
