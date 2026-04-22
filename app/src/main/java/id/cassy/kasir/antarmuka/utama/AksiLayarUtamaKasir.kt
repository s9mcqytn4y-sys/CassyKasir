package id.cassy.kasir.antarmuka.utama

import androidx.compose.runtime.Immutable

/**
 * Representasi aksi (Intent) yang dapat dikirim dari antarmuka ke logika bisnis.
 *
 * Mengikuti pola MVI (Model-View-Intent) atau UDF (Unidirectional Data Flow).
 * Penggunaan sealed interface memastikan penanganan aksi yang komprehensif pada ViewModel.
 */
@Immutable
sealed interface AksiLayarUtamaKasir {

    /**
     * Aksi untuk memperbarui filter pencarian produk.
     *
     * @property kataKunciBaru String yang dimasukkan pengguna pada kolom pencarian.
     */
    data class UbahKataKunciPencarian(val kataKunciBaru: String) : AksiLayarUtamaKasir

    /**
     * Aksi untuk mengalihkan (toggle) tampilan panel ringkasan pembayaran.
     */
    data object UbahVisibilitasRingkasanPembayaran : AksiLayarUtamaKasir
}
