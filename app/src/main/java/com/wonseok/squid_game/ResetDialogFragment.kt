package com.wonseok.squid_game

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.wonseok.squid_game.databinding.FragmentResetDialogBinding


class ResetDialogFragment(private val playerScore: Int) : DialogFragment() {
    private var _binding: FragmentResetDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResetDialogBinding.inflate(inflater, container, false)
        val view = binding.root

        // layout background transperency
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.playerScoreNumberTextView.text = playerScore.toString()

        // click game reset button
        binding.resetImageView.setOnClickListener {
            dismiss()
            (context as StageActivity).setReGameView()
        }

        return view
    }
}