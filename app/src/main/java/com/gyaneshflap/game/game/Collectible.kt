package com.gyaneshflap.game.game

import android.graphics.RectF
import com.gyaneshflap.game.utils.GameMetrics
import kotlin.math.sin

class Collectible(
    var x: Float,
    var baseY: Float,
    val size: Float,
    var speed: Float
) {
    var isCollected: Boolean = false
    private var animPhase: Float = (Math.random() * 6.28).toFloat()

    fun update(animTime: Float) {
        x -= speed
    }

    fun getCurrentY(animTime: Float): Float {
        return baseY + sin(animTime * 5f + animPhase) * GameMetrics.dpToPx(12f)
    }

    fun isOffScreen(): Boolean {
        return x + size < 0f
    }

    fun getHitBox(animTime: Float): RectF {
        val cy = getCurrentY(animTime)
        return RectF(x - size / 2f, cy - size / 2f, x + size / 2f, cy + size / 2f)
    }

    fun collidesWith(playerHitBox: RectF, animTime: Float): Boolean {
        return !isCollected && RectF.intersects(getHitBox(animTime), playerHitBox)
    }
}
