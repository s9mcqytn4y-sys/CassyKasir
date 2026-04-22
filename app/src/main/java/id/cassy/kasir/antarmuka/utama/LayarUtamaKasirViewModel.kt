package id.cassy.kasir.antarmuka.utama

import androidx.lifecycle.ViewModel
import id.cassy.kasir.ranah.contoh.KatalogProdukContoh
import id.cassy.kasir.ranah.fungsi.cariProduk
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
 * Pengelola status layar utama kasir.
 *
 * ViewModel ini menjadi pemilik status layar yang mengelola:
 * - Daftar item dalam keranjang belanja.
 * - Kata kunci pencarian produk.
 * - Visibilitas panel rincian pembayaran.
 *
 * Seluruh pembaruan status dilakukan secara atomik menggunakan fungsi [update]
 * untuk menjaga konsistensi data pada aliran data satu arah (UDF).
 */
class LayarUtamaKasirViewModel : ViewModel() {

    private val daftarProdukPenuh: List<Produk> = KatalogProdukContoh.daftarAwal()

    private val _modelTampilan = MutableStateFlow(
        bentukModelTampilan(
            daftarItemKeranjang = emptyList(),
            kataKunciPencarian = "",
            apakahRingkasanPembayaranTampil = true,
        ),
    )

    /**
     * Aliran status UI yang dapat diamati oleh komponen antarmuka.
     */
    val modelTampilan: StateFlow<ModelTampilanLayarUtamaKasir> = _modelTampilan.asStateFlow()

    /**
     * Memproses aksi yang dikirimkan dari lapisan antarmuka.
     *
     * @param aksi Objek [AksiLayarUtamaKasir] yang mewakili interaksi pengguna.
     */
    fun tanganiAksi(aksi: AksiLayarUtamaKasir) {
        when (aksi) {
            is AksiLayarUtamaKasir.UbahKataKunciPencarian -> {
                perbaruiPencarian(aksi.kataKunciBaru)
            }

            AksiLayarUtamaKasir.UbahVisibilitasRingkasanPembayaran -> {
                alihkanVisibilitasPembayaran()
            }

            is AksiLayarUtamaKasir.TambahProdukKeKeranjang -> {
                tambahProdukKeKeranjang(aksi.produkId)
            }
        }
    }

    private fun perbaruiPencarian(kataKunci: String) {
        if (_modelTampilan.value.kataKunciPencarian == kataKunci) return

        _modelTampilan.update { statusLama ->
            bentukModelTampilan(
                daftarItemKeranjang = statusLama.daftarItemKeranjang,
                kataKunciPencarian = kataKunci,
                apakahRingkasanPembayaranTampil = statusLama.apakahRingkasanPembayaranTampil,
            )
        }
    }

    private fun alihkanVisibilitasPembayaran() {
        _modelTampilan.update { statusLama ->
            bentukModelTampilan(
                daftarItemKeranjang = statusLama.daftarItemKeranjang,
                kataKunciPencarian = statusLama.kataKunciPencarian,
                apakahRingkasanPembayaranTampil = !statusLama.apakahRingkasanPembayaranTampil,
            )
        }
    }

    private fun tambahProdukKeKeranjang(produkId: String) {
        val produkTarget = daftarProdukPenuh.firstOrNull { produk ->
            produk.id == produkId && produk.aktif && produk.stokTersedia > 0
        } ?: return

        _modelTampilan.update { statusLama ->
            val daftarBaru = statusLama.daftarItemKeranjang.toMutableList()
            val indeksLama = daftarBaru.indexOfFirst { it.produk.id == produkId }

            if (indeksLama >= 0) {
                val itemLama = daftarBaru[indeksLama]
                if (itemLama.jumlah < produkTarget.stokTersedia) {
                    daftarBaru[indeksLama] = itemLama.copy(jumlah = itemLama.jumlah + 1)
                }
            } else {
                daftarBaru.add(ItemKeranjang(produk = produkTarget, jumlah = 1))
            }

            bentukModelTampilan(
                daftarItemKeranjang = daftarBaru,
                kataKunciPencarian = statusLama.kataKunciPencarian,
                apakahRingkasanPembayaranTampil = statusLama.apakahRingkasanPembayaranTampil,
            )
        }
    }

    private fun bentukModelTampilan(
        daftarItemKeranjang: List<ItemKeranjang>,
        kataKunciPencarian: String,
        apakahRingkasanPembayaranTampil: Boolean,
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
                totalBelanjaSementara = subtotal.sebagaiRupiahSederhana(),
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
                subtotal = subtotal.sebagaiRupiahSederhana(),
                potongan = "Rp0",
                pajak = "Rp0",
                totalAkhir = total.sebagaiRupiahSederhana(),
                labelAksiUtama = if (jumlahItem > 0) "Lanjut Pembayaran" else "Pilih Produk",
                aksiUtamaAktif = jumlahItem > 0,
            ),
            kataKunciPencarian = kataKunciPencarian,
            apakahRingkasanPembayaranTampil = apakahRingkasanPembayaranTampil,
        )
    }

    private fun Long.sebagaiRupiahSederhana(): String = "Rp$this"
}
