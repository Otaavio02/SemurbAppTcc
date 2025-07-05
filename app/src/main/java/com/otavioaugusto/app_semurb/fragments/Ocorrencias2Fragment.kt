package com.otavioaugusto.app_semurb.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.dbHelper.ocorrenciasDBHelper
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrencias2Binding

class Ocorrencias2Fragment : Fragment() {

    private var _binding: FragmentOcorrencias2Binding? = null
    private val binding get() = _binding!!

    private var ocorrenciaId: Long = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOcorrencias2Binding.inflate(inflater, container, false)

        ocorrenciaId = arguments?.getLong("ocorrencia_id") ?: 0L

        binding.btnProximoOcorrencias2.setOnClickListener {
            val endereco = binding.EditTextEnderecoOcorrencia.text.toString()
            if (endereco.isEmpty()) {
                // opcional: mostrar erro
                return@setOnClickListener
            }
            val dbHelper = ocorrenciasDBHelper(requireContext())
            dbHelper.updateEndereco(ocorrenciaId, endereco)

            // Passa para o fragment contato
            val fragmentContato = Ocorrencias3Fragment().apply {
                arguments = Bundle().apply {
                    putLong("ocorrencia_id", ocorrenciaId)
                }
            }

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.FragmentContainerView2, fragmentContato)
                .addToBackStack(null)
                .commit()
        }

        binding.btnVoltarOcorrencias2.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
