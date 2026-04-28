package id.cassy.kasir.antarmuka.utama

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.cassy.kasir.antarmuka.komponen.StatusKosongSederhana

/**
 * Tata letak khusus untuk perangkat dengan lebar layar terbatas (ponsel).
 *
 * @param modelTampilan Representasi status layar yang akan dirender.
 * @param saatAksiDikirim Callback untuk mengirimkan aksi pengguna ke ViewModel.
 * @param saatBukaRiwayatTransaksi Callback saat navigasi ke riwayat transaksi dipicu.
 * @param saatBukaDetailProduk Callback saat navigasi ke detail produk dipicu.
 * @param modifier Modifier untuk kustomisasi tata letak.
 */
@Composable
internal fun TataLetakPonselKasir(
    modelTampilan: ModelTampilanLayarUtamaKasir,
    saatAksiDikirim: (AksiLayarUtamaKasir) -> Unit,
    saatBukaRiwayatTransaksi: () -> Unit,
    saatBukaDetailProduk: (String) -> Unit,
    saatBukaKelolaProduk: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            HeaderBerandaKasir(
                namaAplikasi = modelTampilan.statusBeranda.namaAplikasi,
                sloganAplikasi = modelTampilan.statusBeranda.sloganAplikasi,
                saatBukaRiwayatTransaksi = saatBukaRiwayatTransaksi,
                saatBukaKelolaProduk = saatBukaKelolaProduk,
            )
        }

        item {
            BagianPencarianProdukKasir(
                nilaiPencarian = modelTampilan.kataKunciPencarian,
                saatNilaiPencarianBerubah = { kataKunciBaru ->
                    saatAksiDikirim(
                        AksiLayarUtamaKasir.UbahKataKunciPencarian(
                            kataKunciBaru = kataKunciBaru,
                        ),
                    )
                },
                jumlahHasil = modelTampilan.daftarProdukTersaring.size,
                tampilkanAksiResetPencarian = modelTampilan.tampilkanAksiResetPencarian,
                saatResetPencarian = {
                    saatAksiDikirim(AksiLayarUtamaKasir.ResetPencarian)
                },
            )
        }

        item {
            RingkasanKasir(
                statusBeranda = modelTampilan.statusBeranda,
            )
        }

        if (modelTampilan.statusHasilCheckout.apakahTampil) {
            item {
                KartuHasilCheckoutKasir(
                    statusHasilCheckout = modelTampilan.statusHasilCheckout,
                    saatTutup = {
                        saatAksiDikirim(AksiLayarUtamaKasir.TutupStatusHasilCheckout)
                    },
                )
            }
        }

        item {
            PanelKeranjangKasir(
                daftarItemKeranjang = modelTampilan.daftarItemKeranjang,
                statusKeranjang = modelTampilan.statusKeranjang,
                saatTambahProduk = { produkId ->
                    saatAksiDikirim(
                        AksiLayarUtamaKasir.TambahProdukKeKeranjang(
                            produkId = produkId,
                        ),
                    )
                },
                saatKurangiProduk = { produkId ->
                    saatAksiDikirim(
                        AksiLayarUtamaKasir.KurangiProdukDiKeranjang(
                            produkId = produkId,
                        ),
                    )
                },
                saatHapusProduk = { produkId ->
                    saatAksiDikirim(
                        AksiLayarUtamaKasir.HapusProdukDariKeranjang(
                            produkId = produkId,
                        ),
                    )
                },
            )
        }

        item {
            BagianRingkasanPembayaranKasir(
                ringkasanPembayaran = modelTampilan.ringkasanPembayaran,
                apakahRingkasanPembayaranTampil = modelTampilan.apakahRingkasanPembayaranTampil,
                saatUbahVisibilitasRingkasanPembayaran = {
                    saatAksiDikirim(
                        AksiLayarUtamaKasir.UbahVisibilitasRingkasanPembayaran,
                    )
                },
                saatCheckout = {
                    saatAksiDikirim(AksiLayarUtamaKasir.CobaCheckout)
                },
            )
        }

        item {
            JudulBagianKasir(
                judul = "Katalog produk",
            )
        }

        if (modelTampilan.daftarProdukTersaring.isEmpty()) {
            item {
                StatusKosongSederhana(
                    judul = "Produk tidak ditemukan",
                    deskripsi = "Coba gunakan kata kunci lain.",
                )
            }
        } else {
            items(
                items = modelTampilan.daftarProdukTersaring,
                key = { produk -> produk.id },
                contentType = { "KartuProduk" },
            ) { produk ->
                KartuProdukKasir(
                    produk = produk,
                    saatTambahProduk = {
                        saatAksiDikirim(
                            AksiLayarUtamaKasir.TambahProdukKeKeranjang(
                                produkId = produk.id,
                            ),
                        )
                    },
                    saatBukaDetailProduk = {
                        saatBukaDetailProduk(produk.id)
                    },
                )
            }
        }
    }
}

/**
 * Tata letak khusus untuk perangkat dengan layar lebar (tablet atau desktop).
 *
 * @param modelTampilan Representasi status layar yang akan dirender.
 * @param saatAksiDikirim Callback untuk mengirimkan aksi pengguna ke ViewModel.
 * @param saatBukaRiwayatTransaksi Callback saat navigasi ke riwayat transaksi dipicu.
 * @param saatBukaDetailProduk Callback saat navigasi ke detail produk dipicu.
 * @param modifier Modifier untuk kustomisasi tata letak.
 */
@Composable
internal fun TataLetakTabletKasir(
    modelTampilan: ModelTampilanLayarUtamaKasir,
    saatAksiDikirim: (AksiLayarUtamaKasir) -> Unit,
    saatBukaRiwayatTransaksi: () -> Unit,
    saatBukaDetailProduk: (String) -> Unit,
    saatBukaKelolaProduk: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1.35f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                HeaderBerandaKasir(
                    namaAplikasi = modelTampilan.statusBeranda.namaAplikasi,
                    sloganAplikasi = modelTampilan.statusBeranda.sloganAplikasi,
                    saatBukaRiwayatTransaksi = saatBukaRiwayatTransaksi,
                    saatBukaKelolaProduk = saatBukaKelolaProduk,
                )
            }

            item {
                BagianPencarianProdukKasir(
                    nilaiPencarian = modelTampilan.kataKunciPencarian,
                    saatNilaiPencarianBerubah = { kataKunciBaru ->
                        saatAksiDikirim(
                            AksiLayarUtamaKasir.UbahKataKunciPencarian(
                                kataKunciBaru = kataKunciBaru,
                            ),
                        )
                    },
                    jumlahHasil = modelTampilan.daftarProdukTersaring.size,
                    tampilkanAksiResetPencarian = modelTampilan.tampilkanAksiResetPencarian,
                    saatResetPencarian = {
                        saatAksiDikirim(AksiLayarUtamaKasir.ResetPencarian)
                    },
                )
            }

            item {
                JudulBagianKasir(
                    judul = "Katalog produk",
                )
            }

            if (modelTampilan.daftarProdukTersaring.isEmpty()) {
                item {
                    StatusKosongSederhana(
                        judul = "Produk tidak ditemukan",
                        deskripsi = "Coba gunakan kata kunci lain.",
                    )
                }
            } else {
                items(
                    items = modelTampilan.daftarProdukTersaring,
                    key = { produk -> produk.id },
                    contentType = { "KartuProduk" },
                ) { produk ->
                    KartuProdukKasir(
                        produk = produk,
                        saatTambahProduk = {
                            saatAksiDikirim(
                                AksiLayarUtamaKasir.TambahProdukKeKeranjang(
                                    produkId = produk.id,
                                ),
                            )
                        },
                        saatBukaDetailProduk = {
                            saatBukaDetailProduk(produk.id)
                        },
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(0.95f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            RingkasanKasir(
                statusBeranda = modelTampilan.statusBeranda,
            )

            if (modelTampilan.statusHasilCheckout.apakahTampil) {
                KartuHasilCheckoutKasir(
                    statusHasilCheckout = modelTampilan.statusHasilCheckout,
                    saatTutup = {
                        saatAksiDikirim(AksiLayarUtamaKasir.TutupStatusHasilCheckout)
                    },
                )
            }

            PanelKeranjangKasir(
                daftarItemKeranjang = modelTampilan.daftarItemKeranjang,
                statusKeranjang = modelTampilan.statusKeranjang,
                saatTambahProduk = { produkId ->
                    saatAksiDikirim(
                        AksiLayarUtamaKasir.TambahProdukKeKeranjang(
                            produkId = produkId,
                        ),
                    )
                },
                saatKurangiProduk = { produkId ->
                    saatAksiDikirim(
                        AksiLayarUtamaKasir.KurangiProdukDiKeranjang(
                            produkId = produkId,
                        ),
                    )
                },
                saatHapusProduk = { produkId ->
                    saatAksiDikirim(
                        AksiLayarUtamaKasir.HapusProdukDariKeranjang(
                            produkId = produkId,
                        ),
                    )
                },
            )

            BagianRingkasanPembayaranKasir(
                ringkasanPembayaran = modelTampilan.ringkasanPembayaran,
                apakahRingkasanPembayaranTampil = modelTampilan.apakahRingkasanPembayaranTampil,
                saatUbahVisibilitasRingkasanPembayaran = {
                    saatAksiDikirim(
                        AksiLayarUtamaKasir.UbahVisibilitasRingkasanPembayaran,
                    )
                },
                saatCheckout = {
                    saatAksiDikirim(AksiLayarUtamaKasir.CobaCheckout)
                },
            )
        }
    }
}
