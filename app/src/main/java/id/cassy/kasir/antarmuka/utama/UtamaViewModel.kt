package id.cassy.kasir.antarmuka.utama

import androidx.lifecycle.ViewModel
import id.cassy.kasir.ranah.contoh.KatalogProdukContoh
import id.cassy.kasir.ranah.fungsi.hitungJumlahItem
import id.cassy.kasir.ranah.fungsi.hitungSubtotalKeranjang
import id.cassy.kasir.ranah.fungsi.hitungTotalTransaksi
import id.cassy.kasir.ranah.model.ItemKeranjang
import id.cassy.kasir.ranah.model.Produk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * State UI untuk layar utama kasir.
 * Dibungkus dalam satu kelas data untuk menjamin atomisitas perubahan status.
 */
data class UtamaUiState(
    val daftarProduk: List<Produk> = emptyList(),
    val keranjang: List<ItemKeranjang> = emptyList(),
    val kataKunciPencarian: String = "",
    val potongan: Long = 0,
    val pajak: Long = 0,
    val biayaLayanan: Long = 0,
) {
    // Properti komputasi untuk mempermudah akses di lapisan UI
    val subtotal: Long get() = keranjang.hitungSubtotalKeranjang()
    val totalAkhir: Long get() = hitungTotalTransaksi(keranjang, potongan, biayaLayanan, pajak)
    val jumlahItem: Int get() = keranjang.hitungJumlahItem()
}

/**
 * ViewModel yang bertanggung jawab mengelola logika bisnis pada layar utama.
 * Menggunakan StateFlow untuk mengirimkan pembaruan status secara reaktif ke UI.
 */
class UtamaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UtamaUiState())
    val uiState: StateFlow<UtamaUiState> = _uiState.asStateFlow()

    init {
        // Memuat data awal dari katalog contoh
        muatKatalog()
    }

    private fun muatKatalog() {
        _uiState.update {
            it.copy(daftarProduk = KatalogProdukContoh.daftarAwal())
        }
    }

    /**
     * Menambahkan produk ke dalam keranjang atau menambah jumlahnya jika sudah ada.
     *
     * @param produk Produk yang dipilih pengguna.
     */
    fun tambahKeKeranjang(produk: Produk) {
        _uiState.update { state ->
            val indexLama = state.keranjang.indexOfFirst { it.produk.id == produk.id }

            val keranjangBaru = if (indexLama != -1) {
                state.keranjang.mapIndexed { index, item ->
                    if (index == indexLama) item.copy(jumlah = item.jumlah + 1) else item
                }
            } else {
                state.keranjang + ItemKeranjang(produk = produk, jumlah = 1)
            }

            state.copy(keranjang = keranjangBaru)
        }
    }

    /**
     * Menghapus seluruh item dari keranjang (Reset transaksi).
     */
    fun bersihkanKeranjang() {
        _uiState.update { it.copy(keranjang = emptyList()) }
    }
}
