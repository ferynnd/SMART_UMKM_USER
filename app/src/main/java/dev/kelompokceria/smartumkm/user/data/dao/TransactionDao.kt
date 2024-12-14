package dev.kelompokceria.smartumkm.user.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.kelompokceria.smartumkm.user.model.Transaction

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transaction_table WHERE id = :id")
    suspend fun getTransactionById(id: String): Transaction

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<Transaction>)

    @Delete
    suspend fun deleteAll(transactions: List<Transaction>)
//
//    @Delete
//    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transaction_table ORDER BY id ASC")
    fun getAllTransactions(): List<Transaction>

}