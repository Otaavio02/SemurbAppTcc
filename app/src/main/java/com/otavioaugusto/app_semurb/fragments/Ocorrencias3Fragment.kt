package com.otavioaugusto.app_semurb.fragments

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.dbHelper.ocorrenciasDBHelper
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrencias3Binding
import java.util.*
import java.util.regex.Pattern
import kotlin.concurrent.schedule

class Ocorrencias3Fragment : Fragment() {

    private var _binding: FragmentOcorrencias3Binding? = null
    private val binding get() = _binding!!
    
    private var etapaAtual = 2 // etapa 3
    private var tipo: String? = null
    private var endereco: String? = null
    private var nome: String? = null
    private var numContato: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOcorrencias3Binding.inflate(inflater, container, false)



        tipo = arguments?.getString("tipo")
        endereco = arguments?.getString("endereco")
        nome = arguments?.getString("nome")
        numContato = arguments?.getString("numContato")


        binding.btnProximoOcorrencias3.setOnClickListener {
            nome = binding.EditTextNomeContato.text.toString()
            numContato = binding.EditTextNumContato.text.toString()
            if (nome?.isBlank() == true || numContato?.isBlank() == true) {
                mostrarAlerta(
                    titulo = "Digite um nome e telefone",
                    mensagem = "Para avançar, digite um nome e telefone"
                )
                return@setOnClickListener
            }
            if (!mobileValidate(numContato)) {

                binding.EditTextNumContato.error = "Telefone inválido – (xx) xxxxx‑xxxx"
                binding.EditTextNumContato.requestFocus()

                return@setOnClickListener
            }

            val dbHelper = ocorrenciasDBHelper(requireContext())
            dbHelper.insertOcorrencia(tipo, endereco, nome, numContato)

            (activity as? PlaceHolderGameficadoActivity)?.concluirEtapaFinal(2)

            Timer().schedule(700) {
                requireActivity().finish()
            }
        }

        binding.btnVoltarOcorrencias3.setOnClickListener {
            nome = binding.EditTextNomeContato.text.toString()
            numContato = binding.EditTextNumContato.text.toString()
            if (etapaAtual > 0) {
                (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual - 1, "voltar")
            }

            val fragmentEndereco = Ocorrencias2Fragment().apply {
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

    private fun mobileValidate(text: String?): Boolean {
        var padraoNumero = Pattern.compile("^\\(?[1-9]{2}\\)? ?(?:[2-8]|9[0-9])[0-9]{3}\\-?[0-9]{4}\$") // Padrão de numero telefone brasileiro, que deixa os parênteses, o espaço em branco e hífen opcionais
        val m = padraoNumero.matcher(text)
        return m.matches()
    }

    private fun Fragment.mostrarAlerta(titulo: String, mensagem: String) {
        val tituloSpan = SpannableString(titulo).apply {
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.CinzaMedio)),
                0, length, 0
            )
        }
        val mensagemSpan = SpannableString(mensagem).apply {
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.CinzaMedio)),
                0, length, 0
            )
        }

        AlertDialog.Builder(requireContext())
            .setTitle(tituloSpan)
            .setMessage(mensagemSpan)
            .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
            .create().apply {
                setOnShowListener {
                    window?.setBackgroundDrawable(
                        ColorDrawable(ContextCompat.getColor(requireContext(), R.color.Branco))
                    )
                }
                show()
            }
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
}
