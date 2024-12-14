package dev.kelompokceria.smartumkm.user.data.repository

import dev.kelompokceria.smartumkm.user.data.api.LoginResponse
import dev.kelompokceria.smartumkm.user.data.api.UpdateUserResponse
import dev.kelompokceria.smartumkm.user.data.dao.UserDao
import dev.kelompokceria.smartumkm.user.data.helper.RetrofitHelper
import dev.kelompokceria.smartumkm.user.model.User

private val ApiUser = RetrofitHelper.userApiService

class UserRepository(private val userDao: UserDao) {


    suspend fun getUserByUserName(username: String): UpdateUserResponse {
        return try {
            val response = ApiUser.getUserByUsername(username)
            response // Mengembalikan response dari API
        } catch (e: Exception) {
            UpdateUserResponse(
                status = false,
                message = e.message ?: "An error occurred",
                data = null
            )
        }
    }

    suspend fun getUserByName(username: String): User? {
        return userDao.getUserByName(username)
    }



     suspend fun loginUser(username: String, password: String): LoginResponse {
        return try {
            ApiUser.login( username, password)
        } catch (e: Exception) {
            LoginResponse(
                status = false,
                message = e.message ?: "An error occurred",
                data = null
            )
        }

    }

    suspend fun insert(user: User) {
        userDao.insert(user)
    }


}