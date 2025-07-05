package com.otavioaugusto.app_semurb.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.dbHelper.ocorrenciasDBHelper
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrencias1Binding

class Ocorrencias1Fragment : Fragment() {

    private var _binding: FragmentOcorrencias1Binding? = null
    private val binding get() = _binding!!

    private var novaOcorrenciaId: Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOcorrencias1Binding.inflate(inflater, container, false)

        binding.btnVoltarOcorrencias1.setOnClickListener{
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

                return@setOnClickListener
            }


            val dbHelper = ocorrenciasDBHelper(requireContext())
            val id = dbHelper.insertOcorrencia(tipo)
            novaOcorrenciaId = id


            val fragmentEndereco = Ocorrencias2Fragment().apply {
                arguments = Bundle().apply {
                    putLong("ocorrencia_id", id)
                }
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
