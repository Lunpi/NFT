package com.example.nft

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.UnknownHostException

class Repository {

    private val httpClient = OkHttpClient.Builder().apply {
        addInterceptor { chain ->
            val request = chain.request().newBuilder().addHeader("X-API-KEY", "5b294e9193d240e39eefc5e6e551ce83").build()
            chain.proceed(request)
        }
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.opensea.io/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient.build())
        .build()

    private val service = retrofit.create(NFTApiService::class.java)

    suspend fun query(offset: Int): List<Collection> {
        return suspendCancellableCoroutine {
            service.getAssets(offset).enqueue(object : Callback<Assets> {

                override fun onResponse(call: Call<Assets>?, response: Response<Assets>?) {
                    val collections = ArrayList<Collection>()
                    response?.body()?.assets?.let { list -> 
                        collections.addAll(list)
                    }
                    it.resumeWith(Result.success(collections))
                }

                override fun onFailure(call: Call<Assets>?, t: Throwable?) {
                    val exception = if (t.toString().contains("UnknownHostException")) UnknownHostException() else Exception(t)
                    it.resumeWith(Result.failure(exception))
                }
            })
        }
    }
}