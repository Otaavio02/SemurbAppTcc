package com.otavioaugusto.app_semurb

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.TypedValueCompat.dpToPx
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private lateinit var textInputMatricula: TextInputLayout
    private lateinit var editMatricula: EditText

    private lateinit var textInputSenha: TextInputLayout
    private lateinit var editSenha: EditText

    private lateinit var btnEntrar : androidx.appcompat.widget.AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        inicializarComponentesInterfaceMain()
        btnEntrar.setOnClickListener {
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

        val campoMatricula = editMatricula.text.toString()
        val campoSenha = editSenha.text.toString()

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

        textInputSenha.error = null
        textInputMatricula.error = null

        if (cSenha.isEmpty() ) {
            textInputSenha.error = "A matrícula está vazia"
            isValid = false
        }
        if (cMatricula.isEmpty()) {
            textInputMatricula.error = "A matrícula está vazia"
            isValid = false
        }



        return isValid
    }



    private fun inicializarComponentesInterfaceMain() {
        textInputMatricula = findViewById(R.id.text_input_matricula)
        textInputSenha = findViewById(R.id.text_input_senha)
        editMatricula = findViewById(R.id.edit_text_matricula)
        editSenha = findViewById(R.id.edit_text_senha)
        btnEntrar = findViewById(R.id.btn_Entrar)
        }


}