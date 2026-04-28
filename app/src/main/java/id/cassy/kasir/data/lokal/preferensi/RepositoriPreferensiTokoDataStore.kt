package id.cassy.kasir.data.lokal.preferensi

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import id.cassy.kasir.ranah.model.PreferensiToko
import id.cassy.kasir.ranah.repositori.RepositoriPreferensiToko
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Delegasi untuk mengakses DataStore Preferences.
 */
private val Context.dataStorePreferensiToko by preferencesDataStore(
    name = "preferensi_toko",
)

/**
 * Implementasi repository preferensi toko berbasis Jetpack DataStore Preferences.
 *
 * @property konteks Context Android untuk akses file DataStore.
 */
class RepositoriPreferensiTokoDataStore(
    private val konteks: Context,
) : RepositoriPreferensiToko {

    override fun amatiPreferensiToko(): Flow<PreferensiToko> {
        return konteks.dataStorePreferensiToko.data.map { preferensi ->
            PreferensiToko(
                namaToko = preferensi[KunciPreferensi.namaToko] ?: "Cassy Kasir",
                alamatToko = preferensi[KunciPreferensi.alamatToko] ?: "",
                basisPoinPajakDefault = preferensi[KunciPreferensi.basisPoinPajakDefault] ?: 0,
                biayaLayananDefault = preferensi[KunciPreferensi.biayaLayananDefault] ?: 0L,
            )
        }
    }

    override suspend fun simpanPreferensiToko(
        preferensiToko: PreferensiToko,
    ) {
        konteks.dataStorePreferensiToko.edit { preferensi ->
            preferensi[KunciPreferensi.namaToko] = preferensiToko.namaToko.trim()
            preferensi[KunciPreferensi.alamatToko] = preferensiToko.alamatToko.trim()
            preferensi[KunciPreferensi.basisPoinPajakDefault] =
                preferensiToko.basisPoinPajakDefault
            preferensi[KunciPreferensi.biayaLayananDefault] =
                preferensiToko.biayaLayananDefault
        }
    }

    /**
     * Kunci identifikasi untuk setiap item data di DataStore.
     */
    private object KunciPreferensi {
        val namaToko = stringPreferencesKey("nama_toko")
        val alamatToko = stringPreferencesKey("alamat_toko")
        val basisPoinPajakDefault = intPreferencesKey("basis_poin_pajak_default")
        val biayaLayananDefault = longPreferencesKey("biaya_layanan_default")
    }
}
