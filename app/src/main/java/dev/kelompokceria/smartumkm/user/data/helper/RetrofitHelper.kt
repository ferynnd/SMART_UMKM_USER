package dev.kelompokceria.smartumkm.user.data.helper

import dev.kelompokceria.smartumkm.user.data.api.CategoryApiService
import dev.kelompokceria.smartumkm.user.data.api.ProductApiService
import dev.kelompokceria.smartumkm.user.data.api.TransactionApiService
import dev.kelompokceria.smartumkm.user.data.api.UserApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

    private const val BASE_URL = "https://smartumkm.infitechd.my.id/public/api/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()


    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val productApiService: ProductApiService by lazy {
        retrofit.create(ProductApiService::class.java)
    }

    val productCategoryApiService: CategoryApiService by lazy {
        retrofit.create(CategoryApiService::class.java)
    }

    val userApiService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }

    val transactionApiService: TransactionApiService by lazy {
        retrofit.create(TransactionApiService::class.java)
    }


}
