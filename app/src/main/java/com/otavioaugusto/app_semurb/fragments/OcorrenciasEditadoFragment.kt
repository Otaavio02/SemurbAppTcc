package com.otavioaugusto.app_semurb.fragments

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrenciaseditadoBinding
import com.otavioaugusto.app_semurb.dbHelper.AppDatabaseHelper
import java.util.regex.Pattern

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
        val numSequencial = activity?.intent?.getStringExtra("NUMERO_SEQUENCIAL")
        var confirmarAlteracao = "sem alteração"
        val data_envio = activity?.intent?.getStringExtra("DATA_ENVIO")

        Log.d("TESTE", "DATA ENVIO: ${data_envio}")

        if (!data_envio.isNullOrEmpty()) {
            binding.textViewIdNomeOcorrenciasEditado.text = "${data_envio}"
            binding.btnExcluirOcorrencia.visibility = View.GONE
            binding.btnFinalizarOcorrenciasEditado.visibility = View.GONE
            binding.editTextNome.isEnabled = false
            binding.editTextEndereco.isEnabled = false
            binding.editTextContato.isEnabled = false
            binding.rbSinistro.isEnabled = false
            binding.rbAtendimento.isEnabled = false
            binding.rbGrandeVulto.isEnabled = false
        }

        binding.editTextEndereco.setText(endereco)
        binding.editTextNome.setText(nome)
        binding.editTextContato.setText(contato)
        binding.textViewIdNomeOcorrenciasEditado.text = "Ocorrência $numSequencial"
        when (tipo) {
            "Sinistro de Trânsito" -> binding.rbSinistro.isChecked = true
            "Sinistro de Grande Vulto" -> binding.rbGrandeVulto.isChecked = true
            "Atendimento ao Cidadão" -> binding.rbAtendimento.isChecked = true
        }

        binding.editTextContato.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (mobileValidate(binding.editTextContato.text.toString())){
                    confirmarAlteracao = "com alteração"
                } else {
                    confirmarAlteracao = "erro"
                    binding.editTextContato.setError("Telefone Inválido - (xx) 9xxxx-xxxx")
                }
            }
        })


        binding.btnFinalizarOcorrenciasEditado.setOnClickListener {
            if (confirmarAlteracao == "com alteração" || confirmarAlteracao == "sem alteração"){
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
                    val dbHelper = AppDatabaseHelper(requireContext())
                    dbHelper.updateOcorrenciaCompleta(
                        id = idOcorrencia,
                        tipo = tipoSelecionado,
                        endereco = novoEndereco,
                        nome = novoNome,
                        numcontato = novoContato
                    )

                        Toast.makeText(
                            requireContext(),
                            "Ocorrência atualizada com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()

                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), "ID da ocorrência inválido!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            if (confirmarAlteracao == "erro"){
                val titulo = SpannableString("Erro ao finalizar.").apply {
                    setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.CinzaMedio)),
                        0, length, 0
                    )
                }
                val mensagem = SpannableString("Por favor, confira se você digitou o telefone no formato certo.").apply {
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
            }
        }

        binding.btnExcluirOcorrencia.setOnClickListener {
            if (idOcorrencia != -1L) {
                val dbHelper = AppDatabaseHelper(requireContext())
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

    private fun mobileValidate(text: String): Boolean {
        var padraoNumero = Pattern.compile("^\\(?[1-9]{2}\\)? ?(?:[2-8]|9[0-9])[0-9]{3}\\-?[0-9]{4}\$") // Padrão de numero telefone brasileiro, que deixa os parênteses, o espaço em branco e hífen opcionais
        val m = padraoNumero.matcher(text)
        return m.matches()
    }
}
