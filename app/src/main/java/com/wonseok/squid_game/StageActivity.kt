package com.wonseok.squid_game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wonseok.squid_game.databinding.ActivityStageBinding
import com.wonseok.squid_game.databinding.ActivityStageBinding.inflate

class StageActivity : AppCompatActivity() {

    lateinit var binding: ActivityStageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)

        binding.characterProfileNameTextView.text = intent.getStringExtra("profileName")
        when (intent.getStringExtra("profileName")) {
            "미녀" -> binding.characterProfileImageView.setImageResource(R.drawable.character_minyeo_profile)
            "덕수" -> binding.characterProfileImageView.setImageResource(R.drawable.character_deoksoo_profile)
            "알리" -> binding.characterProfileImageView.setImageResource(R.drawable.character_ali_profile)
            "상우" -> binding.characterProfileImageView.setImageResource(R.drawable.character_sangwoo_profile)
            "일남" -> binding.characterProfileImageView.setImageResource(R.drawable.character_ilnam_profile)
            "기훈" -> binding.characterProfileImageView.setImageResource(R.drawable.character_gihoon_profile)
            "새벽" -> binding.characterProfileImageView.setImageResource(R.drawable.character_saebyeok_profile)
            "지영" -> binding.characterProfileImageView.setImageResource(R.drawable.character_jiyeong_profile)
        }
    }
}