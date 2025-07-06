package com.otavioaugusto.app_semurb.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.dbHelper.ocorrenciasDBHelper
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrencias1Binding
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity

class Ocorrencias1Fragment : Fragment() {

    private var _binding: FragmentOcorrencias1Binding? = null
    private val binding get() = _binding!!

    private var etapaAtual = 0 // etapa 2
    private var totalEtapas = 3
    private var novaOcorrenciaId: Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOcorrencias1Binding.inflate(inflater, container, false)

        // Animação inicial do carrinho chegando
        val carrinho = requireActivity().findViewById<ImageView>(R.id.carrinho)
        val bolinhaInicial = requireActivity().findViewById<ImageView>(R.id.progress_bar_circle1)
        bolinhaInicial.post {
            val destinoX = bolinhaInicial.x + bolinhaInicial.width / 2 - carrinho.width / 2

            carrinho.animate()
                .x(destinoX)
                .setDuration(700)
                .start()
        }

        binding.btnVoltarOcorrencias1.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnProximoOcorrencias1.setOnClickListener {

            val tipo = when (binding.rgOcorrencias?.checkedRadioButtonId) {
                R.id.rbSinistro -> "Sinistro de Trânsito"
                R.id.rbGrandeVulto -> "Sinistro de Grande Vulto"
                R.id.rbAtendimento -> "Atendimento ao Cidadão"
                else -> ""
            }

            if (tipo.isEmpty()) {
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

            val dbHelper = ocorrenciasDBHelper(requireContext())
            val id = dbHelper.insertOcorrencia(tipo) // Como assim? vai inserir o tipo no lugar do ID?
            novaOcorrenciaId = id

            val fragmentEndereco = Ocorrencias2Fragment().apply {
                arguments = Bundle().apply {
                    putLong("ocorrencia_id", id)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
