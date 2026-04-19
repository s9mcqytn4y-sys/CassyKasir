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
 * Definisi palet warna untuk mode terang aplikasi CassyKasir.
 * Menggunakan warna hijau hutan sebagai identitas utama untuk kesan profesional dan segar.
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
 * Definisi palet warna untuk mode gelap aplikasi CassyKasir.
 * Mengoptimalkan kontras rendah untuk kenyamanan penggunaan di malam hari bagi kasir.
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
 * Konfigurasi tipografi standar Material 3 yang disesuaikan.
 * Memberikan penekanan pada headline untuk kejelasan nama toko/aplikasi.
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
 * Konfigurasi bentuk sudut (shapes) yang konsisten di seluruh aplikasi.
 */
private val BentukCassyKasir = Shapes(
    small = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
)

/**
 * Komponen tema utama yang membungkus aplikasi.
 * Mendukung fitur modern seperti Dynamic Color (Android 12+) dan Insets Controller.
 */
@Composable
fun TemaCassyKasir(
    modeGelap: Boolean = isSystemInDarkTheme(),
    gunakanWarnaDinamis: Boolean = false,
    konten: @Composable () -> Unit,
) {
    val konteks = LocalContext.current
    val tampilan = LocalView.current

    // Logika pemilihan skema warna berdasarkan kondisi sistem
    val skemaWarna = when {
        gunakanWarnaDinamis && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (modeGelap) dynamicDarkColorScheme(konteks) else dynamicLightColorScheme(konteks)
        }

        modeGelap -> SkemaWarnaGelap
        else -> SkemaWarnaTerang
    }

    // Mengatur gaya visual status bar sistem agar selaras dengan tema
    if (!tampilan.isInEditMode) {
        SideEffect {
            val aktivitas = tampilan.context as? Activity ?: return@SideEffect
            val jendela = aktivitas.window
            WindowCompat.getInsetsController(jendela, tampilan).isAppearanceLightStatusBars =
                !modeGelap
        }
    }

    MaterialTheme(
        colorScheme = skemaWarna,
        typography = TipografiCassyKasir,
        shapes = BentukCassyKasir,
        content = konten,
    )
}
