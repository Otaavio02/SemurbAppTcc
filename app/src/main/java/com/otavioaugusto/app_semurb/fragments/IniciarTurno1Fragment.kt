package com.otavioaugusto.app_semurb.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

import com.otavioaugusto.app_semurb.R



class IniciarTurno1Fragment : Fragment() {

    private lateinit var btnvoltarTurno1: ImageButton
    private lateinit var btnproximaTurno1: AppCompatButton
    private lateinit var carrinho: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_iniciarturno1, container, false)

        btnvoltarTurno1 = view.findViewById(R.id.btn_voltarTurno1)
        btnvoltarTurno1.setOnClickListener {

            requireActivity().finish()
        }
        btnproximaTurno1 = view.findViewById(R.id.btn_proximoTurno1)
        carrinho = view.findViewById(R.id.imageView3)
        btnproximaTurno1.setOnClickListener {

            val deslocamento = 410f

            carrinho.animate()
                .translationXBy(deslocamento)
                .setDuration(400)
                .withEndAction {

                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        .replace(R.id.FragmentContainerView2, IniciarTurno2Fragment())
                        .addToBackStack(null)
                        .commit()
                }
                .start()
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        activity?.window?.let { window ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.let {
                    it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                View.SYSTEM_UI_FLAG_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        )
            }
        }
    }
}