package dev.kelompokceria.smartumkm.user.data.repository

import dev.kelompokceria.smartumkm.user.data.dao.ProductCategoryDao
import dev.kelompokceria.smartumkm.user.data.helper.RetrofitHelper
import dev.kelompokceria.smartumkm.user.model.ProductCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

    private val ApiProduct = RetrofitHelper.productCategoryApiService

class ProductCategoryRespository(private val productCategoryDao: ProductCategoryDao) {


     suspend fun getAllProductCategory(): List<ProductCategory> {
          return withContext(Dispatchers.IO) {
            productCategoryDao.getAllProductCategory()
        }
    }

    suspend fun insertAll(productCategory: List<ProductCategory>) {
        productCategoryDao.insertAll(productCategory)
    }
    suspend fun deleteAll(productCategory: List<ProductCategory>) {
        productCategoryDao.deleteAll(productCategory)
    }

    suspend fun getProductCategoryFromApi(): List<ProductCategory> {
        val response = ApiProduct.getCategoryList()
        return response.data
    }


}