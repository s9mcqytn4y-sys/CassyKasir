package id.cassy.kasir

import android.app.Application

/**
 * Kelas Application utama untuk Cassy Kasir.
 * Berfungsi sebagai titik pusat inisialisasi dependensi global.
 */
class AplikasiKasir : Application() {

    /**
     * Kontainer dependensi yang dapat diakses di seluruh aplikasi.
     */
    lateinit var kontainer: GudangDependensiKasir
        private set

    override fun onCreate() {
        super.onCreate()
        kontainer = GudangDependensiKasir(this)
    }
}
