package com.otavioaugusto.app_semurb

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.otavioaugusto.app_semurb.databinding.ActivityPlaceholderBinding
import com.otavioaugusto.app_semurb.fragments.ConfigFragment
import com.otavioaugusto.app_semurb.fragments.HomeFragment
import com.otavioaugusto.app_semurb.fragments.NotificacoesFragment
import com.otavioaugusto.app_semurb.fragments.OcorrenciasFragment
import com.otavioaugusto.app_semurb.fragments.PerfilFragment
import com.otavioaugusto.app_semurb.fragments.ViarioFragment

class PlaceHolderActivity : AppCompatActivity() {


    private fun esconderSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }



    private val binding by lazy{
        ActivityPlaceholderBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        esconderSystemBars()


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
                R.id.notificacao -> replaceFragment(NotificacoesFragment())

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
    override fun onResume() {
        super.onResume()
        esconderSystemBars()
        window.statusBarColor = android.graphics.Color.TRANSPARENT
    }
}

