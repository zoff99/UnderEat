package com.zoffcc.applications.undereat

import com.zoffcc.applications.sorm.Restaurant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class StateRestaurantList(val restaurantlist: List<Restaurant> = emptyList(),
)

interface RestaurantListStore
{
    fun add(item: Restaurant)
    fun clear()
    fun update(item: Restaurant)
    val stateFlow: StateFlow<StateRestaurantList>
    val state get() = stateFlow.value
}

fun CoroutineScope.createRestaurantListStore(): RestaurantListStore
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
                var new_list: ArrayList<Restaurant> = ArrayList()
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

        override fun update(item: Restaurant)
        {

        }

        override fun clear()
        {
            @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            mutableStateFlow.value = state.copy(restaurantlist = emptyList())
        }
    }
}