package com.wonseok.squid_game

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class StageLoadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stage_loading)

        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            val loadingIntent = Intent(this@StageLoadingActivity, StageActivity::class.java)
            loadingIntent.putExtra("profileName", intent.getStringExtra("name"))
            startActivity(loadingIntent)
        }, 4000) // 스테이지 이동 화면 로딩 4초 후 스테이지 액티비티로 전환
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}