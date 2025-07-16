package com.otavioaugusto.app_semurb

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.TypedValueCompat.dpToPx
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.otavioaugusto.app_semurb.databinding.ActivityMainBinding
import android.Manifest
import android.os.Looper
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val binding by lazy{
       ActivityMainBinding.inflate(layoutInflater)
    }

    val autenticacao by lazy {
        FirebaseAuth.getInstance()
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        verificarPermissaoLocalizacao()


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

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    private fun verificarUsuarioLogado() {
        val usuario = autenticacao.currentUser

        if (usuario != null){
            startActivity(
                Intent(this, PlaceHolderActivity::class.java)
            )
        }

    }


    private fun checarCampos(view: View) {

        val campoMatricula = binding.editTextMatricula.text.toString()
        val campoSenha = binding.editTextSenha.text.toString()

        val resultadoChecagemNulo = checagemNulo(campoMatricula, campoSenha, view)
        if (resultadoChecagemNulo){

                val emailFalso = "$campoMatricula@gmail.com"

            autenticacao.signInWithEmailAndPassword(
                emailFalso, campoSenha
            ).addOnSuccessListener { authResult ->
                binding.btnEntrar.isEnabled = false
                startActivity(
                    Intent(this, PlaceHolderActivity::class.java)
                )
            }.addOnFailureListener {
                val textInputMatricula = binding.textInputMatricula
                val textInputSenha = binding.textInputSenha
                val editTextSenha = binding.editTextSenha
                val editTextMatricula = binding.editTextMatricula


                textInputMatricula.error = "Senha está vazia"

                textInputMatricula.postDelayed({
                    textInputMatricula.error = null
                }, 2500)

                textInputSenha.error = "Matricula está vazia"

                textInputSenha.postDelayed({
                    textInputSenha.error = null
                }, 2500)

                editTextSenha.clearFocus()
                editTextMatricula.clearFocus()

                textInputSenha.editText?.setText("")
                textInputMatricula.editText?.setText("")



                val snackBarErroLogin = Snackbar.make(view, "Erro de login: Matrícula ou senha incorretos", Snackbar.LENGTH_SHORT)



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

        val textInputSenha = binding.textInputSenha
        val editTextSenha = binding.editTextSenha
        val editTextMatricula = binding.editTextMatricula
        val textInputMatricula = binding.textInputMatricula

        textInputSenha.error = null
        textInputMatricula.error = null

        if (cSenha.isEmpty() ) {
            textInputSenha.error = "A senha está vazia"
            isValid = false
            editTextSenha.clearFocus()
            editTextMatricula.clearFocus()
            textInputSenha.postDelayed({
                textInputSenha.error = null
            }, 2500)
        }
        if (cMatricula.isEmpty()) {
           binding.textInputMatricula.error = "A matrícula está vazia"
            isValid = false
            editTextSenha.clearFocus()
            editTextMatricula.clearFocus()
            textInputMatricula.postDelayed({
                textInputMatricula.error = null
            }, 2500)
        }

        if (cMatricula.isEmpty() || cSenha.isEmpty()){
            val snackBarErroNulo = Snackbar.make(view, "Preencha todos os campos!", Snackbar.LENGTH_SHORT)


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


    private fun verificarPermissaoLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show()
        }
    }


}