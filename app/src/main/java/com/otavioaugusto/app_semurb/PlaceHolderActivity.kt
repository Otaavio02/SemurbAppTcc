package com.otavioaugusto.app_semurb

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.databinding.ActivityPlaceholderBinding
import com.otavioaugusto.app_semurb.fragments.ConfigFragment
import com.otavioaugusto.app_semurb.fragments.HomeFragment
import com.otavioaugusto.app_semurb.fragments.OcorrenciasFragment
import com.otavioaugusto.app_semurb.fragments.PerfilFragment
import com.otavioaugusto.app_semurb.fragments.ViarioFragment

class PlaceHolderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaceholderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceholderBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val fragmentKey = intent.getStringExtra("FRAGMENT_KEY2")

        val fragmentToShow: Fragment = when (fragmentKey) {
            "INICIAR_HOME" -> HomeFragment()
            "INICIAR_PERFIL" -> PerfilFragment()
            "INICIAR_CONFIG" -> ConfigFragment()
            "INICIAR_VIARIOHOME" -> ViarioFragment()
            "INICIAR_OCORRENCIASHOME" -> OcorrenciasFragment()
            else -> HomeFragment()
        }


        replaceFragment(fragmentToShow)


        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.perfil -> replaceFragment(PerfilFragment())
                R.id.config -> replaceFragment(ConfigFragment())
                else -> { }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
        fragmentTransaction.commit()
    }
}
