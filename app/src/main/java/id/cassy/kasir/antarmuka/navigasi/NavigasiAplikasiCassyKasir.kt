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
import id.cassy.kasir.antarmuka.detail.AksiLayarDetailProduk
import id.cassy.kasir.antarmuka.detail.EfekLayarDetailProduk
import id.cassy.kasir.antarmuka.detail.LayarDetailProduk
import id.cassy.kasir.antarmuka.detail.LayarDetailProdukViewModel
import id.cassy.kasir.antarmuka.riwayat.LayarRiwayatTransaksi
import id.cassy.kasir.antarmuka.riwayat.LayarRiwayatTransaksiViewModel
import id.cassy.kasir.antarmuka.transaksi.LayarDetailTransaksi
import id.cassy.kasir.antarmuka.transaksi.LayarDetailTransaksiViewModel
import id.cassy.kasir.antarmuka.utama.AksiLayarUtamaKasir
import id.cassy.kasir.antarmuka.utama.LayarUtamaKasir
import id.cassy.kasir.antarmuka.utama.LayarUtamaKasirViewModel
import id.cassy.kasir.antarmuka.PenyediaViewModelKasir
import kotlinx.coroutines.flow.collectLatest

/**
 * Komposabel pengelola navigasi antar layar di seluruh aplikasi.
 * Mendefinisikan [NavHost] dan rute untuk setiap layar seperti Utama, Riwayat, dan Detail.
 */
@Composable
fun NavigasiAplikasiCassyKasir() {
    val pengendaliNavigasi = rememberNavController()

    val layarUtamaKasirViewModel: LayarUtamaKasirViewModel = viewModel(
        factory = PenyediaViewModelKasir.Factory,
    )
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
            val layarRiwayatTransaksiViewModel: LayarRiwayatTransaksiViewModel = viewModel(
                factory = PenyediaViewModelKasir.Factory,
            )
            val modelTampilanRiwayat = layarRiwayatTransaksiViewModel.modelTampilan.collectAsStateWithLifecycle()

            LayarRiwayatTransaksi(
                modelTampilan = modelTampilanRiwayat.value,
                saatKembali = {
                    pengendaliNavigasi.navigateUp()
                },
                saatBukaDetailTransaksi = { transaksiId ->
                    pengendaliNavigasi.bukaDetailTransaksi(transaksiId)
                },
                saatCobaMuatUlang = layarRiwayatTransaksiViewModel::muatUlang,
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

            LaunchedEffect(layarDetailProdukViewModel) {
                layarDetailProdukViewModel.efek.collectLatest { efek ->
                    when (efek) {
                        is EfekLayarDetailProduk.MintaTambahKeKeranjang -> {
                            layarUtamaKasirViewModel.tanganiAksi(
                                AksiLayarUtamaKasir.TambahProdukKeKeranjang(
                                    produkId = efek.produkId,
                                ),
                            )

                            pengendaliNavigasi.previousBackStackEntry
                                ?.savedStateHandle
                                ?.simpanPesanTambahProdukDariDetail(
                                    pesan = "${efek.namaProduk} ditambahkan ke keranjang.",
                                )

                            pengendaliNavigasi.navigateUp()
                        }
                    }
                }
            }

            LayarDetailProduk(
                modelTampilan = modelTampilanDetail.value,
                saatKembali = {
                    pengendaliNavigasi.navigateUp()
                },
                saatAksiDikirim = layarDetailProdukViewModel::tanganiAksi,
            )
        }

        composable(
            route = TujuanNavigasiKasir.DetailTransaksi.rute,
            arguments = listOf(
                navArgument(TujuanNavigasiKasir.DetailTransaksi.namaArgumenTransaksiId) {
                    type = NavType.StringType
                },
            ),
        ) {
            val layarDetailTransaksiViewModel: LayarDetailTransaksiViewModel = viewModel(
                factory = PenyediaViewModelKasir.Factory,
            )
            val modelTampilanDetailTransaksi = layarDetailTransaksiViewModel.modelTampilan.collectAsStateWithLifecycle()

            LayarDetailTransaksi(
                modelTampilan = modelTampilanDetailTransaksi.value,
                saatKembali = {
                    pengendaliNavigasi.navigateUp()
                },
                saatCobaMuatUlang = layarDetailTransaksiViewModel::muatUlang,
            )
        }
    }
}
