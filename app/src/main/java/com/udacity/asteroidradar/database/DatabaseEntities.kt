package com.udacity.asteroidradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.ImageOfTheDay

@Entity
data class DatabaseAsteroid constructor(
    @PrimaryKey val id: Long, val codename: String, val closeApproachDate: String,
    val absoluteMagnitude: Double, val estimatedDiameter: Double,
    val relativeVelocity: Double, val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

fun List<DatabaseAsteroid>.asDomainModel(): List<Asteroid> {
    return map {
        Asteroid(
            id= it.id,
            codename= it.codename,
            closeApproachDate= it.closeApproachDate,
            absoluteMagnitude= it.absoluteMagnitude,
            estimatedDiameter= it.estimatedDiameter,
            relativeVelocity= it.relativeVelocity,
            distanceFromEarth= it.distanceFromEarth,
            isPotentiallyHazardous= it.isPotentiallyHazardous
        )
    }
}

@Entity
data class DatabaseImageOfTheDay constructor(
    @PrimaryKey val id: String,
    val date: String,
    val explanation: String,
    val mediaType: String,
    val title: String,
    val url: String
)

fun DatabaseImageOfTheDay?.asDomainModel(): ImageOfTheDay? {
    return this?.let {
        ImageOfTheDay(
            id = it.id,
            date = it.date,
            explanation = it.explanation,
            mediaType = it.mediaType,
            title = it.title,
            url = it.url
        )
    }
}
