package id.cassy.kasir.data.lokal.identitas

import java.util.UUID

/**
 * Pembangkit ID transaksi lokal.
 *
 * Standar yang dipakai CassyKasir saat ini:
 * - UUID string sebagai identitas internal transaksi
 * - stabil untuk Room
 * - aman dipakai lintas layer
 * - tidak dipakai langsung sebagai label ramah manusia
 */
object PembangkitIdTransaksiLokal {

    fun buatIdBaru(): String {
        return UUID.randomUUID().toString()
    }
}
