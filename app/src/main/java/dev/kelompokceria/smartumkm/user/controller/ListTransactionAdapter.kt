package dev.kelompokceria.smartumkm.user.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smartumkm.user.databinding.CardCategoryLayoutBinding
import dev.kelompokceria.smartumkm.user.databinding.CardListTransactionBinding
import dev.kelompokceria.smartumkm.user.model.Transaction
import java.text.SimpleDateFormat
import java.util.Locale

class ListTransactionAdapter :
    ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback()) {

    inner class TransactionViewHolder(private val binding: CardListTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            binding.transactionid.text= transaction.id
            // Format input tetap sama karena data dari transaction.transactionTime dalam format "yyyy-MM-dd"
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            // Format output sesuai yang diinginkan "dd MMM yy" dengan Locale.ENGLISH
            val outputFormat = SimpleDateFormat("dd MMM yy", Locale.ENGLISH) // Langsung set Locale di konstruktor

//            try {
//                // Parse tanggal dari string input
//                val date = inputFormat.parse(transaction.time)
//                // Format tanggal ke output yang diinginkan
//                date?.let {
//                    binding.timestamp.text = outputFormat.format(it).uppercase() // Ubah ke uppercase agar tampil: 06 JAN 24
//                } ?: run {
//                    binding.timestamp.text = "Invalid date"
//                }
//            } catch (e: Exception) {
//                binding.timestamp.text = "Invalid date"
//            }

            binding.timestamp.text = transaction.time?.uppercase()

            binding.propertyName.text = transaction.user
            binding.amount.text = transaction.total
            binding.propertyName.text = "CASHIER - ${transaction.user}"

        }
    }

    inner class CatergoryViewHolder(val binding: CardCategoryLayoutBinding) : RecyclerView.ViewHolder(binding.root)

     enum class TYPE_VIEW { HEADER , CONTENT }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Transaction -> TYPE_VIEW.CONTENT.ordinal
              is String -> TYPE_VIEW.HEADER.ordinal
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            TYPE_VIEW.CONTENT.ordinal -> {
                val binding = CardListTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return TransactionViewHolder(binding)
            }
            TYPE_VIEW.HEADER.ordinal -> {
                val binding = CardCategoryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return CatergoryViewHolder(binding)
            }
             else -> throw IllegalArgumentException("Unknown viewType: $viewType")

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
         val item = getItem(position)
        when(holder) {
               is TransactionViewHolder -> {
                   val transaction = getItem(position) as Transaction
                   holder.bind(transaction)
               }
               is CatergoryViewHolder -> {
                   holder.binding.textViewHeader.text = (item as String)
               }
           }

    }

    class DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when ( oldItem) {
                is Transaction -> {
                    if (newItem is Transaction) {
                        (oldItem.id) == (newItem.id)
                    } else {
                        false
                    }
                }
                else -> {
                    if (newItem is Transaction) {
                        false
                    } else {
                        (oldItem) == (newItem)
                    }
                }
            } // Compare by unique ID
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when ( oldItem) {
                is Transaction -> {
                    if (newItem is Transaction) {
                        (oldItem) == (newItem)
                    } else {
                        false
                    }
                }
                else -> {
                    if (newItem is Transaction) {
                        false
                    } else {
                        (oldItem) == (newItem)
                    }
                }
            } // Compare by unique ID
        }
    }

}
