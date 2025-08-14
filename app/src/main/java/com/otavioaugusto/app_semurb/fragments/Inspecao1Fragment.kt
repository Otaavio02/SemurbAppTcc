package com.otavioaugusto.app_semurb.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.databinding.FragmentInspecao1Binding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Timer
import kotlin.concurrent.schedule

class Inspecao1Fragment : Fragment() {

    private var _binding: FragmentInspecao1Binding? = null
    private val binding get() = _binding!!

    private val autenticacao by lazy { FirebaseAuth.getInstance() }
    private val bancoDados by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInspecao1Binding.inflate(inflater, container, false)

        val idAgenteLogado = autenticacao.currentUser?.uid

        lifecycleScope.launch {
            try {
                val documento = withContext(Dispatchers.IO) {
                    bancoDados.collection("agentes")
                        .document(idAgenteLogado.toString())
                        .get()
                        .await()
                }

                val dados = documento.data
                if (dados != null) {
                    val idViatura = dados["viatura"]
                    withContext(Dispatchers.Main){
                        binding.textViewInspecaoViatura.text = "Inspeção da viatura $idViatura"
                        binding.textView35.text = "$idViatura"
                    }

                } else {
                    Toast.makeText(requireContext(), "Dados do usuário não encontrados", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("InspeçãoFragment", "Erro ao carregar dados: ${e.message}", e)
                Toast.makeText(requireContext(), "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
            }
        }



        val carrinho = requireActivity().findViewById<ImageView>(R.id.carrinho)
        val bolinhaInicial = requireActivity().findViewById<ImageView>(R.id.progress_bar_circle1)
        bolinhaInicial.post {
            val destinoX = bolinhaInicial.x + bolinhaInicial.width / 2 - carrinho.width / 2

            carrinho.animate()
                .x(destinoX)
                .setDuration(700)
                .start()
        }


        binding.btnVoltarInspecao.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnProximoInspecao1.setOnClickListener {

                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                    )
                    .replace(R.id.FragmentContainerView2, Inspecao2Fragment())
                    .addToBackStack(null)
                    .commit()
            }


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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
