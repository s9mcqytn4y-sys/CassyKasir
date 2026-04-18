package id.cassy.kasir.ranah.contoh

import id.cassy.kasir.ranah.model.Produk

object KatalogProdukContoh {

    fun daftarAwal(): List<Produk> {
        return listOf(
            // --- DATA ASLI ---
            Produk(
                id = "produk-kopi-hitam",
                nama = "Kopi Hitam",
                harga = 18000,
                stokTersedia = 40,
                kodePindai = "899001",
            ),
            Produk(
                id = "produk-es-teh",
                nama = "Es Teh",
                harga = 8000,
                stokTersedia = 60,
                kodePindai = "899002",
            ),
            Produk(
                id = "produk-roti-bakar",
                nama = "Roti Bakar",
                harga = 15000,
                stokTersedia = 25,
                kodePindai = null,
            ),

            // --- MENU KOPI (ESPRESSO BASED) ---
            Produk(
                id = "produk-kopi-susu-gula-aren",
                nama = "Kopi Susu Gula Aren",
                harga = 22000,
                stokTersedia = 50,
                kodePindai = null,
            ),
            Produk(
                id = "produk-cappuccino",
                nama = "Cappuccino (Hot/Ice)",
                harga = 25000,
                stokTersedia = 30,
                kodePindai = null,
            ),
            Produk(
                id = "produk-americano",
                nama = "Americano",
                harga = 20000,
                stokTersedia = 45,
                kodePindai = null,
            ),
            Produk(
                id = "produk-caramel-macchiato",
                nama = "Caramel Macchiato",
                harga = 28000,
                stokTersedia = 20,
                kodePindai = null,
            ),

            // --- MENU NON-KOPI ---
            Produk(
                id = "produk-matcha-latte",
                nama = "Matcha Latte",
                harga = 25000,
                stokTersedia = 35,
                kodePindai = null,
            ),
            Produk(
                id = "produk-red-velvet",
                nama = "Red Velvet Latte",
                harga = 24000,
                stokTersedia = 25,
                kodePindai = null,
            ),
            Produk(
                id = "produk-es-coklat",
                nama = "Es Coklat Signature",
                harga = 22000,
                stokTersedia = 40,
                kodePindai = null,
            ),
            Produk(
                id = "produk-lemon-tea",
                nama = "Ice Lemon Tea",
                harga = 15000,
                stokTersedia = 30,
                kodePindai = null,
            ),

            // --- MAKANAN & CAMILAN (KITCHEN) ---
            Produk(
                id = "produk-kentang-goreng",
                nama = "Kentang Goreng",
                harga = 18000,
                stokTersedia = 30,
                kodePindai = null,
            ),
            Produk(
                id = "produk-cireng-rujak",
                nama = "Cireng Bumbu Rujak",
                harga = 15000,
                stokTersedia = 25,
                kodePindai = null,
            ),
            Produk(
                id = "produk-dimsum-ayam",
                nama = "Dimsum Ayam (Isi 4)",
                harga = 20000,
                stokTersedia = 40,
                kodePindai = null,
            ),
            Produk(
                id = "produk-pisang-bakar-keju",
                nama = "Pisang Bakar Keju",
                harga = 18000,
                stokTersedia = 20,
                kodePindai = null,
            ),
            Produk(
                id = "produk-nasi-goreng",
                nama = "Nasi Goreng Spesial",
                harga = 30000,
                stokTersedia = 15,
                kodePindai = null,
            ),
            Produk(
                id = "produk-mie-goreng-telur",
                nama = "Mie Goreng Telur",
                harga = 20000,
                stokTersedia = 20,
                kodePindai = null,
            ),

            // --- PASTRY / BAKERY ---
            Produk(
                id = "produk-croissant-butter",
                nama = "Butter Croissant",
                harga = 22000,
                stokTersedia = 10,
                kodePindai = null,
            ),
            Produk(
                id = "produk-brownies",
                nama = "Fudgy Brownies Slice",
                harga = 15000,
                stokTersedia = 12,
                kodePindai = null,
            ),

            // --- MINUMAN KEMASAN (RETAIL / BARCODE) ---
            Produk(
                id = "produk-air-mineral-600",
                nama = "Air Mineral 600ml",
                harga = 5000,
                stokTersedia = 100,
                kodePindai = "8991234567890", // Contoh barcode standar retail
            ),
            Produk(
                id = "produk-air-mineral-330",
                nama = "Air Mineral 330ml",
                harga = 3500,
                stokTersedia = 80,
                kodePindai = "8991234567891",
            ),
            Produk(
                id = "produk-teh-kotak",
                nama = "Teh Kotak 300ml",
                harga = 6000,
                stokTersedia = 50,
                kodePindai = "8990099887766",
            ),

            // --- ADD-ONS / TAMBAHAN ---
            Produk(
                id = "produk-addon-espresso",
                nama = "Extra Shot Espresso",
                harga = 5000,
                stokTersedia = 999, // Add-ons biasanya tidak terbatas stok fisik langsung
                kodePindai = null,
            ),
            Produk(
                id = "produk-addon-oatmilk",
                nama = "Upgrade Oat Milk",
                harga = 8000,
                stokTersedia = 50,
                kodePindai = null,
            )
        )
    }
}