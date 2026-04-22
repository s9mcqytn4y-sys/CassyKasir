package id.cassy.kasir.antarmuka.utama

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.cassy.kasir.ranah.contoh.KatalogProdukContoh
import id.cassy.kasir.ranah.fungsi.cariProduk
import id.cassy.kasir.ranah.fungsi.hitungJumlahItem
import id.cassy.kasir.ranah.fungsi.hitungSubtotalKeranjang
import id.cassy.kasir.ranah.fungsi.hitungTotalTransaksi
import id.cassy.kasir.ranah.model.ItemKeranjang
import id.cassy.kasir.ranah.model.Produk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Pengelola status layar utama kasir dengan pola Unidirectional Data Flow (UDF).
 *
 * ViewModel ini mengelola aliran status (StateFlow) secara atomik untuk:
 * - Sinkronisasi data keranjang belanja.
 * - Penyaringan katalog produk berdasarkan input pengguna.
 * - Perhitungan kalkulasi finansial secara real-time.
 */
class LayarUtamaKasirViewModel : ViewModel() {

    private val daftarProdukPenuh: List<Produk> = KatalogProdukContoh.daftarAwal()

    // Status internal yang bersifat privat
    private val _daftarItemKeranjang = MutableStateFlow<List<ItemKeranjang>>(emptyList())
    private val _kataKunciPencarian = MutableStateFlow("")
    private val _apakahRingkasanTampil = MutableStateFlow(true)

    /**
     * Aliran status UI tunggal yang menggabungkan seluruh status internal.
     * Menggunakan [combine] untuk memastikan re-kalkulasi hanya terjadi saat data berubah.
     */
    val modelTampilan: StateFlow<ModelTampilanLayarUtamaKasir> = combine(
        _daftarItemKeranjang,
        _kataKunciPencarian,
        _apakahRingkasanTampil
    ) { keranjang, kataKunci, tampil ->
        bentukModelTampilan(keranjang, kataKunci, tampil)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = bentukModelTampilan(emptyList(), "", true)
    )

    /**
     * Menangani interaksi pengguna dari layar utama.
     *
     * @param aksi Representasi interaksi pengguna [AksiLayarUtamaKasir].
     */
    fun tanganiAksi(aksi: AksiLayarUtamaKasir) {
        when (aksi) {
            is AksiLayarUtamaKasir.UbahKataKunciPencarian -> {
                _kataKunciPencarian.value = aksi.kataKunciBaru
            }

            AksiLayarUtamaKasir.UbahVisibilitasRingkasanPembayaran -> {
                _apakahRingkasanTampil.update { !it }
            }

            is AksiLayarUtamaKasir.TambahProdukKeKeranjang -> {
                tambahProdukKeKeranjang(aksi.produkId)
            }
        }
    }

    private fun tambahProdukKeKeranjang(produkId: String) {
        val produkTarget = daftarProdukPenuh.firstOrNull { it.id == produkId && it.aktif && it.stokTersedia > 0 }
            ?: return

        _daftarItemKeranjang.update { daftarLama ->
            val indeksLama = daftarLama.indexOfFirst { it.produk.id == produkId }
            if (indeksLama >= 0) {
                val itemLama = daftarLama[indeksLama]
                if (itemLama.jumlah < produkTarget.stokTersedia) {
                    daftarLama.toMutableList().apply {
                        this[indeksLama] = itemLama.copy(jumlah = itemLama.jumlah + 1)
                    }
                } else daftarLama
            } else {
                daftarLama + ItemKeranjang(produk = produkTarget, jumlah = 1)
            }
        }
    }

    private fun bentukModelTampilan(
        daftarItemKeranjang: List<ItemKeranjang>,
        kataKunciPencarian: String,
        apakahRingkasanTampil: Boolean,
    ): ModelTampilanLayarUtamaKasir {
        val daftarProdukTersaring = daftarProdukPenuh.cariProduk(kataKunciPencarian)
        val jumlahItem = daftarItemKeranjang.hitungJumlahItem()
        val subtotal = daftarItemKeranjang.hitungSubtotalKeranjang()
        val total = hitungTotalTransaksi(
            daftarItemKeranjang = daftarItemKeranjang,
            potongan = 0,
            biayaLayanan = 0,
            pajak = 0,
        )

        return ModelTampilanLayarUtamaKasir(
            statusBeranda = StatusBerandaKasir(
                namaAplikasi = "Cassy Kasir",
                sloganAplikasi = "Solusi Digital UMKM Modern",
                jumlahProdukTersedia = daftarProdukPenuh.size,
                jumlahItemKeranjang = jumlahItem,
                totalBelanjaSementara = subtotal.rupiahkan(),
                statusSinkronisasi = "Tersimpan Lokal",
            ),
            daftarProdukTersaring = daftarProdukTersaring,
            daftarItemKeranjang = daftarItemKeranjang,
            statusKeranjang = StatusKeranjangKasir(
                judul = if (daftarItemKeranjang.isEmpty()) "Keranjang kosong" else "Keranjang aktif",
                deskripsi = if (daftarItemKeranjang.isEmpty()) {
                    "Mulai transaksi dengan memilih produk."
                } else {
                    "Periksa item sebelum lanjut ke pembayaran."
                },
                jumlahItem = "$jumlahItem item",
            ),
            ringkasanPembayaran = RingkasanPembayaranKasir(
                subtotal = subtotal.rupiahkan(),
                potongan = "Rp0",
                pajak = "Rp0",
                totalAkhir = total.rupiahkan(),
                labelAksiUtama = if (jumlahItem > 0) "Lanjut Pembayaran" else "Pilih Produk",
                aksiUtamaAktif = jumlahItem > 0,
            ),
            kataKunciPencarian = kataKunciPencarian,
            apakahRingkasanPembayaranTampil = apakahRingkasanTampil,
        )
    }

    private fun Long.rupiahkan(): String = "Rp$this"
}
