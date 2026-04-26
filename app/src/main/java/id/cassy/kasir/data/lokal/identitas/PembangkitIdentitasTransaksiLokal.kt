package id.cassy.kasir.data.lokal.identitas

import java.util.UUID

/**
 * Pembangkit identitas transaksi lokal.
 *
 * Identitas internal memakai UUID agar stabil untuk Room dan aman dipakai
 * lintas layer.
 */
object PembangkitIdentitasTransaksiLokal {

    fun buatIdentitasBaru(): String {
        return UUID.randomUUID().toString()
    }
}
