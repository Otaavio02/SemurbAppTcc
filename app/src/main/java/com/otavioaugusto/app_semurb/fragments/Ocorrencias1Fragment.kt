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
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrencias1Binding
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity

class Ocorrencias1Fragment : Fragment() {

    private var _binding: FragmentOcorrencias1Binding? = null
    private val binding get() = _binding!!

    private var etapaAtual = 0 // etapa 2
    private var totalEtapas = 3
    private var tipo: String? = null
    private var endereco: String? = null
    private var nome: String? = null
    private var numContato: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOcorrencias1Binding.inflate(inflater, container, false)

        tipo = arguments?.getString("tipo")
        endereco = arguments?.getString("endereco")
        nome = arguments?.getString("nome")
        numContato = arguments?.getString("numContato")

        val carrinho = requireActivity().findViewById<ImageView>(R.id.carrinho)
        val bolinhaInicial = requireActivity().findViewById<ImageView>(R.id.progress_bar_circle1)
        bolinhaInicial.post {
            val destinoX = bolinhaInicial.x + bolinhaInicial.width / 2 - carrinho.width / 2

            carrinho.animate()
                .x(destinoX)
                .setDuration(700)
                .start()
        }

        if (tipo != null) {
            when (tipo) {
                "Sinistro de Trânsito" ->  binding.rgOcorrencias.check(R.id.rbSinistro)
                "Sinistro de Grande Vulto" ->  binding.rgOcorrencias.check(R.id.rbGrandeVulto)
                "Atendimento ao Cidadão" ->  binding.rgOcorrencias.check(R.id.rbAtendimento)
            }
        }

        binding.btnVoltarOcorrencias1.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnProximoOcorrencias1.setOnClickListener {

            tipo = when (binding.rgOcorrencias.checkedRadioButtonId) {
                R.id.rbSinistro -> "Sinistro de Trânsito"
                R.id.rbGrandeVulto -> "Sinistro de Grande Vulto"
                R.id.rbAtendimento -> "Atendimento ao Cidadão"
                else -> ""
            }

            if (tipo == "") {
                val titulo = SpannableString("Campo  incompleto").apply {
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
            } else {

            val fragmentEndereco = Ocorrencias2Fragment().apply {
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
                .replace(R.id.FragmentContainerView2, fragmentEndereco)
                .addToBackStack(null)
                .commit()
        }}

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
