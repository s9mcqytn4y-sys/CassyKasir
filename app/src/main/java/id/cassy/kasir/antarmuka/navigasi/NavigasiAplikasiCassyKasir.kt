package id.cassy.kasir.antarmuka.navigasi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import id.cassy.kasir.antarmuka.detail.LayarDetailProduk
import id.cassy.kasir.antarmuka.detail.LayarDetailProdukViewModel
import id.cassy.kasir.antarmuka.detail.StatusMuatDetailProduk
import id.cassy.kasir.antarmuka.riwayat.LayarRiwayatTransaksi
import id.cassy.kasir.antarmuka.utama.AksiLayarUtamaKasir
import id.cassy.kasir.antarmuka.utama.LayarUtamaKasir
import id.cassy.kasir.antarmuka.utama.LayarUtamaKasirViewModel

/**
 * Root navigasi aplikasi CassyKasir.
 *
 * File ini hanya bertanggung jawab pada:
 * - pemetaan destination
 * - pembuatan ViewModel per layar
 * - perpindahan antar layar
 */
@Composable
fun NavigasiAplikasiCassyKasir() {
    val pengendaliNavigasi = rememberNavController()

    // ViewModel utama dibuat di level root agar transaksi aktif bisa dibagikan
    val layarUtamaKasirViewModel: LayarUtamaKasirViewModel = viewModel()
    val modelTampilanKasir = layarUtamaKasirViewModel.modelTampilan.collectAsStateWithLifecycle()

    NavHost(
        navController = pengendaliNavigasi,
        startDestination = TujuanNavigasiKasir.KasirUtama.rute,
    ) {
        composable(
            route = TujuanNavigasiKasir.KasirUtama.rute,
        ) { entriBackStack ->
            val pesanTambahProdukDariDetail = entriBackStack
                .savedStateHandle
                .ambilAlurPesanTambahProdukDariDetail()
                .collectAsStateWithLifecycle()

            LaunchedEffect(pesanTambahProdukDariDetail.value) {
                val pesan = pesanTambahProdukDariDetail.value ?: return@LaunchedEffect

                layarUtamaKasirViewModel.tampilkanPesanOperasional(
                    pesan = pesan,
                )

                entriBackStack.savedStateHandle.konsumsiPesanTambahProdukDariDetail()
            }

            LayarUtamaKasir(
                modelTampilan = modelTampilanKasir.value,
                saatAksiDikirim = layarUtamaKasirViewModel::tanganiAksi,
                alurEfek = layarUtamaKasirViewModel.efek,
                saatBukaRiwayatTransaksi = {
                    pengendaliNavigasi.bukaRiwayatTransaksi()
                },
                saatBukaDetailProduk = { produkId ->
                    pengendaliNavigasi.bukaDetailProduk(produkId)
                },
            )
        }

        composable(
            route = TujuanNavigasiKasir.RiwayatTransaksi.rute,
        ) {
            LayarRiwayatTransaksi(
                saatKembali = {
                    pengendaliNavigasi.navigateUp()
                },
            )
        }

        composable(
            route = TujuanNavigasiKasir.DetailProduk.rute,
            arguments = listOf(
                navArgument(TujuanNavigasiKasir.DetailProduk.namaArgumenProdukId) {
                    type = NavType.StringType
                },
            ),
        ) {
            val layarDetailProdukViewModel: LayarDetailProdukViewModel = viewModel()
            val modelTampilanDetail = layarDetailProdukViewModel.modelTampilan.collectAsStateWithLifecycle()

            LayarDetailProduk(
                modelTampilan = modelTampilanDetail.value,
                saatKembali = {
                    pengendaliNavigasi.navigateUp()
                },
                saatCobaMuatUlang = layarDetailProdukViewModel::muatUlang,
                saatTambahKeKeranjang = { produkId ->
                    layarUtamaKasirViewModel.tanganiAksi(
                        AksiLayarUtamaKasir.TambahProdukKeKeranjang(
                            produkId = produkId,
                        ),
                    )

                    val pesanHasil = when (val statusMuat = modelTampilanDetail.value.statusMuat) {
                        is StatusMuatDetailProduk.Berhasil -> {
                            "${statusMuat.namaProduk} ditambahkan ke keranjang."
                        }

                        else -> {
                            "Produk ditambahkan ke keranjang."
                        }
                    }

                    pengendaliNavigasi.previousBackStackEntry
                        ?.savedStateHandle
                        ?.simpanPesanTambahProdukDariDetail(
                            pesan = pesanHasil,
                        )

                    pengendaliNavigasi.navigateUp()
                },
            )
        }
    }
}
