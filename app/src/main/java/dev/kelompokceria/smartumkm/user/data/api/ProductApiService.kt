package dev.kelompokceria.smartumkm.user.data.api

import retrofit2.http.GET

interface ProductApiService {

    @GET("product")
    suspend fun getProductList(): ProductApiResponse


}