package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.database.AsteroidRadarDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.ImageOfTheDay
import com.udacity.asteroidradar.network.NasaApi
import com.udacity.asteroidradar.network.asDatabaseModel
import com.udacity.asteroidradar.network.parseAsteroidsJsonResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// AsteroidRadarRepository is responsible for providing a simple API to our data sources
class AsteroidRadarRepository(private val database: AsteroidRadarDatabase) {

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids(getCurrentDate())) {
            it.asDomainModel()
        }

    val imageOfTheDay: LiveData<ImageOfTheDay> =
        Transformations.map(database.imageOfTheDayDao.getImageOfTheDay()) {
            it?.asDomainModel()
        }


    // Make it a suspend function since it will be called from a coroutine.
    // responsible for updating the offline cash
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val asteroidsList = parseAsteroidsJsonResult(
                    JSONObject(NasaApi.retrofitService.getAsteroidsAsync(BuildConfig.API_KEY)
                        .await().toString()))

                database.asteroidDao.insertAll(*asteroidsList.asDatabaseModel())

            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (e: Exception) {
                Log.w(TAG, "refreshAsteroids: Exception = $e")
            }
        }
    }

    suspend fun refreshImageOfTheDay() {
        withContext(Dispatchers.IO) {
            try {
                val imageOfTheDay = NasaApi.retrofitService.getImageOfTheDayAsync().await()
                database.imageOfTheDayDao.deleteAndInsert(imageOfTheDay.asDatabaseModel())
            } catch (e: Exception) {
                Log.w(TAG, "refreshImageOfTheDay: Exception = $e")
            }
        }
    }

    companion object {
        const val TAG = "AsteroidsRepository"
        fun getCurrentDate(): String {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return current.format(formatter)
        }
    }

}