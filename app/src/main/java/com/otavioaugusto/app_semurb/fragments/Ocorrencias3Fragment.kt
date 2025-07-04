package com.otavioaugusto.app_semurb.fragments

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.dbHelper.ocorrenciasDBHelper
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrencias3Binding
import java.util.*

class Ocorrencias3Fragment : Fragment() {

    private var _binding: FragmentOcorrencias3Binding? = null
    private val binding get() = _binding!!

    private var ocorrenciaId: Long = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOcorrencias3Binding.inflate(inflater, container, false)

        ocorrenciaId = arguments?.getLong("ocorrencia_id") ?: 0L

        binding.btnProximoOcorrencias3.setOnClickListener {
            val nome = binding.EditTextNomeContato.text.toString()
            val telefone = binding.EditTextNumContato.text.toString()
            if (nome.isEmpty() || telefone.isEmpty()) {

                val titulo = SpannableString("Digite um nome e telefone").apply {
                    setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.CinzaMedio)),
                        0, length, 0
                    )
                }

                val mensagem = SpannableString("Para Avançar, digite um nome e telefone").apply {
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
            dbHelper.updateContato(ocorrenciaId, nome, telefone)


            requireActivity().finish()
        }

        binding.btnVoltarOcorrencias3.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
