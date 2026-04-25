package id.cassy.kasir.antarmuka.utama

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.cassy.kasir.ranah.contoh.KatalogProdukContoh
import id.cassy.kasir.ranah.kasuspenggunaan.BentukModelTampilanLayarUtamaKasir
import id.cassy.kasir.ranah.kasuspenggunaan.HapusProdukDariKeranjang
import id.cassy.kasir.ranah.kasuspenggunaan.KurangiProdukDiKeranjang
import id.cassy.kasir.ranah.kasuspenggunaan.SelesaikanCheckoutLokalKasir
import id.cassy.kasir.ranah.kasuspenggunaan.TambahProdukKeKeranjang
import id.cassy.kasir.ranah.model.Produk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
 * Pengelola status layar utama kasir dengan pola alur data satu arah.
 *
 * State internal dipisah menjadi:
 * - status transaksi
 * - status elemen layar
 *
 * Efek sekali pakai dipisah ke SharedFlow agar pesan sementara
 * tidak mencemari state layar persisten.
 */
class LayarUtamaKasirViewModel : ViewModel() {

    private val daftarProdukPenuh: List<Produk> = KatalogProdukContoh.daftarAwal()

    private val tambahProdukKeKeranjangUseCase = TambahProdukKeKeranjang()
    private val kurangiProdukDiKeranjangUseCase = KurangiProdukDiKeranjang()
    private val hapusProdukDariKeranjangUseCase = HapusProdukDariKeranjang()
    private val selesaikanCheckoutLokalKasirUseCase = SelesaikanCheckoutLokalKasir()
    private val bentukModelTampilanUseCase = BentukModelTampilanLayarUtamaKasir()

    private val _statusTransaksi = MutableStateFlow(
        StatusTransaksiLayarUtamaKasir(),
    )

    private val _statusElemenLayar = MutableStateFlow(
        StatusElemenLayarUtamaKasir(),
    )

    private val _efek = MutableSharedFlow<EfekLayarUtamaKasir>(
        extraBufferCapacity = 1,
    )
    val efek: SharedFlow<EfekLayarUtamaKasir> = _efek.asSharedFlow()

    /**
     * Aliran status UI publik yang dirender oleh Compose.
     */
    val modelTampilan = combine(
        _statusTransaksi,
        _statusElemenLayar,
    ) { statusTransaksi, statusElemenLayar ->
        bentukModelTampilanUseCase(
            daftarProdukPenuh = daftarProdukPenuh,
            statusTransaksi = statusTransaksi,
            statusElemenLayar = statusElemenLayar,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = bentukModelTampilanUseCase(
            daftarProdukPenuh = daftarProdukPenuh,
            statusTransaksi = StatusTransaksiLayarUtamaKasir(),
            statusElemenLayar = StatusElemenLayarUtamaKasir(),
        ),
    )

    /**
     * Titik masuk tunggal untuk semua aksi dari UI.
     */
    fun tanganiAksi(aksi: AksiLayarUtamaKasir) {
        when (aksi) {
            is AksiLayarUtamaKasir.UbahKataKunciPencarian -> perbaruiPencarian(aksi.kataKunciBaru)
            AksiLayarUtamaKasir.UbahVisibilitasRingkasanPembayaran -> alihkanVisibilitasPembayaran()
            is AksiLayarUtamaKasir.TambahProdukKeKeranjang -> tambahProdukKeKeranjang(aksi.produkId)
            is AksiLayarUtamaKasir.KurangiProdukDiKeranjang -> kurangiProdukDiKeranjang(aksi.produkId)
            is AksiLayarUtamaKasir.HapusProdukDariKeranjang -> hapusProdukDariKeranjang(aksi.produkId)
            AksiLayarUtamaKasir.CobaCheckout -> cobaCheckout()
            AksiLayarUtamaKasir.BatalkanKonfirmasiCheckout -> batalkanKonfirmasiCheckout()
            AksiLayarUtamaKasir.KonfirmasiCheckout -> konfirmasiCheckout()
            AksiLayarUtamaKasir.TutupStatusHasilCheckout -> tutupStatusHasilCheckout()
        }
    }

    private fun perbaruiPencarian(kataKunciBaru: String) {
        _statusElemenLayar.update { statusLama ->
            statusLama.copy(
                kataKunciPencarian = kataKunciBaru,
            )
        }
    }

    private fun alihkanVisibilitasPembayaran() {
        _statusElemenLayar.update { statusLama ->
            statusLama.copy(
                apakahRingkasanPembayaranTampil = !statusLama.apakahRingkasanPembayaranTampil,
            )
        }
    }

    /**
     * Menambahkan produk ke dalam keranjang belanja.
     * Jika produk sudah ada, jumlahnya akan bertambah selama stok mencukupi.
     *
     * @param produkId ID unik dari produk yang ingin ditambahkan.
     */
    private fun tambahProdukKeKeranjang(produkId: String) {
        val produk = daftarProdukPenuh.firstOrNull { it.id == produkId } ?: return

        var apakahStokPenuh = false

        _statusTransaksi.update { statusLama ->
            val daftarLama = statusLama.daftarItemKeranjang
            val daftarBaru = tambahProdukKeKeranjangUseCase(
                daftarItemKeranjang = daftarLama,
                produk = produk,
            )

            apakahStokPenuh = daftarBaru == daftarLama

            statusLama.copy(
                daftarItemKeranjang = daftarBaru,
                statusSinkronisasi = "Tersimpan Lokal",
            )
        }

        _statusElemenLayar.update { statusLama ->
            statusLama.copy(
                statusHasilCheckout = StatusHasilCheckoutKasir(),
            )
        }

        if (apakahStokPenuh) {
            kirimPesanSingkat("Stok produk sudah mencapai batas maksimum.")
        }
    }

    /**
     * Mengurangi satu satuan jumlah produk di dalam keranjang.
     * Jika jumlah menjadi nol, item akan dihapus dari keranjang.
     *
     * @param produkId ID unik dari produk yang ingin dikurangi.
     */
    private fun kurangiProdukDiKeranjang(produkId: String) {
        _statusTransaksi.update { statusLama ->
            statusLama.copy(
                daftarItemKeranjang = kurangiProdukDiKeranjangUseCase(
                    daftarItemKeranjang = statusLama.daftarItemKeranjang,
                    produkId = produkId,
                ),
                statusSinkronisasi = "Tersimpan Lokal",
            )
        }

        resetStatusHasil()
    }

    /**
     * Menghapus seluruh jumlah produk tertentu dari keranjang belanja.
     *
     * @param produkId ID unik dari produk yang ingin dihapus.
     */
    private fun hapusProdukDariKeranjang(produkId: String) {
        _statusTransaksi.update { statusLama ->
            statusLama.copy(
                daftarItemKeranjang = hapusProdukDariKeranjangUseCase(
                    daftarItemKeranjang = statusLama.daftarItemKeranjang,
                    produkId = produkId,
                ),
                statusSinkronisasi = "Tersimpan Lokal",
            )
        }

        resetStatusHasil()
    }

    /**
     * Memvalidasi keranjang sebelum menampilkan dialog konfirmasi checkout.
     */
    private fun cobaCheckout() {
        if (_statusTransaksi.value.daftarItemKeranjang.isEmpty()) {
            kirimPesanSingkat("Keranjang masih kosong. Yuk, tambah produk!")
            return
        }

        _statusElemenLayar.update { statusLama ->
            statusLama.copy(
                apakahDialogKonfirmasiCheckoutTampil = true,
                statusHasilCheckout = StatusHasilCheckoutKasir(),
            )
        }
    }

    /**
     * Menutup dialog konfirmasi tanpa memproses transaksi.
     */
    private fun batalkanKonfirmasiCheckout() {
        _statusElemenLayar.update { statusLama ->
            statusLama.copy(
                apakahDialogKonfirmasiCheckoutTampil = false,
            )
        }
    }

    /**
     * Memproses transaksi dari keranjang ke status selesai secara lokal.
     */
    private fun konfirmasiCheckout() {
        val daftarKeranjangSaatIni = _statusTransaksi.value.daftarItemKeranjang
        if (daftarKeranjangSaatIni.isEmpty()) {
            kirimPesanSingkat("Keranjang masih kosong. Yuk, tambah produk!")
            return
        }

        val hasilCheckout = selesaikanCheckoutLokalKasirUseCase(
            daftarItemKeranjang = daftarKeranjangSaatIni,
        )

        _statusTransaksi.update {
            StatusTransaksiLayarUtamaKasir(
                daftarItemKeranjang = hasilCheckout.daftarItemKeranjangBaru,
                statusSinkronisasi = hasilCheckout.statusSinkronisasiBaru,
            )
        }

        _statusElemenLayar.update { statusLama ->
            statusLama.copy(
                apakahDialogKonfirmasiCheckoutTampil = false,
                statusHasilCheckout = StatusHasilCheckoutKasir(
                    apakahTampil = true,
                    judul = "Transaksi Berhasil",
                    deskripsi = "Sebanyak ${hasilCheckout.jumlahItemCheckout} item dengan total ${hasilCheckout.totalCheckout.sebagaiRupiahSederhana()} telah diproses.",
                ),
            )
        }
    }

    private fun tutupStatusHasilCheckout() {
        _statusElemenLayar.update { statusLama ->
            statusLama.copy(
                statusHasilCheckout = StatusHasilCheckoutKasir(),
            )
        }
    }

    private fun resetStatusHasil() {
        _statusElemenLayar.update { statusLama ->
            statusLama.copy(
                statusHasilCheckout = StatusHasilCheckoutKasir(),
            )
        }
    }

    /**
     * Menampilkan pesan operasional singkat pada layar utama.
     *
     * Fungsi ini dipakai untuk hasil aksi lintas layar yang perlu
     * ditampilkan sebagai snackbar setelah user kembali ke layar utama.
     */
    fun tampilkanPesanOperasional(
        pesan: String,
    ) {
        if (pesan.isBlank()) return
        kirimPesanSingkat(pesan)
    }

    private fun kirimPesanSingkat(pesan: String) {
        _efek.tryEmit(
            EfekLayarUtamaKasir.TampilkanPesanSingkat(
                pesan = pesan,
            ),
        )
    }
}

/**
 * Memformat nilai Long menjadi string representasi Rupiah sederhana.
 */
private fun Long.sebagaiRupiahSederhana(): String {
    return "Rp$this"
}
