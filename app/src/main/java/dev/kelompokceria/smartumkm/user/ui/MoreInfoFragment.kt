package dev.kelompokceria.smart_umkm.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import dev.kelompokceria.smartumkm.user.R
import dev.kelompokceria.smartumkm.user.data.helper.Constant
import dev.kelompokceria.smartumkm.user.data.helper.NetworkStatusViewModel
import dev.kelompokceria.smartumkm.user.data.helper.PreferenceHelper
import dev.kelompokceria.smartumkm.user.databinding.FragmentMoreInfoBinding

class MoreInfoFragment : Fragment() {
    private lateinit var binding: FragmentMoreInfoBinding

    private lateinit var userViewModel: UserViewModel
    private lateinit var networkStatusViewModel: NetworkStatusViewModel
    private lateinit var sharedPref: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
          networkStatusViewModel = ViewModelProvider(this)[NetworkStatusViewModel::class.java]
        sharedPref = PreferenceHelper(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoreInfoBinding.inflate(inflater, container, false)

        hideBottomNavigationView()
        val username = sharedPref.getString(Constant.PREF_USER_NAME)
        if (username == null) {
            Toast.makeText(requireContext(), "Username tidak ditemukan", Toast.LENGTH_SHORT).show()
        } else {
            // Ambil data user berdasarkan username
            networkStatusViewModel.networkStatus.observe(viewLifecycleOwner) { isConnected ->
                    if (isConnected) {
                        userViewModel.getUserByUsername(username)
                    } else {
                        Toast.makeText(requireContext(), "Network Disconnected", Toast.LENGTH_SHORT).show()
                        userViewModel.getUserByNameRoom(username)
                    }

            }

            // Observe LiveData dari userViewModel
            userViewModel.loggedInUser.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    user.image.let {
                        Glide.with(binding.ivProfile.context)
                            .load(it)
                            .placeholder(R.drawable.picture) // Placeholder if image is unavailable
                            .into(binding.ivProfile)
                    } ?: run {
                        binding.ivProfile.setImageResource(R.drawable.picture) // Default image
                    }
                    binding.userName.text = user.name
                    binding.userRole.text = user.role.toString()
                    binding.nameTextView.text = user.name
                    binding.emailTextView.text = user.email
                    binding.phoneTextView.text = user.phone
                    binding.usernameTextView.text = user.username
                } else {
                    Toast.makeText(requireContext(), "User tidak ditemukan", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            // Set up klik listener untuk tombol kembali
            binding.backButton.setOnClickListener {
                // Kembali ke fragment sebelumnya (AdminProfileFragment)
                requireActivity().onBackPressed()
            }

        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        showBottomNavigationView()
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
