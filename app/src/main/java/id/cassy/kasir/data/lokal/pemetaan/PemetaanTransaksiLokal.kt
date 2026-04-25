package id.cassy.kasir.data.lokal.pemetaan

import id.cassy.kasir.data.lokal.entitas.EntitasItemTransaksiLokal
import id.cassy.kasir.data.lokal.entitas.EntitasTransaksiLokal
import id.cassy.kasir.data.lokal.relasi.TransaksiDenganItemLokal
import id.cassy.kasir.ranah.model.ItemKeranjang
import id.cassy.kasir.ranah.model.Produk
import id.cassy.kasir.ranah.model.Transaksi

/**
 * Mengonversi model domain [Transaksi] menjadi entitas database utama [EntitasTransaksiLokal].
 *
 * @return Entitas transaksi yang siap disimpan ke database Room.
 */
fun Transaksi.keLokal(): EntitasTransaksiLokal {
    return EntitasTransaksiLokal(
        id = id,
        uangDibayar = uangDibayar,
        potongan = potongan,
        biayaLayanan = biayaLayanan,
        pajak = pajak,
        waktuTransaksiEpochMili = waktuTransaksiEpochMili,
        catatan = catatan,
    )
}

/**
 * Mengonversi [ItemKeranjang] menjadi entitas database [EntitasItemTransaksiLokal].
 *
 * @param transaksiId ID transaksi induk untuk menghubungkan item dengan transaksi terkait.
 * @return Entitas item transaksi yang siap disimpan ke database Room.
 */
fun ItemKeranjang.keLokal(transaksiId: String): EntitasItemTransaksiLokal {
    return EntitasItemTransaksiLokal(
        transaksiId = transaksiId,
        produkId = produk.id,
        namaProduk = produk.nama,
        hargaProduk = produk.harga,
        jumlah = jumlah,
        catatanItem = catatan,
        kodePindai = produk.kodePindai,
        deskripsiProduk = produk.deskripsi,
    )
}

/**
 * Mengonversi data database [TransaksiDenganItemLokal] kembali menjadi model domain [Transaksi].
 * Melakukan pemetaan rekursif untuk setiap item transaksi ke dalam model domain.
 *
 * @return Objek domain Transaksi yang lengkap dengan daftar itemnya.
 */
fun TransaksiDenganItemLokal.keDomain(): Transaksi {
    return Transaksi(
        id = transaksi.id,
        daftarItemKeranjang = daftarItem.map { itemLokal ->
            ItemKeranjang(
                produk = Produk(
                    id = itemLokal.produkId,
                    nama = itemLokal.namaProduk,
                    harga = itemLokal.hargaProduk,
                    stokTersedia = 0,
                    kodePindai = itemLokal.kodePindai,
                    deskripsi = itemLokal.deskripsiProduk,
                    aktif = true,
                ),
                jumlah = itemLokal.jumlah,
                catatan = itemLokal.catatanItem,
            )
        },
        uangDibayar = transaksi.uangDibayar,
        potongan = transaksi.potongan,
        biayaLayanan = transaksi.biayaLayanan,
        pajak = transaksi.pajak,
        waktuTransaksiEpochMili = transaksi.waktuTransaksiEpochMili,
        catatan = transaksi.catatan,
    )
}
