package com.wonseok.squid_game

import android.annotation.SuppressLint
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

//        initSound()

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
        if(binding.playerLifeTextView.text == "\uD83D\uDC80")
            mDelayHandler.post(::setResetDialog)

        // 죽지않고 시간이 끝난다면
        else if(binding.playerLifeTextView.text == "♥")
            mDelayHandler.post(::setRankDialog)
    }

    // 다 멈추고, 리셋 다이얼로그 버튼 불러오기
    private fun setResetDialog() {
        stopTimer() // 타이머 멈추고
        mDelayHandler.removeCallbacksAndMessages(null) // 과녁 이미지 동작 멈춤
        val score = playerScore

        val dialog = ResetDialogFragment(score)
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, "DialogResetFragment")
    }

    // three stage 까지 완 -> Rank 판 보여주기
    private fun setRankDialog() {
        stopTimer() // 타이머 멈추고
        mDelayHandler.removeCallbacksAndMessages(null) // 과녁 이미지 동작 멈춤
        val score = playerScore

        val dialog = RankDialogFragment(playerNickName, score)
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, "DialogRankFragment")
    }

    // 게임 뷰 세팅하기
    fun setGameView(playerNickName: String) {
//        binding.mainConstraintLayout.setPadding(0, statusBarHeight(this), navigationBarHeight(this), 0)
//        Glide.with(this).load(R.raw.cat_player_01).override(250,250).into(binding.playerImageView)
//

        binding.characterProfileImageView.visibility = View.VISIBLE
        binding.characterProfileNameTextView.visibility = View.VISIBLE
        binding.playerLifeTextView.visibility = View.VISIBLE
        binding.timeLifeMinuteTextView.visibility = View.VISIBLE
        binding.timeLifeSecondTextView.visibility = View.VISIBLE
        binding.playerScoreNumberTextView.visibility = View.VISIBLE
        binding.playerNicknameTextView.visibility = View.VISIBLE
        binding.characterMotionImageView.visibility = View.VISIBLE

        binding.playerScoreNumberTextView.text = playerScore.toString()

//        binding.targetImageView.setImageResource(R.drawable.ic_target)

        binding.playerLifeTextView.text = "♥" // 생명 세팅 ( 목숨은 1개 )
        binding.playerNicknameTextView.text = playerNickName
//        lifeLength = binding.playerLifeTextView.length()
        this.playerNickName = playerNickName // 닉네임 세팅


//        targetPositionX = binding.targetImageView.x
//        arrowPositionX = binding.arrowImageView.x


        initSound()


//
//        if(imageStatus)
//            downImageMove()
//        else
//            upImageMove()
    }

    private fun initSound() {
        // 게임 설명 사운드
        gameSound = MediaPlayer.create(this@StageActivity, R.raw.mugunghwa_ready_sound)
        gameSound.setVolume(1.0F, 1.0F)
        gameSound.start()
        mDelayHandler.postDelayed({
            binding.leftRunButton.visibility = View.VISIBLE
            binding.rightRunButton.visibility = View.VISIBLE
            gameSound.pause()
            gameSound.release()
            startTimer()
            mugunghwaSound()
        }, 9200)
    }

    private fun mugunghwaSound() {
        isDetected = false
        gameSound = MediaPlayer.create(this@StageActivity,R.raw.mugunghwa_sound)
        gameSound.setVolume(1.0F, 1.0F)
        gameSound.start();
        mDelayHandler.postDelayed({
            gameSound.pause()
            gameSound.release()
            detectSound()
        }, 4800)
    }

    private fun detectSound() {
        gameSound = MediaPlayer.create(this@StageActivity,R.raw.mugunghwa_spin_sound)
        gameSound.setVolume(1.0F, 1.0F)
        gameSound.start();
        mDelayHandler.postDelayed({
            isDetected = true
            gameSound.pause()
            gameSound.release()
            gunFirstSound()
        }, 1500)
    }

    private fun gunFirstSound() {
        gameSound = MediaPlayer.create(this@StageActivity,R.raw.mugunghwa_gun_sound)
        gameSound.setVolume(1.0F, 1.0F)
        gameSound.start();
        mDelayHandler.postDelayed({
            gameSound.pause()
            gameSound.release()
            mugunghwaSound()
        }, 2500)
    }


    // 타이머 동작
    private fun startTimer() {
        binding.timeLifeMinuteTextView.text = "00"

        timerTask = timer(period = 10) {
            time++
            val sec = 59 - (time / 100)

            runOnUiThread {
                if (sec < 10)
                    binding.timeLifeSecondTextView.text = "0$sec"
                else
                    binding.timeLifeSecondTextView.text = "$sec"
            }

            if(sec == 0)
                mDelayHandler.post(::setDialog)
        }
    }

    private fun stopTimer() {
        timerTask?.cancel() // 타이머 정지
        time = 0
    }

    // 게임 오바 되었을 때 뷰 재세팅
    fun setReGameView() {
//        stage = 1
        playerScore = 0
//        thisPlayerScore = 0
        binding.playerScoreNumberTextView.text = playerScore.toString()
//        binding.stageNumberTextView.text = stage.toString()

//        binding.mainConstraintLayout.background = resources.getDrawable(backgroundImageArray[stage - 1])

        binding.playerLifeTextView.text = "♥" // 생명 세팅
//        lifeLength = binding.playerLifeTextView.length()

        startTimer()

//        if(imageStatus)
//            downImageMove()
//        else
//            upImageMove()
    }
}