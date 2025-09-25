package com.otavioaugusto.app_semurb.fragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.otavioaugusto.app_semurb.MainActivity
import com.otavioaugusto.app_semurb.PlaceHolderActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.databinding.FragmentConfigBinding

class ConfigFragment : Fragment() {

    private var _binding: FragmentConfigBinding? = null
    private val binding get() = _binding!!
    private val autenticacao by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfigBinding.inflate(inflater, container, false)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        configurarSwitches()
        configurarClicks()

        return binding.root
    }

    private fun configurarSwitches() {
        val prefs = requireContext().getSharedPreferences("config", MODE_PRIVATE)
        val isHighContrast = prefs.getBoolean("high_contrast", false)
        val nightMode = prefs.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)





        val themeListener = { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {

                binding.switchAltoContraste.setOnCheckedChangeListener(null)
                binding.switchAltoContraste.isChecked = false
                binding.switchAltoContraste.setOnCheckedChangeListener { _, checked ->

                }
            }

            prefs.edit()
                .putInt(
                    "night_mode",
                    if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )
                .apply()

            AppCompatDelegate.setDefaultNightMode(
                if (prefs.getBoolean("high_contrast", false))
                    AppCompatDelegate.MODE_NIGHT_NO
                else if (isChecked)
                    AppCompatDelegate.MODE_NIGHT_YES
                else
                    AppCompatDelegate.MODE_NIGHT_NO
            )

            requireActivity().recreate()
        }

        binding.btnTrocarTema.apply {
            setOnCheckedChangeListener(null)
            isChecked = (nightMode == AppCompatDelegate.MODE_NIGHT_YES)
            setOnCheckedChangeListener(themeListener)
        }

        binding.switchAltoContraste.apply {
            setOnCheckedChangeListener(null)
            isChecked = isHighContrast
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {

                    binding.btnTrocarTema.setOnCheckedChangeListener(null)
                    binding.btnTrocarTema.isChecked = false
                    binding.btnTrocarTema.setOnCheckedChangeListener(themeListener)
                }

                prefs.edit().putBoolean("high_contrast", isChecked).apply()


                requireActivity().recreate()
            }
        }
    }



    private fun configurarClicks() {
        val prefs = requireContext().getSharedPreferences("config", MODE_PRIVATE)

        // Logout
        val logoutClick: (View) -> Unit = {
            autenticacao.signOut()
            startActivity(Intent(requireContext(), MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            requireActivity().finish()
        }
        binding.btnLogout.setOnClickListener(logoutClick)
        binding.textViewLogout.setOnClickListener(logoutClick)

        // Navegação para outras fragments
        fun navegar(fragment: Fragment) {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()

            (activity as? PlaceHolderActivity)?.limparBottomNavBar()
        }

        binding.btnPolitica.setOnClickListener { navegar(PoliticaFragment()) }
        binding.textPolitica.setOnClickListener { navegar(PoliticaFragment()) }
        binding.btnSobre.setOnClickListener { navegar(SobreFragment()) }
        binding.textSobre.setOnClickListener { navegar(SobreFragment()) }
        binding.btnTermosCondicoes.setOnClickListener { navegar(TermosCondicoesFragment()) }
        binding.textTermosCondicoes.setOnClickListener { navegar(TermosCondicoesFragment()) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
