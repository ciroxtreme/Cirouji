package com.ciro.uji

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Izin Notifikasi Diberikan!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Minta Izin Notifikasi jika Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            MainTestScreen(
                onSetAlarmSeconds = { seconds ->
                    AlarmHelper.setTestAlarm(this, seconds)
                    Toast.makeText(this, "Alarm disetel untuk $seconds detik lagi! Kunci layar sekarang.", Toast.LENGTH_LONG).show()
                },
                onCancelAlarm = {
                    AlarmHelper.cancelAlarm(this)
                    Toast.makeText(this, "Alarm dibatalkan.", Toast.LENGTH_SHORT).show()
                },
                onOpenOverlaySettings = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:$packageName")
                        )
                        startActivity(intent)
                    }
                }
            )
        }
    }
}

@Composable
fun MainTestScreen(
    onSetAlarmSeconds: (Int) -> Unit,
    onCancelAlarm: () -> Unit,
    onOpenOverlaySettings: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF121212)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CIRO UJI LAB",
                color = Color(0xFF64B5F6),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Pengujian Tembus Layar & Anti-Kill (Android 14)",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onSetAlarmSeconds(10) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("SETEL ALARM: 10 DETIK (TES CEPAT)", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onSetAlarmSeconds(30) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("SETEL ALARM: 30 DETIK", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onSetAlarmSeconds(60) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("SETEL ALARM: 1 MENIT", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = onOpenOverlaySettings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buka Setelan 'Tampilkan di Atas Aplikasi Lain'")
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onCancelAlarm,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Batalkan Alarm Aktif", color = Color(0xFFEF5350))
            }
        }
    }
}
