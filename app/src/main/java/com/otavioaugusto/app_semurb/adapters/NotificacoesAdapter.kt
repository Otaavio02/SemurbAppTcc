import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.dataClasses.DataClassNotificacoes

class NotificacoesAdapter(
    private val notificacoes: List<DataClassNotificacoes>
): RecyclerView.Adapter<NotificacoesAdapter.ViewHolder>(){


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val titulo: TextView = itemView.findViewById(R.id.textViewTitulo)
         val descricao: TextView = itemView.findViewById(R.id.textViewDescricao)
         val horario: TextView = itemView.findViewById(R.id.textViewHorario)
         val data: TextView = itemView.findViewById(R.id.textViewData)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = notificacoes[position]
        holder.titulo.text = item.titulo
        holder.descricao.text = item.descricao
        holder.horario.text = item.horario.toString()
        holder.data.text = item.data.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notificacaonaolida, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = notificacoes.size


    }


