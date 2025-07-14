package com.otavioaugusto.app_semurb.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.adapters.HistoricoAdapter
import com.otavioaugusto.app_semurb.adapters.NotificacoesAdapter
import com.otavioaugusto.app_semurb.dataClasses.DataClassHistorico1
import com.otavioaugusto.app_semurb.dataClasses.DataClassNotificacoes
import com.otavioaugusto.app_semurb.databinding.FragmentHistorico1Binding
import com.otavioaugusto.app_semurb.dbHelper.ocorrenciasDBHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import kotlin.collections.mutableListOf

class HistoricoFragment : Fragment() {

    private var _binding: FragmentHistorico1Binding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: HistoricoAdapter



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistorico1Binding.inflate(inflater, container, false)


        binding.textDiaHistorico1.text = "Dia ${LocalDate.now()}"

        lifecycleScope.launch {
            val dbHelper = ocorrenciasDBHelper(requireContext())
            withContext(Dispatchers.IO){
            dbHelper.getAllOcorrenciasNaoEnviadas()
            }

        }


        val historicoEspecifico = resources.getStringArray(R.array.historico)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, historicoEspecifico)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)



        val editTextDate = binding.editTextData

        val calendar = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
            editTextDate.setText(selectedDate)
        }

        editTextDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        val listaTeste =  mutableListOf(
            DataClassHistorico1("Ocorrências", LocalTime.of(9, 30),),
            DataClassHistorico1("Ocorrências", LocalTime.of(9, 30)),
            DataClassHistorico1("Ocorrências", LocalTime.of(9, 30))
        )

        adapter = HistoricoAdapter(listaTeste)
        binding.rvHistorico.layoutManager= LinearLayoutManager(requireContext())

        binding.rvHistorico.adapter = adapter

        return binding.root
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
