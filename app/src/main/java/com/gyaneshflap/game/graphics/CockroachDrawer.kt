package com.gyaneshflap.game.graphics

import android.graphics.*
import com.gyaneshflap.game.utils.GameMetrics
import kotlin.math.sin

/**
 * Draws authentic, realistic cartoon cockroaches.
 * Features realistic cockroach anatomy:
 * - Pronotum (head shield plate)
 * - Shiny split mahogany elytra (wing covers)
 * - Backward-bent jointed legs with tibia spines
 * - Long sweeping whip-like antennae
 * - Abdominal cerci spikes
 */
class CockroachDrawer {
    private val shellPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#5C2000") }
    private val shellDarkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#381300") }
    private val wingHighlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#8C3B00") }
    private val shinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(120, 255, 255, 255) }

    private val legPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2B0C00")
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val antennaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2B0C00")
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val eyeWhitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    private val eyePupilPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK }
    private val crownPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFD700") }
    private val crownJewelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#D32F2F") }

    private val detailStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#230A00")
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    fun drawCockroachTower(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        isTopTower: Boolean,
        animTime: Float
    ) {
        val width = right - left
        val height = bottom - top
        if (width <= 0 || height <= 0) return

        val roachHeight = width * 1.1f
        val numRoaches = (height / roachHeight).toInt() + 1
        val startY = if (isTopTower) bottom else top

        for (i in 0 until numRoaches) {
            val cy = if (isTopTower) startY - i * roachHeight - roachHeight / 2f else startY + i * roachHeight + roachHeight / 2f
            if (cy + roachHeight / 2f < top || cy - roachHeight / 2f > bottom) continue

            val isHeader = (i == 0) // The lead cockroach at the gap
            val legAnim = sin(animTime * 14f + i) * GameMetrics.dpToPx(3f)

            drawAuthenticCockroach(
                canvas = canvas,
                cx = left + width / 2f,
                cy = cy,
                w = width * 0.85f,
                h = roachHeight * 0.9f,
                isFacingUp = !isTopTower,
                hasCrown = isHeader && isTopTower,
                legOffset = legAnim
            )
        }
    }

    private fun drawAuthenticCockroach(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        w: Float,
        h: Float,
        isFacingUp: Boolean,
        hasCrown: Boolean,
        legOffset: Float
    ) {
        val strokeW = GameMetrics.dpToPx(2.5f)
        legPaint.strokeWidth = strokeW
        antennaPaint.strokeWidth = GameMetrics.dpToPx(1.8f)
        detailStrokePaint.strokeWidth = GameMetrics.dpToPx(2f)

        val dir = if (isFacingUp) -1f else 1f

        val bodyW = w * 0.65f
        val bodyH = h * 0.82f

        // 1. Jointed Backward-Bent Cockroach Legs (3 pairs)
        // Pair 1: Front legs (bent slightly forward/outward)
        drawJointedLeg(canvas, cx - bodyW * 0.4f, cy - dir * bodyH * 0.25f, -1f, dir, 0.7f, legOffset)
        drawJointedLeg(canvas, cx + bodyW * 0.4f, cy - dir * bodyH * 0.25f, 1f, dir, 0.7f, -legOffset)

        // Pair 2: Middle legs (bent backward/outward)
        drawJointedLeg(canvas, cx - bodyW * 0.45f, cy, -1f, dir, 1.0f, -legOffset)
        drawJointedLeg(canvas, cx + bodyW * 0.45f, cy, 1f, dir, 1.0f, legOffset)

        // Pair 3: Rear legs (long, bent strongly backward)
        drawJointedLeg(canvas, cx - bodyW * 0.4f, cy + dir * bodyH * 0.25f, -1f, dir, 1.3f, legOffset)
        drawJointedLeg(canvas, cx + bodyW * 0.4f, cy + dir * bodyH * 0.25f, 1f, dir, 1.3f, -legOffset)

        // 2. Abdominal Cerci (small tail spikes at back)
        val tailY = cy + dir * bodyH * 0.48f
        canvas.drawLine(cx - bodyW * 0.25f, tailY, cx - bodyW * 0.45f, tailY + dir * GameMetrics.dpToPx(12f), detailStrokePaint)
        canvas.drawLine(cx + bodyW * 0.25f, tailY, cx + bodyW * 0.45f, tailY + dir * GameMetrics.dpToPx(12f), detailStrokePaint)

        // 3. Elongated Oval Mahogany Body Shell (Wings / Elytra)
        val bodyRect = RectF(cx - bodyW / 2f, cy - bodyH / 2f, cx + bodyW / 2f, cy + bodyH / 2f)
        canvas.drawRoundRect(bodyRect, bodyW * 0.45f, bodyW * 0.45f, shellPaint)

        // Split line down middle of wing covers
        val headY = cy - dir * bodyH * 0.35f
        canvas.drawLine(cx, headY, cx, tailY, shellDarkPaint)

        // Wing highlights
        val wingHighlightLeft = RectF(cx - bodyW * 0.42f, cy - bodyH * 0.3f, cx - bodyW * 0.08f, cy + bodyH * 0.3f)
        canvas.drawRoundRect(wingHighlightLeft, bodyW * 0.2f, bodyW * 0.2f, wingHighlightPaint)

        val specularRect = RectF(cx - bodyW * 0.35f, cy - bodyH * 0.25f, cx - bodyW * 0.2f, cy + bodyH * 0.15f)
        canvas.drawOval(specularRect, shinePaint)

        // 4. Pronotum (Head Shield Plate at front)
        val pronW = bodyW * 0.85f
        val pronH = bodyH * 0.32f
        val pronRect = RectF(cx - pronW / 2f, headY - pronH / 2f, cx + pronW / 2f, headY + pronH / 2f)
        canvas.drawOval(pronRect, shellDarkPaint)

        // 5. Cartoon Bulging Eyes on Pronotum
        val eyeR = pronW * 0.22f
        val eyeY = headY - dir * pronH * 0.1f
        val eyeXLeft = cx - pronW * 0.28f
        val eyeXRight = cx + pronW * 0.28f

        canvas.drawCircle(eyeXLeft, eyeY, eyeR, eyeWhitePaint)
        canvas.drawCircle(eyeXRight, eyeY, eyeR, eyeWhitePaint)
        canvas.drawCircle(eyeXLeft, eyeY, eyeR, detailStrokePaint)
        canvas.drawCircle(eyeXRight, eyeY, eyeR, detailStrokePaint)

        // Pupils looking menacingly
        canvas.drawCircle(eyeXLeft, eyeY - dir * eyeR * 0.2f, eyeR * 0.45f, eyePupilPaint)
        canvas.drawCircle(eyeXRight, eyeY - dir * eyeR * 0.2f, eyeR * 0.45f, eyePupilPaint)

        // 6. Long Sweeping Whip-like Antennae
        val antTipY = headY - dir * bodyH * 0.9f
        val antPathLeft = Path().apply {
            moveTo(cx - pronW * 0.2f, headY - dir * pronH * 0.4f)
            cubicTo(
                cx - pronW * 0.8f, headY - dir * bodyH * 0.4f,
                cx - pronW * 1.5f, headY - dir * bodyH * 0.6f,
                cx - pronW * 1.3f + legOffset * 2f, antTipY
            )
        }
        val antPathRight = Path().apply {
            moveTo(cx + pronW * 0.2f, headY - dir * pronH * 0.4f)
            cubicTo(
                cx + pronW * 0.8f, headY - dir * bodyH * 0.4f,
                cx + pronW * 1.5f, headY - dir * bodyH * 0.6f,
                cx + pronW * 1.3f - legOffset * 2f, antTipY
            )
        }
        canvas.drawPath(antPathLeft, antennaPaint)
        canvas.drawPath(antPathRight, antennaPaint)

        // 7. Golden Crown on Top Leader Roach
        if (hasCrown) {
            val crownY = headY - dir * pronH * 0.5f
            val crownW = pronW * 0.8f
            val crownH = pronH * 0.9f
            val crownPath = Path().apply {
                moveTo(cx - crownW * 0.4f, crownY)
                lineTo(cx - crownW * 0.5f, crownY - dir * crownH)
                lineTo(cx - crownW * 0.2f, crownY - dir * crownH * 0.5f)
                lineTo(cx, crownY - dir * crownH * 1.1f)
                lineTo(cx + crownW * 0.2f, crownY - dir * crownH * 0.5f)
                lineTo(cx + crownW * 0.5f, crownY - dir * crownH)
                lineTo(cx + crownW * 0.4f, crownY)
                close()
            }
            canvas.drawPath(crownPath, crownPaint)
            canvas.drawCircle(cx, crownY - dir * crownH * 0.8f, GameMetrics.dpToPx(3.5f), crownJewelPaint)
        }
    }

    private fun drawJointedLeg(
        canvas: Canvas,
        startX: Float,
        startY: Float,
        side: Float, // -1 for left, 1 for right
        dir: Float,  // -1 for up, 1 for down
        lengthMult: Float,
        legOffset: Float
    ) {
        val seg1Len = GameMetrics.dpToPx(14f) * lengthMult
        val seg2Len = GameMetrics.dpToPx(18f) * lengthMult

        // Joint 1: Coxa/Femur going outward and slightly backward
        val kneeX = startX + side * seg1Len
        val kneeY = startY + dir * (seg1Len * 0.4f) + legOffset

        // Joint 2: Tibia bending backward/downward
        val footX = kneeX + side * (seg2Len * 0.6f)
        val footY = kneeY + dir * seg2Len

        // Draw jointed leg segments
        canvas.drawLine(startX, startY, kneeX, kneeY, legPaint)
        canvas.drawLine(kneeX, kneeY, footX, footY, legPaint)

        // Tiny tibial spines (spiky cockroach leg hairs)
        val midX = (kneeX + footX) / 2f
        val midY = (kneeY + footY) / 2f
        canvas.drawLine(midX, midY, midX + side * GameMetrics.dpToPx(4f), midY + dir * GameMetrics.dpToPx(2f), legPaint)
    }
}
