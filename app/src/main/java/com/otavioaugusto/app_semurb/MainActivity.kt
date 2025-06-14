package com.otavioaugusto.app_semurb

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.TypedValueCompat.dpToPx
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.otavioaugusto.app_semurb.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy{
       ActivityMainBinding.inflate(layoutInflater)
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)


        binding.Esqueceusenha.setOnClickListener {
            mostrarBottonSheet()
        }
        binding.btnEntrar.setOnClickListener {
            checarCampos()

        }


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

        supportActionBar?.hide()
    }



      private fun checarCampos() {

        val campoMatricula = binding.editTextMatricula.text.toString()
        val campoSenha = binding.editTextSenha.text.toString()

        val resultadoChecagemNulo = checagemNulo(campoMatricula, campoSenha)
        if (resultadoChecagemNulo){

            if(campoSenha == "1" && campoMatricula == "u"){

                val intent = Intent(this, PlaceHolderActivity::class.java)
                startActivity(intent)
            }
            else {
                    val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                    builder.setTitle("Erro de login")
                    builder.setMessage("Matrícula ou senha incorreta")
                    builder.setPositiveButton("OK", null)
                    val dialog = builder.create()
                    dialog.show()

            }

        }


    }

    private fun checagemNulo(cMatricula: String, cSenha: String): Boolean {

        var isValid = true

        binding.textInputSenha.error = null
        binding.textInputMatricula.error = null

        if (cSenha.isEmpty() ) {
            binding.textInputSenha.error = "A matrícula está vazia"
            isValid = false
        }
        if (cMatricula.isEmpty()) {
           binding.textInputMatricula.error = "A matrícula está vazia"
            isValid = false
        }



        return isValid
    }


    private fun mostrarBottonSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_recuperarsenha1, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)


        val botaoVerificar = bottomSheetView.findViewById<Button>(R.id.btn_verificar)
        botaoVerificar.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

}