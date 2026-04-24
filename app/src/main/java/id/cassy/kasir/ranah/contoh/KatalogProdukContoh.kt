package id.cassy.kasir.ranah.contoh

import id.cassy.kasir.ranah.model.Produk

/**
 * Objek penyedia data tiruan (mock data) untuk keperluan pengembangan dan demonstrasi.
 */
object KatalogProdukContoh {

    /**
     * Menghasilkan daftar awal produk yang beragam (Kopi, Makanan, Retail).
     *
     * @return List berisi objek [Produk].
     */
    fun daftarAwal(): List<Produk> {
        return listOf(
            // --- DATA ASLI ---
            Produk(
                id = "produk-kopi-hitam",
                nama = "Kopi Hitam",
                harga = 18000,
                stokTersedia = 40,
                kodePindai = "899001",
                deskripsi = "Kopi hitam robusta murni dengan rasa yang kuat dan berani.",
            ),
            Produk(
                id = "produk-es-teh",
                nama = "Es Teh",
                harga = 8000,
                stokTersedia = 60,
                kodePindai = "899002",
                deskripsi = "Teh melati segar yang disajikan dengan es batu kristal.",
            ),
            Produk(
                id = "produk-roti-bakar",
                nama = "Roti Bakar",
                harga = 15000,
                stokTersedia = 25,
                kodePindai = null,
                deskripsi = "Roti bakar dengan pilihan selai coklat, keju, atau kacang.",
            ),

            // --- MENU KOPI (ESPRESSO BASED) ---
            Produk(
                id = "produk-kopi-susu-gula-aren",
                nama = "Kopi Susu Gula Aren",
                harga = 22000,
                stokTersedia = 50,
                kodePindai = null,
                deskripsi = "Perpaduan espresso premium, susu segar, dan gula aren asli.",
            ),
            Produk(
                id = "produk-cappuccino",
                nama = "Cappuccino (Hot/Ice)",
                harga = 25000,
                stokTersedia = 30,
                kodePindai = null,
                deskripsi = "Klasik cappuccino dengan foam susu yang tebal dan creamy.",
            ),
            Produk(
                id = "produk-americano",
                nama = "Americano",
                harga = 20000,
                stokTersedia = 45,
                kodePindai = null,
                deskripsi = "Espresso ganda yang dilarutkan dalam air panas untuk rasa yang lebih ringan.",
            ),
            Produk(
                id = "produk-caramel-macchiato",
                nama = "Caramel Macchiato",
                harga = 28000,
                stokTersedia = 20,
                kodePindai = null,
                deskripsi = "Espresso dengan susu steamed dan sirup karamel yang manis.",
            ),

            // --- MENU NON-KOPI ---
            Produk(
                id = "produk-matcha-latte",
                nama = "Matcha Latte",
                harga = 25000,
                stokTersedia = 35,
                kodePindai = null,
                deskripsi = "Bubuk teh hijau Jepang premium yang dicampur dengan susu hangat.",
            ),
            Produk(
                id = "produk-red-velvet",
                nama = "Red Velvet Latte",
                harga = 24000,
                stokTersedia = 25,
                kodePindai = null,
                deskripsi = "Minuman rasa red velvet yang manis dan lembut dengan aroma coklat.",
            ),
            Produk(
                id = "produk-es-coklat",
                nama = "Es Coklat Signature",
                harga = 22000,
                stokTersedia = 40,
                kodePindai = null,
                deskripsi = "Coklat hitam pilihan dengan susu segar dan es batu.",
            ),
            Produk(
                id = "produk-lemon-tea",
                nama = "Ice Lemon Tea",
                harga = 15000,
                stokTersedia = 30,
                kodePindai = null,
                deskripsi = "Teh segar dengan perasan jeruk lemon asli yang menyegarkan.",
            ),

            // --- MAKANAN & CAMILAN (KITCHEN) ---
            Produk(
                id = "produk-kentang-goreng",
                nama = "Kentang Goreng",
                harga = 18000,
                stokTersedia = 30,
                kodePindai = null,
                deskripsi = "Kentang goreng renyah dengan bumbu garam atau keju.",
            ),
            Produk(
                id = "produk-cireng-rujak",
                nama = "Cireng Bumbu Rujak",
                harga = 15000,
                stokTersedia = 25,
                kodePindai = null,
                deskripsi = "Cireng goreng hangat disajikan dengan sambal rujak pedas manis.",
            ),
            Produk(
                id = "produk-dimsum-ayam",
                nama = "Dimsum Ayam (Isi 4)",
                harga = 20000,
                stokTersedia = 40,
                kodePindai = null,
                deskripsi = "Dimsum ayam kukus yang lembut disajikan dengan saus sambal.",
            ),
            Produk(
                id = "produk-pisang-bakar-keju",
                nama = "Pisang Bakar Keju",
                harga = 18000,
                stokTersedia = 20,
                kodePindai = null,
                deskripsi = "Pisang bakar dengan taburan keju parut dan susu kental manis.",
            ),
            Produk(
                id = "produk-nasi-goreng",
                nama = "Nasi Goreng Spesial",
                harga = 30000,
                stokTersedia = 15,
                kodePindai = null,
                deskripsi = "Nasi goreng dengan telur, ayam, dan kerupuk udang.",
            ),
            Produk(
                id = "produk-mie-goreng-telur",
                nama = "Mie Goreng Telur",
                harga = 20000,
                stokTersedia = 20,
                kodePindai = null,
                deskripsi = "Mie goreng dengan telur mata sapi dan sayuran segar.",
            ),

            // --- PASTRY / BAKERY ---
            Produk(
                id = "produk-croissant-butter",
                nama = "Butter Croissant",
                harga = 22000,
                stokTersedia = 10,
                kodePindai = null,
                deskripsi = "Kue kering khas Perancis yang berlapis-lapis dan kaya akan mentega.",
            ),
            Produk(
                id = "produk-brownies",
                nama = "Fudgy Brownies Slice",
                harga = 15000,
                stokTersedia = 12,
                kodePindai = null,
                deskripsi = "Potongan brownies coklat yang padat, lembap, dan sangat nyoklat.",
            ),

            // --- MINUMAN KEMASAN (RETAIL / BARCODE) ---
            Produk(
                id = "produk-air-mineral-600",
                nama = "Air Mineral 600ml",
                harga = 5000,
                stokTersedia = 100,
                kodePindai = "8991234567890",
                deskripsi = "Air mineral murni dalam kemasan botol 600ml.",
            ),
            Produk(
                id = "produk-air-mineral-330",
                nama = "Air Mineral 330ml",
                harga = 3500,
                stokTersedia = 80,
                kodePindai = "8991234567891",
                deskripsi = "Air mineral murni dalam kemasan botol kecil 330ml.",
            ),
            Produk(
                id = "produk-teh-kotak",
                nama = "Teh Kotak 300ml",
                harga = 6000,
                stokTersedia = 50,
                kodePindai = "8990099887766",
                deskripsi = "Minuman teh melati dalam kemasan kotak 300ml.",
            ),

            // --- ADD-ONS / TAMBAHAN ---
            Produk(
                id = "produk-addon-espresso",
                nama = "Extra Shot Espresso",
                harga = 5000,
                stokTersedia = 999,
                kodePindai = null,
                deskripsi = "Tambahan satu shot espresso untuk rasa kopi yang lebih mantap.",
            ),
            Produk(
                id = "produk-addon-oatmilk",
                nama = "Upgrade Oat Milk",
                harga = 8000,
                stokTersedia = 50,
                kodePindai = null,
                deskripsi = "Ganti susu sapi dengan susu oat yang sehat dan gurih.",
            )
        )
    }
}
