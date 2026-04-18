package id.cassy.kasir.antarmuka.utama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import id.cassy.kasir.antarmuka.AplikasiCassyKasir

class AktivitasUtama : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AplikasiCassyKasir()
        }
    }
}