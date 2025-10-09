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

class HomeFragment : Fragment() {


    val autenticacao by lazy {
        FirebaseAuth.getInstance()
    }
    val bancoDados by lazy {
        FirebaseFirestore.getInstance()
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val idUsuarioLogado = autenticacao.currentUser?.uid
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

        binding.btnIniciarTurno.setOnClickListener {
            val intent = Intent(requireContext(), PlaceHolderGameficadoActivity::class.java)
            intent.putExtra("FRAGMENT_KEY", "VERIFICAR_TURNO")
            intent.putExtra("VISIBILITY", "GONE")
            startActivity(intent)
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
                    val fotoUrl = (dados["foto_agnt"] as? String)?.replace("\"", "")

                    binding.textViewNomeHome.text = nome.toString()

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
    fun desativarFuncoes(dados: Map<String?, Any?>?){


        val funcao = dados?.get("funcao")

        if (funcao == "motorista"){
            binding.btnOcorrenciaHome.setImageResource(R.drawable.btnocorrencia50)
            binding.btnOcorrenciaHome.setOnClickListener {
                val titulo = SpannableString("Você não tem acesso a essa função").apply {
                    setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.CinzaMedio)),
                        0, length, 0
                    )
                }

                val mensagem = SpannableString("Peça ao auxiliar realizá-la").apply {
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

            binding.btnViarioHome.setImageResource(R.drawable.btnviario50)
            binding.btnViarioHome.setOnClickListener {
                val titulo = SpannableString("Você não tem acesso a essa função").apply {
                    setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.CinzaMedio)),
                        0, length, 0
                    )
                }

                val mensagem = SpannableString("Peça ao auxiliar realizá-la").apply {
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
        if (funcao == "auxiliar"){
            binding.btnInspecaoHome.isEnabled = false
        }
        if (funcao == "adm"){
            binding.btnViarioHome.isEnabled = true
            binding.btnOcorrenciaHome.isEnabled = true
            binding.btnInspecaoHome.isEnabled = true
        }
    }
}




