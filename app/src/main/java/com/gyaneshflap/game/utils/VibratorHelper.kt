package com.gyaneshflap.game.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class VibratorHelper(context: Context, private val prefs: PreferencesManager) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun vibrateShort() {
        if (!prefs.vibrationEnabled || vibrator?.hasVibrator() != true) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(30)
            }
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun vibrateCrash() {
        if (!prefs.vibrationEnabled || vibrator?.hasVibrator() != true) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 150), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 100, 50, 150), -1)
            }
        } catch (e: Exception) {
            // Ignore
        }
    }
}
