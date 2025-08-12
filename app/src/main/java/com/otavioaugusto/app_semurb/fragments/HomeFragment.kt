package com.otavioaugusto.app_semurb.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.otavioaugusto.app_semurb.*
import com.otavioaugusto.app_semurb.databinding.FragmentHomeBinding
import com.otavioaugusto.app_semurb.funcoes.ArredondarFoto
import com.squareup.picasso.Picasso

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


}


