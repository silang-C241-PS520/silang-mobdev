import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.silang_mobdev.R
import com.example.silang_mobdev.data.api.response.TranslationResponse
import com.example.silang_mobdev.databinding.RecyclerItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

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
        init {
            binding.moreOptions.setOnClickListener { view ->
                showPopupMenu(view, binding)
            }
        }

        fun bind(translation: TranslationResponse) {
            binding.itemTitle.text = translation.translation_text

            // Safely handle nullable date_time_created
            val formattedDateTime = translation.date_time_created?.let { formatDate(it) } ?: "N/A"
            binding.itemTime.text = formattedDateTime
        }

        private fun formatDate(dateTimeString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                val parsedDate = inputFormat.parse(dateTimeString)
                if (parsedDate != null) {
                    outputFormat.format(parsedDate)
                } else {
                    "Invalid Date"
                }
            } catch (e: Exception) {
                "Invalid Date"
            }
        }

        private fun showPopupMenu(view: View, binding: RecyclerItemBinding) {
            val popup = PopupMenu(view.context, view)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.overflow_menu, popup.menu)
            popup.setOnMenuItemClickListener { menuItem ->
                handleMenuItemClick(menuItem, binding)
            }
            popup.show()
        }

        private fun handleMenuItemClick(menuItem: MenuItem, binding: RecyclerItemBinding): Boolean {
            return when (menuItem.itemId) {
                R.id.context_menu_copy -> {
                    copyTextToClipboard(binding.itemTitle.text.toString(), itemView.context)
                    true
                }
                else -> false
            }
        }

        private fun copyTextToClipboard(text: String, context: Context) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Result Text", text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }
}
