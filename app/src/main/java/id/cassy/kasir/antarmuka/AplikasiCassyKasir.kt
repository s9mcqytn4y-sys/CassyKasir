package id.cassy.kasir.antarmuka

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import id.cassy.kasir.antarmuka.utama.LayarUtamaKasir
import id.cassy.kasir.antarmuka.utama.StatusBerandaKasir
import id.cassy.kasir.ranah.contoh.KatalogProdukContoh

@Composable
fun AplikasiCassyKasir() {
    val statusBerandaAwal = StatusBerandaKasir(
        namaAplikasi = "Cassy Kasir",
        sloganAplikasi = "Kasir Cepat untuk Usaha Hebat",
        jumlahProdukTersedia = KatalogProdukContoh.daftarAwal().size,
        jumlahItemKeranjang = 0,
        totalBelanjaSementara = "Rp0",
        statusSinkronisasi = "Belum ada sinkronisasi",
    )
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            LayarUtamaKasir(statusBeranda = statusBerandaAwal, KatalogProdukContoh.daftarAwal())
        }
    }
}
