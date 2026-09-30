package com.gyaneshflap.game.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.gyaneshflap.game.utils.PreferencesManager
import java.util.concurrent.Executors
import kotlin.math.sin

class SoundManager(context: Context, private val prefs: PreferencesManager) {
    private val executor = Executors.newCachedThreadPool()
    private val sampleRate = 22050

    fun playFlap() {
        if (!prefs.soundEnabled) return
        executor.execute {
            val durationMs = 120
            val numSamples = sampleRate * durationMs / 1000
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / numSamples
                val freq = 180.0 + t * 450.0 // Ascending sweep
                val sample = sin(2.0 * Math.PI * freq * i / sampleRate) * (1.0 - t)
                buffer[i] = (sample * 28000).toInt().coerceIn(-32768, 32767).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playScore() {
        if (!prefs.soundEnabled) return
        executor.execute {
            val durationMs = 150
            val numSamples = sampleRate * durationMs / 1000
            val buffer = ShortArray(numSamples)
            val half = numSamples / 2
            for (i in 0 until numSamples) {
                val freq = if (i < half) 880.0 else 1320.0
                val t = (i % half).toDouble() / half
                val sample = sin(2.0 * Math.PI * freq * i / sampleRate) * (1.0 - t)
                buffer[i] = (sample * 25000).toInt().coerceIn(-32768, 32767).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playCollect() {
        if (!prefs.soundEnabled) return
        executor.execute {
            val durationMs = 200
            val numSamples = sampleRate * durationMs / 1000
            val buffer = ShortArray(numSamples)
            val notes = doubleArrayOf(659.25, 880.0, 1174.66, 1760.0) // E5, A5, D6, A6
            val noteLen = numSamples / notes.size
            for (i in 0 until numSamples) {
                val noteIdx = (i / noteLen).coerceAtMost(notes.size - 1)
                val freq = notes[noteIdx]
                val t = (i % noteLen).toDouble() / noteLen
                val sample = sin(2.0 * Math.PI * freq * i / sampleRate) * (1.0 - t * 0.7)
                buffer[i] = (sample * 26000).toInt().coerceIn(-32768, 32767).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playCollision() {
        if (!prefs.soundEnabled) return
        executor.execute {
            val durationMs = 250
            val numSamples = sampleRate * durationMs / 1000
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / numSamples
                val freq = 160.0 * (1.0 - t * 0.7)
                val noise = (Math.random() - 0.5) * 0.4
                val square = if (sin(2.0 * Math.PI * freq * i / sampleRate) > 0) 0.6 else -0.6
                val sample = (square + noise) * (1.0 - t)
                buffer[i] = (sample * 30000).toInt().coerceIn(-32768, 32767).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playGameOver() {
        if (!prefs.soundEnabled) return
        executor.execute {
            val durationMs = 450
            val numSamples = sampleRate * durationMs / 1000
            val buffer = ShortArray(numSamples)
            val notes = doubleArrayOf(440.0, 415.3, 392.0, 349.2) // Descending fail chord
            val noteLen = numSamples / notes.size
            for (i in 0 until numSamples) {
                val noteIdx = (i / noteLen).coerceAtMost(notes.size - 1)
                val freq = notes[noteIdx]
                val t = (i % noteLen).toDouble() / noteLen
                val sample = sin(2.0 * Math.PI * freq * i / sampleRate) * (1.0 - t * 0.5)
                buffer[i] = (sample * 25000).toInt().coerceIn(-32768, 32767).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playHighScore() {
        if (!prefs.soundEnabled) return
        executor.execute {
            val durationMs = 500
            val numSamples = sampleRate * durationMs / 1000
            val buffer = ShortArray(numSamples)
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.5) // C5, E5, G5, C6
            val noteLen = numSamples / notes.size
            for (i in 0 until numSamples) {
                val noteIdx = (i / noteLen).coerceAtMost(notes.size - 1)
                val freq = notes[noteIdx]
                val t = (i % noteLen).toDouble() / noteLen
                val sample = sin(2.0 * Math.PI * freq * i / sampleRate) * (1.0 - t * 0.3)
                buffer[i] = (sample * 28000).toInt().coerceIn(-32768, 32767).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playClick() {
        if (!prefs.soundEnabled) return
        executor.execute {
            val durationMs = 30
            val numSamples = sampleRate * durationMs / 1000
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / numSamples
                val sample = sin(2.0 * Math.PI * 1000.0 * i / sampleRate) * (1.0 - t)
                buffer[i] = (sample * 20000).toInt().coerceIn(-32768, 32767).toShort()
            }
            playPcm(buffer)
        }
    }

    private fun playPcm(buffer: ShortArray) {
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(sampleRate)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            Thread.sleep(100)
            track.release()
        } catch (e: Exception) {
            // Ignore sound playback exceptions
        }
    }

    fun release() {
        executor.shutdown()
    }
}
