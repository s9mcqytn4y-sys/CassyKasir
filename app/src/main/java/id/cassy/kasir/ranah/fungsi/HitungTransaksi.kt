package id.cassy.kasir.ranah.fungsi

import id.cassy.kasir.ranah.model.ItemKeranjang

fun hitungTotalTransaksi(
    daftarItemKeranjang: List<ItemKeranjang>,
    potongan: Long,
    biayaLayanan: Long,
    pajak: Long
): Long {
    val subTotal = daftarItemKeranjang.hitungSubtotalKeranjang()
    return (subTotal - potongan + biayaLayanan - pajak).coerceAtLeast(0)
}

fun hitungKembalian(
    uangDibayar: Long,
    totalTransaksi: Long
): Long {
    return (uangDibayar - totalTransaksi).coerceAtLeast(0)
}

fun transaksiSiapDiproses(
    daftarItemKeranjang: List<ItemKeranjang>,
    uangDibayar: Long,
    potongan: Long = 0,
    biayaLayanan: Long = 0,
    pajak: Long = 0
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

