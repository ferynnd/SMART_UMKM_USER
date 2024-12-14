package dev.kelompokceria.smartumkm.user.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update
import dev.kelompokceria.smartumkm.user.model.Product

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    @Delete
    suspend fun deleteAll(products: List<Product>)


    @Delete
    suspend fun delete(product: Product)

    @Query("SELECT * FROM product_table ORDER BY id ASC")
    suspend fun getAllProducts(): List<Product>

    @Query("SELECT * FROM product_table WHERE id IN (:ids)")
    fun getProductsByIds(ids: List<Int>): LiveData<List<Product?>>

}