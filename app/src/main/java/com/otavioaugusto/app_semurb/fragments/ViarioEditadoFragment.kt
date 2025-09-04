package com.otavioaugusto.app_semurb.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.databinding.FragmentViarioeditadoBinding
import com.otavioaugusto.app_semurb.dbHelper.AppDatabaseHelper

class ViarioEditadoFragment : Fragment() {

    private var _binding: FragmentViarioeditadoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViarioeditadoBinding.inflate(inflater, container, false)

        binding.btnVoltarViarioEditado.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnFinalizarViarioEdicao.setOnClickListener {
            requireActivity().finish()
        }

        val idViario = activity?.intent?.getLongExtra("ID_VIARIO", -1L) ?: -1L
        val tipo = activity?.intent?.getStringExtra("TIPO")
        val endereco = activity?.intent?.getStringExtra("ENDERECO")
        val descricao = activity?.intent?.getStringExtra("DESCRICAO")
        val data_envio = activity?.intent?.getStringExtra("DATA_ENVIO")
        val numSequencial = activity?.intent?.getStringExtra("NUMERO_SEQUENCIAL")

        Log.d("TESTE", "DATA ENVIO: ${data_envio}")

        if (!data_envio.isNullOrEmpty()) {
            binding.textViewViarioID.text = "${data_envio}"
            binding.btnExcluirViario.visibility = View.GONE
            binding.btnFinalizarViarioEdicao.visibility = View.GONE
            binding.editTextDescricao.isEnabled = false
            binding.editTextEndereco.isEnabled = false
            binding.rbSugestao.isEnabled = false
            binding.rbSubstituicao.isEnabled = false
            binding.rbSinaInefi.isEnabled = false
        }

        binding.editTextEndereco.setText(endereco)
        binding.editTextDescricao.setText(descricao)
        binding.textViewViarioID.text = "Sinalização $numSequencial"

        when (tipo) {
            "Sinalização Ineficiente" -> binding.rbSinaInefi.isChecked = true
            "Substituição" -> binding.rbSubstituicao.isChecked = true
            "Sugestão" -> binding.rbSugestao.isChecked = true
        }


        binding.btnFinalizarViarioEdicao.setOnClickListener {
            val tipoSelecionado = when (binding.rgViarioEditado.checkedRadioButtonId) {
                R.id.rbSinaInefi -> "Sinalização Ineficiente"
                R.id.rbSubstituicao -> "Substituição"
                R.id.rbSugestao -> "Sugestão"
                else -> ""
            }

            val novoEndereco = binding.editTextEndereco.text.toString()
            val novaDescricao = binding.editTextDescricao.text.toString()

            if (idViario != -1L) {
                val dbHelper = AppDatabaseHelper(requireContext())
                dbHelper.updateViarioCompleto(
                    id = idViario,
                    tipo = tipoSelecionado,
                    endereco = novoEndereco,
                    descricao = novaDescricao
                )

                Toast.makeText(requireContext(), "Sinalização atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "ID da sinalização inválido!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnExcluirViario.setOnClickListener {
            if (idViario != -1L) {
                val dbHelper = AppDatabaseHelper(requireContext())
                dbHelper.deleteViario(idViario)

                Toast.makeText(requireContext(), "Sinalização excluída com sucesso!", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "ID da sinalização inválido!", Toast.LENGTH_SHORT).show()
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
