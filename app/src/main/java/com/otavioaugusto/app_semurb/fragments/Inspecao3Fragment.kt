package com.otavioaugusto.app_semurb.fragments

import android.content.Context
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
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat.getColor
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.adapters.AvariasAdapter
import com.otavioaugusto.app_semurb.dataClasses.AvariaItem
import com.otavioaugusto.app_semurb.databinding.FragmentInspecao2Binding
import com.otavioaugusto.app_semurb.databinding.FragmentInspecao3Binding
import java.util.Timer
import kotlin.concurrent.schedule

class Inspecao3Fragment : Fragment() {




    private var _binding: FragmentInspecao3Binding? = null
    private val binding get() = _binding!!

    private var etapaAtual = 2 // etapa 3

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInspecao3Binding.inflate(inflater, container, false)


        class AvariasRecyclerHelper(
            private val context: Context,
            private val recyclerView: RecyclerView
        ) {
            private val avariasList = mutableListOf(AvariaItem())
            private val adapter = AvariasAdapter(avariasList)

            init {
                setupRecycler()
            }

            private fun setupRecycler() {
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = adapter
            }

            fun getAvarias(): List<AvariaItem> = avariasList

            fun addAvaria(avaria: AvariaItem) {
                avariasList.add(avaria)
                adapter.notifyItemInserted(avariasList.size - 1)
            }

            fun removeAvaria(index: Int) {
                if (index in avariasList.indices) {
                    avariasList.removeAt(index)
                    adapter.notifyItemRemoved(index)
                }
            }

            fun getAdapter(): AvariasAdapter = adapter
        }

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

                    if (frame.isGone) {
                        headerTextView.setBackgroundResource(R.drawable.bg_btninspecaoativado)
                        frame.setBackgroundResource(R.drawable.bg_fundoinspecao)
                        btnToggle.setImageResource(R.drawable.inspecaoabrir)
                        frame.visibility = View.VISIBLE
                    } else if (frame.isVisible) {
                        headerTextView.setBackgroundResource(R.drawable.bg_btninspecaodesativado)
                        frame.setBackgroundColor(ContextCompat.getColor(context, R.color.Transparente))
                        btnToggle.setImageResource(R.drawable.inspecaofechar)
                        frame.visibility = View.GONE
                    }
                }
            }
        }

        binding.btnVoltarInspecao2.setOnClickListener {
            if (etapaAtual > 0) {
                (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual - 1, "voltar")
            }

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.FragmentContainerView2, Inspecao2Fragment())
                .addToBackStack(null)
                .commit()
        }

        val btnFinalizarInspecao = binding.btnFinalizar
        btnFinalizarInspecao.setOnClickListener {
            (activity as? PlaceHolderGameficadoActivity)?.concluirEtapaFinal(2)

            Timer().schedule(700) {
                requireActivity().finish()
            }
        }

        AvariaToggleController(
            context = requireContext(),
            container = binding.containerAvariasTraseira,
            frame = binding.frameAvariaTraseira,
            btnToggle = binding.btnInspecaoTraseira,
            headerTextView = binding.textViewTraseira
        )

        AvariaToggleController(
            context = requireContext(),
            container = binding.containerAvariasEsquerda,
            frame = binding.frameAvariaEsquerda,
            btnToggle = binding.btnInspecaoEsquerda,
            headerTextView = binding.textViewEsquerda
        )

        AvariaToggleController(
            context = requireContext(),
            container = binding.containerAvariasDireita,
            frame = binding.frameAvariaDireita,
            btnToggle = binding.btnInspecaoDireita,
            headerTextView = binding.textViewDireita
        )

        AvariaToggleController(
            context = requireContext(),
            container = binding.containerAvariasOutras,
            frame = binding.frameAvariaOutras,
            btnToggle = binding.btnInspecaoOutras,
            headerTextView = binding.textViewOutras
        )

        AvariaToggleController(
            context = requireContext(),
            container = binding.containerAvariasFrente,
            frame = binding.frameAvariaFrente,
            btnToggle = binding.btnInspecaoFrente,
            headerTextView = binding.textViewFrente
        )

        val recyclerHelperFrente = AvariasRecyclerHelper(requireContext(), binding.rvFrente)
        val recyclerHelperTraseira = AvariasRecyclerHelper(requireContext(), binding.rvTraseira)
        val recyclerHelperDireita = AvariasRecyclerHelper(requireContext(), binding.rvDireita)
        val recyclerHelperEsquerda = AvariasRecyclerHelper(requireContext(), binding.rvEsquerda)
        val recyclerHelperOutras = AvariasRecyclerHelper(requireContext(), binding.rvOutras)

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

