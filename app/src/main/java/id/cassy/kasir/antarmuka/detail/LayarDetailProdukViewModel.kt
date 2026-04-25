package id.cassy.kasir.antarmuka.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.cassy.kasir.antarmuka.navigasi.TujuanNavigasiKasir
import id.cassy.kasir.ranah.contoh.KatalogProdukContoh
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Pengelola status untuk layar detail produk.
 *
 * ViewModel ini:
 * - membaca produkId dari argumen navigasi
 * - memuat detail produk
 * - membentuk state layar yang tegas: memuat, berhasil, kosong, atau gagal
 */
class LayarDetailProdukViewModel(
    statusTersimpan: SavedStateHandle,
) : ViewModel() {

    private val produkId: String = checkNotNull(
        statusTersimpan.get<String>(
            TujuanNavigasiKasir.DetailProduk.namaArgumenProdukId,
        ),
    ) {
        "Argumen produkId wajib tersedia pada layar detail produk."
    }

    private val _modelTampilan = MutableStateFlow(
        ModelTampilanDetailProduk(
            produkId = produkId,
            judulLayar = "Detail Produk",
            statusMuat = StatusMuatDetailProduk.Memuat,
        ),
    )
    val modelTampilan: StateFlow<ModelTampilanDetailProduk> = _modelTampilan.asStateFlow()

    init {
        muatDetailProduk()
    }

    /**
     * Memuat ulang detail produk.
     *
     * Fungsi ini akan berguna saat nanti sumber data berpindah ke repository
     * lokal atau remote.
     */
    fun muatUlang() {
        muatDetailProduk()
    }

    /**
     * Memuat detail produk dari katalog contoh berdasarkan produkId.
     *
     * Pada tahap ini sumber data masih lokal dan sangat cepat,
     * sehingga status memuat bisa berlangsung sangat singkat saat runtime.
     */
    private fun muatDetailProduk() {
        viewModelScope.launch {
            _modelTampilan.update { statusLama ->
                statusLama.copy(
                    statusMuat = StatusMuatDetailProduk.Memuat,
                )
            }

            try {
                val produk = KatalogProdukContoh.daftarAwal().firstOrNull { itemProduk ->
                    itemProduk.id == produkId
                }

                _modelTampilan.value = if (produk != null) {
                    val aksiTambahAktif = produk.aktif && produk.stokTersedia > 0

                    ModelTampilanDetailProduk(
                        produkId = produkId,
                        judulLayar = "Detail Produk",
                        statusMuat = StatusMuatDetailProduk.Berhasil(
                            namaProduk = produk.nama,
                            hargaProduk = "Rp${produk.harga}",
                            stokTersedia = produk.stokTersedia,
                            deskripsiProduk = produk.deskripsi.ifBlank {
                                "Produk ini belum memiliki deskripsi tambahan."
                            },
                            labelAksiTambah = if (aksiTambahAktif) {
                                "Tambah ke Keranjang"
                            } else {
                                "Produk Tidak Tersedia"
                            },
                            aksiTambahAktif = aksiTambahAktif,
                        ),
                    )
                } else {
                    ModelTampilanDetailProduk(
                        produkId = produkId,
                        judulLayar = "Detail Produk",
                        statusMuat = StatusMuatDetailProduk.Kosong(
                            judul = "Produk tidak ditemukan",
                            deskripsi = "Produk dengan id $produkId tidak berhasil ditemukan dari katalog contoh.",
                        ),
                    )
                }
            } catch (_: Exception) {
                _modelTampilan.value = ModelTampilanDetailProduk(
                    produkId = produkId,
                    judulLayar = "Detail Produk",
                    statusMuat = StatusMuatDetailProduk.Gagal(
                        judul = "Gagal memuat detail produk",
                        deskripsi = "Terjadi gangguan saat memuat detail produk. Silakan coba lagi.",
                    ),
                )
            }
        }
    }
}
