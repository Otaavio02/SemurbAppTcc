package com.otavioaugusto.app_semurb

import android.animation.ValueAnimator
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.databinding.ActivityPlaceholderGameficadoBinding
import com.otavioaugusto.app_semurb.fragments.*

class PlaceHolderGameficadoActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPlaceholderGameficadoBinding.inflate(layoutInflater)
    }

    companion object {
        const val EXTRA_FRAGMENT_KEY = "FRAGMENT_KEY"
        const val VISIBILITY        = "VISIBILITY"
        const val VERIFICAR_TURNO   = "VERIFICAR_TURNO"
        const val INICIAR_INSPECAO  = "INICIAR_INSPECAO"
        const val HISTORICO_INSPECAO  = "HISTORICO_INSPECAO"
        const val INICIAR_VIARIO    = "INICIAR_VIARIO"
        const val VIARIO_EDITADO    = "VIARIO_EDITADO"
        const val INICIAR_OCORRENCIAS = "INICIAR_OCORRENCIAS"
        const val OCORRENCIAS_EDITADO = "OCORRENCIAS_EDITADO"
    }

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
        enableEdgeToEdge()
        setContentView(binding.root)


        val fragmentKey = intent.getStringExtra(EXTRA_FRAGMENT_KEY)
        val fragmentToShow: Fragment = when (fragmentKey) {
            VERIFICAR_TURNO   -> VerificarTurnoFragment()
            INICIAR_INSPECAO  -> Inspecao1Fragment()
            HISTORICO_INSPECAO  -> Inspecao3Fragment()
            INICIAR_VIARIO    -> Viario1Fragment()
            VIARIO_EDITADO    -> ViarioEditadoFragment()
            INICIAR_OCORRENCIAS -> Ocorrencias1Fragment()
            OCORRENCIAS_EDITADO -> OcorrenciasEditadoFragment()
            else              -> HomeFragment()
        }

      
        if (intent.getStringExtra(VISIBILITY) == "GONE") {
            binding.progressBar.visibility      = View.GONE
            binding.gradientTransition.visibility = View.GONE
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainerView2, fragmentToShow)
            .commit()
    }

    fun moverCarrinhoParaEtapa(index: Int, pularEtapa: String) {
        val carrinho      = binding.carrinho
        val etapas        = listOf(binding.progressBarCircle1,
            binding.progressBarCircle2,
            binding.progressBarCircle3)
        val etapasLinha   = listOf(binding.progressBarLine1,
            binding.progressBarLine2)

        etapas.forEachIndexed { i, etapa ->
            val corFinal = if (i < index) R.color.VerdeLimao else R.color.CorBolas
            animarCorElemento(etapa, corFinal, 500)
        }

        etapasLinha.forEachIndexed { i, linha ->
            val corFinal = if (i < index) R.color.VerdeLimao else R.color.CorBolas
            animarCorElemento(linha, corFinal, 500)
        }

        etapas[index].post {
            val destinoX = if (pularEtapa == "finalizar") {
                750f
            } else {
                etapas[index].x + etapas[index].width / 2 - carrinho.width / 2
            }
            carrinho.animate().x(destinoX).setDuration(400).start()
        }
    }

    private fun animarCorElemento(view: View, corFinalRes: Int, durationMs: Int) {
        val drawable = (view.background?.mutate()as? android.graphics.drawable.GradientDrawable) ?: return
        val corAtual = drawable.color?.defaultColor
            ?: getColor(corFinalRes)
        val corFinal = getColor(corFinalRes)

        if (corAtual == corFinal) return

        ValueAnimator.ofObject(
            android.animation.ArgbEvaluator(),
            corAtual,
            corFinal
        ).apply {
            duration = durationMs.toLong()
            addUpdateListener { anim ->
                drawable.setColor(anim.animatedValue as Int)
            }
            start()
        }
    }

    fun concluirEtapaFinal(index: Int) {
        if (index == 3) moverCarrinhoParaEtapa(2, "finalizar")

        val deslocamento = 850f
        binding.carrinho.animate()
            .translationX(deslocamento)
            .setDuration(500)
            .start()

        val etapa = findViewById<View>(R.id.progress_bar_circle3)
        animarCorElemento(etapa, R.color.VerdeLimao, 500)
    }
}
