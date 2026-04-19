package id.cassy.kasir.antarmuka

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import id.cassy.kasir.antarmuka.tema.TemaCassyKasir
import id.cassy.kasir.antarmuka.utama.LayarUtamaKasir
import id.cassy.kasir.antarmuka.utama.RingkasanPembayaranStatis
import id.cassy.kasir.antarmuka.utama.StatusBerandaKasir
import id.cassy.kasir.antarmuka.utama.StatusKeranjangStatis
import id.cassy.kasir.ranah.contoh.KatalogProdukContoh

/**
 * Komponen tingkat atas (Root Composable) untuk aplikasi Cassy Kasir.
 * Berfungsi sebagai orchestrator yang mengatur tema, state global awal,
 * dan navigasi utama aplikasi.
 */
@Composable
fun AplikasiCassyKasir() {
    // Inisialisasi data contoh untuk demonstrasi UI
    val daftarProdukAwal = remember { KatalogProdukContoh.daftarAwal() }

    // State awal untuk informasi beranda/dashboard
    val statusBerandaAwal = remember {
        StatusBerandaKasir(
            namaAplikasi = "Cassy Kasir",
            sloganAplikasi = "Kasir Cepat untuk Usaha Hebat",
            jumlahProdukTersedia = daftarProdukAwal.size,
            jumlahItemKeranjang = 0,
            totalBelanjaSementara = "Rp0",
            statusSinkronisasi = "Belum ada sinkronisasi",
        )
    }

    // State awal untuk tampilan keranjang yang masih kosong
    val statusKeranjangAwal = remember {
        StatusKeranjangStatis(
            judul = "Keranjang masih kosong",
            deskripsi = "Pilih produk dari katalog untuk mulai transaksi.",
            jumlahItem = "0 item",
        )
    }

    // State awal untuk rincian biaya pembayaran
    val ringkasanPembayaranAwal = remember {
        RingkasanPembayaranStatis(
            subtotal = "Rp0",
            potongan = "Rp0",
            pajak = "Rp0",
            totalAkhir = "Rp0",
            labelAksiUtama = "Pilih produk dulu",
            aksiUtamaAktif = false,
        )
    }

    // Penerapan tema aplikasi dengan konfigurasi Material 3
    TemaCassyKasir(
        gunakanWarnaDinamis = false,
    ) {
        // Kontainer utama dengan dukungan warna permukaan sesuai tema
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Memuat layar utama kasir dengan menyuntikkan state awal
            LayarUtamaKasir(
                statusBeranda = statusBerandaAwal,
                daftarProduk = daftarProdukAwal,
                statusKeranjang = statusKeranjangAwal,
                ringkasanPembayaran = ringkasanPembayaranAwal,
            )
        }
    }
}
