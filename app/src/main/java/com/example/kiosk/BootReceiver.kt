package com.example.kiosk

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Check if the device just finished booting up
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            // Create an intent to launch our MainActivity
            val launchIntent = Intent(context, MainActivity::class.java).apply {
                // FLAG_ACTIVITY_NEW_TASK is mandatory when starting an activity from a receiver
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

            // Start the app
            context.startActivity(launchIntent)
        }
    }
}