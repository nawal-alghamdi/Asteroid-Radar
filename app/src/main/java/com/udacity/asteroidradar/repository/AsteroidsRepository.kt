package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.database.AsteroidRadarDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.AsteroidApi
import com.udacity.asteroidradar.network.asDatabaseModel
import com.udacity.asteroidradar.network.parseAsteroidsJsonResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//AsteroidsRepository: responsible for providing a simple API to our data sources
class AsteroidsRepository(private val database: AsteroidRadarDatabase) {

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids(getCurrentDate())) {
            it.asDomainModel()
        }

    // Make it a suspend function since it will be called from a coroutine.
    // responsible for updating the offline cash
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val asteroidsList = parseAsteroidsJsonResult(
                    JSONObject(AsteroidApi.retrofitService.getAsteroids(BuildConfig.API_KEY)
                        .await().toString()))

                database.asteroidDao.insertAll(*asteroidsList.asDatabaseModel())

            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (e: Exception) {
                Log.w("AsteroidsRepository", "refreshAsteroids: Exception = $e")
            }
        }
    }

    companion object {
        fun getCurrentDate() : String{
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return current.format(formatter)
        }
    }

}