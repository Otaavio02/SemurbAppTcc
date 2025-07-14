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

import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.adapters.HistoricoAdapter
import com.otavioaugusto.app_semurb.dataClasses.DataClassHistorico1Ocorrencias
import com.otavioaugusto.app_semurb.dataClasses.DataClassHistorico1Viario
import com.otavioaugusto.app_semurb.databinding.FragmentHistorico1Binding
import com.otavioaugusto.app_semurb.dbHelper.AppDatabaseHelper
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



        return binding.root
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val dbHelper = AppDatabaseHelper(requireContext())
            withContext(Dispatchers.IO){
                val listaHistoricoOcorrenciasEnviadas = dbHelper.getAllOcorrenciasEnviadas()

                val quantidadeTotalOcorrencias = listaHistoricoOcorrenciasEnviadas.size

                val itemHistoricoOcorrencia = DataClassHistorico1Ocorrencias(
                    titulo = "Envio de $quantidadeTotalOcorrencias ocorrência(s)",
                    horarioEnvio = listaHistoricoOcorrenciasEnviadas.firstOrNull()?.horarioEnvio ?: "",
                    dataEnvio = listaHistoricoOcorrenciasEnviadas.firstOrNull()?.dataEnvio ?: "",
                    id = 0,
                    numeroSequencial = 0,
                    tipo = "-",
                    endereco = "-",
                    nome = "-",
                    numcontato = "-",
                    quantidadeTotal = quantidadeTotalOcorrencias
                )
                val listaHistoricoOcorrencias = mutableListOf(itemHistoricoOcorrencia)


                val quantidadeTotalViario = listaHistoricoOcorrenciasEnviadas.size

                val listaHistoricoViarioEnviados = dbHelper.getAllViarioEnviados()

                val itemHistoricoViario = DataClassHistorico1Viario(
                    titulo = "Envio de $quantidadeTotalViario ocorrência(s)",
                    horarioEnvio = listaHistoricoViarioEnviados.firstOrNull()?.horarioEnvio ?: "",
                    dataEnvio = listaHistoricoViarioEnviados.firstOrNull()?.dataEnvio ?: "",
                    id = 0,
                    numeroSequencial = 0,
                    tipo = "-",
                    endereco = "-",
                    descricao = "-",
                    quantidadeTotal = quantidadeTotalViario
                )
                val listaHistoricoViario = mutableListOf(itemHistoricoViario)


                withContext(Dispatchers.Main){
                    val adapter = HistoricoAdapter(listaHistoricoOcorrencias, listaHistoricoViario)
                    binding.rvHistorico.adapter = adapter
                    binding.rvHistorico.layoutManager = LinearLayoutManager(requireContext())}


            }
        }
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
