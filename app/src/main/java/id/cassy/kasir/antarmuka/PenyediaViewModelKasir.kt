package id.cassy.kasir.antarmuka

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.cassy.kasir.AplikasiKasir
import id.cassy.kasir.antarmuka.riwayat.LayarRiwayatTransaksiViewModel
import id.cassy.kasir.antarmuka.transaksi.LayarDetailTransaksiViewModel
import id.cassy.kasir.antarmuka.utama.LayarUtamaKasirViewModel

/**
 * Penyedia (Factory) untuk membuat instansi ViewModel dengan dependensi yang diperlukan.
 * Menggunakan pola pencarian kontainer melalui objek Application.
 */
object PenyediaViewModelKasir {

    val Factory = viewModelFactory {
        initializer {
            val aplikasi = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AplikasiKasir
            LayarUtamaKasirViewModel(
                muatKatalogProduk = aplikasi.kontainer.muatKatalogProduk,
                selesaikanCheckoutLokalKasirUseCase = aplikasi.kontainer.selesaikanCheckoutLokalKasir,
            )
        }

        initializer {
            val aplikasi = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AplikasiKasir
            LayarRiwayatTransaksiViewModel(
                amatiRiwayatTransaksi = aplikasi.kontainer.amatiRiwayatTransaksi,
            )
        }

        initializer {
            val aplikasi = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AplikasiKasir
            LayarDetailTransaksiViewModel(
                amatiTransaksiBerdasarkanId = aplikasi.kontainer.amatiTransaksiBerdasarkanId,
                savedStateHandle = createSavedStateHandle(),
            )
        }
    }
}
