package com.otavioaugusto.app_semurb.fragments

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.databinding.FragmentInspecao3Binding
import com.otavioaugusto.app_semurb.funcoes.AvariaToggleController
import com.otavioaugusto.app_semurb.funcoes.AvariasRecyclerHelper
import java.io.File
import java.util.Timer
import kotlin.concurrent.schedule

class Inspecao3Fragment : Fragment() {

    private var imagemTempUri: Uri? = null

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            Toast.makeText(requireContext(), "Foto salva em: $imagemTempUri", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(requireContext(), "Falha ao tirar foto", Toast.LENGTH_SHORT).show()
        }
    }

    private var _binding: FragmentInspecao3Binding? = null
    private val binding get() = _binding!!
    private val etapaAtual = 2 // Etapa 3

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInspecao3Binding.inflate(inflater, container, false)
        setupListeners()
        setupToggles()
        setupRecyclers()



        binding.btnInfoFrente?.setOnClickListener {
            mostrarAlertDialogAvaria(requireContext(),"Inclui partes como: ", "Capo, Farol esquerdo/direito, Pisca esquerdo/direito, Para-brisa, Para-choque, Grade frontal, etc." )
        }

        binding.btnInfoTraseira?.setOnClickListener {
            mostrarAlertDialogAvaria(requireContext(),"Inclui partes como: ", "Para-Choque traseiro, Farol esquerdo/direito, Pisca esquerdo/direito, Vidro traseiro, Luz de ré, Escapamento, etc," )
        }
        binding.btnInfoDireita?.setOnClickListener {
            mostrarAlertDialogAvaria(requireContext(),"Inclui partes como: ", "Para-choque lateral direito, porta dianteira direita, porta traseira direita, retrovisor direito, vidros laterais direitos, maçanetas, Pneus direitos etc." )
        }
        binding.btnInfoEsquerda?.setOnClickListener {
            mostrarAlertDialogAvaria(requireContext(),"Inclui partes como: ", "Para-choque lateral esquerda, porta dianteira esquerda, porta traseira esquerda, retrovisor esquerdo, vidros laterais esquerda, maçanetas, Pneus esquerdos etc" )
        }
        binding.btnInfoOutros?.setOnClickListener {
            mostrarAlertDialogAvaria(requireContext(),"Inclui partes como: ", "Teto, Chassi, Motor, Suspensão, Freios, etc." )
        }
        return binding.root




    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual, "continuar")
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupListeners() {
        binding.btnVoltarInspecao2.setOnClickListener {
            (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual - 1, "voltar")
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.FragmentContainerView2, Inspecao2Fragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnFinalizar.setOnClickListener {
            (activity as? PlaceHolderGameficadoActivity)?.concluirEtapaFinal(etapaAtual)
            Timer().schedule(700) {
                requireActivity().finish()
            }
        }
    }

    private fun setupToggles() {
        val context = requireContext()
        AvariaToggleController(context, binding.containerAvariasTraseira, binding.frameAvariaTraseira, binding.btnInspecaoTraseira, binding.textViewTraseira)
        AvariaToggleController(context, binding.containerAvariasFrente, binding.frameAvariaFrente, binding.btnInspecaoFrente, binding.textViewFrente)
        AvariaToggleController(context, binding.containerAvariasDireita, binding.frameAvariaDireita, binding.btnInspecaoDireita, binding.textViewDireita)
        AvariaToggleController(context, binding.containerAvariasEsquerda, binding.frameAvariaEsquerda, binding.btnInspecaoEsquerda, binding.textViewEsquerda)
        AvariaToggleController(context, binding.containerAvariasOutras, binding.frameAvariaOutras, binding.btnInspecaoOutras, binding.textViewOutras)
    }

    private fun setupRecyclers() {
        val context = requireContext()

        val callbackFoto = { _: Int ->
            imagemTempUri = criarUriImagem()
            takePictureLauncher.launch(imagemTempUri)
        }
        AvariasRecyclerHelper(context, binding.rvFrente, callbackFoto)
        AvariasRecyclerHelper(context, binding.rvTraseira, callbackFoto)
        AvariasRecyclerHelper(context, binding.rvDireita, callbackFoto)
        AvariasRecyclerHelper(context, binding.rvEsquerda, callbackFoto)
        AvariasRecyclerHelper(context, binding.rvOutras, callbackFoto)
    }

    private fun hideSystemUI() {
        activity?.window?.let { window ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.apply {
                    hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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

    private fun criarUriImagem(): Uri {
        val fotoFile = File(requireContext().getExternalFilesDir(null), "foto_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            fotoFile
        )
    }

    private fun mostrarAlertDialogAvaria(context: Context, tituloTexto: String, mensagemTexto: String) {
        val titulo = SpannableString(tituloTexto)
        titulo.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.CinzaMedio)),
            0,
            titulo.length,
            0
        )

        val mensagem = SpannableString(mensagemTexto)
        mensagem.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.CinzaMedio)),
            0,
            mensagem.length,
            0
        )

        val dialog = AlertDialog.Builder(context)
            .setTitle(titulo)
            .setMessage(mensagem)
            .setPositiveButton("OK") { dialogInterface, _ -> dialogInterface.dismiss() }
            .create()

        dialog.setOnShowListener {
            dialog.window?.setBackgroundDrawableResource(R.color.Branco)
        }

        dialog.show()
    }

}
