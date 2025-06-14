package com.otavioaugusto.app_semurb

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.databinding.ActivityPlaceholderGameficadoBinding
import com.otavioaugusto.app_semurb.fragments.HomeFragment
import com.otavioaugusto.app_semurb.fragments.Inspecao1Fragment
import com.otavioaugusto.app_semurb.fragments.Ocorrencias2Fragment
import com.otavioaugusto.app_semurb.fragments.Ocorrencias3Fragment
import com.otavioaugusto.app_semurb.fragments.OcorrenciasEditadoFragment
import com.otavioaugusto.app_semurb.fragments.VerificarTurnoFragment
import com.otavioaugusto.app_semurb.fragments.Viario2Fragment
import com.otavioaugusto.app_semurb.fragments.Viario3Fragment
import com.otavioaugusto.app_semurb.fragments.ViarioEditadoFragment


class PlaceHolderGameficadoActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityPlaceholderGameficadoBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)


        val fragmentKey = intent.getStringExtra("FRAGMENT_KEY")

        val fragmentToShow: Fragment = when (fragmentKey) {
            "INICIAR_TURNO" -> VerificarTurnoFragment()
            "INICIAR_INSPECAO" -> Inspecao1Fragment()
            "INICIAR_OCORRENCIAS" -> Ocorrencias2Fragment()
            "INICIAR_VIARIO" -> Viario2Fragment()
            "VIARIO-EDITADO" -> ViarioEditadoFragment()
            "OCORRENCIAS_EDITADO" -> OcorrenciasEditadoFragment()
            "INICIAR_VIARIO2" -> Viario3Fragment()
            "INICIAR_OCORRENCIAS2" -> Ocorrencias3Fragment()


            else -> HomeFragment() //
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainerView2, fragmentToShow)
            .commit()
    }

    fun moverCarrinhoParaEtapa(index: Int) {
        val carrinho = binding.carrinho
        val etapas = listOf(
           binding.progressBarCircle1,
            binding.progressBarCircle2,
            binding.progressBarCircle3
        )

        if (index < 0 || index >= etapas.size) return

        val destino = etapas[index]
        destino.post {
            val destinoX = destino.x + destino.width / 2 - carrinho.width / 2

            carrinho.animate()
                .x(destinoX)
                .setDuration(400)
                .start()
        }
    }
}


