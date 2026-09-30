package com.gyaneshflap.game.graphics

import android.content.Context
import android.graphics.*
import com.gyaneshflap.game.utils.GameMetrics
import kotlin.math.sin

/**
 * UI drawer using the standardized GameMetrics system.
 * Eliminates raw hardcoded dimensions in favor of DP/SP conversions
 * and screen-proportional positioning.
 */
class UiDrawer(context: Context? = null) {
    // --- Dynamic Paints ---------------------------------------------------
    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        color = Color.parseColor("#FFD700")
        setShadowLayer(GameMetrics.dpToPx(4f), 0f, GameMetrics.dpToPx(2f), Color.parseColor("#212121"))
        style = Paint.Style.FILL
    }
    private val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        color = Color.parseColor("#FFFFFF")
        setShadowLayer(GameMetrics.dpToPx(4f), 0f, GameMetrics.dpToPx(2f), Color.parseColor("#212121"))
        style = Paint.Style.FILL
    }
    private val buttonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFC800")
        style = Paint.Style.FILL
    }
    private val buttonShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#C69200")
        style = Paint.Style.FILL
    }
    private val woodBtnPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#6D4C41") }
    private val woodBtnShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#3E2723") }
    private val parchmentBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#F4E8C1") }
    private val parchmentBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#8D6E63")
        style = Paint.Style.STROKE
        strokeWidth = GameMetrics.dpToPx(4f)
    }
    private val tapePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#E0D0B0") }
    private val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    private val yellowBtnPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFC800") }
    private val switchOnPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#4CAF50") }
    private val switchOffPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#D32F2F") }

    // --- Rect Trackers ----------------------------------------------------
    val btnPlay = RectF()
    val btnHighScore = RectF()
    val btnSettings = RectF()
    val btnAbout = RectF()
    val btnHowToPlay = RectF()
    val btnPause = RectF()
    val btnRetry = RectF()
    val btnMenu = RectF()
    val btnBack = RectF()

    val toggleSound = RectF()
    val toggleMusic = RectF()
    val toggleVibration = RectF()

    // --- Public Screen Drawers --------------------------------------------
    fun drawMainMenu(canvas: Canvas, w: Float, h: Float, animTime: Float) {
        val titleY = h * 0.15f
        draw3DText(canvas, "GYANESH", w / 2f, titleY, GameMetrics.spToPx(42f), Color.parseColor("#FFD700"), Color.BLACK)
        draw3DText(canvas, "FLAP", w / 2f, titleY + GameMetrics.dpToPx(45f), GameMetrics.spToPx(40f), Color.parseColor("#FFC800"), Color.BLACK)

        // Subtitle banner
        val subRect = RectF(w * 0.15f, titleY + GameMetrics.dpToPx(70f), w * 0.85f, titleY + GameMetrics.dpToPx(100f))
        val cornerR = GameMetrics.dpToPx(12f)
        canvas.drawRoundRect(subRect, cornerR, cornerR, woodBtnShadowPaint)
        subRect.offset(0f, -GameMetrics.dpToPx(3f))
        canvas.drawRoundRect(subRect, cornerR, cornerR, yellowBtnPaint)
        draw3DText(canvas, "DODGE THE ROACHES", w / 2f, subRect.centerY() + GameMetrics.spToPx(6f), GameMetrics.spToPx(14f), Color.BLACK, Color.TRANSPARENT)

        // Buttons Stack
        val btnW = w * 0.65f
        val btnH = GameMetrics.dpToPx(48f)
        val btnStartY = h * 0.52f
        val spacing = GameMetrics.dpToPx(56f)

        btnPlay.set(w / 2f - btnW / 2f, btnStartY, w / 2f + btnW / 2f, btnStartY + btnH)
        drawPillButton(canvas, btnPlay, "▶ PLAY", yellow = true)

        btnHighScore.set(w / 2f - btnW / 2f, btnStartY + spacing, w / 2f + btnW / 2f, btnStartY + spacing + btnH)
        drawPillButton(canvas, btnHighScore, "🏆 HIGH SCORE", yellow = false)

        btnSettings.set(w / 2f - btnW / 2f, btnStartY + spacing * 2, w / 2f + btnW / 2f, btnStartY + spacing * 2 + btnH)
        drawPillButton(canvas, btnSettings, "⚙ SETTINGS", yellow = false)

        btnAbout.set(w / 2f - btnW / 2f, btnStartY + spacing * 3, w / 2f + btnW / 2f, btnStartY + spacing * 3 + btnH)
        drawPillButton(canvas, btnAbout, "ⓘ ABOUT", yellow = false)

        btnHowToPlay.set(w / 2f - btnW / 2f, btnStartY + spacing * 4, w / 2f + btnW / 2f, btnStartY + spacing * 4 + btnH)
        drawPillButton(canvas, btnHowToPlay, "❓ HOW TO PLAY", yellow = false)
    }

    fun drawGetReady(canvas: Canvas, w: Float, h: Float, animTime: Float) {
        val titleY = h * 0.28f
        draw3DText(canvas, "GET READY!", w / 2f, titleY, GameMetrics.spToPx(32f), Color.parseColor("#FFC800"), Color.parseColor("#0288D1"))
        val tapY = h * 0.68f
        val bob = sin(animTime * 8f) * GameMetrics.dpToPx(6f)
        draw3DText(canvas, "👆 TAP TO START", w / 2f, tapY + bob, GameMetrics.spToPx(20f), Color.WHITE, Color.BLACK)
    }

    fun drawGameplayHud(canvas: Canvas, w: Float, h: Float, score: Int) {
        draw3DText(canvas, score.toString(), w / 2f, h * 0.12f, GameMetrics.spToPx(42f), Color.WHITE, Color.BLACK)

        val btnSize = GameMetrics.dpToPx(42f)
        val pad = GameMetrics.dpToPx(16f)
        btnPause.set(pad, pad, pad + btnSize, pad + btnSize)

        val cornerR = GameMetrics.dpToPx(10f)
        canvas.drawRoundRect(btnPause, cornerR, cornerR, woodBtnShadowPaint)
        val pauseFront = RectF(btnPause).apply { offset(0f, -GameMetrics.dpToPx(2f)) }
        canvas.drawRoundRect(pauseFront, cornerR, cornerR, yellowBtnPaint)
        draw3DText(canvas, "⏸", pauseFront.centerX(), pauseFront.centerY() + GameMetrics.spToPx(5f), GameMetrics.spToPx(18f), Color.BLACK, Color.TRANSPARENT)
    }

    fun drawGameOver(canvas: Canvas, w: Float, h: Float, score: Int, best: Int, isNewBest: Boolean) {
        canvas.drawColor(Color.argb(160, 0, 0, 0))
        draw3DText(canvas, "GAME OVER", w / 2f, h * 0.18f, GameMetrics.spToPx(36f), Color.parseColor("#E53935"), Color.BLACK)
        draw3DText(canvas, "COCKROACH INTERFERENCE DETECTED", w / 2f, h * 0.23f, GameMetrics.spToPx(13f), Color.WHITE, Color.BLACK)

        val cardW = w * 0.8f
        val cardH = GameMetrics.dpToPx(140f)
        val cardX = w / 2f - cardW / 2f
        val cardY = h * 0.45f
        val cardRect = RectF(cardX, cardY, cardX + cardW, cardY + cardH)
        val cornerR = GameMetrics.dpToPx(15f)

        canvas.drawRoundRect(cardRect, cornerR, cornerR, woodBtnShadowPaint)
        cardRect.offset(0f, -GameMetrics.dpToPx(4f))
        canvas.drawRoundRect(cardRect, cornerR, cornerR, parchmentBgPaint)
        canvas.drawRoundRect(cardRect, cornerR, cornerR, parchmentBorderPaint)

        draw3DText(canvas, "SCORE", w / 2f, cardY + GameMetrics.dpToPx(30f), GameMetrics.spToPx(16f), Color.parseColor("#4A2E14"), Color.TRANSPARENT)
        draw3DText(canvas, score.toString(), w / 2f, cardY + GameMetrics.dpToPx(75f), GameMetrics.spToPx(38f), Color.BLACK, Color.TRANSPARENT)
        draw3DText(canvas, "👑 BEST: $best", w / 2f, cardY + GameMetrics.dpToPx(112f), GameMetrics.spToPx(18f), Color.parseColor("#FF8F00"), Color.BLACK)

        if (isNewBest) {
            draw3DText(canvas, "NEW HIGH SCORE!", w / 2f, cardY - GameMetrics.dpToPx(15f), GameMetrics.spToPx(18f), Color.parseColor("#FFC800"), Color.BLACK)
        }

        val btnW = w * 0.7f
        val btnH = GameMetrics.dpToPx(44f)
        btnRetry.set(w / 2f - btnW / 2f, cardY + cardH + GameMetrics.dpToPx(25f), w / 2f + btnW / 2f, cardY + cardH + GameMetrics.dpToPx(25f) + btnH)
        drawPillButton(canvas, btnRetry, "↻ RETRY", yellow = true)

        btnMenu.set(w / 2f - btnW / 2f, cardY + cardH + GameMetrics.dpToPx(80f), w / 2f + btnW / 2f, cardY + cardH + GameMetrics.dpToPx(80f) + btnH)
        drawPillButton(canvas, btnMenu, "⌂ MAIN MENU", yellow = false)
    }

    fun drawHighScoreScreen(canvas: Canvas, w: Float, h: Float, scores: List<Int>) {
        drawParchmentBackground(canvas, w, h, "HIGH SCORES 👑")

        if (scores.isEmpty()) {
            val sy = h * 0.42f
            draw3DText(canvas, "NO HIGH SCORES YET!", w / 2f, sy, GameMetrics.spToPx(20f), Color.parseColor("#4A2E14"), Color.TRANSPARENT)
            draw3DText(canvas, "PLAY A GAME TO SET A RECORD!", w / 2f, sy + GameMetrics.dpToPx(35f), GameMetrics.spToPx(14f), Color.parseColor("#6D4C41"), Color.TRANSPARENT)
        } else {
            var sy = h * 0.28f
            val spacing = GameMetrics.dpToPx(36f)

            for (i in scores.indices) {
                val rank = i + 1
                val sc = scores[i]
                val crown = if (rank == 1) "👑 " else ""
                draw3DText(canvas, "#$rank", w * 0.30f, sy, GameMetrics.spToPx(20f), Color.parseColor("#4A2E14"), Color.TRANSPARENT)
                draw3DText(canvas, "$crown$sc", w * 0.70f, sy, GameMetrics.spToPx(22f), Color.BLACK, Color.TRANSPARENT)
                sy += spacing
            }
        }

        val quoteY = h * 0.75f
        draw3DText(canvas, "\"DODGE TODAY,", w / 2f, quoteY, GameMetrics.spToPx(15f), Color.parseColor("#4A2E14"), Color.TRANSPARENT)
        draw3DText(canvas, "DEMOCRACY TOMORROW!\"", w / 2f, quoteY + GameMetrics.dpToPx(22f), GameMetrics.spToPx(15f), Color.parseColor("#4A2E14"), Color.TRANSPARENT)
    }

    fun drawSettingsScreen(canvas: Canvas, w: Float, h: Float, soundOn: Boolean, musicOn: Boolean, vibOn: Boolean) {
        drawParchmentBackground(canvas, w, h, "SETTINGS ⚙")
        val sY = h * 0.28f
        val sp = GameMetrics.dpToPx(55f)

        toggleSound.set(w * 0.65f, sY - GameMetrics.dpToPx(18f), w * 0.85f, sY + GameMetrics.dpToPx(10f))
        drawSwitch(canvas, "SOUND EFFECTS", soundOn, sY, toggleSound)

        toggleMusic.set(w * 0.65f, sY + sp - GameMetrics.dpToPx(18f), w * 0.85f, sY + sp + GameMetrics.dpToPx(10f))
        drawSwitch(canvas, "MUSIC", musicOn, sY + sp, toggleMusic)

        toggleVibration.set(w * 0.65f, sY + sp * 2 - GameMetrics.dpToPx(18f), w * 0.85f, sY + sp * 2 + GameMetrics.dpToPx(10f))
        drawSwitch(canvas, "VIBRATION", vibOn, sY + sp * 2, toggleVibration)
    }

    fun drawHowToPlayScreen(canvas: Canvas, w: Float, h: Float) {
        drawParchmentBackground(canvas, w, h, "HOW TO PLAY ❓")
        val sY = h * 0.28f
        val spacing = GameMetrics.dpToPx(85f)

        drawInstructionCard(canvas, w, sY, "1. TAP TO FLAP", "Tap screen to flap upward")
        drawInstructionCard(canvas, w, sY + spacing, "2. DODGE COCKROACHES", "Avoid colliding with cockroach towers")
        drawInstructionCard(canvas, w, sY + spacing * 2, "3. COLLECT BALLOT BOXES", "Fly into ballot boxes for +5 extra points!")
    }

    fun drawAboutScreen(canvas: Canvas, w: Float, h: Float) {
        drawParchmentBackground(canvas, w, h, "ABOUT ⓘ")
        val card = RectF(w * 0.12f, h * 0.28f, w * 0.88f, h * 0.72f)
        val cornerR = GameMetrics.dpToPx(15f)

        canvas.drawRoundRect(card, cornerR, cornerR, parchmentBgPaint)
        canvas.drawRoundRect(card, cornerR, cornerR, parchmentBorderPaint)

        val tY = h * 0.36f
        draw3DText(canvas, "GYANESH FLAP", w / 2f, tY, GameMetrics.spToPx(24f), Color.parseColor("#FFC800"), Color.BLACK)
        draw3DText(canvas, "A fan‑made parody game", w / 2f, tY + GameMetrics.dpToPx(35f), GameMetrics.spToPx(14f), Color.BLACK, Color.TRANSPARENT)
        draw3DText(canvas, "created for entertainment purposes.", w / 2f, tY + GameMetrics.dpToPx(58f), GameMetrics.spToPx(14f), Color.BLACK, Color.TRANSPARENT)

        draw3DText(canvas, "No cockroaches were harmed", w / 2f, tY + GameMetrics.dpToPx(140f), GameMetrics.spToPx(14f), Color.parseColor("#D32F2F"), Color.TRANSPARENT)
        draw3DText(canvas, "(in real life) ❤️", w / 2f, tY + GameMetrics.dpToPx(162f), GameMetrics.spToPx(14f), Color.parseColor("#D32F2F"), Color.TRANSPARENT)
    }

    // --- Private Drawing Helpers ------------------------------------------
    private fun drawParchmentBackground(canvas: Canvas, w: Float, h: Float, title: String) {
        canvas.drawColor(Color.argb(180, 0, 0, 0))

        val padX = w * 0.08f
        val padY = h * 0.08f
        val board = RectF(padX, padY, w - padX, h - padY)
        val cornerR = GameMetrics.dpToPx(20f)

        canvas.drawRoundRect(board, cornerR, cornerR, woodBtnShadowPaint)
        board.offset(0f, -GameMetrics.dpToPx(5f))
        canvas.drawRoundRect(board, cornerR, cornerR, parchmentBgPaint)
        canvas.drawRoundRect(board, cornerR, cornerR, parchmentBorderPaint)

        // Corner Tapes
        canvas.drawRect(padX - GameMetrics.dpToPx(5f), padY + GameMetrics.dpToPx(10f), padX + GameMetrics.dpToPx(30f), padY + GameMetrics.dpToPx(22f), tapePaint)
        canvas.drawRect(w - padX - GameMetrics.dpToPx(30f), padY + GameMetrics.dpToPx(10f), w - padX + GameMetrics.dpToPx(5f), padY + GameMetrics.dpToPx(22f), tapePaint)

        // Title
        draw3DText(canvas, title, w / 2f, padY + GameMetrics.dpToPx(48f), GameMetrics.spToPx(26f), Color.parseColor("#4A2E14"), Color.TRANSPARENT)

        // Back button
        val btnSize = GameMetrics.dpToPx(40f)
        btnBack.set(padX + GameMetrics.dpToPx(12f), padY + GameMetrics.dpToPx(12f), padX + GameMetrics.dpToPx(12f) + btnSize, padY + GameMetrics.dpToPx(12f) + btnSize)

        val backCorner = GameMetrics.dpToPx(10f)
        canvas.drawRoundRect(btnBack, backCorner, backCorner, woodBtnShadowPaint)
        val backFront = RectF(btnBack).apply { offset(0f, -GameMetrics.dpToPx(2f)) }
        canvas.drawRoundRect(backFront, backCorner, backCorner, yellowBtnPaint)
        draw3DText(canvas, "←", backFront.centerX(), backFront.centerY() + GameMetrics.spToPx(6f), GameMetrics.spToPx(22f), Color.BLACK, Color.TRANSPARENT)
    }

    private fun drawPillButton(canvas: Canvas, rect: RectF, text: String, yellow: Boolean) {
        val shadow = if (yellow) buttonShadowPaint else woodBtnShadowPaint
        val front = if (yellow) buttonPaint else woodBtnPaint
        val pillR = GameMetrics.dpToPx(20f)

        canvas.drawRoundRect(rect, pillR, pillR, shadow)
        val frontRect = RectF(rect).apply { offset(0f, -GameMetrics.dpToPx(3f)) }
        canvas.drawRoundRect(frontRect, pillR, pillR, front)

        val tColor = if (yellow) Color.BLACK else Color.WHITE
        draw3DText(canvas, text, frontRect.centerX(), frontRect.centerY() + GameMetrics.spToPx(6f), GameMetrics.spToPx(18f), tColor, Color.TRANSPARENT)
    }

    private fun draw3DText(canvas: Canvas, txt: String, x: Float, y: Float, szPx: Float, fcolor: Int, scolor: Int) {
        titlePaint.textSize = szPx
        titlePaint.color = fcolor
        titlePaint.textAlign = Paint.Align.CENTER

        if (scolor != Color.TRANSPARENT) {
            val strokeP = Paint(titlePaint).apply { color = scolor }
            canvas.drawText(txt, x, y, strokeP)
        }
        canvas.drawText(txt, x, y, titlePaint)
    }

    private fun drawInstructionCard(canvas: Canvas, w: Float, y: Float, head: String, sub: String) {
        val r = RectF(w * 0.14f, y - GameMetrics.dpToPx(5f), w * 0.86f, y + GameMetrics.dpToPx(65f))
        val cornerR = GameMetrics.dpToPx(10f)

        canvas.drawRoundRect(r, cornerR, cornerR, whitePaint)
        canvas.drawRoundRect(r, cornerR, cornerR, parchmentBorderPaint)

        draw3DText(canvas, head, w / 2f, y + GameMetrics.dpToPx(22f), GameMetrics.spToPx(16f), Color.parseColor("#4A2E14"), Color.TRANSPARENT)
        draw3DText(canvas, sub, w / 2f, y + GameMetrics.dpToPx(48f), GameMetrics.spToPx(12f), Color.parseColor("#616161"), Color.TRANSPARENT)
    }

    private fun drawSwitch(canvas: Canvas, text: String, on: Boolean, y: Float, rect: RectF) {
        val trackP = if (on) switchOnPaint else switchOffPaint
        val cornerR = GameMetrics.dpToPx(12f)
        canvas.drawRoundRect(rect, cornerR, cornerR, trackP)

        val knobR = GameMetrics.dpToPx(10f)
        val knobX = if (on) rect.right - knobR - GameMetrics.dpToPx(2f) else rect.left + knobR + GameMetrics.dpToPx(2f)
        canvas.drawCircle(knobX, rect.centerY(), knobR, whitePaint)

        draw3DText(canvas, text, rect.left - GameMetrics.dpToPx(15f), y + GameMetrics.spToPx(5f), GameMetrics.spToPx(15f), Color.parseColor("#4A2E14"), Color.TRANSPARENT)
    }
}
