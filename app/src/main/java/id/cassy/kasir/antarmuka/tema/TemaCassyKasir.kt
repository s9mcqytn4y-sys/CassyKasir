package id.cassy.kasir.antarmuka.tema

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

/**
 * Konfigurasi palet warna untuk Tema Terang (Light Mode).
 * Menggunakan skema warna yang kontras untuk penggunaan di siang hari atau ruangan terang.
 * Warna dasar Hijau Hutan (Primary: #1F5B50) memberikan kesan profesional dan segar.
 */
private val SkemaWarnaTerang = lightColorScheme(
    primary = Color(0xFF1F5B50),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD6F0E8),
    onPrimaryContainer = Color(0xFF0C2F28),
    secondary = Color(0xFF9E6A2E),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFE0BA),
    onSecondaryContainer = Color(0xFF3A2200),
    tertiary = Color(0xFF55606E),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF7F3F6),
    onBackground = Color(0xFF1C1B1D),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1D),
    surfaceVariant = Color(0xFFE7E0E5),
    onSurfaceVariant = Color(0xFF49454E),
    outline = Color(0xFF7A757F),
)

/**
 * Konfigurasi palet warna untuk Tema Gelap (Dark Mode).
 * Mengurangi emisi cahaya biru untuk kenyamanan mata saat penggunaan di malam hari oleh kasir.
 */
private val SkemaWarnaGelap = darkColorScheme(
    primary = Color(0xFF9ED6C8),
    onPrimary = Color(0xFF00382F),
    primaryContainer = Color(0xFF1F5B50),
    onPrimaryContainer = Color(0xFFD6F0E8),
    secondary = Color(0xFFFFB96C),
    onSecondary = Color(0xFF5B3A0A),
    secondaryContainer = Color(0xFF7A531C),
    onSecondaryContainer = Color(0xFFFFE0BA),
    tertiary = Color(0xFFBEC7D8),
    onTertiary = Color(0xFF283141),
    background = Color(0xFF141316),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF141316),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454E),
    onSurfaceVariant = Color(0xFFCBC4CC),
    outline = Color(0xFF948F99),
)

/**
 * Konfigurasi Tipografi aplikasi menggunakan standar Material 3.
 * Menentukan ukuran font, ketebalan, dan jarak antar baris untuk elemen teks.
 * Memberikan penekanan pada headline untuk kejelasan nama toko atau aplikasi.
 */
private val TipografiCassyKasir = Typography(
    headlineMedium = TextStyle(
        fontSize = 30.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    titleLarge = TextStyle(
        fontSize = 24.sp,
        lineHeight = 30.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    titleMedium = TextStyle(
        fontSize = 18.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Normal,
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal,
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium,
    ),
)

/**
 * Konfigurasi Bentuk (Shapes) untuk komponen Material 3 di seluruh aplikasi.
 * Menentukan radius sudut untuk kartu, tombol, dan kontainer lainnya.
 */
private val BentukCassyKasir = Shapes(
    small = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
)

/**
 * Tema utama untuk Aplikasi Cassy Kasir yang membungkus seluruh hierarki UI.
 * Mendukung fitur modern seperti Dynamic Color (Android 12+) dan Insets Controller.
 *
 * @param modeGelap Menentukan apakah menggunakan tema gelap atau terang.
 * @param gunakanWarnaDinamis Mendukung skema warna dinamis Android 12+.
 * @param konten Blok Composable yang akan dibungkus oleh tema ini.
 */
@Composable
fun TemaCassyKasir(
    modeGelap: Boolean = isSystemInDarkTheme(),
    gunakanWarnaDinamis: Boolean = false,
    konten: @Composable () -> Unit,
) {
    val konteks = LocalContext.current
    val tampilan = LocalView.current

    // Logika pemilihan skema warna berdasarkan kondisi sistem dan preferensi
    val skemaWarna = when {
        gunakanWarnaDinamis && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (modeGelap) dynamicDarkColorScheme(konteks) else dynamicLightColorScheme(konteks)
        }
        modeGelap -> SkemaWarnaGelap
        else -> SkemaWarnaTerang
    }

    // Mengatur gaya visual status bar sistem agar selaras dengan tema yang aktif
    if (!tampilan.isInEditMode) {
        SideEffect {
            val aktivitas = tampilan.context as? Activity ?: return@SideEffect
            val jendela = aktivitas.window
            WindowCompat.getInsetsController(jendela, tampilan).isAppearanceLightStatusBars = !modeGelap
        }
    }

    MaterialTheme(
        colorScheme = skemaWarna,
        typography = TipografiCassyKasir,
        shapes = BentukCassyKasir,
        content = konten,
    )
}
