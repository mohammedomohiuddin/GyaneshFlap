package com.gyaneshflap.game.game

import android.os.Handler
import android.os.Looper
import android.view.Choreographer

class GameLoop(private val updateAndRender: (deltaTime: Float) -> Unit) : Choreographer.FrameCallback {
    private var isRunning = false
    private var lastFrameTimeNanos: Long = 0
    private val handler = Handler(Looper.getMainLooper())

    fun start() {
        if (isRunning) return
        isRunning = true
        lastFrameTimeNanos = System.nanoTime()
        Choreographer.getInstance().postFrameCallback(this)
    }

    fun stop() {
        isRunning = false
        Choreographer.getInstance().removeFrameCallback(this)
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (!isRunning) return

        if (lastFrameTimeNanos == 0L) {
            lastFrameTimeNanos = frameTimeNanos
        }

        val deltaTime = ((frameTimeNanos - lastFrameTimeNanos) / 1_000_000_000.0f).coerceIn(0.001f, 0.05f)
        lastFrameTimeNanos = frameTimeNanos

        updateAndRender(deltaTime)

        if (isRunning) {
            Choreographer.getInstance().postFrameCallback(this)
        }
    }
}
