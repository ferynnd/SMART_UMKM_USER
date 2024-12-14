package dev.kelompokceria.smartumkm.user.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import dev.kelompokceria.smartumkm.user.R
import dev.kelompokceria.smartumkm.user.databinding.CardDashboardBinding
import dev.kelompokceria.smartumkm.user.model.Product
import java.text.NumberFormat
import java.util.Locale

class DashboardProductAdapter(
    private val onItemClicked: (Product) -> Unit,
    private val onSelectionChanged: (Boolean) -> Unit
) : ListAdapter<Product, DashboardProductAdapter.ProductViewHolder>(DashboardDiffCallback()) {

    // Simpan daftar produk yang dipilih
    private val selectedProducts: MutableSet<Product> = mutableSetOf()

    // ViewHolder untuk item produk
    inner class ProductViewHolder(private val binding: CardDashboardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product, isSelected: Boolean) {
            Glide.with(binding.imageProduct.context)
                .load(product.image)
                .placeholder(R.drawable.picture)
                .into(binding.imageProduct)

            val numberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            binding.tvName.text = product.name
            binding.tvCategory.text = product.category
            binding.tvPrice.text = numberFormat.format(product.price)

             if (isSelected) {
                binding.btnAddToCart.visibility = View.GONE
                binding.btnAddToCartCheklist.visibility = View.VISIBLE
            } else {
                binding.btnAddToCart.visibility = View.VISIBLE
                binding.btnAddToCartCheklist.visibility = View.GONE
            }
//            // Set background color based on selection
//            val view = itemView as MaterialCardView
//            view.setCardBackgroundColor(
//                if (isSelected) {
//                    ContextCompat.getColor(view.context, R.color.gray)
//                } else {
//                    ContextCompat.getColor(view.context, R.color.white)
//                }
//            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = CardDashboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        val isSelected = selectedProducts.contains(product)

        holder.bind(product, isSelected)

        // Klik item untuk mengubah seleksi
        holder.itemView.setOnClickListener {
            toggleSelection(product)
            onItemClicked(product)
            notifyItemChanged(position) // Perbarui tampilan item
        }
    }

    private fun toggleSelection(product: Product) {
        if (selectedProducts.contains(product)) {
            selectedProducts.remove(product)
        } else {
            selectedProducts.add(product)
        }
        onSelectionChanged(selectedProducts.isNotEmpty())
    }

    // Perbarui seleksi dari ViewModel
    fun updateSelections(selections: Set<Product>) {
        selectedProducts.clear()
        selectedProducts.addAll(selections)
        onSelectionChanged(selectedProducts.isNotEmpty())
        notifyDataSetChanged() // Anda bisa menggunakan notifyItemRangeChanged jika perlu
    }

    // Metode untuk mendapatkan ID produk yang dipilih
    fun getSelectedProductIds(): List<Int> {
        return selectedProducts.map { it.id!! }
    }

    class DashboardDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem }
    }
}