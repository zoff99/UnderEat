@file:Suppress("LocalVariableName", "SpellCheckingInspection")

package com.zoffcc.applications.undereat

import com.zoffcc.applications.sorm.Restaurant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class StateRestaurantList(val restaurantlist: List<Restaurant> = emptyList(),
)

interface RestaurantListStore
{
    fun clear()
    fun add(item: Restaurant)
    fun update(item: Restaurant)
    fun sortByName()
    fun sortByAddress()
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

        override fun update(item: Restaurant)
        {

        }

        override fun clear()
        {
            mutableStateFlow.value = state.copy(restaurantlist = emptyList())
        }

        override fun sortByName() {
            mutableStateFlow.value = state.copy(restaurantlist = state.restaurantlist.sortedBy { it.name })
        }

        override fun sortByAddress() {
            mutableStateFlow.value = state.copy(restaurantlist = state.restaurantlist.sortedBy { it.address })
        }
    }
}