package com.udacity.asteroidradar.network

import com.google.gson.JsonObject
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.util.Constants
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Use the Retrofit builder to build a retrofit object using a Scalars converter
 */
private val retrofit = Retrofit.Builder()
    .baseUrl(Constants.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .build()


/**
 * A public interface that exposes the [getAsteroidsAsync] method
 */
interface NasaApiService {

    @GET("neo/rest/v1/feed")
    fun getAsteroidsAsync(@Query("api_key") key: String = BuildConfig.API_KEY): Deferred<JsonObject>

    @GET("planetary/apod")
    fun getImageOfTheDayAsync(@Query("api_key") key: String = BuildConfig.API_KEY): Deferred<NetworkImageOfTheDay>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object NasaApi {
    val retrofitService: NasaApiService by lazy { retrofit.create(NasaApiService::class.java) }
}