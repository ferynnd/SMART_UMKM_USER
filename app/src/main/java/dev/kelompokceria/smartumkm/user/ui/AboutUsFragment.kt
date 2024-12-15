package dev.kelompokceria.smart_umkm.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.card.MaterialCardView
import dev.kelompokceria.smartumkm.user.R
import dev.kelompokceria.smartumkm.user.databinding.FragmentAboutUsBinding


class AboutUsFragment : Fragment() {

    private lateinit var binding: FragmentAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAboutUsBinding.inflate(layoutInflater)

        binding.backButton.setOnClickListener {
            // Navigate back to the previous screen
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        hideBottomNavigationView()

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