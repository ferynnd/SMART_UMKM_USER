package dev.kelompokceria.smartumkm.user.data.api

import dev.kelompokceria.smartumkm.user.model.Transaction
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TransactionApiService {

    @GET("transaction/user")
    suspend fun getTransactionList(
        @Query("query") user: String
    ): TransactionApiResponse

    @POST("transaction") // Adjust the endpoint as necessary
    fun createTransaction(@Body transaction: Transaction): Call<UpdateTransactionResponse>


}