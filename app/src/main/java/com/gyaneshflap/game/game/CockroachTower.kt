package com.gyaneshflap.game.game

import android.graphics.RectF

class CockroachTower(
    var x: Float,
    val width: Float,
    val gapY: Float,
    val gapHeight: Float,
    var speed: Float
) {
    var passed: Boolean = false

    fun update() {
        x -= speed
    }

    fun isOffScreen(): Boolean {
        return x + width < 0f
    }

    fun getTopBounds(screenHeight: Float): RectF {
        val topBottom = gapY - gapHeight / 2f
        return RectF(x, 0f, x + width, topBottom)
    }

    fun getBottomBounds(screenHeight: Float): RectF {
        val bottomTop = gapY + gapHeight / 2f
        return RectF(x, bottomTop, x + width, screenHeight)
    }

    fun collidesWith(playerHitBox: RectF, screenHeight: Float): Boolean {
        return RectF.intersects(getTopBounds(screenHeight), playerHitBox) ||
               RectF.intersects(getBottomBounds(screenHeight), playerHitBox)
    }
}
