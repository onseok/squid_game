package com.wonseok.squid_game

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.wonseok.squid_game.databinding.FragmentStartDialogBinding
import com.wonseok.squid_game.sqlite.DBHelper

class StartDialogFragment : DialogFragment() {

    private var _binding: FragmentStartDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper : DBHelper

    private val mDelayHandler: Handler by lazy { Handler() } // 과녁 이미지 딜레이 관련
    private var playerNickName = ""
    private lateinit var enterSound: MediaPlayer

    private var imm : InputMethodManager? = null // 키보드 InputMethodManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStartDialogBinding.inflate(inflater, container, false)
        val view = binding.root

        imm = activity?.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        // layout background transparency
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // click name tag
        binding.nickNameTextView.setOnClickListener {
            binding.nickNameEditText.requestFocus()
            imm?.showSoftInput(binding.nickNameEditText, 0)
        }

        // name english only
        onlyAlphabetFilterToEnglishET()

        // click game start button
        binding.startImageView.setOnClickListener {
            playerNickName = binding.nickNameEditText.text.toString()

            if(playerNickName.isNotEmpty()) {
                if(checkSameNickName())
                    Toast.makeText(context, "💡 중복된 닉네임입니다", Toast.LENGTH_SHORT).show()
                else
                    mDelayHandler.postDelayed(::startGame, 1000L)
            }
            else
                Toast.makeText(context, "이름을 입력해주세요", Toast.LENGTH_SHORT).show()
        }

        enterSound = MediaPlayer.create(activity, R.raw.enter_stage_sound)
        enterSound.setVolume(1.0F, 1.0F)
        enterSound.start()

        return view
    }

    private fun startGame() {
        enterSound.pause()
        enterSound.release()
        dismiss()
        (context as StageActivity).setGameView(playerNickName)
    }

    private fun checkSameNickName(): Boolean {
        dbHelper = DBHelper(context, "RankDB", null, 1)
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("select nickName from RankDB where nickName = '$playerNickName'", null)

        if(cursor.count > 0)
            return true

        return false
    }

    // write nickName english only
    private fun onlyAlphabetFilterToEnglishET() {
        binding.nickNameEditText.setFilters(arrayOf(
            InputFilter { src, start, end, dst, dstart, dend ->
                if (src == " ") { // for space
                    return@InputFilter src
                }
                if (src == "") { // for backspace
                    return@InputFilter src
                }
                if (src.matches(Regex("[a-zA-Z]+"))) {
                    return@InputFilter src
                }
                Toast.makeText(requireContext(), "영어로 입력해주세요", Toast.LENGTH_SHORT).show()
                binding.nickNameEditText.setText("")
                return@InputFilter ""
            }
        ))
    }
}