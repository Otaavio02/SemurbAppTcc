package com.otavioaugusto.app_semurb.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.databinding.FragmentViario2Binding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class Viario2Fragment : Fragment() {

    private var _binding: FragmentViario2Binding? = null
    private val binding get() = _binding!!

    private var etapaAtual = 1 // etapa 2
    private var totalEtapas = 3
    private var tipo: String? = null
    private var endereco: String? = null
    private var descricao: String? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var localizacaoSolicitacao: LocationRequest
    private lateinit var localizacaoCallBack: LocationCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViario2Binding.inflate(inflater, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        localizacaoSolicitacao = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()

        binding.textViewPreencherViario2.setOnClickListener {

            binding.progressBar2 .visibility = View.VISIBLE
            binding.btnProximoViario2.isEnabled = false

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            localizacaoCallBack = object: LocationCallback(){

                override fun onLocationResult(locationResult: LocationResult) {

                    val location = locationResult.lastLocation
                    if (location != null){
                        lifecycleScope.launch {

                            val enderecoTexto = withContext(Dispatchers.IO){
                                try{
                                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                                    if (!addresses.isNullOrEmpty()){
                                        val address = addresses[0]
                                        val rua = address.thoroughfare ?: "Rua Não identificada"
                                        val numero = address.subThoroughfare ?: "s/n"
                                        "$rua, $numero"
                                    } else {
                                        "Endereço não encontrado"
                                    }
                                }catch (e: Exception){
                                    "Erro ao obter endereço"
                                }finally {

                                    withContext(Dispatchers.Main){
                                        binding.progressBar2.visibility = View.GONE
                                        binding.btnProximoViario2.isEnabled = true
                                    }
                                    }
                            }
                            binding.EditTextEnderecoViario.setText(enderecoTexto)

                            fusedLocationClient.removeLocationUpdates { localizacaoCallBack }
                        }

                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(localizacaoSolicitacao, localizacaoCallBack, Looper.getMainLooper())
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1010)
        }
        }

        tipo = arguments?.getString("tipo")
        endereco = arguments?.getString("endereco")
        descricao = arguments?.getString("descricao")

        if (endereco != null) {
            binding.EditTextEnderecoViario.setText(endereco)
        }

        binding.btnVoltarViario2.setOnClickListener {
            endereco = binding.EditTextEnderecoViario.text.toString()

            if (etapaAtual > 0) {
                (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual - 1, "voltar")
            }

            val fragmentTipo = Viario1Fragment().apply {
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
                .replace(R.id.FragmentContainerView2, fragmentTipo)
                .addToBackStack(null)
                .commit()
        }

        binding.btnProximoViario2.setOnClickListener {
            endereco = binding.EditTextEnderecoViario.text.toString()


            if (endereco == "") {

                val titulo = SpannableString("Campo incompleto").apply {
                    setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.CinzaMedio)),
                        0, length, 0
                    )
                }

                val mensagem = SpannableString("Para Avançar, digite um endereço.").apply {
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


            val fragmentDescricao = Viario3Fragment().apply {
                arguments = Bundle().apply {
                    putString("tipo", tipo)
                    putString("endereco", endereco)
                    putString("descricao", descricao)
                }
            }

            if (etapaAtual < totalEtapas - 1) {
                (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual + 1, "continuar")
            }

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.FragmentContainerView2, fragmentDescricao)
                .addToBackStack(null)
                .commit()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1010 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            parentFragmentManager.beginTransaction().detach(this).attach(this).commit()
        }else
            Toast.makeText(requireContext(), "Permissão de localização negada!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (::localizacaoCallBack.isInitialized) {
            fusedLocationClient.removeLocationUpdates(localizacaoCallBack)
        }
    }
}
