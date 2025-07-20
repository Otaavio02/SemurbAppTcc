package com.otavioaugusto.app_semurb.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.databinding.FragmentInspecao2Binding
import java.util.Timer
import kotlin.concurrent.schedule
import androidx.activity.OnBackPressedCallback

class Inspecao2Fragment : Fragment() {




    private var _binding: FragmentInspecao2Binding? = null
    private val binding get() = _binding!!


    private var etapaAtual = 1 // etapa 2
    private var totalEtapas = 3

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInspecao2Binding.inflate(inflater, container, false)


        binding.btnVoltarInspecao2.setOnClickListener {
            if (etapaAtual > 0) {
                (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual - 1, "voltar")
            }

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.FragmentContainerView2, Inspecao1Fragment())
                .commit()
        }

        val btnProximoInspecao2 = binding.btnProximoInspecao2
        val rgInspecao2 = binding.rgInspecao2


        btnProximoInspecao2?.setOnClickListener {
        val selectedRgId = rgInspecao2?.checkedRadioButtonId
        if (selectedRgId == -1 || selectedRgId == null){

            val titulo = SpannableString("Clique em alguma opção").apply {
                setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.CinzaMedio)),
                    0, length, 0
                )
            }

            val mensagem = SpannableString("Para Avançar, escolha uma opção").apply {
                setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.CinzaMedio)),
                    0, length, 0
                )
            }

                val builder = AlertDialog.Builder(context)
                    builder.setTitle(titulo)
                    builder.setMessage(mensagem)



                    builder.setPositiveButton("Ok") { dialog, _ ->

                        dialog.dismiss()
                    }

                    val dialog = builder.create()

            dialog.setOnShowListener {

                dialog.window?.setBackgroundDrawableResource(R.color.Branco)
            }
                    dialog.show()

            } else {
                if (selectedRgId == R.id.rbSimAvarias){

                    if (etapaAtual < totalEtapas - 1) {
                        (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual + 1, "continuar")
                    }

                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        .replace(R.id.FragmentContainerView2, Inspecao3Fragment())
                        .addToBackStack(null)
                        .commit()
                } else if (selectedRgId == R.id.rbNaoAvarias){
                    (activity as? PlaceHolderGameficadoActivity)?.concluirEtapaFinal(3)

                    Timer().schedule(700) {
                        requireActivity().finish()
                    }
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.post {
            (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual, "continuar")
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (etapaAtual > 0) {
                    (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual - 1, "voltar")
                }

                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )
                    .replace(R.id.FragmentContainerView2, Inspecao1Fragment())
                    .commit()
            }
        })
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

