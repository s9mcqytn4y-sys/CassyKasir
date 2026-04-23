package id.cassy.kasir.antarmuka.utama

/**
 * Representasi efek sekali pakai dari ViewModel ke antarmuka.
 *
 * Efek digunakan untuk kejadian sementara yang tidak perlu disimpan
 * sebagai status layar persisten, seperti pesan singkat operasional.
 */
sealed interface EfekLayarUtamaKasir {

    /**
     * Menampilkan pesan singkat sementara kepada pengguna.
     */
    data class TampilkanPesanSingkat(
        val pesan: String,
    ) : EfekLayarUtamaKasir
}
