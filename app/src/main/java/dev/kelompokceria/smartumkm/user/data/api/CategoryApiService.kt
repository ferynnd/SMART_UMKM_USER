package dev.kelompokceria.smartumkm.user.data.api

import dev.kelompokceria.smartumkm.user.model.ProductCategory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CategoryApiService {

    @GET("categoryproduct")
    suspend fun getCategoryList(): ProductCategoryApiResponse

    @POST("categoryproduct")
    suspend fun createCategory(
        @Body category: ProductCategory
    ): UpdateProductCategoryResponse

    @DELETE("categoryproduct/{categoryId}")
    suspend fun deleteCategory(
        @Path("categoryId") categoryId: Int
    ): UpdateProductCategoryResponse



}