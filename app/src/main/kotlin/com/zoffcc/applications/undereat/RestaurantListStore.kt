@file:Suppress("LocalVariableName", "SpellCheckingInspection", "ConvertToStringTemplate")

package com.zoffcc.applications.undereat

import com.zoffcc.applications.sorm.Restaurant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class StateRestaurantList(val restaurantlist: List<Restaurant> = emptyList(),
                               val restaurantDistance: List<RestDistance> = emptyList()
)

const val MAX_DISTANCE_REST = 9999999999

data class RestDistance(
    var id: Long = 0,
    var distance: Long = MAX_DISTANCE_REST
)

interface RestaurantListStore
{
    fun clear()
    fun add(item: Restaurant)
    fun update(item: Restaurant)
    fun clearDistance()
    fun addDistance(item: RestDistance)
    fun updateDistance(id: Long, distance: Long)
    fun get(itemId: Long): Restaurant
    fun sortByName()
    fun sortByAddress()
    fun sortByDistance(restaurantDistance: ArrayList<RestDistance>)
    fun sortByRating()
    val stateFlow: StateFlow<StateRestaurantList>
    val state get() = stateFlow.value
}

fun createRestaurantListStore(): RestaurantListStore
{
    val mutableStateFlow = MutableStateFlow(StateRestaurantList())

    return object : RestaurantListStore
    {
        override val stateFlow: StateFlow<StateRestaurantList> = mutableStateFlow

        override fun add(item: Restaurant)
        {
            var found = false
            state.restaurantlist.forEach {
                if (item.id == it.id)
                {
                    // already in list
                    found = true
                }
            }
            if (!found)
            {
                val new_list: ArrayList<Restaurant> = ArrayList()
                new_list.addAll(state.restaurantlist)
                new_list.forEach { item2 ->
                    if (item2.id == item.id)
                    {
                        new_list.remove(item2)
                    }
                }
                new_list.add(item)
                mutableStateFlow.value = state.copy(restaurantlist = new_list)
            }
        }

        override fun addDistance(item: RestDistance)
        {
            var found = false
            state.restaurantDistance.forEach {
                if (item.id == it.id)
                {
                    // already in list
                    found = true
                }
            }
            if (!found)
            {
                val new_list: ArrayList<RestDistance> = ArrayList()
                new_list.addAll(state.restaurantDistance)
                new_list.forEach { item2 ->
                    if (item2.id == item.id)
                    {
                        new_list.remove(item2)
                    }
                }
                new_list.add(item)
                mutableStateFlow.value = state.copy(restaurantDistance = new_list)
            }
        }

        override fun update(item: Restaurant)
        {
        }

        override fun updateDistance(id: Long, distance: Long) {
            var found = false
            state.restaurantDistance.forEach {
                if (id == it.id)
                {
                    found = true
                }
            }
            if (found)
            {
                val new_list: ArrayList<RestDistance> = ArrayList()
                new_list.addAll(state.restaurantDistance)
                new_list.forEach {
                    if (id == it.id)
                    {
                        it.distance = distance
                    }
                }
                mutableStateFlow.value = state.copy(restaurantDistance = new_list)
            }
        }

        override fun get(itemId: Long): Restaurant {
            state.restaurantlist.forEach {
                if (itemId == it.id)
                {
                    return it
                }
            }
            return Restaurant()
        }

        override fun clear()
        {
            mutableStateFlow.value = state.copy(restaurantlist = emptyList())
        }

        override fun clearDistance() {
            mutableStateFlow.value = state.copy(restaurantDistance = emptyList())
        }

        override fun sortByName() {
            mutableStateFlow.value = state.copy(restaurantlist = state.restaurantlist.sortedBy { it.name })
        }

        override fun sortByAddress() {
            mutableStateFlow.value = state.copy(restaurantlist = state.restaurantlist.sortedBy { it.address })
        }

        override fun sortByDistance(restaurantDistance: ArrayList<RestDistance>) {
            val distanceComp = Comparator<Restaurant> { a, b ->
                val adist = getRestDist(a, restaurantDistance)
                val bdist = getRestDist(b, restaurantDistance)
                // Log.i(TAG, "a=" + a.name + " b=" + b.name)
                // Log.i(TAG, "adist=" + adist + " bdist=" + bdist)
                if (adist == bdist)
                {
                    return@Comparator 0
                }
                else if (adist < bdist)
                {
                    return@Comparator -1
                }
                return@Comparator 1
            }
            val l = state.restaurantlist.sortedWith(distanceComp)
            // Log.i(TAG, "lbefore=" + state.restaurantlist + "\nlafter:" + l)
            mutableStateFlow.value = state.copy(restaurantlist = l)
        }

        override fun sortByRating() {
            mutableStateFlow.value = state.copy(restaurantlist = state.restaurantlist.sortedByDescending { it.rating })
        }

        private fun getRestDist(a: Restaurant, restaurantDistance: ArrayList<RestDistance>): Long {
            restaurantDistance.forEach {
                if (a.id == it.id)
                {
                    return it.distance
                }
            }
            return MAX_DISTANCE_REST
        }
    }
}