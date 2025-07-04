package com.otavioaugusto.app_semurb.fragments


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController

import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.databinding.FragmentInspecao3Binding
import com.otavioaugusto.app_semurb.funcoes.AvariaToggleController
import com.otavioaugusto.app_semurb.funcoes.AvariasRecyclerHelper
import java.util.Timer
import kotlin.concurrent.schedule

class Inspecao3Fragment : Fragment() {

    private var _binding: FragmentInspecao3Binding? = null
    private val binding get() = _binding!!
    private val etapaAtual = 2 // Etapa 3

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInspecao3Binding.inflate(inflater, container, false)
        setupListeners()
        setupToggles()
        setupRecyclers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual, "continuar")
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupListeners() {
        binding.btnVoltarInspecao2.setOnClickListener {
            (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual - 1, "voltar")
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.FragmentContainerView2, Inspecao2Fragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnFinalizar.setOnClickListener {
            (activity as? PlaceHolderGameficadoActivity)?.concluirEtapaFinal(etapaAtual)
            Timer().schedule(700) {
                requireActivity().finish()
            }
        }
    }

    private fun setupToggles() {
        val context = requireContext()
        AvariaToggleController(context, binding.containerAvariasTraseira, binding.frameAvariaTraseira, binding.btnInspecaoTraseira, binding.textViewTraseira)
        AvariaToggleController(context, binding.containerAvariasFrente, binding.frameAvariaFrente, binding.btnInspecaoFrente, binding.textViewFrente)
        AvariaToggleController(context, binding.containerAvariasDireita, binding.frameAvariaDireita, binding.btnInspecaoDireita, binding.textViewDireita)
        AvariaToggleController(context, binding.containerAvariasEsquerda, binding.frameAvariaEsquerda, binding.btnInspecaoEsquerda, binding.textViewEsquerda)
        AvariaToggleController(context, binding.containerAvariasOutras, binding.frameAvariaOutras, binding.btnInspecaoOutras, binding.textViewOutras)
    }

    private fun setupRecyclers() {
        val context = requireContext()
        AvariasRecyclerHelper(context, binding.rvFrente)
        AvariasRecyclerHelper(context, binding.rvTraseira)
        AvariasRecyclerHelper(context, binding.rvDireita)
        AvariasRecyclerHelper(context, binding.rvEsquerda)
        AvariasRecyclerHelper(context, binding.rvOutras)
    }

    private fun hideSystemUI() {
        activity?.window?.let { window ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.apply {
                    hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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
