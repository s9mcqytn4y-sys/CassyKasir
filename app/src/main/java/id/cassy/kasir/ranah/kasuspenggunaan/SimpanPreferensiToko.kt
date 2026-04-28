package id.cassy.kasir.ranah.kasuspenggunaan

import id.cassy.kasir.ranah.model.PreferensiToko
import id.cassy.kasir.ranah.repositori.RepositoriPreferensiToko

/**
 * Kasus penggunaan untuk memperbarui pengaturan atau preferensi toko.
 */
class SimpanPreferensiToko(
    private val repositoriPreferensiToko: RepositoriPreferensiToko,
) {
    /**
     * Menyimpan data preferensi toko baru.
     *
     * @param preferensiToko Objek model preferensi yang akan disimpan.
     */
    suspend operator fun invoke(
        preferensiToko: PreferensiToko,
    ) {
        repositoriPreferensiToko.simpanPreferensiToko(
            preferensiToko = preferensiToko,
        )
    }
}
