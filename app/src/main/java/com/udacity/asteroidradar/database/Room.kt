package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

// Until now I only have one dao
@Dao
interface AsteroidDao{
     //When we return a livedata room will do the database query on the background for us,
     //and it will update the liveData any time new data is written to the table."watch for changes to the database and update the UI dynamically."
     @Query("select * from DatabaseAsteroid")
     fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    //insertAll() is an upsert, so don’t forget to pass it onConflict=OnConflictStrategy.REPLACE!
     // vararg: it will actually pass an array under the hood
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroid: DatabaseAsteroid)
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidRadarDatabase: RoomDatabase(){
    abstract val asteroidDao: AsteroidDao
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