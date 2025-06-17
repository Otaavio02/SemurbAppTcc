package com.otavioaugusto.app_semurb.fragments

import android.os.Build
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.Fade
import android.transition.PathMotion
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.core.app.NotificationCompat.getColor
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.databinding.FragmentInspecao2Binding

class Inspecao2Fragment : Fragment() {

    private var _binding: FragmentInspecao2Binding? = null
    private val binding get() = _binding!!

    private var etapaAtual = 1
    private var totalEtapas = 3

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInspecao2Binding.inflate(inflater, container, false)

        binding.btnVoltarInspecao2.setOnClickListener {
            if (etapaAtual > 0) {
                (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual - 1)
            }

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.FragmentContainerView2, Inspecao1Fragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnProximoInspecao2.setOnClickListener {
            if (etapaAtual < totalEtapas - 1) {
                (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual + 1)
            }

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                .replace(R.id.FragmentContainerView2, Inspecao3Fragment())
                .addToBackStack(null)
                .commit()
        }

        val frameFrente = binding.frameAvariaFrente
        val btnFechado = binding.btnInspecaoFechado
        val fundoTextoFrente = binding.textViewFrente

        btnFechado.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.containerAvariasFrente, AutoTransition())
            if (frameFrente.isGone){
                fundoTextoFrente.setBackgroundResource(R.drawable.bg_azulpaginas)
                frameFrente.setBackgroundResource(R.drawable.bg_fundoinspecao)
                btnFechado.setImageResource(R.drawable.inspecaoabrir)
                frameFrente.visibility = View.VISIBLE
            } else if (frameFrente.isVisible){
                fundoTextoFrente.setBackgroundResource(R.drawable.bg_azulpaginas2)
                frameFrente.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Transparente))
                btnFechado.setImageResource(R.drawable.inspecaofechar)
                frameFrente.visibility = View.GONE
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.post {
            (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual)
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

