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
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.storage.FirebaseStorage
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrencias3Binding
import com.otavioaugusto.app_semurb.dbHelper.AppDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.concurrent.schedule
import kotlin.math.log

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

        binding.ImageButtonCameraOcorrencia.setOnClickListener {
            val arquivoFoto = File(requireContext().cacheDir, "ocorrencia_${System.currentTimeMillis()}.jpg")
            fotoUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", arquivoFoto)
            cameraLauncher.launch(fotoUri)
        }


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

                binding.EditTextNumContato.error = "Telefone inválido – (xx) 9xxxx‑xxxx"
                binding.EditTextNumContato.requestFocus()

                return@setOnClickListener
            }
            binding.btnProximoOcorrencias3.isEnabled = false
            binding.progressBarOcorrencias.visibility = View.VISIBLE

            if (fotoUri != null) {
                salvarFotoOcorrencia(fotoUri!!, nome!!, numContato!!)
            } else {
                salvarOcorrenciaNoBanco(null, nome!!, numContato!!)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.post {
            (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual, "continuar")
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
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
                    .commit()
            }
        })
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


    private var fotoUri: Uri? = null
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { sucesso ->

        binding.ImageButtonCameraOcorrencia.setImageURI(fotoUri)

    }



    private fun salvarFotoOcorrencia(uri: Uri, nome: String, numContato: String) {
        lifecycleScope.launch{
            try {
                val storage = FirebaseStorage.getInstance().reference
                val caminho = "ocorrencias/${System.currentTimeMillis()}_${nome}.jpg"
                val fotoRef = storage.child(caminho)

                val arquivoComprimido = comprimirImagem(uri)

                fotoRef.putFile(Uri.fromFile(arquivoComprimido)).await()
                val downloadUrl = fotoRef.downloadUrl.await()

                salvarOcorrenciaNoBanco(downloadUrl.toString(), nome, numContato)


            } catch (e: Exception){
                Toast.makeText(requireContext(), "Erro ao enviar foto: ${e.message}", Toast.LENGTH_SHORT).show()
                salvarOcorrenciaNoBanco(null, nome, numContato)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        binding.btnProximoOcorrencias3.isEnabled = true

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

    private fun salvarOcorrenciaNoBanco(urlFoto: String?, nome: String, numContato: String) {
        val dbHelper = AppDatabaseHelper(requireContext())
        try {
            dbHelper.insertOcorrencia(tipo, endereco, nome, numContato, urlFoto)

            (activity as? PlaceHolderGameficadoActivity)?.concluirEtapaFinal(2)

            Timer().schedule(700) {
                requireActivity().finish()
            }
        }catch(e: Exception ){
            "Erro ao obter endereço"

        }finally {
            binding.progressBarOcorrencias.visibility = View.GONE
            binding.btnProximoOcorrencias3.isEnabled = true
        }
    }
}
