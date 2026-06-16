package com.example.kiosk

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // FEATURE 2 TEST: FLAG_SECURE (Blocks screenshots/screen recording)
        // MUST be called before super.onCreate()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                KioskTestScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Attempt to enter kiosk mode automatically when app resumes
        enterKioskMode()
    }

    private fun enterKioskMode() {
        try {
            val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val adminName = ComponentName(this, MyAdminReceiver::class.java)

            // Authorize this app for LockTask
            dpm.setLockTaskPackages(adminName, arrayOf(packageName))

            // Enter Kiosk Mode
            startLockTask()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Composable
fun KioskTestScreen() {
    val context = LocalContext.current
    var isKioskActive by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E2E)), // Dark background
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "🔒 KIOSK MODE ACTIVE",
                color = Color.White,
                fontSize = 28.sp,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Feature 1: LockTask is engaged.\nTry pressing Home or Recent Apps!",
                color = Color.LightGray,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // TESTING ONLY: Exit button so you don't get permanently trapped
            Button(
                onClick = {
                    (context as ComponentActivity).stopLockTask()
                    isKioskActive = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                Text("Exit Kiosk (Testing Only)", color = Color.White)
            }

            if (!isKioskActive) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Kiosk stopped. You can now use the emulator normally.", color = Color.Yellow)
            }
        }
    }
}