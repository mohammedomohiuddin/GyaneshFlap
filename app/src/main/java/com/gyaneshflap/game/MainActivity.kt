package com.gyaneshflap.game

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gyaneshflap.game.databinding.ActivityMainBinding
import com.gyaneshflap.game.game.GameState

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        binding.gameView.onResumeGame()
    }

    override fun onPause() {
        super.onPause()
        binding.gameView.onPauseGame()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.gameView.gameState != GameState.MAIN_MENU) {
            binding.gameView.gameState = GameState.MAIN_MENU
        } else {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }
}
