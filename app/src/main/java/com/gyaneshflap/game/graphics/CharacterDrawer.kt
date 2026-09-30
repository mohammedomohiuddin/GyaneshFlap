package com.gyaneshflap.game.graphics

import android.content.Context
import android.graphics.*
import com.gyaneshflap.game.R
import com.gyaneshflap.game.utils.GameMetrics
import kotlin.math.cos
import kotlin.math.sin

class CharacterDrawer(context: Context) {
    private val rawBitmap: Bitmap? = try {
        BitmapFactory.decodeResource(context.resources, R.drawable.gyanesh_character)
    } catch (e: Exception) {
        null
    }

    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val detailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = GameMetrics.dpToPx(2f)
        strokeCap = Paint.Cap.ROUND
    }
    private val skinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFD1A4") }
    private val suitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1B365D") }

    fun drawCharacter(
        canvas: Canvas,
        x: Float,
        y: Float,
        size: Float,
        rotation: Float,
        animTime: Float,
        isDizzy: Boolean = false
    ) {
        canvas.save()
        canvas.translate(x, y)

        if (isDizzy) {
            canvas.rotate(110f) // Knocked out rotation
        } else {
            canvas.rotate(rotation)
        }

        val halfSize = size / 2f

        if (rawBitmap != null) {
            val destRect = RectF(-halfSize, -halfSize, halfSize, halfSize)
            canvas.drawBitmap(rawBitmap, null, destRect, bitmapPaint)
        } else {
            // Fallback procedurally drawn character if bitmap fails to load
            drawProceduralCharacter(canvas, halfSize)
        }

        if (isDizzy) {
            drawDizzyOverlay(canvas, halfSize)
        }

        canvas.restore()
    }

    private fun drawDizzyOverlay(canvas: Canvas, r: Float) {
        // Spiral Eyes over character face
        drawSpiralEye(canvas, -r * 0.15f, -r * 0.1f, r * 0.14f)
        drawSpiralEye(canvas, r * 0.2f, -r * 0.1f, r * 0.14f)

        // Floating stars around head
        for (i in 0..2) {
            val angle = i * 2.094f
            val starX = cos(angle.toDouble()).toFloat() * r * 0.85f
            val starY = -r * 0.7f + sin(angle.toDouble()).toFloat() * r * 0.25f
            canvas.drawCircle(starX, starY, r * 0.08f, suitPaint)
        }
    }

    private fun drawSpiralEye(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val path = Path()
        var r = radius
        val turns = 3
        var angle = 0.0
        path.moveTo(cx, cy)
        while (r > 0) {
            angle += 0.3
            r -= radius / (turns * 20)
            val x = cx + cos(angle).toFloat() * r
            val y = cy + sin(angle).toFloat() * r
            path.lineTo(x, y)
        }
        canvas.drawPath(path, detailPaint)
    }

    private fun drawProceduralCharacter(canvas: Canvas, r: Float) {
        val headRadius = r * 0.6f
        canvas.drawCircle(0f, 0f, headRadius, skinPaint)
    }
}
