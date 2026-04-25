/**
 * Konfigurasi Build Tingkat Proyek (Root).
 * Mengatur plugin yang akan dibagikan ke seluruh modul di bawahnya tanpa langsung menerapkannya.
 */
plugins {
    // Plugin Android Application: Dasar untuk membangun aplikasi Android
    alias(libs.plugins.android.application) apply false

    // Plugin Compose Compiler: Menangani optimasi fungsi @Composable di Kotlin 2.x
    alias(libs.plugins.compose.compiler) apply false

    // Plugin Kotlin Serialization: Untuk serialisasi data JSON (Type-safe Navigation)
    alias(libs.plugins.kotlin.serialization) apply false

    // Plugin KSP: Kotlin Symbol Processing untuk efisiensi Room Compiler
    alias(libs.plugins.ksp) apply false
}


