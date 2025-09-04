package com.otavioaugusto.app_semurb.fragments

import android.app.AlertDialog
import android.content.Context
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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.adapters.AvariasAdapter
import com.otavioaugusto.app_semurb.dataClasses.DataClassAvariaItem
import com.otavioaugusto.app_semurb.databinding.FragmentInspecao3Binding
import com.otavioaugusto.app_semurb.funcoes.AvariaToggleController
import com.otavioaugusto.app_semurb.funcoes.AvariasRecyclerHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.otavioaugusto.app_semurb.funcoes.EnviarNotificacaoBd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.security.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class Inspecao3Fragment : Fragment() {

    private var imagemTempUri: Uri? = null
    private var ultimaPosicaoFoto: Int? = null
    private var data_envio: String? = null
    private lateinit var lista: MutableList<DataClassAvariaItem>
    val bancoDados by lazy {
        FirebaseFirestore.getInstance()
    }


    private lateinit var adapter: AvariasAdapter
    private lateinit var avariasFrenteHelper: AvariasRecyclerHelper
    private lateinit var avariasTraseiraHelper: AvariasRecyclerHelper
    private lateinit var avariasDireitaHelper: AvariasRecyclerHelper
    private lateinit var avariasEsquerdaHelper: AvariasRecyclerHelper
    private lateinit var avariasOutrasHelper: AvariasRecyclerHelper



    private val TirarFoto = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && imagemTempUri !=null && ultimaPosicaoFoto != null){
            when (ladoAtual) {
                "Frente" -> avariasFrenteHelper.atualizarFoto(ultimaPosicaoFoto!!, imagemTempUri!!)
                "Traseira" -> avariasTraseiraHelper.atualizarFoto(ultimaPosicaoFoto!!, imagemTempUri!!)
                "Direita" -> avariasDireitaHelper.atualizarFoto(ultimaPosicaoFoto!!, imagemTempUri!!)
                "Esquerda" -> avariasEsquerdaHelper.atualizarFoto(ultimaPosicaoFoto!!, imagemTempUri!!)
                "Outro" -> avariasOutrasHelper.atualizarFoto(ultimaPosicaoFoto!!, imagemTempUri!!)
            }
        } else {
            Toast.makeText(requireContext(), "Falha ao tirar a foto", Toast.LENGTH_SHORT).show()
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

        // Convertendo formato de data
        val data_envio_exibicao = arguments?.getString("DATA_ENVIO")
        val entrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val saida = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = entrada.parse(data_envio_exibicao)
        data_envio = saida.format(date!!)

        if (!data_envio_exibicao.isNullOrEmpty()) {
            binding.textViewDescricao.setText("${data_envio_exibicao}")
            binding.cbFrente.visibility = View.GONE
            binding.cbOutra.visibility = View.GONE
            binding.cbDireita.visibility = View.GONE
            binding.cbEsquerda.visibility = View.GONE
            binding.cbTraseira.visibility = View.GONE
            binding.btnFinalizar.visibility = View.GONE
        }

        binding.progressBarInspecao3.visibility = View.GONE
        binding.btnFinalizar.visibility = View.VISIBLE

        // Checkboxes para marcar caso uma parte esteja sem avária
        binding.cbFrente.setOnCheckedChangeListener { _, isChecked ->
            binding.btnInspecaoFrente.isEnabled  = !isChecked
            binding.btnInspecaoFrente.isVisible  = !isChecked
            binding.frameAvariaFrente.visibility = View.GONE
            binding.btnInfoFrente.isVisible = !isChecked
            binding.textViewFrente.setBackgroundResource(R.drawable.bg_btninspecaodesativado)
            binding.btnInspecaoFrente.setImageResource(R.drawable.inspecaofechar)

        }
        binding.cbTraseira.setOnCheckedChangeListener { _, isChecked ->
            binding.btnInspecaoTraseira.isEnabled = !isChecked
            binding.btnInspecaoTraseira.isVisible = !isChecked
            binding.frameAvariaTraseira.visibility = View.GONE
            binding.btnInfoTraseira.isVisible = !isChecked
            binding.textViewTraseira.setBackgroundResource(R.drawable.bg_btninspecaodesativado)
            binding.btnInspecaoTraseira.setImageResource(R.drawable.inspecaofechar)
        }
        binding.cbDireita.setOnCheckedChangeListener { _, isChecked ->
            binding.btnInspecaoDireita.isEnabled  = !isChecked
            binding.btnInspecaoDireita.isVisible  = !isChecked
            binding.frameAvariaDireita.visibility = View.GONE
            binding.btnInfoDireita.isVisible = !isChecked
            binding.textViewDireita.setBackgroundResource(R.drawable.bg_btninspecaodesativado)
            binding.btnInspecaoDireita.setImageResource(R.drawable.inspecaofechar)
        }
        binding.cbEsquerda.setOnCheckedChangeListener { _, isChecked ->
            binding.btnInspecaoEsquerda.isEnabled  = !isChecked
            binding.btnInspecaoEsquerda.isVisible = !isChecked
            binding.frameAvariaEsquerda.visibility = View.GONE
            binding.btnInfoEsquerda.isVisible = !isChecked
            binding.textViewEsquerda.setBackgroundResource(R.drawable.bg_btninspecaodesativado)
            binding.btnInspecaoEsquerda.setImageResource(R.drawable.inspecaofechar)
        }
        binding.cbOutra.setOnCheckedChangeListener { _, isChecked ->
            binding.btnInspecaoOutras.isEnabled  = !isChecked
            binding.btnInspecaoOutras.isVisible  = !isChecked
            binding.frameAvariaOutras.visibility = View.GONE
            binding.btnInfoOutros.isVisible = !isChecked
            binding.textViewOutras.setBackgroundResource(R.drawable.bg_btninspecaodesativado)
            binding.btnInspecaoOutras.setImageResource(R.drawable.inspecaofechar)
        }




        // Botões de informação
        binding.btnInfoFrente.setOnClickListener {
            mostrarAlertDialogAvaria(requireContext(),"Inclui partes como: ", "Capo, Farol esquerdo/direito, Pisca esquerdo/direito, Para-brisa, Para-choque, Grade frontal, etc." )
        }

        binding.btnInfoTraseira.setOnClickListener {
            mostrarAlertDialogAvaria(requireContext(),"Inclui partes como: ", "Para-Choque traseiro, Farol esquerdo/direito, Pisca esquerdo/direito, Vidro traseiro, Luz de ré, Escapamento, etc," )
        }
        binding.btnInfoDireita.setOnClickListener {
            mostrarAlertDialogAvaria(requireContext(),"Inclui partes como: ", "Para-choque lateral direito, porta dianteira direita, porta traseira direita, retrovisor direito, vidros laterais direitos, maçanetas, Pneus direitos etc." )
        }
        binding.btnInfoEsquerda.setOnClickListener {
            mostrarAlertDialogAvaria(requireContext(),"Inclui partes como: ", "Para-choque lateral esquerda, porta dianteira esquerda, porta traseira esquerda, retrovisor esquerdo, vidros laterais esquerda, maçanetas, Pneus esquerdos etc" )
        }
        binding.btnInfoOutros.setOnClickListener {
            mostrarAlertDialogAvaria(requireContext(),"Inclui partes como: ", "Teto, Chassi, Motor, Suspensão, Freios, etc." )
        }
        val idViatura = "12345"
        binding.textViewInspecaoViatura.text = "Inspeção da viatura $idViatura"

        return binding.root




    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual, "continuar")
        }

        if (!data_envio.isNullOrEmpty()) {
            CarregarAvariasHistorico()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(data_envio.isNullOrEmpty()){
                    if (etapaAtual > 0) {
                        (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(
                            etapaAtual - 1,
                            "voltar"
                        )
                    }

                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                        )
                        .replace(R.id.FragmentContainerView2, Inspecao2Fragment())
                        .commit()
                } else {
                    requireActivity().finish()
                }

            }
        })

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
            if(data_envio.isNullOrEmpty()){
                if (etapaAtual > 0) {
                    (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(
                        etapaAtual - 1,
                        "voltar"
                    )
                }

                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )
                    .replace(R.id.FragmentContainerView2, Inspecao2Fragment())
                    .commit()
            } else {
                requireActivity().finish()
            }
        }

        binding.btnFinalizar.setOnClickListener {
            val horarioAtual = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val dataAtual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            lifecycleScope.launch{
                withContext(Dispatchers.IO){
                    val idViatura = "12345"
                    val partesComErro = mutableListOf<String>()

                    val frenteAvarias = avariasFrenteHelper.getAvarias()
                    val frenteVazia = frenteAvarias.all { it.descricao.isBlank() && it.uriFoto == null }
                    val traseiraAvarias = avariasTraseiraHelper.getAvarias()
                    val traseiraVazia = traseiraAvarias.all { it.descricao.isBlank() && it.uriFoto == null }
                    val direitaAvarias = avariasDireitaHelper.getAvarias()
                    val direitaVazia = direitaAvarias.all { it.descricao.isBlank() && it.uriFoto == null }
                    val esquerdaAvarias = avariasEsquerdaHelper.getAvarias()
                    val esquerdaVazia = esquerdaAvarias.all { it.descricao.isBlank() && it.uriFoto == null }
                    val outrasAvarias = avariasOutrasHelper.getAvarias()
                    val outrasVazia = outrasAvarias.all { it.descricao.isBlank() && it.uriFoto == null }


                    if (frenteVazia && !binding.cbFrente.isChecked) partesComErro.add("Frente")
                    if (traseiraVazia && !binding.cbTraseira.isChecked) partesComErro.add("Traseira")
                    if (direitaVazia && !binding.cbDireita.isChecked) partesComErro.add("Lateral Direita")
                    if (esquerdaVazia && !binding.cbEsquerda.isChecked) partesComErro.add("Lateral Esquerda")
                    if (outrasVazia && !binding.cbOutra.isChecked) partesComErro.add("Outra Avária")

                    if (partesComErro.isNotEmpty()) {
                        withContext(Dispatchers.Main) {

                            val builder = AlertDialog.Builder(requireContext())
                                .setTitle(SpannableString("Verifique as informações").apply { setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.CinzaMedio)), 0, length, 0) })
                                .setMessage(SpannableString("As seguintes partes estão incompletas:\n" + "\n" + "• ${partesComErro.joinToString("\n• ")}").apply { setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.CinzaMedio)), 0, length, 0) })
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
                    } else {

                    EnviarNotificacaoBd().notificacaoOcorrencia("Notificação de inspeção", "Inspeção realizada com sucesso", dataAtual, horarioAtual,)
                    salvarInspecaoComFotos(idViatura)
                        withContext(Dispatchers.Main) {
                            (activity as? PlaceHolderGameficadoActivity)?.concluirEtapaFinal(etapaAtual)
                            delay(500)
                            requireActivity().finish()
                        }
                    }
                }
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

    private var ladoAtual: String? = null

    private fun setupRecyclers() {
        val context = requireContext()

        val callBackFotoFrente = { posicao: Int ->
            ladoAtual = "Frente"
            imagemTempUri = criarUriImagem()
            ultimaPosicaoFoto = posicao
            TirarFoto.launch(imagemTempUri)
        }
        val callBackFotoTraseira = { posicao: Int ->
            ladoAtual = "Traseira"
            imagemTempUri = criarUriImagem()
            ultimaPosicaoFoto = posicao
            TirarFoto.launch(imagemTempUri)
        }
        val callBackFotoDireita = { posicao: Int ->
            ladoAtual = "Direita"
            imagemTempUri = criarUriImagem()
            ultimaPosicaoFoto = posicao
            TirarFoto.launch(imagemTempUri)
        }
        val callBackFotoEsquerda = { posicao: Int ->
            ladoAtual = "Esquerda"
            imagemTempUri = criarUriImagem()
            ultimaPosicaoFoto = posicao
            TirarFoto.launch(imagemTempUri)
        }
        val callBackFotoOutro = { posicao: Int ->
            ladoAtual = "Outro"
            imagemTempUri = criarUriImagem()
            ultimaPosicaoFoto = posicao
            TirarFoto.launch(imagemTempUri)
        }


        avariasFrenteHelper = AvariasRecyclerHelper(context, binding.rvFrente, callBackFotoFrente)
        avariasTraseiraHelper = AvariasRecyclerHelper(context, binding.rvTraseira, callBackFotoTraseira)
        avariasDireitaHelper = AvariasRecyclerHelper(context, binding.rvDireita, callBackFotoDireita)
        avariasEsquerdaHelper = AvariasRecyclerHelper(context, binding.rvEsquerda, callBackFotoEsquerda)
        avariasOutrasHelper = AvariasRecyclerHelper(context, binding.rvOutras, callBackFotoOutro)
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
            dialog.window?.setBackgroundDrawableResource(R.color.AzulPetroleo)
        }

        dialog.show()
    }

    private fun CarregarAvariasHistorico() {
        val idViatura = "12345" // ou o id real que você usa
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                bancoDados.collection("veiculos")
                    .document(idViatura)
                    .collection("inspecoes")
                    .document(data_envio!!)  // <- data no formato yyyy-MM-dd
                    .get()
                    .addOnSuccessListener { doc ->
                        if (doc.exists()) {
                            val frente = (doc["frente"] as? List<Map<String, Any>>)?.map {
                                DataClassAvariaItem(
                                    descricao = it["descricao"] as? String ?: "",
                                    uriFoto = (it["uriFoto"] as? String)?.let { url -> Uri.parse(url) }
                                )
                            } ?: emptyList()

                            val traseira = (doc["traseira"] as? List<Map<String, Any>>)?.map {
                                DataClassAvariaItem(
                                    descricao = it["descricao"] as? String ?: "",
                                    uriFoto = (it["uriFoto"] as? String)?.let { url -> Uri.parse(url) }
                                )
                            } ?: emptyList()

                            val direita = (doc["direita"] as? List<Map<String, Any>>)?.map {
                                DataClassAvariaItem(
                                    descricao = it["descricao"] as? String ?: "",
                                    uriFoto = (it["uriFoto"] as? String)?.let { url -> Uri.parse(url) }
                                )
                            } ?: emptyList()

                            val esquerda = (doc["esquerda"] as? List<Map<String, Any>>)?.map {
                                DataClassAvariaItem(
                                    descricao = it["descricao"] as? String ?: "",
                                    uriFoto = (it["uriFoto"] as? String)?.let { url -> Uri.parse(url) }
                                )
                            } ?: emptyList()

                            val outras = (doc["outras"] as? List<Map<String, Any>>)?.map {
                                DataClassAvariaItem(
                                    descricao = it["descricao"] as? String ?: "",
                                    uriFoto = (it["uriFoto"] as? String)?.let { url -> Uri.parse(url) }
                                )
                            } ?: emptyList()

                                // Passa as listas pros helpers
                                avariasFrenteHelper.setAvarias(frente.toMutableList())
                                avariasTraseiraHelper.setAvarias(traseira.toMutableList())
                                avariasDireitaHelper.setAvarias(direita.toMutableList())
                                avariasEsquerdaHelper.setAvarias(esquerda.toMutableList())
                                avariasOutrasHelper.setAvarias(outras.toMutableList())

                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Erro ao carregar inspeção",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    private suspend fun uploadFotosInspecao(
        parte: String,
        lista: List<DataClassAvariaItem>,
        idVeiculo: String,
        dataHoje: String
    ): List<Map<String, String>> = withContext(Dispatchers.IO) {
        val storage = FirebaseStorage.getInstance().reference
        val avariasComLinks = mutableListOf<Map<String, String>>()

        for ((index, item) in lista.withIndex()) {
            val uriFoto = item.uriFoto ?: continue

            val caminho = "inspecoes/$idVeiculo/$dataHoje/$parte/avaria_$index.jpg"
            val fotoRef = storage.child(caminho)

            try {
                val uploadTask = fotoRef.putFile(uriFoto).await()
                val downloadUrl = fotoRef.downloadUrl.await()

                avariasComLinks.add(
                    mapOf(
                        "descricao" to item.descricao,
                        "uriFoto" to downloadUrl.toString()
                    )
                )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Erro ao enviar foto: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return@withContext avariasComLinks
    }

    private suspend fun salvarInspecaoComFotos(idVeiculo: String) = withContext(Dispatchers.IO) {

        withContext(Dispatchers.Main){
            binding.progressBarInspecao3.visibility = View.VISIBLE
            binding.btnFinalizar.visibility = View.GONE
        }


        val frenteAvarias = avariasFrenteHelper.getAvarias()
        val traseiraAvarias = avariasTraseiraHelper.getAvarias()
        val direitaAvarias = avariasDireitaHelper.getAvarias()
        val esquerdaAvarias = avariasEsquerdaHelper.getAvarias()
        val outrasAvarias = avariasOutrasHelper.getAvarias()

        val checkBoxFrente = binding.cbFrente.isChecked
        val checkBoxTraseira = binding.cbTraseira.isChecked
        val checkBoxDireita = binding.cbDireita.isChecked
        val checkBoxEsquerda = binding.cbEsquerda.isChecked
        val checkBoxOutra = binding.cbOutra.isChecked


        val dataHoje = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())


        val dadosInspecao = hashMapOf<String, Any>(
            "dataRegistro" to com.google.firebase.Timestamp.now()
        )

        val frenteResultado = if (!checkBoxFrente) {
            uploadFotosInspecao("frente", frenteAvarias, idVeiculo, dataHoje)
        } else {
            listOf(mapOf("info" to "Parte sem avaria"))
        }
        dadosInspecao["frente"] = frenteResultado

        val traseiraResultado = if (!checkBoxTraseira) {
            uploadFotosInspecao("traseira", traseiraAvarias, idVeiculo, dataHoje)
        } else {
            listOf(mapOf("info" to "Parte sem avaria"))
        }
        dadosInspecao["traseira"] = traseiraResultado

        val direitaResultado = if (!checkBoxDireita) {
            uploadFotosInspecao("direita", direitaAvarias, idVeiculo, dataHoje)
        } else {
            listOf(mapOf("info" to "Parte sem avaria"))
        }
        dadosInspecao["direita"] = direitaResultado

        val esquerdaResultado = if (!checkBoxEsquerda) {
            uploadFotosInspecao("esquerda", esquerdaAvarias, idVeiculo, dataHoje)
        } else {
            listOf(mapOf("info" to "Parte sem avaria"))
        }
        dadosInspecao["esquerda"] = esquerdaResultado

        val outrasResultado = if (!checkBoxOutra) {
            uploadFotosInspecao("outras", outrasAvarias, idVeiculo, dataHoje)
        } else {
            listOf(mapOf("info" to "Parte sem avaria"))
        }
        dadosInspecao["outras"] = outrasResultado

        try {
            bancoDados.collection("veiculos")
                .document(idVeiculo)
                .collection("inspecoes")
                .document(dataHoje)
                .set(dadosInspecao)
                .await()

            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Inspeção realizada com sucesso", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Erro ao salvar inspeção: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
