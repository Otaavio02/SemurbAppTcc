package com.otavioaugusto.app_semurb

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.fragments.HomeFragment
import com.otavioaugusto.app_semurb.fragments.IniciarTurno1Fragment
import com.otavioaugusto.app_semurb.fragments.InspecaoFragment
import com.otavioaugusto.app_semurb.fragments.Ocorrencias2Fragment
import com.otavioaugusto.app_semurb.fragments.Ocorrencias3Fragment
import com.otavioaugusto.app_semurb.fragments.OcorrenciasEditadoFragment
import com.otavioaugusto.app_semurb.fragments.Viario2Fragment
import com.otavioaugusto.app_semurb.fragments.Viario3Fragment
import com.otavioaugusto.app_semurb.fragments.ViarioEditadoFragment

class PlaceHolderGameficadoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_placeholder_gameficado)


        val fragmentKey = intent.getStringExtra("FRAGMENT_KEY")

        val fragmentToShow: Fragment = when (fragmentKey) {
            "INICIAR_TURNO" -> IniciarTurno1Fragment()
            "INICIAR_INSPECAO" -> InspecaoFragment()
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
}

