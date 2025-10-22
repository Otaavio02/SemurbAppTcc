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
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrencias1Binding
import kotlin.collections.mutableListOf

class Ocorrencias1Fragment : Fragment() {

    private var _binding: FragmentOcorrencias1Binding? = null
    private val binding get() = _binding!!

    private var etapaAtual = 0
    private var totalEtapas = 3
    private var tipo: String? = null
    private var endereco: String? = null
    private var nome: String? = null
    private var numContato: String? = null
    private lateinit var tiposList: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOcorrencias1Binding.inflate(inflater, container, false)

        tipo = arguments?.getString("tipo")
        endereco = arguments?.getString("endereco")
        nome = arguments?.getString("nome")
        numContato = arguments?.getString("numContato")

        val db = FirebaseFirestore.getInstance()
        val autoComplete = binding.autoCompleteTipoOcorrencia

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            tiposList
        )
        autoComplete.setAdapter(adapter)

        // Permite abrir a lista sem digitar
        autoComplete.threshold = 0

        // Forçar abrir dropdown ao clicar
        autoComplete?.setOnTouchListener { _, _ ->
            if (tiposList.isNotEmpty()) {
                autoComplete.showDropDown()
            }
            false
        }

        // Busca tipos de ocorrência no Firestore
        db.collection("tipos_ocorrencia").get().addOnSuccessListener { documents ->
            tiposList.clear()
            for (doc in documents) {
                doc.getString("tipo")?.let { tiposList.add(it) }
            }
            adapter.notifyDataSetChanged()
        }

        // Abrir dropdown ao digitar
        autoComplete.addTextChangedListener {
            if (autoComplete.text.isNotEmpty()) {
                autoComplete.showDropDown()
            }
        }

        // Captura item selecionado
        autoComplete.setOnItemClickListener { _, _, position, _ ->
            tipo = tiposList[position]

            val carrinho = requireActivity().findViewById<ImageView>(R.id.carrinho)
            val bolinhaInicial =
                requireActivity().findViewById<ImageView>(R.id.progress_bar_circle1)

            bolinhaInicial.post {
                val destinoX = bolinhaInicial.x + bolinhaInicial.width / 2 - carrinho.width / 2
                carrinho.animate().x(destinoX).setDuration(700).start()
            }

        }

        binding.btnVoltarOcorrencias1.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnProximoOcorrencias1.setOnClickListener {
            tipo = binding.autoCompleteTipoOcorrencia.text.toString().trim()

            if (tipo.isNullOrEmpty()) {
                mostrarAlerta("Campo incompleto", "Para avançar, selecione um tipo de ocorrência")
                return@setOnClickListener
            }

            if (!tiposList.contains(tipo)) {
                mostrarAlerta(
                    "Tipo inválido",
                    "O tipo de ocorrência informado não existe. Selecione um tipo válido da lista."
                )
                return@setOnClickListener
            }

            val fragmentEndereco = Ocorrencias2Fragment().apply {
                arguments = Bundle().apply {
                    putString("tipo", tipo)
                    putString("endereco", endereco)
                    putString("nome", nome)
                    putString("numContato", numContato)
                }
            }

            if (etapaAtual < totalEtapas - 1) {
                (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(
                    etapaAtual + 1, "continuar"
                )
            }

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.FragmentContainerView2, fragmentEndereco)
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    private fun mostrarAlerta(tituloTxt: String, mensagemTxt: String) {
        val titulo = SpannableString(tituloTxt).apply {
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.CinzaMedio)),
                0, length, 0
            )
        }
        val mensagem = SpannableString(mensagemTxt).apply {
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.CinzaMedio)),
                0, length, 0
            )
        }

        val builder = AlertDialog.Builder(requireContext())
            .setTitle(titulo)
            .setMessage(mensagem)
            .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.window?.setBackgroundDrawable(
                ColorDrawable(ContextCompat.getColor(requireContext(), R.color.Branco))
            )
        }
        dialog.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
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
                    it.systemBarsBehavior =
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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
