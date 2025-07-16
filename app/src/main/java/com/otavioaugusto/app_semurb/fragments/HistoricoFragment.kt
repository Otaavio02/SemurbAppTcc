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
import com.otavioaugusto.app_semurb.dataClasses.DataClassHistoricoListItem
import com.otavioaugusto.app_semurb.databinding.FragmentHistorico1Binding
import com.otavioaugusto.app_semurb.dbHelper.AppDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                val todosItens = dbHelper.getAllHistorico()
                val agrupado = todosItens.groupBy { it.data_envio }

                val listaFinal = mutableListOf<DataClassHistoricoListItem>()
                agrupado.forEach { (data, itens) ->
                    listaFinal.add(DataClassHistoricoListItem.Header(data))
                    itens.forEach { item ->
                        listaFinal.add(DataClassHistoricoListItem.Item(item))
                    }
                }


                withContext(Dispatchers.Main){
                    adapter = HistoricoAdapter(listaFinal) { historico ->
                        val fragment = OcorrenciasFragment()
                        val bundle = Bundle().apply {
                            putString("ID_LISTA", historico.id_lista.toString())
                            putString("DATA_ENVIO", historico.data_envio)
                            putString("TOPICO", historico.topico)
                        }
                        fragment.arguments = bundle

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView, fragment)
                            .addToBackStack(null)
                            .commit()
                    }
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
