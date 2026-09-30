package com.gyaneshflap.game.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.gyaneshflap.game.utils.PreferencesManager
import kotlin.concurrent.thread
import kotlin.math.sin

class MusicPlayer(context: Context, private val prefs: PreferencesManager) {
    @Volatile
    private var isPlaying = false
    private var musicThread: Thread? = null
    private val sampleRate = 22050

    fun start() {
        if (!prefs.musicEnabled || isPlaying) return
        isPlaying = true
        musicThread = thread(start = true, name = "MusicThread") {
            runMusicLoop()
        }
    }

    fun stop() {
        isPlaying = false
        musicThread?.interrupt()
        musicThread = null
    }

    fun updateState() {
        if (prefs.musicEnabled) {
            if (!isPlaying) start()
        } else {
            if (isPlaying) stop()
        }
    }

    private fun runMusicLoop() {
        val notes = intArrayOf(
            262, 329, 392, 523, 392, 329, 262, 196,
            220, 277, 330, 440, 330, 277, 220, 165,
            247, 294, 370, 494, 370, 294, 247, 185,
            262, 330, 392, 523, 659, 523, 392, 330
        )
        val noteDurationMs = 120
        val samplesPerNote = sampleRate * noteDurationMs / 1000
        val totalSamples = samplesPerNote * notes.size
        val pcmBuffer = ShortArray(totalSamples)

        for (idx in notes.indices) {
            val freq = notes[idx].toDouble()
            val startSample = idx * samplesPerNote
            for (i in 0 until samplesPerNote) {
                val overallIdx = startSample + i
                val t = i.toDouble() / samplesPerNote
                val sineLead = sin(2.0 * Math.PI * freq * i / sampleRate)
                val squareBass = if (sin(2.0 * Math.PI * (freq / 2) * i / sampleRate) > 0) 0.3 else -0.3
                val sample = (sineLead * 0.4 + squareBass * 0.2) * (1.0 - t * 0.3)
                pcmBuffer[overallIdx] = (sample * 8000).toInt().coerceIn(-32768, 32767).toShort()
            }
        }

        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
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
                .setBufferSizeInBytes(pcmBuffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(pcmBuffer, 0, pcmBuffer.size)
            track.setLoopPoints(0, totalSamples, -1) // Loop infinitely
            track.play()

            while (isPlaying && !Thread.currentThread().isInterrupted) {
                Thread.sleep(200)
            }

            track.stop()
            track.release()
        } catch (e: Exception) {
            // Ignore music thread interruption
        }
    }
}
