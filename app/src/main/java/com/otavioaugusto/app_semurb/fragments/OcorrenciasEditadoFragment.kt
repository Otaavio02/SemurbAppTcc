package com.otavioaugusto.app_semurb.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrenciaseditadoBinding
import com.otavioaugusto.app_semurb.dbHelper.ocorrenciasDBHelper

class OcorrenciasEditadoFragment : Fragment() {

    private var _binding: FragmentOcorrenciaseditadoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOcorrenciaseditadoBinding.inflate(inflater, container, false)

        binding.btnVoltarOcorrenciasEditado.setOnClickListener {
            requireActivity().finish()
        }


        val idOcorrencia = activity?.intent?.getLongExtra("ID_OCORRENCIA", -1L) ?: -1L
        val tipo = activity?.intent?.getStringExtra("TIPO")
        val endereco = activity?.intent?.getStringExtra("ENDERECO")
        val nome = activity?.intent?.getStringExtra("NOME")
        val contato = activity?.intent?.getStringExtra("CONTATO")

        binding.editTextEndereco.setText(endereco)
        binding.editTextNome.setText(nome)
        binding.editTextContato.setText(contato)

        when (tipo) {
            "Sinistro de Trânsito" -> binding.rbSinistro.isChecked = true
            "Sinistro de Grande Vulto" -> binding.rbGrandeVulto.isChecked = true
            "Atendimento ao Cidadão" -> binding.rbAtendimento.isChecked = true
        }


        binding.btnFinalizarOcorrenciasEditado.setOnClickListener {
            val tipoSelecionado = when (binding.rgOcorrenciasEditado.checkedRadioButtonId) {
                R.id.rbSinistro -> "Sinistro de Trânsito"
                R.id.rbGrandeVulto -> "Sinistro de Grande Vulto"
                R.id.rbAtendimento -> "Atendimento ao Cidadão"
                else -> ""
            }

            val novoEndereco = binding.editTextEndereco.text.toString()
            val novoNome = binding.editTextNome.text.toString()
            val novoContato = binding.editTextContato.text.toString()

            if (idOcorrencia != -1L) {
                val dbHelper = ocorrenciasDBHelper(requireContext())
                dbHelper.updateOcorrenciaCompleta(
                    id = idOcorrencia,
                    tipo = tipoSelecionado,
                    endereco = novoEndereco,
                    nome = novoNome,
                    numcontato = novoContato
                )

                Toast.makeText(requireContext(), "Ocorrência atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "ID da ocorrência inválido!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnExcluirOcorrencia.setOnClickListener {
            if (idOcorrencia != -1L) {
                val dbHelper = ocorrenciasDBHelper(requireContext())
                dbHelper.deleteOcorrencia(idOcorrencia)

                Toast.makeText(requireContext(), "Ocorrência excluída com sucesso!", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "ID da ocorrência inválido!", Toast.LENGTH_SHORT).show()
            }
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
