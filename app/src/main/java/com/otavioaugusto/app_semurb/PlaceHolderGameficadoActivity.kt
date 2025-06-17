package com.otavioaugusto.app_semurb

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.databinding.ActivityPlaceholderGameficadoBinding
import com.otavioaugusto.app_semurb.fragments.*

class PlaceHolderGameficadoActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPlaceholderGameficadoBinding.inflate(layoutInflater)
    }

    companion object {
        const val EXTRA_FRAGMENT_KEY = "FRAGMENT_KEY"

        const val INICIAR_TURNO = "INICIAR_TURNO"
        const val INICIAR_INSPECAO = "INICIAR_INSPECAO"
        const val INICIAR_OCORRENCIAS = "INICIAR_OCORRENCIAS"
        const val INICIAR_VIARIO = "INICIAR_VIARIO"
        const val VIARIO_EDITADO = "VIARIO-EDITADO"
        const val OCORRENCIAS_EDITADO = "OCORRENCIAS_EDITADO"
        const val INICIAR_VIARIO2 = "INICIAR_VIARIO2"
        const val INICIAR_OCORRENCIAS2 = "INICIAR_OCORRENCIAS2"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val fragmentKey = intent.getStringExtra(EXTRA_FRAGMENT_KEY)

        val fragmentToShow: Fragment = when (fragmentKey) {
            INICIAR_TURNO -> VerificarTurnoFragment()
            INICIAR_INSPECAO -> Inspecao1Fragment()
            INICIAR_OCORRENCIAS -> Ocorrencias2Fragment()
            INICIAR_VIARIO -> Viario2Fragment()
            VIARIO_EDITADO -> ViarioEditadoFragment()
            OCORRENCIAS_EDITADO -> OcorrenciasEditadoFragment()
            INICIAR_VIARIO2 -> Viario3Fragment()
            INICIAR_OCORRENCIAS2 -> Ocorrencias3Fragment()
            else -> HomeFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainerView2, fragmentToShow)
            .commit()
    }


    fun moverCarrinhoParaEtapa(index: Int) {
        val carrinho = binding.carrinho
        val etapas = listOf(
           binding.progressBarCircle1,
            binding.progressBarCircle2,
            binding.progressBarCircle3,
        )
        val etapasLinha = listOf(
            binding.progressBarLine1,
            binding.progressBarLine2,
        )

        if (index < 0 || index >= etapas.size) return

        etapas.forEachIndexed { i, etapa ->
            val corFinal = if (i < index) {
                R.color.VerdeLimao
            } else {
                R.color.AzulEscuroProfundo
            }

            val duration = 500;
            animarCorElemento(etapa, corFinal, duration)
        }

        etapasLinha.forEachIndexed { i, etapa ->
            val corFinal = if (i < index) {
                R.color.VerdeLimao
            } else {
                R.color.AzulEscuroProfundo
            }
            val duration = 500;

            animarCorElemento(etapa, corFinal, duration);

        }

        val destino = etapas[index]
        destino.post {
            val destinoX = destino.x + destino.width / 2 - carrinho.width / 2

            carrinho.animate()
                .x(destinoX)
                .setDuration(400)
                .start()
        }
    }

    fun animarCorElemento(view: ImageView, corFinalRes: Int, durationTime: Int) {
        val drawable = view.background?.mutate() as? android.graphics.drawable.GradientDrawable ?: return;
        val corFinal = getColor(corFinalRes);
        val corAtual = (drawable.color?.defaultColor ?: corFinal);

        if (corAtual == corFinal) return;

        val animator = ValueAnimator.ofObject(ArgbEvaluator(), corAtual, corFinal).apply {
            duration = durationTime.toLong();
            addUpdateListener { animation ->
                val cor = animation.animatedValue as Int;
                drawable.setColor(cor);
            }
        }
        animator.start()
    }

    fun concluirEtapaFinal() {
        val etapa = findViewById<ImageView>(R.id.progress_bar_circle3);
        animarCorElemento(etapa, R.color.VerdeLimao, 500);
    }

}
