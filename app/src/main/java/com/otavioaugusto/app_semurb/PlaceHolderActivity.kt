package com.otavioaugusto.app_semurb

import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.databinding.ActivityPlaceholderBinding
import com.otavioaugusto.app_semurb.fragments.*

class PlaceHolderActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPlaceholderBinding.inflate(layoutInflater)
    }

    private var currentFragmentKey: String = "INICIAR_HOME"
    private var currentMenuItemId: Int = R.id.home

    override fun onCreate(savedInstanceState: Bundle?) {

        val prefs      = getSharedPreferences("config", MODE_PRIVATE)
        val isHC       = prefs.getBoolean("high_contrast", false)
        val nightMode  = prefs.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        if (isHC) {

            setTheme(R.style.Theme_AppSemurb_HighContrast)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {

            setTheme(R.style.Base_Theme_AppSemurb)
            AppCompatDelegate.setDefaultNightMode(nightMode)
        }


        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = Color.TRANSPARENT
        val insetController = WindowInsetsControllerCompat(window, window.decorView)
        val uiModeFlags = resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK

        insetController.isAppearanceLightStatusBars =
            (uiModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_NO)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(sysBars.left, sysBars.top, sysBars.right, 0)
            insets
        }


        if (savedInstanceState != null) {
            currentFragmentKey  = savedInstanceState.getString("CURRENT_FRAGMENT_KEY", "INICIAR_HOME")!!
            currentMenuItemId   = savedInstanceState.getInt("CURRENT_MENU_ITEM_ID", R.id.home)
        } else {
            currentFragmentKey = intent.getStringExtra("FRAGMENT_KEY2") ?: "INICIAR_HOME"
            currentMenuItemId  = menuIdFromKey(currentFragmentKey)
            replaceFragment(fragmentFromKey(currentFragmentKey))
            binding.bottomNavigationView.selectedItemId = currentMenuItemId
        }


        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            if (item.itemId == currentMenuItemId) return@setOnItemSelectedListener false

            currentMenuItemId  = item.itemId
            currentFragmentKey = keyFromMenuId(item.itemId)
            replaceFragment(fragmentFromKey(currentFragmentKey))
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
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

    fun limparBottomNavBar() {
        binding.bottomNavigationView.menu.setGroupCheckable(0, true, false)
        for (i in 0 until binding.bottomNavigationView.menu.size()) {
            binding.bottomNavigationView.menu.getItem(i).isChecked = false
        }
        binding.bottomNavigationView.menu.setGroupCheckable(0, true, true)
        currentMenuItemId = -1
    }

    fun selecionarBottomNavBar(itemId: Int) {
        binding.bottomNavigationView.menu.setGroupCheckable(0, true, false)
        for (i in 0 until binding.bottomNavigationView.menu.size()) {
            binding.bottomNavigationView.menu.getItem(i).isChecked =
                (binding.bottomNavigationView.menu.getItem(i).itemId == itemId)
        }
        binding.bottomNavigationView.menu.setGroupCheckable(0, true, true)
        currentMenuItemId = itemId
    }

    private fun fragmentFromKey(key: String): Fragment = when (key) {
        "INICIAR_HOME"         -> HomeFragment()
        "INICIAR_PERFIL"       -> PerfilFragment()
        "INICIAR_CONFIG"       -> ConfigFragment()
        "INICIAR_VIARIOHOME"   -> ViarioFragment()
        "INICIAR_OCORRENCIASHOME" -> OcorrenciasFragment()
        "INICIAR_NOTIFICACOES" -> NotificacoesFragment()
        else                   -> HomeFragment()
    }

    private fun menuIdFromKey(key: String): Int = when (key) {
        "INICIAR_HOME"         -> R.id.home
        "INICIAR_PERFIL"       -> R.id.perfil
        "INICIAR_CONFIG"       -> R.id.config
        "INICIAR_NOTIFICACOES" -> R.id.notificacao
        else                   -> R.id.home
    }

    private fun keyFromMenuId(menuId: Int): String = when (menuId) {
        R.id.home        -> "INICIAR_HOME"
        R.id.perfil      -> "INICIAR_PERFIL"
        R.id.config      -> "INICIAR_CONFIG"
        R.id.notificacao -> "INICIAR_NOTIFICACOES"
        else             -> "INICIAR_HOME"
    }
}
