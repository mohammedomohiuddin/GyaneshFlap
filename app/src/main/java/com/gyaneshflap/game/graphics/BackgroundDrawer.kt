package com.gyaneshflap.game.graphics

import android.content.Context
import android.graphics.*
import com.gyaneshflap.game.R
import kotlin.math.max
import kotlin.math.sin

class BackgroundDrawer(context: Context) {
    private val bgBitmap: Bitmap? = try {
        BitmapFactory.decodeResource(context.resources, R.drawable.bg_polling_booth)
    } catch (e: Exception) {
        null
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val groundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#D3A056") }
    private val groundDarkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#A67A38") }
    private val grassEdgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#388E3C") }

    private val srcRect = Rect()
    private val destRect = RectF()

    fun drawBackground(
        canvas: Canvas,
        width: Float,
        height: Float,
        scrollX: Float,
        animTime: Float
    ) {
        if (bgBitmap != null && bgBitmap.width > 0 && bgBitmap.height > 0) {
            srcRect.set(0, 0, bgBitmap.width, bgBitmap.height)

            // Center-crop / Aspect Fill so artwork is perfectly centered, symmetrical, and seamless
            val scale = max(width / bgBitmap.width.toFloat(), height / bgBitmap.height.toFloat())
            val drawW = bgBitmap.width * scale
            val drawH = bgBitmap.height * scale

            // Gentle, smooth camera sway for life without ugly tiling seams
            val swayX = sin(animTime * 1.5f) * 12f
            val left = (width - drawW) / 2f + swayX
            val top = (height - drawH) / 2f

            destRect.set(left, top, left + drawW, top + drawH)
            canvas.drawBitmap(bgBitmap, srcRect, destRect, paint)

            // Scrolling ground track at bottom for forward flight motion feedback
            drawScrollingGround(canvas, width, height, scrollX)
        } else {
            // Fallback sky gradient background if bitmap fails to load
            val skyGradient = LinearGradient(0f, 0f, 0f, height, Color.parseColor("#50A4EC"), Color.parseColor("#E0F7FA"), Shader.TileMode.CLAMP)
            paint.shader = skyGradient
            canvas.drawRect(0f, 0f, width, height, paint)
            paint.shader = null

            drawScrollingGround(canvas, width, height, scrollX)
        }
    }

    private fun drawScrollingGround(canvas: Canvas, width: Float, height: Float, scrollX: Float) {
        val groundY = height * 0.84f

        // Ground base
        canvas.drawRect(0f, groundY, width, height, groundPaint)

        // Top grass trim line
        canvas.drawRect(0f, groundY, width, groundY + 6f, grassEdgePaint)

        // Scrolling dirt pebbles for flight speed feedback
        val pebbleSpacing = 70f
        val numPebbles = (width / pebbleSpacing).toInt() + 2
        val offset = scrollX % pebbleSpacing

        for (i in 0..numPebbles) {
            val px = i * pebbleSpacing - offset
            val py = groundY + 20f + ((i * 37) % 25)
            canvas.drawCircle(px, py, 3f + ((i * 13) % 4), groundDarkPaint)
        }
    }
}
