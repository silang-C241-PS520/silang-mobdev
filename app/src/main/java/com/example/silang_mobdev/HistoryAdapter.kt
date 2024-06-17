import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.silang_mobdev.data.api.response.TranslationResponse
import com.example.silang_mobdev.databinding.RecyclerItemBinding

class HistoryAdapter : ListAdapter<TranslationResponse, HistoryAdapter.HistoryViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TranslationResponse>() {
            override fun areItemsTheSame(oldItem: TranslationResponse, newItem: TranslationResponse): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TranslationResponse, newItem: TranslationResponse): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = RecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val translationItem = getItem(position)
        holder.bind(translationItem)
    }

    class HistoryViewHolder(private val binding: RecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(translation: TranslationResponse) {
            binding.itemTitle.text = translation.translation_text
            binding.itemTime.text = translation.date_time_created
        }
    }
}
