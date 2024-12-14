package dev.kelompokceria.smartumkm.user.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import dev.kelompokceria.smart_umkm.viewmodel.TransactionViewModel
import dev.kelompokceria.smartumkm.user.R
import dev.kelompokceria.smartumkm.user.data.helper.Constant
import dev.kelompokceria.smartumkm.user.data.helper.NetworkStatusViewModel
import dev.kelompokceria.smartumkm.user.data.helper.PreferenceHelper
import dev.kelompokceria.smartumkm.user.databinding.ActivityUserBinding
import dev.kelompokceria.smartumkm.user.viewmodel.ProductViewModel
import kotlinx.coroutines.launch


class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding

     private var myData: String? = null
    // Fragments
    val dashboardFragment = DashboardFragment()
    private val profileFragment = ProfileFragment()
    private val listTransactionFragment = ListTransactionFragment()
    val transactionFragment = TransactionFragment()

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var networkStatusViewModel: NetworkStatusViewModel
    private lateinit var productViewModel: ProductViewModel

    private lateinit var sharedPref: PreferenceHelper

    // Active fragment
    private lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityUserBinding.inflate(layoutInflater)
    setContentView(binding.root)

        // Initialize ViewModels
        transactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        networkStatusViewModel = ViewModelProvider(this)[NetworkStatusViewModel::class.java]
        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        // Observe network status
        networkStatusViewModel.networkStatus.observe(this, Observer { isConnected ->
            if (isConnected) {
                val sharedPref = PreferenceHelper(this)
                val nama = sharedPref.getString(Constant.PREF_USER_REAL_NAME)
                if (nama != null) {
                    transactionViewModel.refreshTransaction(nama)
                    transactionViewModel.refreshDelete(nama)
                }
                productViewModel.refreshProducts()
                productViewModel.refreshDeleteProducts()
            } else {
                showConnectionErrorDialog()
            }
        })

            loadFragment(dashboardFragment)
            binding.bottomNavUser.setOnItemSelectedListener {
                when (it.itemId) {
                        R.id.dashboard -> loadFragment(dashboardFragment)
                        R.id.user_profile -> loadFragment(profileFragment)
                        R.id.list_transaction -> loadFragment(listTransactionFragment)
                        else -> false
                }
                true
            }

    }


    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.navigation_fragment,fragment)
        transaction.commit()
    }

     private fun showConnectionErrorDialog() {
        val connectionErrorDialog = ConnectionDialog(this)
        connectionErrorDialog.show {
            // This is the retry callback
            // Implement your retry logic here
            Toast.makeText(this, "Retrying connection...", Toast.LENGTH_SHORT).show()
            // Retry the connection
        }
    }

}
