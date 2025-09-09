package com.otavioaugusto.app_semurb.fragments

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.databinding.FragmentViario3Binding
import com.otavioaugusto.app_semurb.dbHelper.AppDatabaseHelper
import java.util.Timer
import kotlin.concurrent.schedule
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Viario3Fragment : Fragment() {

    private var _binding: FragmentViario3Binding? = null
    private val binding get() = _binding!!

    private var etapaAtual = 2 // etapa 3
    private var tipo: String? = null
    private var endereco: String? = null
    private var descricao: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViario3Binding.inflate(inflater, container, false)

        tipo = arguments?.getString("tipo")
        endereco = arguments?.getString("endereco")
        descricao = arguments?.getString("descricao")

        if (descricao != null) {
            binding.EditTextDescricaoViario.setText(descricao)
        }

        binding.ImageButtonCameraViario.setOnClickListener {
            val arquivoFoto = File(requireContext().cacheDir, "ocorrencia_${System.currentTimeMillis()}.jpg")
            fotoUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", arquivoFoto)
            cameraLauncher.launch(fotoUri)
        }

        binding.btnVoltarViario3.setOnClickListener {
            descricao = binding.EditTextDescricaoViario.text.toString()
            if (etapaAtual > 0) {
                (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual - 1, "voltar")
            }

            val fragmentDescricao = Viario2Fragment().apply {
                arguments = Bundle().apply {
                    putString("tipo", tipo)
                    putString("endereco", endereco)
                    putString("descricao", descricao)
                }
            }

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.FragmentContainerView2, fragmentDescricao)
                .commit()
        }

        binding.btnProximoViario3.setOnClickListener {
            descricao = binding.EditTextDescricaoViario.text.toString()
            if (descricao == "") {

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

            binding.btnProximoViario3.isEnabled = false

            if (fotoUri != null) {
                salvarFotoOcorrencia(fotoUri!!, endereco!!, descricao!!)
            } else {
                salvarOcorrenciaNoBanco(null, endereco!!, descricao!!)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.post {
            (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual, "continuar")
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                descricao = binding.EditTextDescricaoViario.text.toString()
                if (etapaAtual > 0) {
                    (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual - 1, "voltar")
                }

                val fragmentDescricao = Viario2Fragment().apply {
                    arguments = Bundle().apply {
                        putString("tipo", tipo)
                        putString("endereco", endereco)
                        putString("descricao", descricao)
                    }
                }

                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )
                    .replace(R.id.FragmentContainerView2, fragmentDescricao)
                    .commit()
            }
        })
    }

    private var fotoUri: Uri? = null
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { sucesso ->

        binding.ImageButtonCameraViario.setImageURI(fotoUri)

    }

    private fun salvarFotoOcorrencia(uri: Uri, endereco: String, descricao: String) {
        lifecycleScope.launch{
            try {
                val storage = FirebaseStorage.getInstance().reference
                val caminho = "avarias/${System.currentTimeMillis()}_${endereco}.jpg"
                val fotoRef = storage.child(caminho)

                val arquivoComprimido = comprimirImagem(uri)

                fotoRef.putFile(Uri.fromFile(arquivoComprimido)).await()
                val downloadUrl = fotoRef.downloadUrl.await()

                salvarOcorrenciaNoBanco(downloadUrl.toString(), endereco, descricao)


            } catch (e: Exception){
                Toast.makeText(requireContext(), "Erro ao enviar foto: ${e.message}", Toast.LENGTH_SHORT).show()
                salvarOcorrenciaNoBanco(null, endereco, descricao)
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private suspend fun comprimirImagem(uri: Uri): File = withContext(Dispatchers.IO) {
        val context = requireContext()
        val inputStream = context.contentResolver.openInputStream(uri)
        val originalBitMap = BitmapFactory.decodeStream(inputStream)

        val larguraMax = 1080
        val escala = larguraMax.toFloat() / originalBitMap.width.toFloat()
        val novaAltura = (originalBitMap.height * escala).toInt()
        val bitmapReduzido = Bitmap.createScaledBitmap(originalBitMap, larguraMax, novaAltura, true)

        val arquivoTemp = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(arquivoTemp)

        bitmapReduzido.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        outputStream.flush()
        outputStream.close()

        arquivoTemp
    }

    private fun salvarOcorrenciaNoBanco(urlFoto: String?, endereco: String,  descricao: String) {
        val dbHelper = AppDatabaseHelper(requireContext())
        dbHelper.insertViarioCompleto(tipo, endereco, descricao, urlFoto)

        (activity as? PlaceHolderGameficadoActivity)?.concluirEtapaFinal(2)

        Timer().schedule(700) {
            requireActivity().finish()
        }
    }
}
