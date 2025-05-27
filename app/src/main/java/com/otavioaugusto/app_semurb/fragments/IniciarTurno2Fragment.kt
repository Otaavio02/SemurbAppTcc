package com.otavioaugusto.app_semurb.fragments

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
import com.otavioaugusto.app_semurb.R

class IniciarTurno2Fragment: Fragment() {

    private lateinit var btnvoltarTurno2: ImageButton
    private lateinit var btnproximoTurno2: AppCompatButton
    private lateinit var carrinho: ImageView
    private var posicaoInicialX = 0



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_iniciarturno2,
            container,
            false
        )

        carrinho = view.findViewById(R.id.imageView3)
        btnvoltarTurno2 = view.findViewById(R.id.btn_voltarTurno2)

        btnvoltarTurno2.setOnClickListener {
            val deslocamento = -410f

            carrinho.animate()
                .translationXBy(deslocamento)
                .setDuration(400)
                .withEndAction {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                        )
                        .replace(R.id.FragmentContainerView2, IniciarTurno1Fragment())
                        .addToBackStack(null)
                        .commit()
                }
                .start()
        }
        btnproximoTurno2 = view.findViewById(R.id.btn_proximoTurno2)
        btnproximoTurno2.setOnClickListener {
            val deslocamento = 410f

            carrinho.animate()
                .translationXBy(deslocamento)
                .setDuration(400)
                .withEndAction {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,

                )
                .replace(R.id.FragmentContainerView2, IniciarTurno3Fragment())
                .addToBackStack(null)
                .commit()
        }}

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
        }}}