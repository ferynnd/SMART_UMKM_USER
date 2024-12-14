package dev.kelompokceria.smart_umkm.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.kelompokceria.smartumkm.user.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.data.repository.TransactionRepository
import dev.kelompokceria.smartumkm.user.data.api.UpdateTransactionResponse
import dev.kelompokceria.smartumkm.user.model.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TransactionViewModel (application: Application) : AndroidViewModel(application) {

    private val _allTransac = MutableLiveData<List<Transaction>>()
    val trans : LiveData<List<Transaction>> get() = _allTransac


    private val repository : TransactionRepository

      init {
            val transaksiDao = AppDatabase.getInstance(application).transactionDao()
            repository = TransactionRepository(transaksiDao)
            getAllTransaction()
      }

    private fun getAllTransaction() {
        viewModelScope.launch(Dispatchers.IO) {
            _allTransac.postValue(repository.getAllTransactions())
        }
    }

     suspend fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(transaction)
        }
    }

     fun createTransaction(transaction: Transaction, callback: (UpdateTransactionResponse?) -> Unit) {
        repository.createTransaction(transaction, callback)
    }


    fun refreshTransaction(user: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Ambil transaksi dari API
                val apiTransaction = repository.getTransactionsFromApi(user)

                // Ambil transaksi yang ada di ROOM
                val existingTransaction = repository.getAllTransactions()

                 val transactionsToSync = existingTransaction.filter { localTransaction ->
                        apiTransaction.none { apiTransaction ->
                            apiTransaction.id == localTransaction.id
                        }
                 }

                for (transaction in transactionsToSync) {
                    repository.createTransaction(transaction) { response ->
                        if (response != null) {
                            Toast.makeText(getApplication(), "Success Add Transaction", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(getApplication(), "Failed Add Transaction", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                val newTransaction = apiTransaction.filter { apiTransaction ->
                        existingTransaction.none { existingTransaction ->
                            existingTransaction.id == apiTransaction.id
                        }
                    }
                repository.insertAll(newTransaction)

                val updatedTransaction = repository.getAllTransactions()
                _allTransac.postValue(updatedTransaction)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshDelete(user: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Ambil transaksi dari API
                val apiTransaction = repository.getTransactionsFromApi(user)

                // Ambil transaksi yang ada di ROOM
                val existingTransaction = repository.getAllTransactions()

                val transactionsToSync = existingTransaction.filter { localTransaction ->
                        apiTransaction.none { apiTransaction ->
                            apiTransaction.id == localTransaction.id
                        }
                }

                repository.deleteAlll(transactionsToSync)

                val updatedTransaction = repository.getAllTransactions()
                _allTransac.postValue(updatedTransaction)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}