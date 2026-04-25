package id.cassy.kasir.antarmuka.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import id.cassy.kasir.antarmuka.navigasi.TujuanNavigasiKasir
import id.cassy.kasir.ranah.contoh.KatalogProdukContoh
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Pengelola status untuk layar detail produk.
 *
 * Pada scope ini ViewModel membaca produkId dari argumen navigasi,
 * lalu membentuk state layar detail secara terpusat.
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

    private val _modelTampilan = MutableStateFlow(ModelTampilanDetailProduk())
    val modelTampilan: StateFlow<ModelTampilanDetailProduk> = _modelTampilan.asStateFlow()

    init {
        muatDetailProduk()
    }

    /**
     * Memicu pemuatan ulang data jika terjadi kesalahan.
     */
    fun cobaLagi() {
        muatDetailProduk()
    }

    /**
     * Memuat detail produk dari katalog contoh berdasarkan produkId.
     *
     * Nantinya sumber data ini bisa diganti ke repository tanpa mengubah layar.
     */
    private fun muatDetailProduk() {
        _modelTampilan.value = _modelTampilan.value.copy(
            sedangMemuat = true,
            pesanKesalahan = null,
        )

        try {
            val produk = KatalogProdukContoh.daftarAwal().firstOrNull { itemProduk ->
                itemProduk.id == produkId
            }

            _modelTampilan.value = if (produk != null) {
                ModelTampilanDetailProduk(
                    sedangMemuat = false,
                    produkId = produkId,
                    judulLayar = "Detail Produk",
                    namaProduk = produk.nama,
                    hargaProduk = "Rp${produk.harga}",
                    stokTersedia = produk.stokTersedia,
                    deskripsiProduk = produk.deskripsi.ifBlank {
                        "Produk ini belum memiliki deskripsi tambahan."
                    },
                    apakahProdukDitemukan = true,
                )
            } else {
                ModelTampilanDetailProduk(
                    sedangMemuat = false,
                    produkId = produkId,
                    judulLayar = "Detail Produk",
                    apakahProdukDitemukan = false,
                    judulStatusKosong = "Produk tidak ditemukan",
                    deskripsiStatusKosong = "Produk dengan id $produkId tidak berhasil ditemukan dari katalog contoh.",
                )
            }
        } catch (e: Exception) {
            _modelTampilan.value = ModelTampilanDetailProduk(
                sedangMemuat = false,
                pesanKesalahan = "Terjadi kesalahan: ${e.message}",
            )
        }
    }
}
