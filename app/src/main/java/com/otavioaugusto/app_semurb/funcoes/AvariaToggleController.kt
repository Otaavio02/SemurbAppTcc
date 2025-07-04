package com.otavioaugusto.app_semurb.funcoes

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.otavioaugusto.app_semurb.R

class AvariaToggleController(
    private val context: Context,
    private val container: ConstraintLayout,
    private val frame: FrameLayout,
    private val btnToggle: ImageButton,
    private val headerTextView: TextView
) {
    init {
        setupToggle()
    }

    private fun setupToggle() {
        btnToggle.setOnClickListener {
            TransitionManager.beginDelayedTransition(container, AutoTransition())
            val isOpening = frame.isGone

            headerTextView.setBackgroundResource(
                if (isOpening) R.drawable.bg_btninspecaoativado else R.drawable.bg_btninspecaodesativado
            )

            if (isOpening) {
                frame.setBackgroundResource(R.drawable.bg_fundoinspecao)
                btnToggle.setImageResource(R.drawable.inspecaoabrir)
                frame.visibility = View.VISIBLE
            } else {
                frame.setBackgroundColor(ContextCompat.getColor(context, R.color.Transparente))
                btnToggle.setImageResource(R.drawable.inspecaofechar)
                frame.visibility = View.GONE
            }
        }
    }
}
