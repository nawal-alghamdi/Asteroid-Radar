package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRadarRepository
import retrofit2.HttpException

// pre-fetch data when the app is in the background
class RefreshDataWorker(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params){

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    //doWork will run in a background thread
    override suspend fun doWork(): Result {
        //suspend: so Our worker will run until doWork return a result
        val database = getDatabase(applicationContext)
        val repository = AsteroidRadarRepository(database)

        return try {
            repository.refreshAsteroids()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }

}