package id.cassy.kasir.antarmuka.tema

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
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
 * Pengaturan warna untuk Tema Terang.
 * Kita menggunakan perpaduan warna hijau hutan yang profesional namun tetap terlihat segar.
 * Sangat cocok digunakan saat kondisi ruangan terang agar teks tetap mudah dibaca.
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
 * Pengaturan warna untuk Tema Gelap.
 * Tema ini dirancang agar mata kasir tidak cepat lelah saat bekerja di malam hari atau ruangan redup.
 * Warna hijaunya dibuat lebih lembut agar tetap nyaman dipandang.
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
 * Kumpulan gaya tulisan (Tipografi) yang digunakan di seluruh aplikasi.
 * Ukuran dan ketebalan huruf diatur sedemikian rupa agar informasi penting seperti
 * nama produk dan total harga bisa terlihat dengan jelas.
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
 * Definisi bentuk lengkungan pada komponen seperti kartu dan tombol.
 * Kita menggunakan sudut yang agak membulat agar tampilan aplikasi terasa lebih ramah dan modern.
 */
private val BentukCassyKasir = Shapes(
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(24.dp),
)

/**
 * Fungsi utama untuk membungkus seluruh tampilan aplikasi dengan Tema Cassy Kasir.
 * Fungsi ini otomatis mengatur warna, tulisan, dan bentuk sesuai dengan mode yang dipilih.
 *
 * @param modeGelap Otomatis mengikuti pengaturan sistem, tapi bisa dipaksa manual jika perlu.
 * @param gunakanWarnaDinamis Fitur Android 12+ yang menyesuaikan warna aplikasi dengan wallpaper pengguna.
 * @param konten Isi tampilan aplikasi yang akan diberikan tema ini.
 */
@Composable
fun TemaCassyKasir(
    modeGelap: Boolean = isSystemInDarkTheme(),
    gunakanWarnaDinamis: Boolean = false,
    konten: @Composable () -> Unit,
) {
    val konteks = LocalContext.current
    val tampilan = LocalView.current

    // Tentukan skema warna yang paling pas untuk digunakan
    val skemaWarna = when {
        gunakanWarnaDinamis && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (modeGelap) dynamicDarkColorScheme(konteks) else dynamicLightColorScheme(konteks)
        }

        modeGelap -> SkemaWarnaGelap
        else -> SkemaWarnaTerang
    }

    // Mengatur agar ikon di status bar (baterai, jam, dll) tetap terlihat jelas sesuai tema
    if (!tampilan.isInEditMode) {
        SideEffect {
            val aktivitas = tampilan.context as? Activity ?: return@SideEffect
            val jendela = aktivitas.window
            // Jika mode terang, gunakan ikon gelap. Jika mode gelap, gunakan ikon terang.
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
