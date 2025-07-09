package com.otavioaugusto.app_semurb.fragments

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.dbHelper.ocorrenciasDBHelper
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrencias2Binding

class Ocorrencias2Fragment : Fragment() {

    private var _binding: FragmentOcorrencias2Binding? = null
    private val binding get() = _binding!!

    private var etapaAtual = 1 // etapa 2
    private var totalEtapas = 3
    private var tipo: String? = null
    private var endereco: String? = null
    private var nome: String? = null
    private var numContato: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOcorrencias2Binding.inflate(inflater, container, false)

        tipo = arguments?.getString("tipo")
        endereco = arguments?.getString("endereco")
        nome = arguments?.getString("nome")
        numContato = arguments?.getString("numContato")

        if (endereco != null) {
            binding.EditTextEnderecoOcorrencia.setText(endereco)
        }

        binding.btnProximoOcorrencias2.setOnClickListener {
            endereco = binding.EditTextEnderecoOcorrencia.text.toString()
            if (endereco == "") {

                val titulo = SpannableString("Digite um endereço").apply {
                    setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.CinzaMedio)),
                        0, length, 0
                    )
                }

                val mensagem = SpannableString("Para Avançar, digite um endereço").apply {
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

            val fragmentContato = Ocorrencias3Fragment().apply {
                arguments = Bundle().apply {
                    putString("tipo", tipo)
                    putString("endereco", endereco)
                    putString("nome", nome)
                    putString("numContato", numContato)
                }
            }

            if (etapaAtual < totalEtapas - 1) {
                (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual + 1, "continuar")
            }

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.FragmentContainerView2, fragmentContato)
                .addToBackStack(null)
                .commit()
        }

        binding.btnVoltarOcorrencias2.setOnClickListener {
            endereco = binding.EditTextEnderecoOcorrencia.text.toString()
            if (etapaAtual > 0) {
                (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual - 1, "voltar")
            }

            val fragmentTipo = Ocorrencias1Fragment().apply {
                arguments = Bundle().apply {
                    putString("tipo", tipo)
                    putString("endereco", endereco)
                    putString("nome", nome)
                    putString("numContato", numContato)
                }
            }

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.FragmentContainerView2, fragmentTipo)
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
