# CassyKasir 🛡️

Solusi Digital UMKM Modern untuk manajemen kasir yang cepat, akurat, dan andal.

## 🚀 Gambaran Proyek
CassyKasir adalah aplikasi kasir berbasis Android yang dirancang khusus untuk memenuhi kebutuhan pemilik usaha kecil dan menengah (UMKM). Fokus utama proyek ini adalah pada kemudahan penggunaan, kecepatan transaksi, dan akurasi perhitungan keuangan.

## 🛠️ Arsitektur & Teknologi
Proyek ini dikembangkan dengan standar industri modern:
- **Bahasa**: Kotlin 2.2.x (100%)
- **UI Framework**: Jetpack Compose dengan Material 3
- **Pola Arsitektur**: Unidirectional Data Flow (UDF) menggunakan StateFlow dan ViewModel
- **Build System**: Gradle 9.x (Kotlin DSL)
- **Dokumentasi**: KDoc dalam Bahasa Indonesia penuh

## 📐 Standar Kode
Kami menerapkan aturan ketat untuk menjaga kualitas dan keterbacaan kode:
1. **Bahasa Indonesia Penuh**: Seluruh sintaks buatan sendiri (fungsi, variabel, kelas) dan komentar wajib menggunakan Bahasa Indonesia.
2. **Atomisitas State**: Pembaruan status UI dilakukan secara atomik menggunakan fungsi `.update` pada `StateFlow`.
3. **Stateless UI**: Komponen antarmuka dirancang untuk tidak memiliki status internal (*stateless*) untuk memudahkan pengujian dan pemeliharaan.

## 🧮 Logika Keuangan
Rumus perhitungan transaksi utama:
```kotlin
Total = (Subtotal - Potongan) + BiayaLayanan + Pajak
```
*Catatan: Pajak bersifat menambah total belanja.*

## 📦 Menjalankan Proyek
Pastikan Anda memiliki Android Studio terbaru (Koala atau lebih baru).

```bash
# Clone repository
git clone https://github.com/username/CassyKasir.git

# Build proyek
./gradlew assembleDebug

# Jalankan pengujian
./gradlew test
```

## 📄 Lisensi
Hak Cipta © 2024 Cassy Digital. Seluruh hak cipta dilindungi undang-undang.
