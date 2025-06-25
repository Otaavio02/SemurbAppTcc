package com.otavioaugusto.app_semurb

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.otavioaugusto.app_semurb.databinding.ActivityPlaceholderBinding
import com.otavioaugusto.app_semurb.fragments.*

class PlaceHolderActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPlaceholderBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.statusBarColor = Color.TRANSPARENT


        val insetsController = WindowInsetsControllerCompat(window, window.decorView)

        val nightModeFlags = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> {

                insetsController.isAppearanceLightStatusBars = false
            }
            android.content.res.Configuration.UI_MODE_NIGHT_NO,
            android.content.res.Configuration.UI_MODE_NIGHT_UNDEFINED -> {

                insetsController.isAppearanceLightStatusBars = true
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())


            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                0
            )


            binding.bottomNavigationView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = systemBars.bottom
            }

            insets
        }


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

        binding.bottomNavigationView.setOnItemSelectedListener { item ->

            if (item.itemId == binding.bottomNavigationView.selectedItemId) {
                return@setOnItemSelectedListener false
            }

            when (item.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.perfil -> replaceFragment(PerfilFragment())
                R.id.config -> replaceFragment(ConfigFragment())
                R.id.notificacao -> replaceFragment(NotificacoesFragment())
            }

            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }
}
