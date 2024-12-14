package dev.kelompokceria.smartumkm.user.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import dev.kelompokceria.smartumkm.user.controller.DashboardProductAdapter
import dev.kelompokceria.smartumkm.user.data.helper.PreferenceHelper
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import dev.kelompokceria.smartumkm.user.R
import dev.kelompokceria.smartumkm.user.data.helper.Constant
import dev.kelompokceria.smartumkm.user.data.helper.NetworkStatusViewModel
import dev.kelompokceria.smartumkm.user.databinding.FragmentDashboardBinding
import dev.kelompokceria.smartumkm.user.model.Product
import dev.kelompokceria.smartumkm.user.viewmodel.ProductCategoryViewModel
import dev.kelompokceria.smartumkm.user.viewmodel.ProductViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding


    private lateinit var productViewModel: ProductViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var dashboardProductAdapter: DashboardProductAdapter
    private lateinit var networkStatusViewModel: NetworkStatusViewModel
    private lateinit var categoryViewModel: ProductCategoryViewModel

    private lateinit var sharedPref: PreferenceHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        networkStatusViewModel = ViewModelProvider(this)[NetworkStatusViewModel::class.java]
        categoryViewModel = ViewModelProvider(this)[ProductCategoryViewModel::class.java]
        sharedPref = PreferenceHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)

        binding.swiperefresh.setOnRefreshListener {
            lifecycleScope.launch {
                try {
                    productViewModel.refreshProducts()
                    productViewModel.refreshDeleteProducts()
                    categoryViewModel.refreshProductCategory()
                    categoryViewModel.refreshDeleteProductCategory()
                } catch (e: Exception) {
                    Toast.makeText( requireContext(),"Error fetching product data", Toast.LENGTH_SHORT).show()
                } finally {
                    binding.swiperefresh.isRefreshing = false
                }
            }
        }

        categoryViewModel.productCategory.observe(viewLifecycleOwner) { productCategories ->
            // Menghapus tab yang ada sebelum menambahkan yang baru
            binding.tabLayout.removeAllTabs()

            // Menambahkan tab "ALL"
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("ALL"))

            // Menambahkan tab kategori secara dinamis
            for (category in productCategories) {
                binding.tabLayout.addTab(binding.tabLayout.newTab().setText(category.name))
            }
        }

        // Menangani pemilihan tab
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // Menghapus pengamat sebelumnya untuk menghindari kebocoran memori
                productViewModel.products.removeObservers(viewLifecycleOwner)

                if (tab.text == "ALL") {
                    productViewModel.getAllProduct()
                    productViewModel.products.observe(viewLifecycleOwner) { products ->
                        dashboardProductAdapter.submitList(products)
                    }
                } else {
                    // Mengambil produk berdasarkan kategori
                    productViewModel.getProductByCategory(tab.text.toString())
                    productViewModel.products.observe(viewLifecycleOwner) { products ->
                        dashboardProductAdapter.submitList(products)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // Tidak perlu melakukan apa-apa di sini
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Tidak perlu melakukan apa-apa di sini
            }
        })


        setupRecyclerView()
        setupProductObservers()
        setupCheckoutButton()
        showBottomNavigationView()
        setupUserObservers()


        return binding.root
    }

    private fun setupRecyclerView() {
        dashboardProductAdapter = DashboardProductAdapter(
            { product -> productViewModel.toggleSelection(product) },
            { isVisible -> toggleCheckoutButton(isVisible)}
        )

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)

            adapter = dashboardProductAdapter
        }

    }

    private fun setupUserObservers() {
        val username = sharedPref.getString(Constant.PREF_USER_NAME)
        if (username != null) {

            networkStatusViewModel.networkStatus.observe(viewLifecycleOwner) { isNetworkAvailable ->
                if (isNetworkAvailable) {
                    userViewModel.getUserByUsername(username)
                } else {
                    userViewModel.getUserByNameRoom(username)
                }
            }


            userViewModel.loggedInUser.observe(viewLifecycleOwner) { user ->
                if (user != null) {

                     user.image.let {
                            Glide.with(binding.imageProfile.context)
                                .load(it)
                                .placeholder(R.drawable.picture) // Placeholder if image is unavailable
                                .into(binding.imageProfile)
                        }

                    binding.tvName.text = "Hey ${user.name.toString().toUpperCase()},"

                } else {
                    Toast.makeText(requireContext(), "User tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Username tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupProductObservers() {
        productViewModel.products.observe(viewLifecycleOwner) { products ->
            lifecycleScope.launch(Dispatchers.Main) {
                if (products.isNotEmpty()) {
                    dashboardProductAdapter.submitList(products)
                } else {
                    dashboardProductAdapter.submitList(emptyList())
                    dashboardProductAdapter.notifyDataSetChanged()
                }
           }
        }

        productViewModel.selectedPositions.observe(viewLifecycleOwner) { selectedProducts ->
            dashboardProductAdapter.updateSelections(selectedProducts)
        }
    }



    private fun setupCheckoutButton() {
        binding.btnCheckout.setOnClickListener {
            val selectedIds = dashboardProductAdapter.getSelectedProductIds()
            val bundle = Bundle().apply {
                putIntArray("KEY_SELECTED_IDS", selectedIds.toIntArray())
            }
            val checkoutFragment = TransactionFragment()
            checkoutFragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.navigation_fragment, checkoutFragment)
                .commit()
        }
    }

    private fun toggleCheckoutButton(isVisible: Boolean) {
        binding.btnCheckout.visibility = if (isVisible) View.VISIBLE else View.GONE
        val bottomNavigationView = activity?.findViewById<MaterialCardView>(R.id.layoutNavUser)
        bottomNavigationView?.visibility = if (isVisible) View.GONE else View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }

    private fun showBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<MaterialCardView>(R.id.layoutNavUser)
        bottomNavigationView?.visibility = View.VISIBLE
    }


}
