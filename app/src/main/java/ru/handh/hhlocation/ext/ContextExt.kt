package ru.handh.hhlocation.ext

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast

fun Context.startForegroundServiceCompat(intent: Intent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(intent)
    } else {
        startService(intent)
    }
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(messageId: Int) {
    Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()
}