package com.otavioaugusto.app_semurb.fragments

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrenciaseditadoBinding
import com.otavioaugusto.app_semurb.dbHelper.AppDatabaseHelper
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.regex.Pattern

class OcorrenciasEditadoFragment : Fragment() {

    private var _binding: FragmentOcorrenciaseditadoBinding? = null
    private val binding get() = _binding!!

    private var idOcorrencia: Long = 0
    private var tipo: String? = null
    private var endereco: String? = null
    private var nome: String? = null
    private var contato: String? = null
    private var numSequencial: String? = null
    private var confirmarAlteracao: Boolean = true
    private var data_envio: String? = null
    private var foto_url: String? = null
    private var downloadUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOcorrenciaseditadoBinding.inflate(inflater, container, false)

        binding.btnVoltarOcorrenciasEditado.setOnClickListener {
            requireActivity().finish()
        }


        idOcorrencia = activity?.intent?.getLongExtra("ID_OCORRENCIA", -1L) ?: -1L
        tipo = activity?.intent?.getStringExtra("TIPO")
        endereco = activity?.intent?.getStringExtra("ENDERECO")
        nome = activity?.intent?.getStringExtra("NOME")
        contato = activity?.intent?.getStringExtra("CONTATO")
        numSequencial = activity?.intent?.getStringExtra("NUMERO_SEQUENCIAL")
        data_envio = activity?.intent?.getStringExtra("DATA_ENVIO")
        var old_foto_url = activity?.intent?.getStringExtra("FOTO_URL")
        downloadUrl = old_foto_url

        Log.d("TESTESTSE", "ID OCORRENCIA É: ${idOcorrencia}")


        if (!data_envio.isNullOrEmpty()) {
            binding.textViewIdNomeOcorrenciasEditado.text = "${data_envio}"
            binding.btnExcluirOcorrencia.visibility = View.GONE
            binding.btnFinalizarOcorrenciasEditado.visibility = View.GONE
            binding.editTextNome.isFocusable = false
            binding.editTextEndereco.isFocusable = false
            binding.editTextContato.isFocusable = false
            binding.rbSinistro.isClickable = false
            binding.rbAtendimento.isClickable = false
            binding.rbGrandeVulto.isClickable = false
            binding.imageViewFoto.isEnabled = false // TODO FAZER EXPANDIR A IMAGEM QUE NEM EM INSPEÇÃO DA VIATURA
            if (!old_foto_url.isNullOrEmpty()) {
                Picasso.get()
                    .load(old_foto_url)
                    .fit()
                    .centerInside()
                    .rotate(90F)
                    .placeholder(R.drawable.bg_caixa)
                    .into(binding.imageViewFoto, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            Log.i("Picasso", "Imagem carregada com sucesso")
                        }

                        override fun onError(e: Exception?) {
                            Toast.makeText(requireContext(), "Erro ao carregar imagem", Toast.LENGTH_SHORT).show()
                            Log.e("Picasso", "Erro no carregamento da imagem", e)
                        }
                    })
                binding.textViewFoto.visibility = View.GONE
            } else {
                binding.textViewFoto.setText("Nenhuma imagem foi registrada.")
            }
        }
        Log.d("TESTE", "OLD FOTO URL É: ${old_foto_url}")
        Log.d("TESTE", "DOWNLOAD URL É: ${downloadUrl}")
        binding.editTextEndereco.setText(endereco)
        binding.editTextNome.setText(nome)
        binding.editTextContato.setText(contato)
        if (!old_foto_url.isNullOrEmpty()) {
            Picasso.get()
                .load(old_foto_url)
                .fit()
                .centerInside()
                .rotate(90F)
                .placeholder(R.drawable.bg_caixa)
                .into(binding.imageViewFoto, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        Log.i("Picasso", "Imagem carregada com sucesso")
                    }

                    override fun onError(e: Exception?) {
                        Toast.makeText(requireContext(), "Erro ao carregar imagem", Toast.LENGTH_SHORT).show()
                        Log.e("Picasso", "Erro no carregamento da imagem", e)
                    }
                })
            binding.textViewFoto.visibility = View.GONE
        } else {
            binding.textViewFoto.setText("Nenhuma imagem foi registrada.")
        }
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
                    confirmarAlteracao = true
                } else {
                    confirmarAlteracao = false
                    binding.editTextContato.setError("Telefone Inválido - (xx) 9xxxx-xxxx")
                }
            }
        })

        binding.imageViewFoto.setOnClickListener() {
            val arquivoFoto = File(requireContext().cacheDir, "ocorrencia_${System.currentTimeMillis()}.jpg")
            fotoUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", arquivoFoto)
            cameraLauncher.launch(fotoUri)
        }


        binding.btnFinalizarOcorrenciasEditado.setOnClickListener {
            lifecycleScope.launch {
                Log.d("TESTE", "OLD FOTO URL É: ${old_foto_url}")
                Log.d("TESTE", "DOWNLOAD URL É: ${downloadUrl}")
                val novaUrl = salvarFotoOcorrencia(fotoUri!!, nome!!)
                Log.d("TESTE", "NOVA URL É: ${novaUrl}")
                if (old_foto_url != novaUrl) {
                    if (novaUrl != null) {
                        val storage = FirebaseStorage.getInstance()
                        // Cria uma referência para o arquivo utilizando a URL
                        val storageRef: StorageReference = storage.getReferenceFromUrl(old_foto_url.toString())

                        // Deleta o arquivo
                        storageRef.delete()
                            .addOnSuccessListener {
                                // Sucesso ao deletar o arquivo
                                Log.d("FIREBASE","Imagem deletada com sucesso.")
                            }
                            .addOnFailureListener { exception ->
                                // Falha ao deletar o arquivo
                                Log.d("FIREBASE","Falha ao deletar a imagem: ${exception.message}")
                            }
                        FinalizarEdicao(confirmarAlteracao, idOcorrencia, novaUrl.toString())
                    }
                } else {
                    FinalizarEdicao(confirmarAlteracao, idOcorrencia, downloadUrl)
                }
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

    private var fotoUri: Uri? = null
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { sucesso ->
        if (sucesso && fotoUri != null) {
            lifecycleScope.launch {
                val arquivoComprimido = comprimirImagem(fotoUri!!)
                val uriComprimida = Uri.fromFile(arquivoComprimido)
                binding.imageViewFoto.setImageURI(uriComprimida)

                // Atualiza a variável global para não usar a foto original depois
                fotoUri = uriComprimida
            }
        }
    }

    private suspend fun comprimirImagem(uri: Uri): File = withContext(Dispatchers.IO) {
        val context = requireContext()
        val inputStream = context.contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        // Crop quadrado (1x1) na imagem
        val tamanho = minOf(originalBitmap.width, originalBitmap.height)
        val xOffset = (originalBitmap.width - tamanho) / 2
        val yOffset = (originalBitmap.height - tamanho) / 2
        val bitmapQuadrado = Bitmap.createBitmap(originalBitmap, xOffset, yOffset, tamanho, tamanho)

        // Redimensiona para largura máxima
        val larguraMax = 1080
        val escala = larguraMax.toFloat() / bitmapQuadrado.width.toFloat()
        val novaAltura = (bitmapQuadrado.height * escala).toInt()
        val bitmapReduzido = Bitmap.createScaledBitmap(bitmapQuadrado, larguraMax, novaAltura, true)

        // Salva imagem comprimida
        val arquivoTemp = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(arquivoTemp)

        bitmapReduzido.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        outputStream.flush()
        outputStream.close()

        arquivoTemp
    }

    private suspend fun salvarFotoOcorrencia(uri: Uri, nome: String): String? {
        return try {
            val storage = FirebaseStorage.getInstance().reference
            val caminho = "ocorrencias/${System.currentTimeMillis()}_${nome}.jpg"
            val fotoRef = storage.child(caminho)

            val arquivoComprimido = comprimirImagem(uri)

            fotoRef.putFile(Uri.fromFile(arquivoComprimido)).await()
            downloadUrl = fotoRef.downloadUrl.await().toString()
            fotoRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Erro ao enviar foto: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun FinalizarEdicao(confirmarAlteracao: Boolean?, idOcorrencia: Long, fotoUrl: String?) {
        if (confirmarAlteracao == true){
            val tipoSelecionado = when (binding.rgOcorrenciasEditado.checkedRadioButtonId) {
                R.id.rbSinistro -> "Sinistro de Trânsito"
                R.id.rbGrandeVulto -> "Sinistro de Grande Vulto"
                R.id.rbAtendimento -> "Atendimento ao Cidadão"
                else -> ""
            }

            val novoEndereco = binding.editTextEndereco.text.toString()
            val novoNome = binding.editTextNome.text.toString()
            val novoContato = binding.editTextContato.text.toString()
            Log.d("TESTE", "o ID q ta aparecendo é: ${idOcorrencia}")

            if (idOcorrencia != -1L) {
                val dbHelper = AppDatabaseHelper(requireContext())
                dbHelper.updateOcorrenciaCompleta(
                    id = idOcorrencia,
                    tipo = tipoSelecionado,
                    endereco = novoEndereco,
                    nome = novoNome,
                    numcontato = novoContato,
                    fotoUrl = fotoUrl
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
        if (confirmarAlteracao == false){
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



}
