package id.cassy.kasir.ranah.kasuspenggunaan

import id.cassy.kasir.data.lokal.repositori.RepositoriTransaksi
import id.cassy.kasir.ranah.model.Transaksi
import kotlinx.coroutines.flow.Flow

/**
 * Kasus penggunaan untuk mengamati satu transaksi berdasarkan ID.
 *
 * Dipakai oleh layar detail transaksi agar pembacaan detail tidak
 * menembus repository langsung dari ViewModel.
 */
class AmatiTransaksiBerdasarkanId(
    private val repositoriTransaksi: RepositoriTransaksi,
) {
    operator fun invoke(
        transaksiId: String,
    ): Flow<Transaksi?> {
        return repositoriTransaksi.amatiTransaksiBerdasarkanId(transaksiId)
    }
}
