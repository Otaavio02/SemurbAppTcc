package com.otavioaugusto.app_semurb.fragments

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.database.ViarioDBHelper
import com.otavioaugusto.app_semurb.databinding.FragmentViario1Binding
import com.otavioaugusto.app_semurb.dbHelper.ocorrenciasDBHelper

class Viario1Fragment : Fragment() {

    private var _binding: FragmentViario1Binding? = null
    private val binding get() = _binding!!

    private var etapaAtual = 0 // etapa 2
    private var totalEtapas = 3
    private var tipo: String? = null
    private var endereco: String? = null
    private var descricao: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViario1Binding.inflate(inflater, container, false)

        tipo = arguments?.getString("tipo")
        endereco = arguments?.getString("endereco")
        descricao = arguments?.getString("descricao")


        val carrinho = requireActivity().findViewById<ImageView>(R.id.carrinho)
        val bolinhaInicial = requireActivity().findViewById<ImageView>(R.id.progress_bar_circle1)
        bolinhaInicial.post {
            val destinoX = bolinhaInicial.x + bolinhaInicial.width / 2 - carrinho.width / 2

            carrinho.animate()
                .x(destinoX)
                .setDuration(700)
                .start()
        }

        if (tipo != null) {
            when (tipo) {
                "Sinalização Ineficiente" ->  binding.rgViario.check(R.id.rbSinaInefi)
                "Substituição" ->  binding.rgViario.check(R.id.rbSubstituicao)
                "Sugestão" ->  binding.rgViario.check(R.id.rbSugestao)
            }
        }

        binding.btnVoltarViario1.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnProximoViario1.setOnClickListener {
            tipo = when (binding.rgViario.checkedRadioButtonId) {
                R.id.rbSinaInefi -> "Sinalização Ineficiente"
                R.id.rbSubstituicao -> "Substituição"
                R.id.rbSugestao -> "Sugestão"
                // Provavelmente vai ter mais
                else -> ""
            }

            if (tipo == "") {
                val titulo = SpannableString("Campo incompleto").apply {
                    setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.CinzaMedio)),
                        0, length, 0
                    )
                }

                val mensagem = SpannableString("Para Avançar, escolha uma opção.").apply {
                    setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.CinzaMedio)),
                        0, length, 0
                    )
                }

                val builder = AlertDialog.Builder(requireContext())
                    .setTitle(titulo)
                    .setMessage(mensagem)
                    .setPositiveButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }

                val dialog = builder.create()
                dialog.setOnShowListener {
                    dialog.window?.setBackgroundDrawable(
                        ColorDrawable(ContextCompat.getColor(requireContext(), R.color.Branco))
                    )
                }
                dialog.show()

                return@setOnClickListener
            }

            val fragmentEndereco = Viario2Fragment().apply {
                arguments = Bundle().apply {
                    putString("tipo", tipo)
                    putString("endereco", endereco)
                    putString("descricao", descricao)
                }
            }

            if (etapaAtual < totalEtapas - 1) {
                (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual + 1, "continuar")
            }

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.FragmentContainerView2, fragmentEndereco)
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
