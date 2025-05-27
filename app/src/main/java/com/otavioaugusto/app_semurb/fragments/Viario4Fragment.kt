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

class Viario4Fragment: Fragment() {


    private lateinit var btnVoltarViario4: ImageButton
    private lateinit var btnProximoViario4: AppCompatButton
    private lateinit var carrinho: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_viario4,
            container,
            false
        )

        carrinho = view.findViewById(R.id.carrinho)
        btnVoltarViario4 = view.findViewById(R.id.btn_voltarViario4)
        btnVoltarViario4.setOnClickListener {

                val deslocamento = -390f

                carrinho.animate()
                    .translationXBy(deslocamento)
                    .setDuration(400)
                    .withEndAction {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.FragmentContainerView2, Viario3Fragment())
                .addToBackStack(null)
                .commit()
        }}
        btnProximoViario4 = view.findViewById(R.id.btn_proximoViario4)
        btnProximoViario4.setOnClickListener {
            val deslocamento = 530f

            carrinho.animate()
                .translationXBy(deslocamento)
                .setDuration(400)
                .withEndAction {
            requireActivity().finish()
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