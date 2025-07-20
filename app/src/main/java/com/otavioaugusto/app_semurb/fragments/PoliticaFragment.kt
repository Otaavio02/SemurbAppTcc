package com.otavioaugusto.app_semurb.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.PlaceHolderActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.databinding.FragmentPoliticaBinding

class PoliticaFragment : Fragment() {

    private var _binding: FragmentPoliticaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPoliticaBinding.inflate(inflater, container, false)

        binding.btnVoltarPolitica.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.fragmentContainerView, ConfigFragment())
                .commit()

            (activity as? PlaceHolderActivity)?.selecionarBottomNavBar(R.id.config)
        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )
                    .replace(R.id.fragmentContainerView, ConfigFragment())
                    .commit()

                (activity as? PlaceHolderActivity)?.selecionarBottomNavBar(R.id.config)
            }
        })
    }
}