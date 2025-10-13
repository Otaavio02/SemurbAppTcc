package com.otavioaugusto.app_semurb.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.otavioaugusto.app_semurb.*
import com.otavioaugusto.app_semurb.databinding.FragmentHomeBinding
import com.otavioaugusto.app_semurb.funcoes.ArredondarFoto
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

class HomeFragment : Fragment() {


    val autenticacao by lazy {
        FirebaseAuth.getInstance()
    }
    val bancoDados by lazy {
        FirebaseFirestore.getInstance()
    }

    lateinit var idUsuario: String

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val idUsuarioLogado = autenticacao.currentUser?.uid
        idUsuario = autenticacao.currentUser?.uid.toString()
        lifecycleScope.launch {
                if (idUsuarioLogado!=null){
                    val documento = withContext(Dispatchers.IO) {
                        bancoDados.collection("agentes")
                            .document(idUsuarioLogado)
                            .get()
                            .await()
                    }
                    val dados = documento.data
                    withContext(Dispatchers.Main){
                        desativarFuncoes(dados)
                    }
                   }
            }

        binding.btnViarioHome.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                .replace(R.id.fragmentContainerView, ViarioFragment())
                .addToBackStack(null)
                .commit()

            (activity as? PlaceHolderActivity)?.limparBottomNavBar()
        }

        binding.btnInspecaoHome.setOnClickListener {

            val intent = Intent(requireContext(), PlaceHolderGameficadoActivity::class.java)
            intent.putExtra("FRAGMENT_KEY", "INICIAR_INSPECAO")
            //intent.putExtra("idUsuario", idUsuario)
            startActivity(intent)


        }

        binding.btnHistoricoHome.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                )
                .replace(R.id.fragmentContainerView, HistoricoFragment())
                .addToBackStack(null)
                .commit()

            (activity as? PlaceHolderActivity)?.limparBottomNavBar()
        }

        binding.btnOcorrenciaHome.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                )
                .replace(R.id.fragmentContainerView, OcorrenciasFragment())
                .addToBackStack(null)
                .commit()

            (activity as? PlaceHolderActivity)?.limparBottomNavBar()
        }





        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        val idUsuarioLogado = autenticacao.currentUser?.uid ?: return

        bancoDados.collection("agentes")
            .document(idUsuarioLogado)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (!isAdded || _binding == null) return@addOnSuccessListener

                Log.i("FIREBASETESTE", "Dados Puxados com Sucesso")
                val dados = documentSnapshot.data
                if (dados != null) {
                    val nome = dados["nome"]
                    val funcao: String = dados["funcao"] as String
                    val funcaoCapitalizada = funcao.titlecase()
                    val fotoUrl = (dados["foto_agnt"] as? String)?.replace("\"", "")

                    binding.textViewNomeHome.text = nome.toString()
                    binding.textViewFuncao?.text = funcaoCapitalizada

                    if (!fotoUrl.isNullOrEmpty()) {
                        Picasso.get()
                            .load(fotoUrl)
                            .transform(ArredondarFoto())
                            .fit()
                            .centerCrop()
                            .placeholder(R.drawable.home_foto)  // opcional: imagem placeholder enquanto carrega
                            .into(binding.imagaViewFotoHome)
                    } else {
                        Log.w("HomeFragment", "URL da foto está vazia ou nula")

                        binding.imagaViewFotoHome.setImageResource(R.drawable.home_foto)
                    }
                }
            }
            .addOnFailureListener {
                Log.i("FIREBASETESTE", "Erro ao puxar dados")
            }
    }
    fun desativarFuncoes(dados: Map<String?, Any?>?) {
        val funcao = dados?.get("funcao").toString().trim().lowercase()


        when (funcao) {
            "motorista" -> {
                bloquearBotao(binding.btnOcorrenciaHome, "Peça ao encarregado para realizá-la")
                bloquearBotao(binding.btnViarioHome, "Peça ao encarregado para realizá-la")
            }
            "auxiliar" -> {
                bloquearBotao(binding.btnOcorrenciaHome, "Peça ao encarregado para realizá-la")
                bloquearBotao(binding.btnInspecaoHome, "Peça ao motorista para realizá-la")
                bloquearBotao(binding.btnViarioHome, "Peça ao encarregado para realizá-la")
            }
            "encarregado" -> {
                bloquearBotao(binding.btnInspecaoHome, "Peça ao motorista para realizá-la")
            }
            "adm" -> {
                binding.btnViarioHome.isEnabled = true
                binding.btnOcorrenciaHome.isEnabled = true
                binding.btnInspecaoHome.isEnabled = true
            }
        }
    }

    private fun bloquearBotao(botao: View, mensagem: String) {
        botao.alpha = 0.5f
        botao.setOnClickListener {
            val titulo = SpannableString("Você não tem acesso a essa função").apply {
                setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.CinzaMedio)), 0, length, 0)
            }

            val msg = SpannableString(mensagem).apply {
                setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.CinzaMedio)), 0, length, 0)
            }

            val dialog = AlertDialog.Builder(requireContext())
                .setTitle(titulo)
                .setMessage(msg)
                .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                .create()

            dialog.setOnShowListener {
                dialog.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.Branco)))
            }
            dialog.show()
        }
    }

    fun String.titlecase(): String =
        this.replaceFirstChar { // it: Char
            if (it.isLowerCase())
                it.titlecase(Locale.getDefault())
            else
                it.toString()
        }
}





