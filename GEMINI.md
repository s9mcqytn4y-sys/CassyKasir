# Gemini & Agentic Workflow

Dokumen ini mendefinisikan protokol interaksi antara pengembang dan agen AI (Gemini) dalam proyek Cassy Kasir.

## Peran Gemini

Gemini bertindak sebagai pendamping rekayasa perangkat lunak yang memiliki pemahaman terhadap domain bisnis UMKM Indonesia dan arsitektur MVI-Lite.

## Panduan Pengembangan

Setiap perubahan kode harus mematuhi aturan berikut:

### 1. Konsistensi Bahasa & Naming

- **Logika & API**: Gunakan Bahasa Indonesia teknis.
    - *Fungsi*: Kata kerja + Objek (misal: `hitungTotal()`, `muatDaftarProduk()`).
    - *Variabel*: Kata benda/sifat yang deskriptif (misal: `hargaSatuan`, `apakahSedangMemuat`).
    - *Kelas*: Kata benda (misal: `PengelolaKeranjang`, `RepositoriProduk`).
    - *Boolean*: Gunakan awalan `apakah` atau `sudah` (misal: `apakahAktif`, `sudahBayar`).

#### Tabel Konvensi Naming:

| Bahasa Inggris      | Bahasa Indonesia (Target) | Konteks     |
|:--------------------|:--------------------------|:------------|
| `get` / `fetch`     | `ambil` / `muat`          | Fungsi Data |
| `is` / `has`        | `apakah` / `punya`        | Boolean     |
| `loading`           | `sedangMemuat`            | State       |
| `success`           | `berhasil`                | State       |
| `error` / `failure` | `gagal` / `salah`         | State       |
| `update` / `edit`   | `ubah` / `perbarui`       | Fungsi      |

### 2. Standar Copy Modern (UX Writing)

Gunakan gaya bahasa yang manusiawi, ringkas, dan langsung (*direct*):

- **Hindari Kata Kerja Pasif**: Gunakan "Hapus" bukan "Dihapus", "Simpan" bukan "Disimpan".
- **Konfirmasi**: Gunakan pertanyaan langsung (misal: "Hapus item ini?" bukan "Apakah Anda yakin ingin menghapus item ini?").
- **Error**: Fokus pada solusi (misal: "Koneksi terputus. Coba lagi?" bukan "Terjadi kesalahan pada jaringan").
- **Empty State**: Gunakan bahasa yang memotivasi (misal: "Keranjang masih kosong. Yuk, tambah produk!" bukan "Tidak ada item di keranjang").

### 3. Implementasi MVI-Lite

- Status Immutable: Perbarui status hanya melalui fungsi .update pada MutableStateFlow.
- Aksi Tersegel: Definisi interaksi pengguna menggunakan sealed interface atau sealed class.
- UI Stateless: Komposabel harus murni, menerima status dan mengirim aksi melalui parameter.

### 4. Standar UI

- Gunakan Modifier sebagai parameter opsional pertama pada Komposabel.
- Manfaatkan MaterialTheme untuk konsistensi warna dan tipografi.
- Pastikan dukungan tata letak adaptif untuk berbagai ukuran layar.

## Alat Bantu Analisis

Agen disarankan menggunakan alat berikut:

- find_usages: Memahami dampak perubahan pada aliran data.
- analyze_file: Memastikan validitas sintaksis.
- code_search: Mencari pola implementasi yang sudah ada.

## Alur Kerja

1. Observasi: Analisis file dan dependensi.
2. Rencana: Deskripsikan langkah dalam Bahasa Indonesia.
3. Eksekusi: Lakukan perubahan atomik.
4. Verifikasi: Sinkronisasi dokumentasi dengan perubahan kode.

---
Dokumen ini diperbarui secara berkala sesuai perkembangan kapabilitas agen.
