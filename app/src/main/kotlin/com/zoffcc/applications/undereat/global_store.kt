@file:Suppress("ClassName", "SpellCheckingInspection")

package com.zoffcc.applications.undereat

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class globalstore_state(
    val mainscreen_state: MAINSCREEN = MAINSCREEN.MAINLIST,
    val restaurantId: Long = -1
)

interface GlobalStore {
    fun setEditRestaurantId(value: Long)
    fun updateMainscreenState(value: MAINSCREEN)
    fun getMainscreenState(): MAINSCREEN
    fun getRestaurantId(): Long
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

        override fun getMainscreenState(): MAINSCREEN
        {
            return state.mainscreen_state
        }

        override fun getRestaurantId(): Long
        {
            return state.restaurantId
        }
    }
}