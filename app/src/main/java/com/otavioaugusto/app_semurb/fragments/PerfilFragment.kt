package com.otavioaugusto.app_semurb.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.databinding.FragmentPerfilBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private val autenticacao by lazy { FirebaseAuth.getInstance() }
    private val bancoDados by lazy { FirebaseFirestore.getInstance() }

    // Variável para guardar os dados localmente após o primeiro fetch
    private var dadosCache: Map<String, Any>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // Se já tem dados no cache, só atualiza a UI sem refazer a consulta
        dadosCache?.let {
            atualizarUI(it)
            return
        }

        val idUsuarioLogado = autenticacao.currentUser?.uid ?: return

        lifecycleScope.launch {
            try {
                val documento = withContext(Dispatchers.IO) {
                    bancoDados.collection("agentes")
                        .document(idUsuarioLogado)
                        .get()
                        .await()
                }

                val dados = documento.data
                if (dados != null) {
                    dadosCache = dados // guarda no cache

                    if (!isAdded || _binding == null) return@launch

                    atualizarUI(dados)
                } else {
                    Toast.makeText(requireContext(), "Dados do usuário não encontrados", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("PerfilFragment", "Erro ao carregar dados: ${e.message}", e)
                Toast.makeText(requireContext(), "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun atualizarUI(dados: Map<String, Any>) {
        val nome = dados["nome"]
        val matricula = dados["matricula"]
        val viario = dados["viario"]
        val ocorrencias = dados["ocorrencias"]
        val fotoUrl = (dados["foto_agnt"] as? String)?.replace("\"", "")

        binding.textViewNome.text = nome.toString()
        binding.textViewMatricula.text = matricula.toString()
        binding.textViewServicos.text = viario.toString()
        binding.textViewOcorrencias.text = ocorrencias.toString()

        if (!fotoUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(fotoUrl)
                .fit()
                .centerInside()
                .placeholder(R.drawable.perfil_foto)
                .into(binding.imageViewFoto, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        Log.i("Picasso", "Imagem carregada com sucesso")
                    }

                    override fun onError(e: Exception?) {
                        Toast.makeText(requireContext(), "Erro ao carregar imagem", Toast.LENGTH_SHORT).show()
                        Log.e("Picasso", "Erro no carregamento da imagem", e)
                    }
                })
        } else {
            Toast.makeText(requireContext(), "URL da foto está vazia ou nula", Toast.LENGTH_SHORT).show()
            Log.w("PerfilFragment", "URL da foto está vazia ou nula")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
