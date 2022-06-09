package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.database.AsteroidRadarDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.AsteroidApi
import com.udacity.asteroidradar.network.asDatabaseModel
import com.udacity.asteroidradar.network.parseAsteroidsJsonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

//AsteroidsRepository: responsible for providing a simple API to our data sources
class AsteroidsRepository(private val database: AsteroidRadarDatabase) {

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    // Make it a suspend function since it will be called from a coroutine.
    // responsible for updating the offline cash
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val asteroidsList = parseAsteroidsJsonResult(
                JSONObject(
                    AsteroidApi.retrofitService.getAsteroids(
                        BuildConfig.API_KEY
                    ).toString()
                )
            )

            database.asteroidDao.insertAll(*asteroidsList.asDatabaseModel())
        }
    }
}