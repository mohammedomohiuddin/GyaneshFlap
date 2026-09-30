package com.gyaneshflap.game.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND, value).apply()

    var musicEnabled: Boolean
        get() = prefs.getBoolean(KEY_MUSIC, true)
        set(value) = prefs.edit().putBoolean(KEY_MUSIC, value).apply()

    var vibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATION, value).apply()

    /**
     * Returns the list of real player high scores in descending order.
     * Returns an empty list if no games have been completed yet.
     */
    fun getHighScores(): List<Int> {
        val scoresStr = prefs.getString(KEY_HIGH_SCORES, null)
        if (scoresStr.isNullOrEmpty()) {
            return emptyList()
        }
        return try {
            scoresStr.split(",")
                .mapNotNull { it.trim().toIntOrNull() }
                .sortedDescending()
                .distinct()
                .take(5)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Saves a new game score and returns true if it sets a NEW PERSONAL RECORD
     * (strictly higher than the player's previous best score).
     */
    fun addScore(score: Int): Boolean {
        if (score <= 0) return false

        val previousBest = getBestScore()
        val isNewBest = score > previousBest

        val currentScores = getHighScores().toMutableList()
        currentScores.add(score)

        val updatedList = currentScores.sortedDescending().distinct().take(5)
        val newStr = updatedList.joinToString(",")

        prefs.edit().putString(KEY_HIGH_SCORES, newStr).apply()

        return isNewBest
    }

    /**
     * Returns the player's highest score achieved so far (or 0 if no score yet).
     */
    fun getBestScore(): Int {
        return getHighScores().firstOrNull() ?: 0
    }

    companion object {
        private const val PREFS_NAME = "gyanesh_flap_prefs"
        private const val KEY_SOUND = "key_sound"
        private const val KEY_MUSIC = "key_music"
        private const val KEY_VIBRATION = "key_vibration"
        private const val KEY_HIGH_SCORES = "key_high_scores"
    }
}
