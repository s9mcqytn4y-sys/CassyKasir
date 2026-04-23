# Gemini & Agentic Workflow

Dokumen ini mendefinisikan protokol interaksi antara pengembang dan agen AI (Gemini) dalam proyek Cassy Kasir.

## Peran Gemini
Gemini bertindak sebagai pendamping rekayasa perangkat lunak yang memiliki pemahaman terhadap domain bisnis UMKM Indonesia dan arsitektur MVI-Lite.

## Panduan Pengembangan
Setiap perubahan kode harus mematuhi aturan berikut:

### 1. Konsistensi Bahasa
- Logika dan API: Gunakan Bahasa Indonesia teknis untuk penamaan fungsi, variabel, dan kelas (misal: hitungPajak).
- Dokumen: Gunakan KDoc dalam Bahasa Indonesia formal untuk setiap deklarasi publik.

### 2. Implementasi MVI-Lite
- Status Immutable: Perbarui status hanya melalui fungsi .update pada MutableStateFlow.
- Aksi Tersegel: Definisi interaksi pengguna menggunakan sealed interface atau sealed class.
- UI Stateless: Komposabel harus murni, menerima status dan mengirim aksi melalui parameter.

### 3. Standar UI
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
