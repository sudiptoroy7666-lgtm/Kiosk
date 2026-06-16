package com.example.kiosk
import android.content.IntentFilter // <-- ADD THIS IMPORT
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
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


    // REMOVE the old onResume() function entirely

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // Only lock the device when the app actually has focus and is fully drawn
        if (hasFocus) {
            enterKioskMode()
        }
    }

    private fun enterKioskMode() {
        try {
            val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val adminName = ComponentName(this, MyAdminReceiver::class.java)

            // 1. Authorize this app for LockTask
            dpm.setLockTaskPackages(adminName, arrayOf(packageName))

            // 2. FORCE our app to be the absolute default HOME screen (No prompts)
            val intentFilter = IntentFilter(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                addCategory(Intent.CATEGORY_DEFAULT)
            }
            dpm.addPersistentPreferredActivity(
                adminName,
                intentFilter,
                ComponentName(this, MainActivity::class.java)
            )

            // 3. HIDE the default Android Launcher so it can't draw over us on boot!
            try {
                dpm.setApplicationHidden(adminName, "com.android.launcher3", true)
            } catch (e: Exception) {
                // Ignore if the specific emulator uses a different launcher package name
            }

            // 4. Enter Kiosk Mode
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