@file:Suppress("ClassName", "SpellCheckingInspection", "ConvertToStringTemplate", "PropertyName")

package com.zoffcc.applications.undereat

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class globalstore_state(
    val mainscreen_state: MAINSCREEN = MAINSCREEN.MAINLIST,
    val restaurantId: Long = -1,
    val filterCategoryId: Long = -1,
    val compactMainList: Boolean = false,
    val forsummerFilter: Boolean = false,
    val sorterId: Long = 0
)

interface GlobalStore {
    fun setEditRestaurantId(value: Long)
    fun setFilterCategoryId(value: Long)
    fun setSorterId(value: Long)
    fun setCompactMainList(value: Boolean)
    fun setForsummerFilter(value: Boolean)
    fun updateMainscreenState(value: MAINSCREEN)
    fun getMainscreenState(): MAINSCREEN
    fun getRestaurantId(): Long
    fun getFilterCategoryId(): Long
    fun getSorterId(): Long
    fun getCompactMainList(): Boolean
    fun getForsummerFilter(): Boolean
    val stateFlow: StateFlow<globalstore_state>
    val state get() = stateFlow.value
}

fun createGlobalStore(): GlobalStore {
    val mutableStateFlow = MutableStateFlow(globalstore_state())
    return object : GlobalStore {
        override val stateFlow: StateFlow<globalstore_state> = mutableStateFlow

        override fun updateMainscreenState(value: MAINSCREEN) {
            mutableStateFlow.value = state.copy(mainscreen_state = value)
        }

        override fun setEditRestaurantId(value: Long) {
            mutableStateFlow.value = state.copy(restaurantId = value)
        }

        override fun setFilterCategoryId(value: Long) {
            mutableStateFlow.value = state.copy(filterCategoryId = value)
        }

        override fun setSorterId(value: Long) {
            Log.i(TAG, "setSorterId:value=" + value)
            if (value == SORTER.DISTANCE.value) {
                Log.i(TAG, "setSorterId:2")
                try {
                    gps?.startUsingGPS()
                } catch(e: java.lang.Exception) {
                    e.printStackTrace()
                }
            } else {
                try {
                    gps?.stopUsingGPS()
                } catch(e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
            mutableStateFlow.value = state.copy(sorterId = value)
        }

        override fun setCompactMainList(value: Boolean) {
            mutableStateFlow.value = state.copy(compactMainList = value)
        }

        override fun setForsummerFilter(value: Boolean) {
            mutableStateFlow.value = state.copy(forsummerFilter = value)
        }

        override fun getMainscreenState(): MAINSCREEN
        {
            return state.mainscreen_state
        }

        override fun getRestaurantId(): Long
        {
            return state.restaurantId
        }

        override fun getFilterCategoryId(): Long
        {
            return state.filterCategoryId
        }

        override fun getSorterId(): Long {
            return state.sorterId
        }

        override fun getCompactMainList(): Boolean {
            return state.compactMainList
        }

        override fun getForsummerFilter(): Boolean {
            return state.forsummerFilter
        }
    }
}