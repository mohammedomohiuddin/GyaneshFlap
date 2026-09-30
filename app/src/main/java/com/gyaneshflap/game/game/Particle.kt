package com.gyaneshflap.game.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import com.gyaneshflap.game.utils.GameMetrics

class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val text: String? = null,
    val color: Int = Color.YELLOW,
    var life: Float = 1.0f,
    val decay: Float = 0.03f
) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    fun update(): Boolean {
        x += vx
        y += vy
        vy += GameMetrics.dpToPx(0.12f) // gravity on particle
        life -= decay
        return life > 0f
    }

    fun draw(canvas: Canvas) {
        if (life <= 0f) return
        paint.alpha = (life * 255).toInt().coerceIn(0, 255)

        if (!text.isNullOrEmpty()) {
            paint.color = color
            paint.textSize = GameMetrics.spToPx(22f) * life
            canvas.drawText(text, x, y, paint)
        } else {
            paint.color = color
            canvas.drawCircle(x, y, GameMetrics.dpToPx(5f) * life, paint)
        }
    }
}
