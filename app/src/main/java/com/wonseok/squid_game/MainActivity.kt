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
    private val data: MutableList<CharacterData> = mutableListOf()
    private lateinit var bgmPlayer: MediaPlayer
    private val characterNumber: Int = 8
    private var count = 0


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
        bgmPlayer.setVolume(1.0F, 1.0F)


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

        setCharactersInfo()
        changeCharacterInfo() // 왼쪽, 오른쪽 버튼 클릭에 따라 캐릭터 정보가 바뀌는 함수

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

    override fun onStop() {
        super.onStop()
        if (bgmPlayer.isPlaying) {
            bgmPlayer.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bgmPlayer.release()
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

    private fun setCharactersInfo() {
        data.add(
            CharacterData(
                R.drawable.character_minyeo,
                "미녀",
                "사회에서도 살기 위해, 돈을 벌기 위해 가리는 것이 없었다. 게임장에서도 그녀의 생존 능력은 빛을 발한다. 강해 보이거나 이길 것 같은 참가자에게 접근해 수시로 입장을 바꿔가며 어떻게든 한 팀을 이뤄보려 한다. 언제까지 그게 먹힐지 모르는 채."
            )
        )
        data.add(
            CharacterData(
                R.drawable.character_deoksoo,
                "덕수",
                "기세등등한 조폭 덕수는, 실상 카지노에서 조직의 돈까지 모두 잃고 쫓기고 있는 신세다. 조직에 잡히는 순간 어차피 죽은 목숨, 게임에서 승리하면 모든 것을 한 방에 해결할 수 있다는 생각에 수단과 방법을 가리지 않는다. 그것이 살인일지라도 말이다."
            )
        )
        data.add(
            CharacterData(
                R.drawable.character_ali,
                "알리",
                "코리안 드림을 꿈꿨지만 꿈을 이루기는커녕 몸과 마음을 혹사당하고 상처투성이가 되었다. 산재를 당했지만 사장이 병원비는커녕 집으로 돌아갈 여비도 마련해주지 않은 채 그를 홀대하자, 결국 큰 사고를 치고 만다. 한국에 온 것은 가족과 함께 잘 살고 싶어서였다. 잘 살기 위해 마지막으로 한 번 더 사람을 믿어보려 한다."
            )
        )
        data.add(
            CharacterData(
                R.drawable.character_sangwoo,
                "상우",
                "어릴 적부터 수재였던 기훈의 동네 후배. 서울대를 졸업하고 대기업에 입사해 승승장구하고 있다고 모두가 알고 있지만, 실상은 고객의 돈까지 유용했던 투자에 실패해 거액의 빚더미에 앉았다. 미래도 희망도 없는 그에게, 이 기회는 목숨을 투자할 가치가 있다."
            )
        )
        data.add(
            CharacterData(
                R.drawable.character_ilnam,
                "일남",
                "뇌종양에 걸린 칠순 노인으로 치매 증상이 있다. 어차피 얼마 남지 않은 삶, 목숨을 건 서바이벌 게임에서 겁을 먹기는커녕 오히려 게임 자체를 순수하게 즐긴다."
            )
        )
        data.add(
            CharacterData(
                R.drawable.character_gihoon,
                "기훈",
                "구조조정으로 실직한 후 사채와 도박을 전전하다 이혼을 하고 무기력한 삶을 이어가고 있다. 어머니 돈을 훔쳐 경마장에 갈 만큼 철 없는 기훈은 새 아빠를 따라 미국에 간다는 딸과 당뇨로 당장 입원해야 하는 어머니를 위해 큰 돈이 절실하다. 지하철에서 만난 의문의 남자가 건넨 명함, 말도 안 된다고 생각하면서도 결국 기훈은 456억 원의 상금이 걸린 게임에 참여한다. 하지만 자신이 무엇을 걸어야 하는지는 도착해서야 알게 된다."
            )
        )
        data.add(
            CharacterData(
                R.drawable.character_saebyeok,
                "새벽",
                "소매치기까지 하며 거칠게 살아온 새터민. 죽기 살기로 돈을 버는 것은, 보육원에 혼자 남겨진 남동생과 북한에 있는 어머니를 탈북시켜 함께 살고 싶기 때문이었다. 하지만 브로커에게 사기당해 돈을 모두 잃고 만다. 가족이 함께 모일 수 있는 마지막 희망, 이 게임에 모든 것을 걸었다."
            )
        )
        data.add(
            CharacterData(
                R.drawable.character_jiyeong, "지영", "\"강새벽!\"\n" +
                        "\"고마워, 나랑 같이 해 줘서….\""
            )
        )

        // 초기 캐릭터 정보 세팅
        binding.characterImageView.setImageResource(data[count % characterNumber].img)
        binding.characterNameTextView.text = data[count % characterNumber].name
        binding.characterInfoTextView.text = data[count % characterNumber].info
    }

    private fun changeCharacterInfo() {
        binding.rightButton.setOnClickListener {
            ++count
            binding.characterImageView.setImageResource(data[(count) % characterNumber].img)
            binding.characterNameTextView.text = data[(count) % characterNumber].name
            binding.characterInfoTextView.text = data[(count) % characterNumber].info
        }

        binding.leftButton.setOnClickListener {
            if (count == 0) {
                count = 8
            }
            --count
            binding.characterImageView.setImageResource(data[(count) % characterNumber].img)
            binding.characterNameTextView.text = data[(count) % characterNumber].name
            binding.characterInfoTextView.text = data[(count) % characterNumber].info
        }
    }
}
