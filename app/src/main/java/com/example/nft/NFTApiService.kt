package com.example.nft

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NFTApiService {
    @GET("v1/assets?format=json&owner=0x19818f44faf5a217f619aff0fd487cb2a55cca65&limit=20")
    fun getAssets(@Query("offset") offset: Int): Call<Assets>
}