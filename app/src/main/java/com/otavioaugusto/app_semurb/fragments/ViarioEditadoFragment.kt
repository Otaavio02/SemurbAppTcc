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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.databinding.FragmentViarioeditadoBinding
import com.otavioaugusto.app_semurb.dbHelper.AppDatabaseHelper
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ViarioEditadoFragment : Fragment() {

    private var _binding: FragmentViarioeditadoBinding? = null
    private val binding get() = _binding!!

    private var idViario: Long = 0
    private var tipo: String? = null
    private var endereco: String? = null
    private var descricao: String? = null
    private var numSequencial: String? = null
    private var data_envio: String? = null
    private var downloadUrl: String? = null
    private var fotoUri: Uri? = null
    private lateinit var tipoListGlobal: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViarioeditadoBinding.inflate(inflater, container, false)

        binding.btnVoltarViarioEditado.setOnClickListener {
            requireActivity().finish()
        }

        idViario = activity?.intent?.getLongExtra("ID_VIARIO", -1L) ?: -1L
        tipo = activity?.intent?.getStringExtra("TIPO")
        endereco = activity?.intent?.getStringExtra("ENDERECO")
        descricao = activity?.intent?.getStringExtra("DESCRICAO")
        data_envio = activity?.intent?.getStringExtra("DATA_ENVIO")
        numSequencial = activity?.intent?.getStringExtra("NUMERO_SEQUENCIAL")
        var old_foto_url = activity?.intent?.getStringExtra("FOTO_URL")
        var historicoChecker = activity?.intent?.getBooleanExtra("HISTORICO", false)
        downloadUrl = old_foto_url

        Log.d("TESTE", "DATA ENVIO: ${data_envio}")

        if (historicoChecker == true) {
            binding.textViewViarioID.text = "${data_envio}"
            binding.btnExcluirViario.visibility = View.GONE
            binding.btnFinalizarViarioEdicao.visibility = View.GONE
            binding.editTextDescricao.isFocusable = false
            binding.editTextEndereco.isFocusable = false
            binding.autoCompleteTipoOcorrencia?.isFocusable = false
            binding.autoCompleteTipoOcorrencia?.setCompoundDrawables(null,null,null,null)
            binding.autoCompleteTipoOcorrencia?.setText(tipo)
            binding.inputLayoutTipo?.hint = ""

            if (!old_foto_url.isNullOrEmpty()) {
                Picasso.get()
                    .load(old_foto_url)
                    .fit()
                    .centerInside()
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
                binding.imageViewFoto.isEnabled = false
                binding.textViewFoto.setText("Nenhuma imagem foi registrada.")
            }
        } else {
            val db = FirebaseFirestore.getInstance()
            val tiposList = mutableListOf<String>()
            val autoComplete = binding.autoCompleteTipoOcorrencia

            binding.autoCompleteTipoOcorrencia?.setText(tipo)

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                tiposList
            )
            autoComplete?.setAdapter(adapter)

            // Permite abrir a lista sem digitar
            autoComplete?.threshold = 0

            // Forçar abrir dropdown ao clicar
            autoComplete?.setOnTouchListener { _, _ ->
                if (tiposList.isNotEmpty()) {
                    autoComplete.showDropDown()
                }
                false
            }

            // Busca tipos de ocorrência no Firestore
            db.collection("tipos_servico").get().addOnSuccessListener { documents ->
                tiposList.clear()
                for (doc in documents) {
                    doc.getString("tipo")?.let { tiposList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }
            tipoListGlobal = tiposList

            // Abrir dropdown ao digitar
            autoComplete?.addTextChangedListener {
                if (autoComplete.text.isNotEmpty()) {
                    autoComplete.showDropDown()
                }
            }

            // Captura item selecionado
            autoComplete?.setOnItemClickListener { _, _, position, _ ->
                tipo = tiposList[position]
            }
        }

        binding.editTextEndereco.setText(endereco)
        binding.editTextDescricao.setText(descricao)
        binding.textViewViarioID.text = "Sinalização $numSequencial"
        if (!old_foto_url.isNullOrEmpty()) {
            Picasso.get()
                .load(old_foto_url)
                .fit()
                .centerInside()
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

        binding.imageViewFoto.setOnClickListener {
            if (!data_envio.isNullOrEmpty()) {
                val dialogView = ImageView(context).apply {
                    Picasso.get()
                        .load(old_foto_url)  // Carrega a URL diretamente do Firebase
                        .into(this)  // Carrega a imagem no ImageView
                    adjustViewBounds = true
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }

                AlertDialog.Builder(context)
                    .setView(dialogView)
                    .setPositiveButton("Fechar", null)
                    .show()
            } else {
                    val arquivoFoto =
                        File(requireContext().cacheDir, "viario_${System.currentTimeMillis()}.jpg")
                    fotoUri = FileProvider.getUriForFile(
                        requireContext(),
                        "${requireContext().packageName}.provider",
                        arquivoFoto
                    )
                    cameraLauncher.launch(fotoUri)
            }
        }

        binding.btnFinalizarViarioEdicao.setOnClickListener {
            lifecycleScope.launch {
                Log.d("TESTE", "OLD FOTO URL É: ${old_foto_url}")
                Log.d("TESTE", "DOWNLOAD URL É: ${downloadUrl}")
                val novaUrl = if (fotoUri != null) salvarFotoOcorrencia(fotoUri!!, endereco!!) else null
                Log.d("TESTE", "NOVA URL É: ${novaUrl}")
                if (!old_foto_url.isNullOrEmpty() && novaUrl != null && old_foto_url != novaUrl) {
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
                    FinalizarEdicao(idViario, novaUrl.toString())
                } else {
                    FinalizarEdicao(idViario, downloadUrl)
                }
            }
        }

        binding.btnExcluirViario.setOnClickListener {
            if (idViario != -1L) {
                val dbHelper = AppDatabaseHelper(requireContext())
                dbHelper.deleteViario(idViario)

                if (!old_foto_url.isNullOrEmpty()) {
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
                }

                Toast.makeText(requireContext(), "Sinalização excluída com sucesso!", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "ID da sinalização inválido!", Toast.LENGTH_SHORT).show()
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


    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { sucesso ->
        if (sucesso && fotoUri != null) {
            lifecycleScope.launch {
                val arquivoComprimido = comprimirImagem(fotoUri!!)
                val uriComprimida = Uri.fromFile(arquivoComprimido)
                binding.imageViewFoto.setImageURI(uriComprimida)
                binding.textViewFoto.visibility = View.GONE

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

    private suspend fun salvarFotoOcorrencia(uri: Uri, endereco: String): String? {
        return try {
            val storage = FirebaseStorage.getInstance().reference
            val caminho = "viario/${System.currentTimeMillis()}_${endereco}.jpg"
            val fotoRef = storage.child(caminho)

            val arquivoComprimido = comprimirImagem(uri)

            fotoRef.putFile(arquivoComprimido.toUri()).await()
            downloadUrl = fotoRef.downloadUrl.await().toString()
            fotoRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Erro ao enviar foto: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun FinalizarEdicao(idViario: Long, fotoUrl: String?) {
            tipo = binding.autoCompleteTipoOcorrencia?.text.toString().trim()

            if (tipo.isNullOrEmpty()) {
                mostrarAlerta("Campo incompleto", "Para avançar, selecione um tipo de ocorrência")
                return
            }

            if (!tipoListGlobal.contains(tipo)) {
                mostrarAlerta(
                    "Tipo inválido",
                    "O tipo de ocorrência informado não existe. Selecione um tipo válido da lista."
                )
                return
            }

            val novoEndereco = binding.editTextEndereco.text.toString()
            val novaDescricao = binding.editTextDescricao.text.toString()
            Log.d("TESTE", "o ID q ta aparecendo é: ${idViario}")

            if (idViario != -1L) {
                val dbHelper = AppDatabaseHelper(requireContext())
                dbHelper.updateViarioCompleto(
                    id = idViario,
                    tipo = tipo.toString(),
                    endereco = novoEndereco,
                    descricao = novaDescricao,
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
}
