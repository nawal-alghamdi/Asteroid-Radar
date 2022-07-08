package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.repository.AsteroidRadarRepository
import kotlinx.coroutines.launch

enum class AsteroidsFilter {TODAY_ASTEROIDS, WEEK_ASTEROIDS, SAVED_ASTEROIDS}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Creating the database singleton
    private val database = getDatabase(application)
    // Creating the repository
    private val asteroidRadarRepository = AsteroidRadarRepository(database)

    private val mainAsteroidList: LiveData<List<Asteroid>> = asteroidRadarRepository.asteroids
    private val asteroidsListBasedOnFilter = MutableLiveData<List<Asteroid>>()
    val liveDataManager: MediatorLiveData<List<Asteroid>> = MediatorLiveData()

    init {
        liveDataManager.addSource(mainAsteroidList) {
            liveDataManager.value = it
        }
        liveDataManager.addSource(asteroidsListBasedOnFilter) {
            liveDataManager.value = it
        }
        viewModelScope.launch {
            asteroidRadarRepository.refreshAsteroids()
            asteroidRadarRepository.refreshImageOfTheDay()
        }
    }

    val imageOfTheDay = asteroidRadarRepository.imageOfTheDay

    // Internally, we use a MutableLiveData to handle navigation to the selected asteroid
    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()

    // The external immutable LiveData for the navigation property
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedAsteroid is set to null
     */
    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    fun showAsteroidsBasedOnFilters(asteroidsFilter: AsteroidsFilter) {
        viewModelScope.launch {
            if (asteroidsFilter == AsteroidsFilter.SAVED_ASTEROIDS) {
                asteroidsListBasedOnFilter.postValue(asteroidRadarRepository.getAsteroidsByDate(null, null))
            } else {
                val startDate = AsteroidRadarRepository.getCurrentDate()
                val endDate = when (asteroidsFilter) {
                    AsteroidsFilter.TODAY_ASTEROIDS -> startDate
                    else -> AsteroidRadarRepository.getDateAfterSixDays()
                }
                asteroidsListBasedOnFilter.postValue(
                    asteroidRadarRepository.getAsteroidsByDate(
                        startDate,
                        endDate
                    )
                )
            }
        }
    }

    companion object {
        const val TAG: String = "MainViewModel"
    }

    /**
     * Factory for constructing MainViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}