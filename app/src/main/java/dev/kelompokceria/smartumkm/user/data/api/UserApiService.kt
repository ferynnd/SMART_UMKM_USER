package dev.kelompokceria.smartumkm.user.data.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApiService {

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("username") userName: String,
        @Field("password") userPassword: String
    ): LoginResponse

    @GET("user/username/{username}")
    suspend fun getUserByUsername(
        @Path("username") username: String
    ): UpdateUserResponse

}