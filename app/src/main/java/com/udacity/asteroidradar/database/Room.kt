package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao{
     //When we return a livedata room will do the database query on the background for us,
     //and it will update the liveData any time new data is written to the table."watch for changes to the database and update the UI dynamically."
     @Query("select * from DatabaseAsteroid where closeApproachDate >= :currentTime order by closeApproachDate")
     fun getAsteroids(currentTime: String): LiveData<List<DatabaseAsteroid>>

    @Query("select * from DatabaseAsteroid where closeApproachDate >= :startDate and closeApproachDate <= :endDate order by closeApproachDate")
    fun getAsteroidsByDate(startDate: String, endDate: String): List<DatabaseAsteroid>

    @Query("select * from DatabaseAsteroid order by closeApproachDate")
    fun getSavedAsteroids(): List<DatabaseAsteroid>

    //insertAll() is an upsert, so don’t forget to pass it onConflict=OnConflictStrategy.REPLACE!
     // vararg: it will actually pass an array under the hood
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroid: DatabaseAsteroid)

    @Query("delete from DatabaseAsteroid where closeApproachDate < :currentTime")
    fun deleteOldAsteroids(currentTime: String)
}

@Dao
abstract class ImageOfTheDayDao {
    @Transaction
    open fun deleteAndInsert(imageOfTheDay: DatabaseImageOfTheDay) {
        deleteAll()
        insert(imageOfTheDay)

    }

    @Query("select * from DatabaseImageOfTheDay")
    abstract fun getImageOfTheDay(): LiveData<DatabaseImageOfTheDay>

    @Query("delete from DatabaseImageOfTheDay")
    abstract fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(imageOfTheDay: DatabaseImageOfTheDay)
}

@Database(
    entities = [DatabaseAsteroid::class, DatabaseImageOfTheDay::class], version = 2,
    autoMigrations = [AutoMigration(from = 1, to = 2)],
    exportSchema = true
)
abstract class AsteroidRadarDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
    abstract val imageOfTheDayDao: ImageOfTheDayDao
}

private lateinit var INSTANCE: AsteroidRadarDatabase

fun getDatabase(context: Context): AsteroidRadarDatabase {
    // synchronized: to make it thread save
    synchronized(AsteroidRadarDatabase::class.java){
    if (!::INSTANCE.isInitialized){
        INSTANCE = Room.databaseBuilder(context.applicationContext,
            AsteroidRadarDatabase::class.java,
               "asteroids").build()
    }}
    return INSTANCE
}