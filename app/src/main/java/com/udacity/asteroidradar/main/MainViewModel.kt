package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.AsteroidApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    // Fake data to test displaying list of asteroid
    val lists: List<Asteroid> =
        listOf(
            Asteroid(11L, "asteroid1", "20-02-02", 0.0, 0.0, 0.0, 0.0, true),
            Asteroid(12L, "asteroid2", "20-02-02", 0.0, 0.0, 0.0, 0.0, false),
            Asteroid(13L, "asteroid3", "20-02-02", 0.0, 0.0, 0.0, 0.0, true)
        )

    private val _response = MutableLiveData<String>()

    val response: LiveData<String>
      get() = _response

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

    private fun getAsteroidProperties(){
        AsteroidApi.retrofitService.getProperties().enqueue(object: Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                _response.value = response.body()
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                _response.value = "Failure: " + t.message
            }
        } )
    }

}