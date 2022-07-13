package com.udacity.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MediatorLiveData
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.ImageOfTheDay
import com.udacity.asteroidradar.main.MainAdapter

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Asteroid>?) {
    //This binding adapter is to initialize the MainAdapter with list data
    val adapter = recyclerView.adapter as MainAdapter
    adapter.submitList(data)
}

/**
 * Binding adapter used to hide the spinner once data is available
 */
@BindingAdapter("goneIfNotNull")
fun goneIfNotNull(view: View, it: MediatorLiveData<List<Asteroid>>?) {
    view.visibility = if (it?.value?.isNotEmpty() == true) View.GONE else View.VISIBLE
}

@BindingAdapter("loadTheImage")
fun bindImageOfTheDay(imageView: ImageView, imageOfTheDay: ImageOfTheDay?) {
    if (imageOfTheDay?.mediaType == "image") {
        Picasso.with(imageView.context)
            .load(imageOfTheDay.url)
            .placeholder(R.drawable.placeholder_picture_of_day)
            .into(imageView)

        imageView.contentDescription = imageView.context.getString(
            R.string.nasa_picture_of_day_content_description_format,
            imageOfTheDay.title
        )
    }
}

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    val context = imageView.context
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
        imageView.contentDescription =
            context.getString(R.string.hazardous_asteroid_icon_content_description)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
        imageView.contentDescription =
            context.getString(R.string.not_hazardous_asteroid_icon_content_description)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    val context = imageView.context
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
        imageView.contentDescription =
            context.getString(R.string.potentially_hazardous_asteroid_image)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
        imageView.contentDescription = context.getString(R.string.not_hazardous_asteroid_image)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
    if (textView.id == R.id.distance_from_earth) {
        textView.contentDescription =
            context.getString(R.string.distance_from_earth_value_content_description, number)
    } else if (textView.id == R.id.absolute_magnitude)
        textView.contentDescription =
            context.getString(R.string.absolute_magnitude_value_content_description, number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
    textView.contentDescription =
        context.getString(R.string.estimated_diameter_value_content_description, number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
    textView.contentDescription =
        context.getString(R.string.relative_velocity_value_content_description, number)
}

