@file:Suppress("ClassName", "SpellCheckingInspection", "LocalVariableName")

package com.zoffcc.applications.undereat

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.lang.Exception
import kotlin.math.roundToInt
import kotlin.math.roundToLong

data class localtionstore_state(
    val heading: Int = 0,
    val lat: Double = 0.0,
    val lon: Double = 0.0
)

var last_location_sort = 0L
const val LOCATION_SORT_INTERAL_MILLISECS = 5 * 1000

interface LocationStore {
    fun setLocation(lat_value: Double, lon_value: Double)
    fun setHeading(azimuth: Int)
    val stateFlow: StateFlow<localtionstore_state>
    val state get() = stateFlow.value
}

fun createLocationStore(): LocationStore {
    val mutableStateFlow = MutableStateFlow(localtionstore_state())
    return object : LocationStore {
        override val stateFlow: StateFlow<localtionstore_state> = mutableStateFlow

        override fun setLocation(lat_value: Double, lon_value: Double) {
            if ((state.lat != lat_value) || (state.lon != lon_value))
            {
                try
                {
                    if (globalstore.getSorterId() == SORTER.DISTANCE.value) {
                        // HINT: location has changed
                        if ((last_location_sort + LOCATION_SORT_INTERAL_MILLISECS) < System.currentTimeMillis()) {
                            // Log.i(TAG, "sort by distance -------------")
                            // calculate all restaurant distances
                            val restaurantDistance: ArrayList<RestDistance> = ArrayList()
                            restaurantliststore.state.restaurantlist.forEach { rest ->
                                // Log.i(TAG, "" + state.lat + " " + state.lon)
                                val lat = state.lat
                                val lon = state.lon
                                val distance_in_meters = GPSTracker.calculateDistance(
                                    lat, lon, 0.0,
                                    geo_coord_longdb_to_double(rest.lat),
                                    geo_coord_longdb_to_double(rest.lon),
                                    0.0
                                )
                                if ((lat == 0.0) || (lon == 0.0)
                                    || (distance_in_meters.roundToInt() > MAX_DISTANCE)
                                ) {
                                    restaurantDistance.add(RestDistance(rest.id, MAX_DISTANCE_REST))
                                } else {
                                    restaurantDistance.add(
                                        RestDistance(
                                            rest.id,
                                            distance_in_meters.roundToLong()
                                        )
                                    )
                                }
                            }
                            restaurantliststore.sortByDistance(restaurantDistance)
                            last_location_sort = System.currentTimeMillis()
                        }
                    }
                }
                catch(e: Exception)
                {
                    e.printStackTrace()
                }
                mutableStateFlow.value = state.copy(lat = lat_value, lon = lon_value)
            }
        }

        override fun setHeading(azimuth: Int) {
            if (azimuth != state.heading) {
                mutableStateFlow.value = state.copy(heading = azimuth)
            }
        }
    }
}