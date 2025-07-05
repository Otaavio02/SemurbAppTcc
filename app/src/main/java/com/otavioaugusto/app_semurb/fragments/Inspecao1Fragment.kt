package com.otavioaugusto.app_semurb.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.databinding.FragmentInspecao1Binding
import java.util.Timer
import kotlin.concurrent.schedule

class Inspecao1Fragment : Fragment() {

    private var _binding: FragmentInspecao1Binding? = null
    private val binding get() = _binding!!

    private var etapaAtual = 0 // etapa 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInspecao1Binding.inflate(inflater, container, false)


        val carrinho = requireActivity().findViewById<ImageView>(R.id.carrinho)
        val bolinhaInicial = requireActivity().findViewById<ImageView>(R.id.progress_bar_circle1)
        bolinhaInicial.post {
            val destinoX = bolinhaInicial.x + bolinhaInicial.width / 2 - carrinho.width / 2

            carrinho.animate()
                .x(destinoX)
                .setDuration(700)
                .start()
        }


        binding.btnVoltarInspecao.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnProximoInspecao1.setOnClickListener {

                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                    )
                    .replace(R.id.FragmentContainerView2, Inspecao2Fragment())
                    .addToBackStack(null)
                    .commit()
            }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        activity?.window?.let { window ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.let {
                    it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    it.systemBarsBehavior =
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
