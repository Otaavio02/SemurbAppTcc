package com.otavioaugusto.app_semurb.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
