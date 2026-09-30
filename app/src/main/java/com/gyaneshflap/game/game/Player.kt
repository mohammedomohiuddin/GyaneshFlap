package com.gyaneshflap.game.game

import android.graphics.RectF
import com.gyaneshflap.game.utils.GameMetrics

class Player {
    var x: Float = 0f
    var y: Float = 0f
    var vy: Float = 0f
    var size: Float = GameMetrics.dpToPx(60f)
    var rotation: Float = 0f

    private var gravity: Float = GameMetrics.dpToPx(0.8f)
    private var flapImpulse: Float = -GameMetrics.dpToPx(13f)

    fun reset(startX: Float, startY: Float, playerSize: Float, screenHeight: Float) {
        x = startX
        y = startY
        vy = 0f
        size = playerSize
        rotation = 0f

        gravity = screenHeight * 0.0011f
        flapImpulse = -screenHeight * 0.018f
    }

    fun flap() {
        vy = flapImpulse
    }

    fun update(screenHeight: Float, groundY: Float) {
        vy += gravity
        y += vy

        rotation = (vy * 2.8f).coerceIn(-30f, 75f)

        if (y - size / 2f < 0f) {
            y = size / 2f
            vy = 0f
        }
    }

    /**
     * Refined, forgiving Hitbox:
     * Focuses on the core body and head of Gyanesh.
     * Generous padding prevents transparent PNG margins from causing unfair deaths!
     */
    fun getHitBox(): RectF {
        val padX = size * 0.32f
        val padY = size * 0.28f
        return RectF(
            x - size / 2f + padX,
            y - size / 2f + padY,
            x + size / 2f - padX,
            y + size / 2f - padY
        )
    }

    fun isCollidingWithGround(groundY: Float): Boolean {
        return y + size * 0.35f >= groundY
    }
}
