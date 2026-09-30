package com.gyaneshflap.game.graphics

import android.graphics.*
import com.gyaneshflap.game.utils.GameMetrics
import kotlin.math.cos
import kotlin.math.sin

class CollectibleDrawer {
    private val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    private val boxBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#4A2E14")
        style = Paint.Style.STROKE
        strokeWidth = GameMetrics.dpToPx(2f)
    }
    private val slotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFC800") }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1B365D")
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }
    private val wingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFF9C4") }
    private val wingBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFB300")
        style = Paint.Style.STROKE
        strokeWidth = GameMetrics.dpToPx(1.5f)
    }
    private val sparklePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFD700") }

    fun drawBallotBox(canvas: Canvas, x: Float, y: Float, size: Float, animTime: Float) {
        canvas.save()
        canvas.translate(x, y)

        val r = size / 2f
        val wingFlap = sin(animTime * 15f) * GameMetrics.dpToPx(10f)

        // Left Wing
        val leftWingPath = Path().apply {
            moveTo(-r * 0.5f, 0f)
            cubicTo(-r * 1.2f, -r * 0.8f + wingFlap, -r * 1.5f, -r * 0.2f + wingFlap, -r * 1.6f, -r * 0.6f + wingFlap)
            cubicTo(-r * 1.2f, r * 0.2f, -r * 0.8f, 0f, -r * 0.5f, r * 0.3f)
            close()
        }
        canvas.drawPath(leftWingPath, wingPaint)
        canvas.drawPath(leftWingPath, wingBorderPaint)

        // Right Wing
        val rightWingPath = Path().apply {
            moveTo(r * 0.5f, 0f)
            cubicTo(r * 1.2f, -r * 0.8f + wingFlap, r * 1.5f, -r * 0.2f + wingFlap, r * 1.6f, -r * 0.6f + wingFlap)
            cubicTo(r * 1.2f, r * 0.2f, r * 0.8f, 0f, r * 0.5f, r * 0.3f)
            close()
        }
        canvas.drawPath(rightWingPath, wingPaint)
        canvas.drawPath(rightWingPath, wingBorderPaint)

        // Ballot Box Body
        val boxRect = RectF(-r * 0.6f, -r * 0.6f, r * 0.6f, r * 0.6f)
        val cornerR = GameMetrics.dpToPx(5f)
        canvas.drawRoundRect(boxRect, cornerR, cornerR, boxPaint)
        canvas.drawRoundRect(boxRect, cornerR, cornerR, boxBorderPaint)

        // Voting Slot on top
        canvas.drawRect(-r * 0.3f, -r * 0.5f, r * 0.3f, -r * 0.35f, slotPaint)

        // Text "VOTE"
        textPaint.textSize = r * 0.45f
        canvas.drawText("VOTE", 0f, r * 0.2f, textPaint)

        // Sparkling Star particles
        for (i in 0..3) {
            val angle = i * 1.57f + animTime * 3f
            val sx = cos(angle.toDouble()).toFloat() * r * 1.1f
            val sy = sin(angle.toDouble()).toFloat() * r * 1.1f
            canvas.drawCircle(sx, sy, GameMetrics.dpToPx(2f), sparklePaint)
        }

        canvas.restore()
    }
}
