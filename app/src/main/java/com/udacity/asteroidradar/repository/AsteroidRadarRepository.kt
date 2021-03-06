package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
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
                    JSONObject(
                        NasaApi.retrofitService.getAsteroidsAsync(startDate = getCurrentDate())
                            .await().toString()
                    )
                )

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

    suspend fun deleteOldAsteroids() {
        withContext(Dispatchers.IO) {
            database.asteroidDao.deleteOldAsteroids(getCurrentDate())
        }
    }

    suspend fun getAsteroidsByDate(startDate: String?, endDate: String?): List<Asteroid> {
         var asteroidList: List<Asteroid> = ArrayList()
        withContext(Dispatchers.IO) {
            asteroidList = if (startDate == null || endDate == null) {
                database.asteroidDao.getSavedAsteroids().asDomainModel()
            } else {
                database.asteroidDao.getAsteroidsByDate(startDate, endDate).asDomainModel()
            }
        }
        return asteroidList
    }

    companion object {
        const val TAG = "AsteroidsRepository"
        fun getCurrentDate(): String {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return current.format(formatter)
        }

        fun getDateAfterSixDays(): String {
            val current = LocalDateTime.now()
            val newDate = current.plusDays(6)
            return newDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }
    }

}