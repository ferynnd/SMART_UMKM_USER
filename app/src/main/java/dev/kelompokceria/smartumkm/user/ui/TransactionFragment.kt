package dev.kelompokceria.smartumkm.user.ui

import android.content.Intent
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
import com.google.android.material.card.MaterialCardView
import dev.kelompokceria.smartumkm.user.controller.TransactionAdapter
import dev.kelompokceria.smartumkm.user.data.api.UpdateTransactionResponse
import dev.kelompokceria.smartumkm.user.model.TransactionProduct
import dev.kelompokceria.smartumkm.user.model.Transaction
import dev.kelompokceria.smart_umkm.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch
import dev.kelompokceria.smartumkm.user.data.helper.PreferenceHelper
import dev.kelompokceria.smartumkm.user.data.helper.RetrofitHelper
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import dev.kelompokceria.smartumkm.user.R
import dev.kelompokceria.smartumkm.user.data.helper.Constant
import dev.kelompokceria.smartumkm.user.data.helper.NetworkStatusViewModel
import dev.kelompokceria.smartumkm.user.databinding.FragmentTransactionBinding
import dev.kelompokceria.smartumkm.user.viewmodel.ProductViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionFragment : Fragment() {

    private lateinit var binding: FragmentTransactionBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var networkStatusViewModel: NetworkStatusViewModel
    private lateinit var selectedProductIds: List<Int>
    private val productQuantities = mutableMapOf<Int, Int>()
    private var totalHargaSemuaProduk = 0
    private lateinit var userTransaction : String
    private lateinit var sharedPref: PreferenceHelper
    private var isOrderButtonClicked = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        transactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        networkStatusViewModel = ViewModelProvider(this)[NetworkStatusViewModel::class.java]
        val ids = arguments?.getIntArray("KEY_SELECTED_IDS")
        selectedProductIds = ids?.toList() ?: emptyList()
         sharedPref = PreferenceHelper(requireContext())
        val nama =  sharedPref.getString(Constant.PREF_USER_REAL_NAME)
        if (nama != null) {
            userTransaction = nama
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupTransactionDetails()
        hideBottomNavigationView()

        binding.btnBack.setOnClickListener{
            val checkoutFragment = DashboardFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.navigation_fragment, checkoutFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.btnCashback.setOnClickListener {
            updateTotalHargaSemuaProduk()
        }

        binding.btnOrder.setOnClickListener {
                if (validasiCashback()) {
                    isOrderButtonClicked = true
                    saveTransaction()
                    val dashboard = DashboardFragment()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.navigation_fragment, dashboard)
                        .commit()
                    Toast.makeText(requireContext(), "Transaction Success", Toast.LENGTH_SHORT).show()
                }
        }

        return binding.root
    }

    private fun validasiCashback(): Boolean {

        val cash = binding.tvcashback.text.toString()
        if (cash.contains("-")) {
            Toast.makeText(requireContext(), "Nominal Tidak Boleh Minus", Toast.LENGTH_LONG).show()
            return false
        }

        val cashInput = binding.edCashback.text?.toString()


        if (cashInput.isNullOrEmpty()) {
            binding.edCashback.error = "Nominal harus diisi"
            return false
        }

        val cashValue = cashInput?.toIntOrNull()
        if (cashValue == null) {
            binding.edCashback.error = "Nominal harus berupa angka"
            return false
        }

        if (cashValue < totalHargaSemuaProduk) {
            binding.edCashback.error = "Nominal tidak valid atau kurang dari total harga"
            return false
        }

        return true
    }



    override fun onDestroy() {
        super.onDestroy()
        showBottomNavigationView()
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = TransactionAdapter { product, newQuantity ->
                product.id?.let { productId ->
                    productQuantities[productId] = newQuantity
                    updateTotalHargaSemuaProduk()
                }
            }
        }
    }

    private fun updateTotalHargaSemuaProduk() {
        val numberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

        totalHargaSemuaProduk = productQuantities.entries.sumOf { entry ->
            val productId = entry.key
            val quantity = entry.value
            val product = (binding.recyclerView.adapter as TransactionAdapter).currentList.find { it.id == productId }
            product?.price?.times(quantity) ?: 0
        }

        binding.tvtotal.text = numberFormat.format(totalHargaSemuaProduk)

        val cashInput = binding.edCashback.text.toString()
        val totalUang = if (cashInput.isNotEmpty()) cashInput.toInt() else 0
        val cashback = totalUang - totalHargaSemuaProduk
        binding.tvcashback.text = numberFormat.format(cashback)
    }

    private fun setupTransactionDetails() {
        if (selectedProductIds.isEmpty()) {
            Toast.makeText(requireContext(), "Tidak ada produk yang dipilih", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            productViewModel.getProductsByIds(selectedProductIds).observe(viewLifecycleOwner) { products ->
                if (products.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Tidak ada produk yang ditemukan", Toast.LENGTH_SHORT).show()
                } else {
                    products.forEach { product ->
                        if (product != null) {
                            product.id?.let { productId ->
                                productQuantities[productId] = 1
                            }
                        }
                    }
                    (binding.recyclerView.adapter as TransactionAdapter).submitList(products)
                    updateTotalHargaSemuaProduk()
                }
            }

        }
    }

    private fun saveTransaction() {
        val transactionProducts = productQuantities.mapNotNull { entry ->
            val product =
                (binding.recyclerView.adapter as TransactionAdapter).currentList.find { it.id == entry.key }
            product?.let {
                TransactionProduct(
                    name = it.name ?: "Unknown",
                    price = it.price.toString().trim(),
                    quantity = entry.value
                )
            }
        }

        // Buat objek transaksi
        val dataTransaction = Transaction(
            id = "TRX${System.currentTimeMillis()}",
            time = getCurrentDateTime(),
            user = userTransaction,
            total = binding.tvtotal.text.toString().trim(),
            cashback = binding.tvcashback.text.toString().trim(),
            products = transactionProducts,
            createAt = "", // Ini akan diisi oleh server
            updateAt = ""  // Ini akan diisi oleh server
        )

        networkStatusViewModel.networkStatus.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                lifecycleScope.launch(Dispatchers.IO) {
                     transactionViewModel.createTransaction(dataTransaction) { response ->
                         if (isAdded) {
                                  if (response != null) {
                                        Toast.makeText(requireContext(), "Transaction Saved", Toast.LENGTH_SHORT).show()
                                  } else {
                                        Toast.makeText(requireContext(), "Failed to save transaction", Toast.LENGTH_SHORT)
                                            .show()
                                  }
                         }
                    }
                    withContext(Dispatchers.Main){
                         Toast.makeText(requireContext(), "Transaction Success", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    transactionViewModel.insertTransaction(dataTransaction)
                    withContext(Dispatchers.Main){
                         Toast.makeText(requireContext(), "Transaction Saved Offline", Toast.LENGTH_SHORT).show()
                     }
                }
            }
        }
    }


    private fun hideBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<MaterialCardView>(R.id.layoutNavUser)
        bottomNavigationView?.visibility = View.GONE
    }

    private fun showBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<MaterialCardView>(R.id.layoutNavUser)
        bottomNavigationView?.visibility = View.VISIBLE
    }

}
