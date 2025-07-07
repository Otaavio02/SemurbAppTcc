package com.otavioaugusto.app_semurb

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import com.otavioaugusto.app_semurb.databinding.ActivityPlaceholderBinding
import com.otavioaugusto.app_semurb.fragments.*

class PlaceHolderActivity : AppCompatActivity() {

    private var currentFragmentKey: String = "INICIAR_HOME"
    private var currentMenuItemId: Int = R.id.home

    private val binding by lazy {
        ActivityPlaceholderBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, true)
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
            insets
        }


        if (savedInstanceState != null) {
            currentFragmentKey = savedInstanceState.getString("CURRENT_FRAGMENT_KEY", "INICIAR_HOME")
            currentMenuItemId = savedInstanceState.getInt("CURRENT_MENU_ITEM_ID", R.id.home)
        } else {
            currentFragmentKey = intent.getStringExtra("FRAGMENT_KEY2") ?: "INICIAR_HOME"
            currentMenuItemId = menuIdFromKey(currentFragmentKey)
        }

        val fragmentToShow = fragmentFromKey(currentFragmentKey)
        replaceFragment(fragmentToShow)
        binding.bottomNavigationView.selectedItemId = currentMenuItemId

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            if (item.itemId == currentMenuItemId) {
                return@setOnItemSelectedListener false
            }

            currentFragmentKey = keyFromMenuId(item.itemId)
            currentMenuItemId = item.itemId

            val newFragment = fragmentFromKey(currentFragmentKey)
            replaceFragment(newFragment)

            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("CURRENT_FRAGMENT_KEY", currentFragmentKey)
        outState.putInt("CURRENT_MENU_ITEM_ID", currentMenuItemId)
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


    fun updateFragmentSelection(fragmentKey: String, menuItemId: Int) {
        currentFragmentKey = fragmentKey
        currentMenuItemId = menuItemId
        binding.bottomNavigationView.selectedItemId = menuItemId
    }




    private fun fragmentFromKey(key: String): Fragment {
        return when (key) {
            "INICIAR_HOME" -> HomeFragment()
            "INICIAR_PERFIL" -> PerfilFragment()
            "INICIAR_CONFIG" -> ConfigFragment()
            "INICIAR_VIARIOHOME" -> ViarioFragment()
            "INICIAR_OCORRENCIASHOME" -> OcorrenciasFragment()
            "INICIAR_NOTIFICACOES" -> NotificacoesFragment()
            else -> HomeFragment()
        }
    }

    private fun menuIdFromKey(key: String): Int {
        return when (key) {
            "INICIAR_HOME" -> R.id.home
            "INICIAR_PERFIL" -> R.id.perfil
            "INICIAR_CONFIG" -> R.id.config
            "INICIAR_NOTIFICACOES" -> R.id.notificacao
            else -> R.id.home
        }
    }

    private fun keyFromMenuId(menuId: Int): String {
        return when (menuId) {
            R.id.home -> "INICIAR_HOME"
            R.id.perfil -> "INICIAR_PERFIL"
            R.id.config -> "INICIAR_CONFIG"
            R.id.notificacao -> "INICIAR_NOTIFICACOES"
            else -> "INICIAR_HOME"
        }
    }

    fun clearBottomNavigationSelection() {
        binding.bottomNavigationView.menu.setGroupCheckable(0, true, false)
        for (i in 0 until binding.bottomNavigationView.menu.size()) {
            binding.bottomNavigationView.menu.getItem(i).isChecked = false
        }
        binding.bottomNavigationView.menu.setGroupCheckable(0, true, true)


        currentMenuItemId = -1
    }


}