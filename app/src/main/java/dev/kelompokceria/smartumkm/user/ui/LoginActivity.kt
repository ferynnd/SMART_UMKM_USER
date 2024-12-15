package dev.kelompokceria.smartumkm.user.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dev.kelompokceria.smartumkm.user.data.helper.Constant
import dev.kelompokceria.smartumkm.user.data.helper.NetworkStatusViewModel
import dev.kelompokceria.smartumkm.user.data.helper.PreferenceHelper
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import dev.kelompokceria.smartumkm.user.databinding.ActivityLoginBinding
import dev.kelompokceria.smartumkm.user.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPref: PreferenceHelper
    private val userViewModel: UserViewModel by viewModels()

    private val networkStatusViewModel: NetworkStatusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = PreferenceHelper(this)

        checkLogin()

        networkStatusViewModel.networkStatus.observe(this){ isConnected ->
            if (isConnected) {
                Toast.makeText(this, "Network is connected", Toast.LENGTH_SHORT).show()
                binding.btnLogin.setOnClickListener {
                    val username = binding.edUsername.text.toString()
                    val password = binding.edPassword.text.toString()

                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            userViewModel.getUserLogin(username, password)
                        }
                    } else {
                        Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                showConnectionErrorDialog()
            }
        }

        userViewModel.loggedInUser.observe(this) { user ->
            if (user != null) {
                sharedPref.put(Constant.PREF_IS_LOGIN, true)
                user.username?.let { sharedPref.put(Constant.PREF_USER_NAME, it) }
                user.password?.let { sharedPref.put(Constant.PREF_USER_PASSWORD, it) }
                user.name?.let { sharedPref.put(Constant.PREF_USER_REAL_NAME, it) }
                user.role?.let { sharedPref.put(Constant.PREF_USER_ROLE, it.name) }
                user.role?.let { navigateToRole(it) }
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLogin() {
        if (sharedPref.getBoolean(Constant.PREF_IS_LOGIN)) {
            val role = sharedPref.getString(Constant.PREF_USER_ROLE)
            if (role == UserRole.USER.toString()) {
                navigateToRole(UserRole.USER)
            } else {
                sharedPref.clear()
            }
        }
    }

    private fun navigateToRole(role: UserRole) {
        startActivity(Intent(this, UserActivity::class.java))
        finish()
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
