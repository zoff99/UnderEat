@file:Suppress("ClassName", "SpellCheckingInspection")

package com.zoffcc.applications.undereat

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class localtionstore_state(
    val heading: Int = 0,
    val lat: Double = 0.0,
    val lon: Double = 0.0
)

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
            mutableStateFlow.value = state.copy(lat = lat_value, lon = lon_value)
        }

        override fun setHeading(azimuth: Int) {
            mutableStateFlow.value = state.copy(heading = azimuth)
        }
    }
}