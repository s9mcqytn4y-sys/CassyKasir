package id.cassy.kasir.antarmuka.riwayat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.cassy.kasir.data.lokal.repositori.RepositoriTransaksi
import id.cassy.kasir.ranah.fungsi.hitungTotalTransaksi
import id.cassy.kasir.ranah.model.Transaksi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Pengelola logika dan status untuk Layar Riwayat Transaksi.
 * Mengambil data secara asinkron dari Room dan memetakan ke [ModelTampilanRiwayatTransaksi].
 *
 * @property repositori Sumber data transaksi lokal.
 */
class LayarRiwayatTransaksiViewModel(
    private val repositori: RepositoriTransaksi,
) : ViewModel() {

    private val _apakahSedangMemuat = MutableStateFlow(true)
    private val _pesanGagal = MutableStateFlow<String?>(null)

    /**
     * Aliran status tampilan yang dapat diobservasi oleh UI.
     */
    val modelTampilan: StateFlow<ModelTampilanRiwayatTransaksi> = combine(
        repositori.ambilSemuaTransaksi(),
        _apakahSedangMemuat,
        _pesanGagal,
    ) { daftar, sedangMemuat, gagal ->
        if (gagal != null) {
            ModelTampilanRiwayatTransaksi(
                judulLayar = "Riwayat Transaksi",
                statusMuat = StatusMuatRiwayatTransaksi.Gagal(
                    judul = "Gagal memuat riwayat transaksi",
                    deskripsi = gagal,
                ),
            )
        } else if (sedangMemuat) {
            ModelTampilanRiwayatTransaksi(
                judulLayar = "Riwayat Transaksi",
                statusMuat = StatusMuatRiwayatTransaksi.Memuat,
            )
        } else if (daftar.isEmpty()) {
            ModelTampilanRiwayatTransaksi(
                judulLayar = "Riwayat Transaksi",
                statusMuat = StatusMuatRiwayatTransaksi.Kosong(
                    judul = "Belum ada riwayat transaksi",
                    deskripsi = "Riwayat transaksi akan tampil di sini setelah data transaksi mulai disimpan.",
                ),
            )
        } else {
            ModelTampilanRiwayatTransaksi(
                judulLayar = "Riwayat Transaksi",
                statusMuat = StatusMuatRiwayatTransaksi.Berhasil(
                    judulBagian = "Riwayat penjualan",
                    deskripsiBagian = "Data diambil dari database lokal.",
                    daftarRingkasanTransaksi = daftar.map { transaksi ->
                        RingkasanTransaksiRiwayat(
                            transaksiId = transaksi.id,
                            labelWaktu = transaksi.waktuTransaksiEpochMili
                                .sebagaiLabelWaktuTransaksi(),
                            labelJumlahItem = "${transaksi.hitungJumlahItem()} item",
                            labelTotalAkhir = transaksi.hitungTotalAkhir().sebagaiRupiahSederhana(),
                        )
                    },
                ),
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ModelTampilanRiwayatTransaksi(
            judulLayar = "Riwayat Transaksi",
            statusMuat = StatusMuatRiwayatTransaksi.Memuat,
        )
    )

    init {
        muatUlang()
    }

    /**
     * Memperbarui status pemuatan untuk memicu UI.
     */
    fun muatUlang() {
        viewModelScope.launch {
            _apakahSedangMemuat.value = true
            _pesanGagal.value = null
            delay(300) // Memberikan jeda visual
            _apakahSedangMemuat.value = false
        }
    }
}

private fun Transaksi.hitungJumlahItem(): Int {
    return daftarItemKeranjang.sumOf { itemKeranjang ->
        itemKeranjang.jumlah
    }
}

private fun Transaksi.hitungTotalAkhir(): Long {
    return hitungTotalTransaksi(
        daftarItemKeranjang = daftarItemKeranjang,
        potongan = potongan,
        biayaLayanan = biayaLayanan,
        pajak = pajak,
    )
}

private fun Long.sebagaiLabelWaktuTransaksi(): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}

private fun Long.sebagaiRupiahSederhana(): String {
    return "Rp$this"
}
