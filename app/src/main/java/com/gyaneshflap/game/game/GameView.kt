package com.gyaneshflap.game.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.gyaneshflap.game.audio.MusicPlayer
import com.gyaneshflap.game.audio.SoundManager
import com.gyaneshflap.game.graphics.*
import com.gyaneshflap.game.utils.GameMetrics
import com.gyaneshflap.game.utils.PreferencesManager
import com.gyaneshflap.game.utils.VibratorHelper
import kotlin.math.max

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Managers
    val metrics = GameMetrics(context)
    val prefs = PreferencesManager(context)
    val sound = SoundManager(context, prefs)
    val music = MusicPlayer(context, prefs)
    val vibrator = VibratorHelper(context, prefs)

    // Drawers
    private val bgDrawer = BackgroundDrawer(context)
    private val characterDrawer = CharacterDrawer(context)
    private val roachDrawer = CockroachDrawer()
    private val collectibleDrawer = CollectibleDrawer()
    private val uiDrawer = UiDrawer(context)

    // Game Objects & State
    var gameState = GameState.MAIN_MENU
    val player = Player()
    private val towers = mutableListOf<CockroachTower>()
    private val collectibles = mutableListOf<Collectible>()
    private val particles = mutableListOf<Particle>()

    // Variables
    var score = 0
    var bestScore = prefs.getBestScore()
    var isNewHighScore = false

    private var animTime = 0f
    private var scrollX = 0f
    private var spawnTimer = 0f
    private var spawnInterval = 2.2f // seconds
    private var baseSpeed = GameMetrics.dpToPx(3.5f)

    private var screenWidth = 1080f
    private var screenHeight = 1920f
    private var groundY = 1920f * 0.84f

    private val gameLoop = GameLoop { deltaTime ->
        updateGame(deltaTime)
        invalidate()
    }

    init {
        isFocusable = true
    }

    fun onResumeGame() {
        music.start()
        gameLoop.start()
    }

    fun onPauseGame() {
        music.stop()
        gameLoop.stop()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        screenWidth = w.toFloat()
        screenHeight = h.toFloat()
        groundY = screenHeight * 0.84f
        baseSpeed = GameMetrics.dpToPx(3.5f)

        resetToGetReady(startState = GameState.MAIN_MENU)
    }

    fun resetToGetReady(startState: GameState = GameState.GET_READY) {
        gameState = startState
        score = 0
        isNewHighScore = false
        towers.clear()
        collectibles.clear()
        particles.clear()
        spawnTimer = 0f

        val playerSize = screenWidth * 0.18f
        player.reset(screenWidth * 0.28f, screenHeight * 0.42f, playerSize, screenHeight)
    }

    private fun startGameplay() {
        gameState = GameState.PLAYING
        player.flap()
        sound.playFlap()
        vibrator.vibrateShort()
    }

    private fun updateGame(deltaTime: Float) {
        animTime += deltaTime
        scrollX += baseSpeed * (1f + score * 0.02f)

        when (gameState) {
            GameState.MAIN_MENU, GameState.GET_READY -> {
                // Bob character idle
                player.y = screenHeight * 0.42f + kotlin.math.sin(animTime * 4f) * 18f
                player.rotation = 0f
            }
            GameState.PLAYING -> {
                updatePlaying(deltaTime)
            }
            GameState.GAME_OVER -> {
                // Update particles
                val iter = particles.iterator()
                while (iter.hasNext()) {
                    if (!iter.next().update()) iter.remove()
                }
            }
            else -> {}
        }
    }

    private fun updatePlaying(deltaTime: Float) {
        val currentSpeed = baseSpeed + score * 0.15f

        // Update Player Physics
        player.update(screenHeight, groundY)

        // Check Ground Collision
        if (player.isCollidingWithGround(groundY)) {
            triggerGameOver()
            return
        }

        // Spawn Towers & Collectibles
        spawnTimer += deltaTime
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0f
            spawnObstacleAndCollectible(currentSpeed)
        }

        // Update Towers
        val towerIter = towers.iterator()
        while (towerIter.hasNext()) {
            val tower = towerIter.next()
            tower.speed = currentSpeed
            tower.update()

            val playerBox = player.getHitBox()

            // Check Collision
            if (tower.collidesWith(playerBox, screenHeight)) {
                triggerGameOver()
                return
            }

            // Check Pass Score
            if (!tower.passed && tower.x + tower.width < player.x) {
                tower.passed = true
                score += 1
                sound.playScore()
                vibrator.vibrateShort()

                // Spawn score particle or milestone alert
                if (score % 10 == 0) {
                    particles.add(Particle(player.x, player.y - 60f, 0f, -4f, text = "LEVEL $score! TIGHTER GAP!", color = Color.parseColor("#FFD700")))
                } else {
                    particles.add(Particle(player.x, player.y - 40f, 0f, -3f, text = "+1", color = Color.YELLOW))
                }
            }

            if (tower.isOffScreen()) {
                towerIter.remove()
            }
        }

        // Update Collectibles
        val collIter = collectibles.iterator()
        while (collIter.hasNext()) {
            val item = collIter.next()
            item.speed = currentSpeed
            item.update(animTime)

            if (item.collidesWith(player.getHitBox(), animTime)) {
                item.isCollected = true
                score += 5
                sound.playCollect()
                vibrator.vibrateShort()

                // Sparkle particles
                particles.add(Particle(item.x, item.getCurrentY(animTime), 0f, -4f, text = "+5 BONUS!", color = Color.parseColor("#FFD700")))
                collIter.remove()
            } else if (item.isOffScreen()) {
                collIter.remove()
            }
        }

        // Update Particles
        val pIter = particles.iterator()
        while (pIter.hasNext()) {
            if (!pIter.next().update()) pIter.remove()
        }
    }

    private fun spawnObstacleAndCollectible(speed: Float) {
        val towerWidth = screenWidth * 0.22f

        // Progressive Gap Height:
        // Bigger/wider at first (score < 10) so Gyanesh passes easily,
        // and gets tighter at milestone 10, 20, 30, and 40+.
        val gapHeight = when {
            score < 10 -> screenWidth * 0.58f  // Very wide starting gap
            score < 20 -> screenWidth * 0.48f  // Tighter after score 10
            score < 30 -> screenWidth * 0.40f  // Tighter after score 20
            score < 40 -> screenWidth * 0.34f  // Tighter after score 30
            else -> screenWidth * 0.29f       // Challenging gap for 40+
        }

        val minGapY = screenHeight * 0.22f
        val maxGapY = groundY - screenHeight * 0.22f
        val gapY = minGapY + (Math.random() * (maxGapY - minGapY)).toFloat()

        towers.add(CockroachTower(screenWidth, towerWidth, gapY, gapHeight, speed))

        // 35% chance to spawn Winged Ballot Box in gap
        if (Math.random() < 0.35) {
            val collSize = screenWidth * 0.12f
            collectibles.add(Collectible(screenWidth + towerWidth * 1.8f, gapY, collSize, speed))
        }
    }

    private fun triggerGameOver() {
        gameState = GameState.GAME_OVER
        sound.playCollision()
        vibrator.vibrateCrash()

        isNewHighScore = prefs.addScore(score)
        bestScore = prefs.getBestScore()

        if (isNewHighScore) {
            sound.playHighScore()
        } else {
            sound.playGameOver()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 1. Draw Multi-Layer Parallax Background
        bgDrawer.drawBackground(canvas, screenWidth, screenHeight, scrollX, animTime)

        // 2. Draw Cockroach Towers
        for (tower in towers) {
            val topBounds = tower.getTopBounds(screenHeight)
            val botBounds = tower.getBottomBounds(screenHeight)

            roachDrawer.drawCockroachTower(canvas, topBounds.left, topBounds.top, topBounds.right, topBounds.bottom, isTopTower = true, animTime = animTime)
            roachDrawer.drawCockroachTower(canvas, botBounds.left, botBounds.top, botBounds.right, botBounds.bottom, isTopTower = false, animTime = animTime)
        }

        // 3. Draw Collectibles
        for (item in collectibles) {
            if (!item.isCollected) {
                collectibleDrawer.drawBallotBox(canvas, item.x, item.getCurrentY(animTime), item.size, animTime)
            }
        }

        // 4. Draw Player (Gyanesh)
        val isDizzy = (gameState == GameState.GAME_OVER)
        characterDrawer.drawCharacter(canvas, player.x, player.y, player.size, player.rotation, animTime, isDizzy = isDizzy)

        // 5. Draw Particles
        for (p in particles) {
            p.draw(canvas)
        }

        // 6. Draw UI according to current State
        when (gameState) {
            GameState.MAIN_MENU -> uiDrawer.drawMainMenu(canvas, screenWidth, screenHeight, animTime)
            GameState.GET_READY -> {
                uiDrawer.drawGameplayHud(canvas, screenWidth, screenHeight, score)
                uiDrawer.drawGetReady(canvas, screenWidth, screenHeight, animTime)
            }
            GameState.PLAYING -> uiDrawer.drawGameplayHud(canvas, screenWidth, screenHeight, score)
            GameState.PAUSED -> {
                uiDrawer.drawGameplayHud(canvas, screenWidth, screenHeight, score)
                uiDrawer.drawGameOver(canvas, screenWidth, screenHeight, score, bestScore, isNewBest = false)
            }
            GameState.GAME_OVER -> uiDrawer.drawGameOver(canvas, screenWidth, screenHeight, score, bestScore, isNewHighScore)
            GameState.HIGH_SCORES -> uiDrawer.drawHighScoreScreen(canvas, screenWidth, screenHeight, prefs.getHighScores())
            GameState.SETTINGS -> uiDrawer.drawSettingsScreen(canvas, screenWidth, screenHeight, prefs.soundEnabled, prefs.musicEnabled, prefs.vibrationEnabled)
            GameState.HOW_TO_PLAY -> uiDrawer.drawHowToPlayScreen(canvas, screenWidth, screenHeight)
            GameState.ABOUT -> uiDrawer.drawAboutScreen(canvas, screenWidth, screenHeight)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_DOWN) return super.onTouchEvent(event)

        val touchX = event.x
        val touchY = event.y

        sound.playClick()

        when (gameState) {
            GameState.MAIN_MENU -> {
                if (uiDrawer.btnPlay.contains(touchX, touchY)) {
                    resetToGetReady(GameState.GET_READY)
                } else if (uiDrawer.btnHighScore.contains(touchX, touchY)) {
                    gameState = GameState.HIGH_SCORES
                } else if (uiDrawer.btnSettings.contains(touchX, touchY)) {
                    gameState = GameState.SETTINGS
                } else if (uiDrawer.btnAbout.contains(touchX, touchY)) {
                    gameState = GameState.ABOUT
                } else if (uiDrawer.btnHowToPlay.contains(touchX, touchY)) {
                    gameState = GameState.HOW_TO_PLAY
                }
            }
            GameState.GET_READY -> {
                startGameplay()
            }
            GameState.PLAYING -> {
                if (uiDrawer.btnPause.contains(touchX, touchY)) {
                    gameState = GameState.PAUSED
                } else {
                    player.flap()
                    sound.playFlap()
                    vibrator.vibrateShort()
                }
            }
            GameState.PAUSED -> {
                if (uiDrawer.btnRetry.contains(touchX, touchY)) {
                    resetToGetReady(GameState.GET_READY)
                } else if (uiDrawer.btnMenu.contains(touchX, touchY)) {
                    gameState = GameState.MAIN_MENU
                }
            }
            GameState.GAME_OVER -> {
                if (uiDrawer.btnRetry.contains(touchX, touchY)) {
                    resetToGetReady(GameState.GET_READY)
                } else if (uiDrawer.btnMenu.contains(touchX, touchY)) {
                    gameState = GameState.MAIN_MENU
                }
            }
            GameState.SETTINGS -> {
                if (uiDrawer.btnBack.contains(touchX, touchY)) {
                    gameState = GameState.MAIN_MENU
                } else if (uiDrawer.toggleSound.contains(touchX, touchY)) {
                    prefs.soundEnabled = !prefs.soundEnabled
                } else if (uiDrawer.toggleMusic.contains(touchX, touchY)) {
                    prefs.musicEnabled = !prefs.musicEnabled
                    music.updateState()
                } else if (uiDrawer.toggleVibration.contains(touchX, touchY)) {
                    prefs.vibrationEnabled = !prefs.vibrationEnabled
                }
            }
            GameState.HIGH_SCORES, GameState.HOW_TO_PLAY, GameState.ABOUT -> {
                if (uiDrawer.btnBack.contains(touchX, touchY)) {
                    gameState = GameState.MAIN_MENU
                }
            }
        }
        return true
    }
}
