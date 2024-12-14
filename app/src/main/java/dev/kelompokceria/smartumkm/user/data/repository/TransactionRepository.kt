package dev.kelompokceria.smart_umkm.data.repository

import dev.kelompokceria.smartumkm.user.data.api.UpdateTransactionResponse
import dev.kelompokceria.smartumkm.user.data.dao.TransactionDao
import dev.kelompokceria.smartumkm.user.data.helper.RetrofitHelper
import dev.kelompokceria.smartumkm.user.model.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val ApiTransaction = RetrofitHelper.transactionApiService
class TransactionRepository(private val transactionDao: TransactionDao) {

    suspend fun getAllTransactions(): List<Transaction> {
        return withContext(Dispatchers.IO) {
            transactionDao.getAllTransactions()
        }
    }

    suspend fun insertAll(transactions: List<Transaction>) {
        transactionDao.insertAll(transactions)
    }

    suspend fun getTransactionsFromApi(user: String): List<Transaction> {
        val response = ApiTransaction.getTransactionList(user)
        return response.data
    }


    fun createTransaction(transaction: Transaction, callback: (UpdateTransactionResponse?) -> Unit) {
        ApiTransaction.createTransaction(transaction).enqueue(object :
            Callback<UpdateTransactionResponse> {
            override fun onResponse(call: Call<UpdateTransactionResponse>, response: Response<UpdateTransactionResponse>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null) // Atau tangani error sesuai kebutuhan
                }
            }

            override fun onFailure(call: Call<UpdateTransactionResponse>, t: Throwable) {
                callback(null) // Atau tangani error sesuai kebutuhan
            }
        })
    }

    suspend fun insert(transaction: Transaction) {
        transactionDao.insert(transaction)
    }

    suspend fun deleteAlll(transactions: List<Transaction>) {
        transactionDao.deleteAll(transactions)
    }



}