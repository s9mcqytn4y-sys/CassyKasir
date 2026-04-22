package id.cassy.kasir.ranah.fungsi

import id.cassy.kasir.ranah.model.ItemKeranjang

/**
 * Kumpulan logika perhitungan keuangan untuk alur transaksi.
 */

/**
 * Menghitung nominal akhir yang harus dibayarkan pelanggan.
 *
 * @param daftarItemKeranjang List belanjaan.
 * @param potongan Nilai pengurangan harga.
 * @param biayaLayanan Nilai penambahan biaya.
 * @param pajak Nilai pajak.
 * @return Total akhir (minimal 0).
 */
fun hitungTotalTransaksi(
    daftarItemKeranjang: List<ItemKeranjang>,
    potongan: Long,
    biayaLayanan: Long,
    pajak: Long,
): Long {
    val subTotal = daftarItemKeranjang.hitungSubtotalKeranjang()
    return (subTotal - potongan + biayaLayanan + pajak).coerceAtLeast(0)
}

/**
 * Menghitung sisa uang yang harus dikembalikan ke pelanggan.
 *
 * @param uangDibayar Nominal uang dari pelanggan.
 * @param totalTransaksi Kewajiban bayar.
 * @return Nilai kembalian (minimal 0).
 */
fun hitungKembalian(
    uangDibayar: Long,
    totalTransaksi: Long,
): Long {
    return (uangDibayar - totalTransaksi).coerceAtLeast(0)
}

/**
 * Memvalidasi apakah sebuah transaksi sudah sah secara logika untuk disimpan.
 *
 * @return True jika keranjang tidak kosong, semua item valid, dan uang pembayaran mencukupi.
 */
fun transaksiSiapDiproses(
    daftarItemKeranjang: List<ItemKeranjang>,
    uangDibayar: Long,
    potongan: Long = 0,
    biayaLayanan: Long = 0,
    pajak: Long = 0,
): Boolean {
    if (daftarItemKeranjang.isEmpty()) {
        return false
    }

    if (daftarItemKeranjang.any { it.jumlah <= 0 }) {
        return false
    }

    if (daftarItemKeranjang.any { !it.produk.aktif }) {
        return false
    }

    val totalTransaksi = hitungTotalTransaksi(
        daftarItemKeranjang = daftarItemKeranjang,
        potongan = potongan,
        biayaLayanan = biayaLayanan,
        pajak = pajak,
    )

    return uangDibayar >= totalTransaksi
}

