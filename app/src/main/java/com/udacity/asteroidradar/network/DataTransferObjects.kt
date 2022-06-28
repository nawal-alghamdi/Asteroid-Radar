package com.udacity.asteroidradar.network

import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.DatabaseImageOfTheDay

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 */
data class NetworkAsteroid(
    val id: Long, val codename: String, val closeApproachDate: String,
    val absoluteMagnitude: Double, val estimatedDiameter: Double,
    val relativeVelocity: Double, val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

/**
 * Convert Network results to database objects
 */
fun List<NetworkAsteroid>.asDatabaseModel(): Array<DatabaseAsteroid> {
    return map {
        DatabaseAsteroid(
            id= it.id,
            codename= it.
            codename,
            closeApproachDate= it.closeApproachDate,
            absoluteMagnitude= it.absoluteMagnitude,
            estimatedDiameter= it.estimatedDiameter,
            relativeVelocity= it.relativeVelocity,
            distanceFromEarth= it.distanceFromEarth,
            isPotentiallyHazardous= it.isPotentiallyHazardous
        )
    }.toTypedArray()
}

data class NetworkImageOfTheDay(
    val date: String,
    val explanation: String,
    val media_type: String,
    val title: String,
    val url: String
)

/**
 * Convert Network results to database objects
 */
fun NetworkImageOfTheDay.asDatabaseModel(): DatabaseImageOfTheDay {
    return DatabaseImageOfTheDay(
        id = this.url,
        date = this.date,
        explanation = this.explanation,
        mediaType = this.media_type,
        title = this.title,
        url = this.url
    )
}