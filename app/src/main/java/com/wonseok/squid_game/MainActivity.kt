package com.wonseok.squid_game

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.wonseok.squid_game.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var isMute = true
    private lateinit var bgmPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val window = window
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        // 메인 BGM 리소스 설정
        bgmPlayer = MediaPlayer.create(this@MainActivity, R.raw.main_bgm)
        bgmPlayer.isLooping = true

        binding.mainSoundButton.setOnClickListener {
            if (isMute) {
                isMute = false
                binding.mainSoundButton.setImageResource(R.drawable.ic_volume_off)
                bgmPlayer.pause()
            } else {
                isMute = true
                binding.mainSoundButton.setImageResource(R.drawable.ic_sound_on)
                bgmPlayer.start()
            }
        }

        binding.startButton.setOnClickListener {
            setCharacters()
        }

    }

    override fun onStart() {
        super.onStart()
        if (isMute) {
            bgmPlayer.start()
        } else {
            binding.mainSoundButton.setImageResource(R.drawable.ic_volume_off)
        }
    }

    override fun onPause() {
        super.onPause()
        if (bgmPlayer.isPlaying) {
            bgmPlayer.pause()
        }
    }

    private fun setCharacters() {
        binding.characterImageView.visibility = View.VISIBLE
        binding.leftButton.visibility = View.VISIBLE
        binding.rightButton.visibility = View.VISIBLE
        binding.selectCharacterTextView.visibility = View.VISIBLE
        binding.startButton.visibility = View.INVISIBLE
        binding.characterInfoTextView.visibility = View.VISIBLE
        binding.characterNameTextView.visibility = View.VISIBLE
        binding.characterSelectButton.visibility = View.VISIBLE

    }

}