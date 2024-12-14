package dev.kelompokceria.smartumkm.user.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kelompokceria.smart_umkm.viewmodel.TransactionViewModel
import dev.kelompokceria.smartumkm.user.R
import dev.kelompokceria.smartumkm.user.controller.ListTransactionAdapter
import dev.kelompokceria.smartumkm.user.data.helper.Constant
import dev.kelompokceria.smartumkm.user.data.helper.PreferenceHelper
import dev.kelompokceria.smartumkm.user.data.helper.RetrofitHelper
import dev.kelompokceria.smartumkm.user.databinding.FragmentListTransactionBinding
import dev.kelompokceria.smartumkm.user.model.Transaction
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ListTransactionFragment : Fragment() {

    private lateinit var binding: FragmentListTransactionBinding

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var listTransactionAdapter: ListTransactionAdapter
    private lateinit var sharedPref: PreferenceHelper

    private val groupedData = mutableListOf<Any>()

     private var myData: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        sharedPref = PreferenceHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListTransactionBinding.inflate(layoutInflater)

        val username = sharedPref.getString(Constant.PREF_USER_REAL_NAME)
        fetchTransactions()

         binding.swiperefresh.setOnRefreshListener {
            lifecycleScope.launch {
                try {
                    if (username != null) {
                        transactionViewModel.refreshTransaction(username)
                        transactionViewModel.refreshDelete(username)
                    }
                } catch (e: Exception) {
                    Toast.makeText( requireContext(),"Error fetching product data", Toast.LENGTH_SHORT).show()
                } finally {
                    binding.swiperefresh.isRefreshing = false
                }
            }
        }

        listTransactionAdapter = ListTransactionAdapter()
        binding.transactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ListTransactionFragment.listTransactionAdapter
        }

        return binding.root
    }


    private fun fetchTransactions() {
        transactionViewModel.trans.observe(viewLifecycleOwner) { transactions ->
            transactions.let {
                lifecycleScope.launch {
                    if (transactions.isNotEmpty()) {
                        listTransactionAdapter.submitList(it)
                        setTransaction(it)

                    } else {
                        listTransactionAdapter.submitList(emptyList())
                        listTransactionAdapter.notifyDataSetChanged() // Memaksa pembaruan adapter
                    }
                }
            }

        }


    }

    private fun setTransaction(newProducts: List<Transaction>) {
        groupedData.clear()

        // Format tanggal untuk parsing dan pengelompokan
        val formatTanggal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatBulan = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

        // Urutkan transaksi berdasarkan waktu
        val sortedTransactions = newProducts.sortedByDescending { it.time }

        // Kelompokkan transaksi berdasarkan bulan
        val groupedByMonth = sortedTransactions.groupBy { transaksi ->
            val date = formatTanggal.parse(transaksi.time)
            date?.let { formatBulan.format(it) } ?: "Unknown" // Handle null values
        }

        // Bangun data untuk adapter
        groupedByMonth.forEach { (bulan, transaksiList) ->
            groupedData.add(bulan) // Tambahkan header bulan
            groupedData.addAll(transaksiList) // Tambahkan transaksi di bulan itu
        }

        // Kirim data yang sudah dikelompokkan ke adapter
        listTransactionAdapter.submitList(groupedData)
    }


     override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("myDataKey", myData)
    }

}