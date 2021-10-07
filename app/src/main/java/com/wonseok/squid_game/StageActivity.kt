package com.wonseok.squid_game

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.wonseok.squid_game.databinding.ActivityStageBinding
import com.wonseok.squid_game.databinding.ActivityStageBinding.inflate
import java.util.*
import kotlin.concurrent.timer

class StageActivity : AppCompatActivity() {

    lateinit var binding: ActivityStageBinding
    private var time = 0
    private var timerTask: Timer? = null
    private val mDelayHandler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private var playerScore = 0
    private var playerNickName = ""
    private var runLeftFlag = false
    private var runRightFlag = false
    private var isDetected = false
    private lateinit var gameSound: MediaPlayer
    private var soundChooser: Int = 0
    private var soundLength: Long = 4800


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)

        val window = window
        window.setFlags( // 풀스크린
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

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

        setStartDialog()


        binding.leftRunButton.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> { // 왼쪽 버튼 터치했을 때
                    binding.characterMotionImageView.setImageResource(R.drawable.left_motion)
                }
                MotionEvent.ACTION_UP -> { // 왼쪽 버튼 누르고 땟을 때
                    binding.characterMotionImageView.setImageResource(R.drawable.left_after_motion)
                    if (!runLeftFlag) {
                        playerScore++
                        binding.playerScoreNumberTextView.text = playerScore.toString()
                    }
                    runLeftFlag = true
                    runRightFlag = false
                }
            }

            if (isDetected) {
                binding.playerLifeTextView.text = "\uD83D\uDC80"
                setDialog()
            }
            true
        }

        binding.rightRunButton.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> { // 오른쪽 버튼 터치했을 때
                    binding.characterMotionImageView.setImageResource(R.drawable.right_motion)
                }
                MotionEvent.ACTION_UP -> { // 오른쪽 버튼 누르고 땟을 때
                    binding.characterMotionImageView.setImageResource(R.drawable.right_after_motion)
                    if (!runRightFlag) {
                        playerScore++
                        binding.playerScoreNumberTextView.text = playerScore.toString()
                    }
                    runRightFlag = true
                    runLeftFlag = false
                }
            }
            if (isDetected) {
                binding.playerLifeTextView.text = "\uD83D\uDC80"
                setDialog()
            }

            true
        }

    }

    // 시작 다이얼로그 버튼 불러오기
    private fun setStartDialog() {
        val dialog = StartDialogFragment()
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, "DialogStartFragment")
    }

    // 시간 끝나거나 죽으면 다이얼로그 세팅
    private fun setDialog() {
        // 죽는다면 리셋 다이얼로그
        if (binding.playerLifeTextView.text == "\uD83D\uDC80")
            mDelayHandler.post(::setResetDialog)

        // 죽지않고 시간이 끝난다면
        else if (binding.playerLifeTextView.text == "♥")
            mDelayHandler.post(::setRankDialog)
    }

    // 다 멈추고, 리셋 다이얼로그 버튼 불러오기
    private fun setResetDialog() {
        stopTimer() // 타이머 멈추고
        mDelayHandler.removeCallbacksAndMessages(null)
        val score = playerScore

        val dialog = ResetDialogFragment(score)
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, "DialogResetFragment")
    }

    private fun setRankDialog() {
        stopTimer() // 타이머 멈추고
        mDelayHandler.removeCallbacksAndMessages(null)
        val score = playerScore

        val dialog = RankDialogFragment(playerNickName, score)
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, "DialogRankFragment")
    }

    // 게임 뷰 세팅하기
    fun setGameView(playerNickName: String) {

        binding.characterProfileImageView.visibility = View.VISIBLE
        binding.characterProfileNameTextView.visibility = View.VISIBLE
        binding.playerLifeTextView.visibility = View.VISIBLE
        binding.timeLifeMinuteTextView.visibility = View.VISIBLE
        binding.timeLifeSecondTextView.visibility = View.VISIBLE
        binding.playerScoreNumberTextView.visibility = View.VISIBLE
        binding.playerNicknameTextView.visibility = View.VISIBLE
        binding.characterMotionImageView.visibility = View.VISIBLE

        binding.playerScoreNumberTextView.text = playerScore.toString()
        Glide.with(this).load(R.drawable.detecting_robot).into(binding.detectRobotGifView)

        binding.playerLifeTextView.text = "♥" // 생명 세팅 ( 목숨은 1개 )
        binding.playerNicknameTextView.text = playerNickName
        this.playerNickName = playerNickName // 닉네임 세팅

        initSound()

    }

    private fun initSound() {
        // 게임 설명 사운드 쓰레드 시작
        Thread {
            gameSound = MediaPlayer.create(this@StageActivity, R.raw.mugunghwa_ready_sound)
            gameSound.setVolume(1.0F, 1.0F)
            gameSound.start()
        }.start()

        mDelayHandler.postDelayed({
            binding.leftRunButton.visibility = View.VISIBLE
            binding.rightRunButton.visibility = View.VISIBLE
            gameSound.pause()
            gameSound.release()
            startTimer()
            mugunghwaSound()
        }, 9500)
    }

    private fun mugunghwaSound() {
        isDetected = false
        binding.detectRobotGifView.visibility = View.INVISIBLE

        when (soundChooser) {
            0 -> {
                gameSound = MediaPlayer.create(this@StageActivity, R.raw.mugunghwa_sound)
                soundLength = 4800
            }
            1 -> {
                gameSound =
                    MediaPlayer.create(this@StageActivity, R.raw.mugunghwa_sound_standard)
                soundLength = 3600
            }
            2 -> {
                gameSound = MediaPlayer.create(this@StageActivity, R.raw.mugunghwa_sound_hard)
                soundLength = 2800
            }
        }

        // 무궁화 꽃이 피었습니다 쓰레드
        Thread {
            gameSound.setVolume(1.0F, 1.0F)
            gameSound.start();
        }.start()

        mDelayHandler.postDelayed({
            gameSound.pause()
            gameSound.release()
            detectSound()
        }, soundLength)
    }

    private fun detectSound() {
        
        // 고개 돌리는 소리 쓰레드
        Thread {
            gameSound = MediaPlayer.create(this@StageActivity, R.raw.mugunghwa_spin_sound)
            gameSound.setVolume(1.0F, 1.0F)
            gameSound.start();
        }.start()

        binding.detectRobotGifView.visibility = View.VISIBLE
        mDelayHandler.postDelayed({
            isDetected = true
            gameSound.pause()
            gameSound.release()
            gunFirstSound()
        }, 1500)
    }

    private fun gunFirstSound() {
        
        // 총쏘는 소리 쓰레드
        Thread {
            gameSound = MediaPlayer.create(this@StageActivity, R.raw.mugunghwa_gun_sound)
            gameSound.setVolume(1.0F, 1.0F)
            gameSound.start();
        }.start()

        mDelayHandler.postDelayed({
            gameSound.pause()
            gameSound.release()
            // 0~2 사이의 무작위 Int 저장
            val random = Random()
            soundChooser = random.nextInt(3)
            mugunghwaSound()
        }, 2500)
    }


    // 타이머 동작
    private fun startTimer() {
        binding.timeLifeMinuteTextView.text = "00:"

        // 백그라운드로 실행되는 부분, UI조작 X
        timerTask = timer(period = 10) {
            time++
            val sec = 59 - (time / 100)

            // UI 조작 로직
            runOnUiThread {
                if (sec < 10)
                    binding.timeLifeSecondTextView.text = "0$sec"
                else
                    binding.timeLifeSecondTextView.text = "$sec"
            }

            if (sec == 0)
                mDelayHandler.post(::setDialog)
        }
    }

    // 타이머 정지
    private fun stopTimer() {
        timerTask?.cancel()
        time = 0
    }

    //  죽었을 때에는 메인메뉴로 돌아가기
    fun setReGameView() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@StageActivity, MainActivity::class.java)
            startActivity(intent)
        }, 1500) // 1.5초후 메인메뉴로 이동
    }
}