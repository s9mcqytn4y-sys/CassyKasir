package id.cassy.kasir.data.jaringan.konfigurasi

/**
 * Konfigurasi dasar untuk akses jaringan Cassy Kasir.
 *
 * Catatan:
 * - 10.0.2.2 adalah alamat khusus emulator Android untuk mengakses localhost mesin host.
 * - Saat nanti diuji di perangkat fisik, alamat ini harus diganti ke IP lokal komputer/server.
 */
object KonfigurasiJaringanKasir {
    const val alamatDasarApi = "http://10.0.2.2:8080/"
    const val batasWaktuKoneksiDetik = 15L
    const val batasWaktuBacaDetik = 15L
    const val batasWaktuTulisDetik = 15L
}
