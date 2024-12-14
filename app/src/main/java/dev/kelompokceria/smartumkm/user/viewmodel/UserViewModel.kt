package dev.kelompokceria.smart_umkm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import dev.kelompokceria.smartumkm.user.data.api.UserApiService
import dev.kelompokceria.smartumkm.user.data.database.AppDatabase
import dev.kelompokceria.smartumkm.user.data.helper.NetworkStatusViewModel
import dev.kelompokceria.smartumkm.user.data.repository.UserRepository
import dev.kelompokceria.smartumkm.user.model.User
import dev.kelompokceria.smartumkm.user.ui.DashboardFragment
import kotlinx.coroutines.launch

private lateinit var  userApiService : UserApiService
private  lateinit var networkStatusViewModel: NetworkStatusViewModel

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository : UserRepository

    private val _allUser = MutableLiveData<List<User>>()
    val allUser : LiveData<List<User>> get() = _allUser


    init {
        val userDao = AppDatabase.getInstance(application).userDao()
        repository = UserRepository(userDao)
    }


    private val _loggedInUser = MutableLiveData<User?>()
    val loggedInUser: LiveData<User?> = _loggedInUser

    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> = _loginStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getUserLogin(username: String, password: String) = viewModelScope.launch {
        val result = repository.loginUser(username, password)
        if (result.status) {
            _loginStatus.postValue(true)
            getUserByUsername(username)
        } else {
            _loginStatus.postValue(false)
            _errorMessage.postValue("Login failed: ${result.message}")
        }
    }

    fun getUserByUsername(username: String) {
        viewModelScope.launch {
            try {
                val user = repository.getUserByUserName(username)
                if (user.status){
                    _loginStatus.postValue(true)
                    _loggedInUser.postValue(user.data)
                    user.data!!.let { repository.insert(it) }
                } else {
                     _loggedInUser.postValue(user.data)
                    user.data!!.let { repository.insert(it) }
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error fetching user data: ${e.message}")
                _loginStatus.postValue(false)
            }
        }
    }

    fun getUserByNameRoom(username: String) {
        viewModelScope.launch {
            try {
                val user = repository.getUserByName(username)
                if (user != null) {
                    _loggedInUser.postValue(user)
                    _loginStatus.postValue(true)
                } else {
                    _loggedInUser.postValue(null)
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error fetching user data: ${e.message}")
                _loginStatus.postValue(false)
            }
        }
    }




}



