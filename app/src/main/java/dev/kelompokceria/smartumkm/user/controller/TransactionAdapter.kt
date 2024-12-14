package dev.kelompokceria.smartumkm.user.controller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dev.kelompokceria.smartumkm.user.R
import dev.kelompokceria.smartumkm.user.databinding.CardTransactionBinding
import dev.kelompokceria.smartumkm.user.model.Product
import java.text.NumberFormat
import java.util.Locale

class TransactionAdapter(
    private val onQuantityChanged: (Product, Int) -> Unit // Callback untuk perubahan jumlah
) : ListAdapter<Product, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    class TransactionViewHolder(
        private val binding: CardTransactionBinding,
        private val onQuantityChanged: (Product, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentQuantity: Int = 1 // Default quantity

        fun bind(product: Product) {
            Glide.with(binding.viewImage.context)
                .load(product.image)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.picture) // Placeholder saat loading
                        .error(R.drawable.picture) // Gambar default jika error
                )
                .into(binding.viewImage)

            val numberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            binding.viewCate.text = product.category.toString()
            binding.viewName.text = product.name
            binding.viewPrice.text = numberFormat.format(product.price)

            binding.textQuantity.text = currentQuantity.toString()

            binding.btnPlus.setOnClickListener {
                currentQuantity++
                binding.textQuantity.text = currentQuantity.toString()
                onQuantityChanged(product, currentQuantity)
            }

            binding.btnMinus.setOnClickListener {
                if (currentQuantity > 1) { // Tidak boleh kurang dari 1
                    currentQuantity--
                    binding.textQuantity.text = currentQuantity.toString()
                    onQuantityChanged(product, currentQuantity)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = CardTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding, onQuantityChanged)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
}
}