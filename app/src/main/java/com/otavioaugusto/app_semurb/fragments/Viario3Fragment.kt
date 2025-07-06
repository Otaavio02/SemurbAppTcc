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
import com.otavioaugusto.app_semurb.database.ViarioDBHelper
import com.otavioaugusto.app_semurb.databinding.FragmentViario2Binding
import com.otavioaugusto.app_semurb.databinding.FragmentViario3Binding
import com.otavioaugusto.app_semurb.dbHelper.ocorrenciasDBHelper
import java.util.Timer
import kotlin.concurrent.schedule

class Viario3Fragment : Fragment() {

    private var _binding: FragmentViario3Binding? = null
    private val binding get() = _binding!!

    private var viarioId: Long = 0L

    private var etapaAtual = 2 // etapa 3

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViario3Binding.inflate(inflater, container, false)

        viarioId = arguments?.getLong("viario_id") ?: 0L

        binding.btnVoltarViario3.setOnClickListener {
            if (etapaAtual > 0) {
                (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual - 1, "voltar")
            }

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.FragmentContainerView2, Viario2Fragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnProximoViario3.setOnClickListener {
            val descricao = binding.EditTextDescricaoViario.text.toString()
            if (descricao.isEmpty()) {

                val titulo = SpannableString("Campo incompleto").apply {
                    setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.CinzaMedio)),
                        0, length, 0
                    )
                }

                val mensagem = SpannableString("Para Finalizar, digite a descrição da sinalização feita.").apply {
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

            val dbHelper = ViarioDBHelper(requireContext())
            dbHelper.updateDescricao(viarioId, descricao)

            (activity as? PlaceHolderGameficadoActivity)?.concluirEtapaFinal(2)

            Timer().schedule(700) {
                requireActivity().finish()
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
