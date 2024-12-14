package dev.kelompokceria.smartumkm.user.data.repository

import androidx.lifecycle.LiveData
import dev.kelompokceria.smartumkm.user.data.dao.ProductDao
import dev.kelompokceria.smartumkm.user.data.helper.RetrofitHelper
import dev.kelompokceria.smartumkm.user.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val ApiProduct = RetrofitHelper.productApiService

class ProductRepository(private val productDao: ProductDao) {

    suspend fun getAllProducts(): List<Product> {
          return withContext(Dispatchers.IO) {
            productDao.getAllProducts()
        }
    }

    suspend fun insertAll(products: List<Product>) {
        productDao.insertAll(products)
    }

    suspend fun deleteAll(products: List<Product>) {
        productDao.deleteAll(products)
    }

    suspend fun getProductsFromApi(): List<Product> {
        val response = ApiProduct.getProductList()
        return response.data
    }

    fun getProductByIds(ids: List<Int>): LiveData<List<Product?>> {
        return productDao.getProductsByIds(ids)
    }



}