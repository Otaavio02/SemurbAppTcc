package com.otavioaugusto.app_semurb

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.TypedValueCompat.dpToPx
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
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
        binding.btnEntrar.setOnClickListener { view ->
            checarCampos(view)

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



      private fun checarCampos(view: View) {

        val campoMatricula = binding.editTextMatricula.text.toString()
        val campoSenha = binding.editTextSenha.text.toString()

        val resultadoChecagemNulo = checagemNulo(campoMatricula, campoSenha, view)
        if (resultadoChecagemNulo){

            if(campoSenha == "1" && campoMatricula == "u"){

                val intent = Intent(this, PlaceHolderActivity::class.java)
                startActivity(intent)
            }
            else {

                binding.textInputMatricula.error = "Senha está vazia"
                binding.textInputSenha.error = "Matricula está vazia"




                   val snackBarErroLogin = Snackbar.make(view, "Erro de login: Matrícula ou senha incorretos", Snackbar.LENGTH_LONG)



                snackBarErroLogin.setTextColor(getColor(R.color.Vermelho))
                val snackbarText = snackBarErroLogin.view.findViewById<TextView>(
                    com.google.android.material.R.id.snackbar_text
                )

                val fontecustomizada = ResourcesCompat.getFont(this, R.font.montserrat_bold)
                snackbarText.typeface = fontecustomizada


                snackbarText.textSize = 16f


                snackbarText.gravity = Gravity.CENTER
                snackbarText.textAlignment = View.TEXT_ALIGNMENT_CENTER

                val params = snackBarErroLogin.view.layoutParams
                params.height = 150
                snackBarErroLogin.view.layoutParams = params
                snackBarErroLogin.show()
            }

        }


    }

    private fun checagemNulo(cMatricula: String, cSenha: String, view: View): Boolean {

        var isValid = true

        binding.textInputSenha.error = null
        binding.textInputMatricula.error = null

        if (cSenha.isEmpty() ) {
            binding.textInputSenha.error = "A senha está vazia"
            isValid = false
        }
        if (cMatricula.isEmpty()) {
           binding.textInputMatricula.error = "A matrícula está vazia"
            isValid = false
        }
        if (cMatricula.isEmpty() || cSenha.isEmpty()){
            val snackBarErroNulo = Snackbar.make(view, "Preencha todos os campos!", Snackbar.LENGTH_LONG)


            snackBarErroNulo.setTextColor(getColor(R.color.Vermelho))
            val snackbarText2 = snackBarErroNulo.view.findViewById<TextView>(
                com.google.android.material.R.id.snackbar_text
            )

            val fontecustomizada = ResourcesCompat.getFont(this, R.font.montserrat_bold)
            snackbarText2.typeface = fontecustomizada

            snackbarText2.textSize = 22f


            snackbarText2.gravity = Gravity.CENTER
            snackbarText2.textAlignment = View.TEXT_ALIGNMENT_CENTER

            val params = snackBarErroNulo.view.layoutParams
            params.height = 150
            snackBarErroNulo.view.layoutParams = params
            snackBarErroNulo.show()
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