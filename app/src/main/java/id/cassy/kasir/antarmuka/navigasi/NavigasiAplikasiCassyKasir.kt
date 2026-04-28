package id.cassy.kasir.antarmuka.navigasi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import id.cassy.kasir.antarmuka.PenyediaViewModelKasir
import id.cassy.kasir.antarmuka.detail.EfekLayarDetailProduk
import id.cassy.kasir.antarmuka.detail.LayarDetailProduk
import id.cassy.kasir.antarmuka.detail.LayarDetailProdukViewModel
import id.cassy.kasir.antarmuka.kelola.LayarFormProduk
import id.cassy.kasir.antarmuka.kelola.LayarKelolaProduk
import id.cassy.kasir.antarmuka.kelola.ViewModelFormProduk
import id.cassy.kasir.antarmuka.kelola.ViewModelKelolaProduk
import id.cassy.kasir.antarmuka.riwayat.LayarRiwayatTransaksi
import id.cassy.kasir.antarmuka.riwayat.LayarRiwayatTransaksiViewModel
import id.cassy.kasir.antarmuka.transaksi.LayarDetailTransaksi
import id.cassy.kasir.antarmuka.transaksi.LayarDetailTransaksiViewModel
import id.cassy.kasir.antarmuka.utama.AksiLayarUtamaKasir
import id.cassy.kasir.antarmuka.utama.LayarUtamaKasir
import id.cassy.kasir.antarmuka.utama.LayarUtamaKasirViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull

/**
 * Komposabel pengelola navigasi utama aplikasi Cassy Kasir.
 *
 * Navigasi memakai route type-safe berbasis kotlinx.serialization.
 * Dengan cara ini, layar dan argumen navigasi direpresentasikan oleh tipe Kotlin,
 * bukan string route manual.
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
        startDestination = TujuanNavigasiKasir.KasirUtama,
    ) {
        composable<TujuanNavigasiKasir.KasirUtama> { entriBackStack ->
            LaunchedEffect(entriBackStack) {
                entriBackStack.savedStateHandle
                    .ambilAlurPesanTambahProdukDariDetail()
                    .filterNotNull()
                    .collectLatest { pesan ->
                        layarUtamaKasirViewModel.tampilkanPesanOperasional(
                            pesan = pesan,
                        )

                        entriBackStack.savedStateHandle.konsumsiPesanTambahProdukDariDetail()
                    }
            }

            LayarUtamaKasir(
                modelTampilan = modelTampilanKasir.value,
                saatAksiDikirim = layarUtamaKasirViewModel::tanganiAksi,
                alurEfek = layarUtamaKasirViewModel.efek,
                saatBukaRiwayatTransaksi = {
                    pengendaliNavigasi.bukaRiwayatTransaksi()
                },
                saatBukaDetailProduk = { identitasProduk ->
                    pengendaliNavigasi.bukaDetailProduk(
                        identitasProduk = identitasProduk,
                    )
                },
                saatBukaKelolaProduk = {
                    pengendaliNavigasi.bukaKelolaProduk()
                },
            )
        }

        composable<TujuanNavigasiKasir.RiwayatTransaksi> {
            val layarRiwayatTransaksiViewModel: LayarRiwayatTransaksiViewModel = viewModel(
                factory = PenyediaViewModelKasir.Factory,
            )

            val modelTampilanRiwayat =
                layarRiwayatTransaksiViewModel.modelTampilan.collectAsStateWithLifecycle()

            LayarRiwayatTransaksi(
                modelTampilan = modelTampilanRiwayat.value,
                saatKembali = {
                    pengendaliNavigasi.navigateUp()
                },
                saatBukaDetailTransaksi = { identitasTransaksi ->
                    pengendaliNavigasi.bukaDetailTransaksi(
                        identitasTransaksi = identitasTransaksi,
                    )
                },
                saatCobaMuatUlang = layarRiwayatTransaksiViewModel::muatUlang,
                saatKataKunciPencarianBerubah =
                    layarRiwayatTransaksiViewModel::perbaruiKataKunciPencarian,
                saatResetPencarian = layarRiwayatTransaksiViewModel::resetPencarian,
            )
        }

        composable<TujuanNavigasiKasir.DetailProduk> {
            val layarDetailProdukViewModel: LayarDetailProdukViewModel = viewModel(
                factory = PenyediaViewModelKasir.Factory,
            )

            val modelTampilanDetail =
                layarDetailProdukViewModel.modelTampilan.collectAsStateWithLifecycle()

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

        composable<TujuanNavigasiKasir.DetailTransaksi> {
            val layarDetailTransaksiViewModel: LayarDetailTransaksiViewModel = viewModel(
                factory = PenyediaViewModelKasir.Factory,
            )

            val modelTampilanDetailTransaksi =
                layarDetailTransaksiViewModel.modelTampilan.collectAsStateWithLifecycle()

            LayarDetailTransaksi(
                modelTampilan = modelTampilanDetailTransaksi.value,
                saatKembali = {
                    pengendaliNavigasi.navigateUp()
                },
                saatCobaMuatUlang = layarDetailTransaksiViewModel::muatUlang,
            )
        }

        composable<TujuanNavigasiKasir.KelolaProduk> {
            val viewModel: ViewModelKelolaProduk = viewModel(
                factory = PenyediaViewModelKasir.Factory,
            )
            LayarKelolaProduk(
                viewModel = viewModel,
                navigasiKembali = { pengendaliNavigasi.navigateUp() },
                navigasiKeTambahProduk = { pengendaliNavigasi.bukaFormProduk() },
                navigasiKeUbahProduk = { id -> pengendaliNavigasi.bukaFormProduk(id) }
            )
        }

        composable<TujuanNavigasiKasir.FormProduk> {
            val viewModel: ViewModelFormProduk = viewModel(
                factory = PenyediaViewModelKasir.Factory,
            )
            LayarFormProduk(
                viewModel = viewModel,
                navigasiKembali = { pengendaliNavigasi.navigateUp() }
            )
        }
    }
}
