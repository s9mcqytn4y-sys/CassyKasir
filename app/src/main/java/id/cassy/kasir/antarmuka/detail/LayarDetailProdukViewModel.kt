package id.cassy.kasir.antarmuka.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.cassy.kasir.antarmuka.navigasi.TujuanNavigasiKasir
import id.cassy.kasir.ranah.contoh.KatalogProdukContoh
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    val modelTampilan = _modelTampilan.asStateFlow()

    private val _efek = MutableSharedFlow<EfekLayarDetailProduk>(
        extraBufferCapacity = 1,
    )
    val efek: SharedFlow<EfekLayarDetailProduk> = _efek.asSharedFlow()

    init {
        muatDetailProduk()
    }

    /**
     * Titik masuk tunggal untuk aksi UI layar detail produk.
     */
    fun tanganiAksi(aksi: AksiLayarDetailProduk) {
        when (aksi) {
            AksiLayarDetailProduk.CobaTambahKeKeranjang -> {
                cobaTambahKeKeranjang()
            }

            AksiLayarDetailProduk.CobaMuatUlang -> {
                muatDetailProduk()
            }
        }
    }

    fun muatUlang() {
        muatDetailProduk()
    }

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
                            statusAksi = StatusAksiDetailProduk(
                                label = if (aksiTambahAktif) {
                                    "Tambah ke Keranjang"
                                } else {
                                    "Produk Tidak Tersedia"
                                },
                                aktif = aksiTambahAktif,
                                keterangan = if (aksiTambahAktif) {
                                    "Produk siap ditambahkan ke transaksi aktif."
                                } else {
                                    "Produk ini tidak bisa ditambahkan karena sedang tidak tersedia."
                                },
                            ),
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

    private fun cobaTambahKeKeranjang() {
        val statusMuatSaatIni = _modelTampilan.value.statusMuat
        if (statusMuatSaatIni !is StatusMuatDetailProduk.Berhasil) return
        if (!statusMuatSaatIni.statusAksi.aktif) return

        _efek.tryEmit(
            EfekLayarDetailProduk.MintaTambahKeKeranjang(
                produkId = produkId,
                namaProduk = statusMuatSaatIni.namaProduk,
            ),
        )
    }
}
