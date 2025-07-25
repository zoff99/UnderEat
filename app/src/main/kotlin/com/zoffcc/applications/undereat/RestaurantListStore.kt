@file:Suppress("LocalVariableName", "SpellCheckingInspection", "ConvertToStringTemplate",
    "FunctionName"
)

package com.zoffcc.applications.undereat

import android.util.Log
import com.zoffcc.applications.sorm.Restaurant
import com.zoffcc.applications.undereat.corefuncs.Category.STORE
import com.zoffcc.applications.undereat.corefuncs.SpecialCategory.SPECIAL_CATEGORY_ALL
import com.zoffcc.applications.undereat.corefuncs.SpecialCategory.SPECIAL_CATEGORY_NOSTORE
import com.zoffcc.applications.undereat.corefuncs.orma
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.Normalizer

data class StateRestaurantList(val restaurantlist: List<Restaurant> = emptyList(),
                               val restaurantDistance: List<RestDistance> = emptyList(),
                               val summerflag: Boolean = false,
                               val haveacflag: Boolean = false,
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
    fun sortByAddeddateDesc()
    fun sortByModifieddateDesc()
    fun filterByString(filter_string: String?)
    fun filterBySummerflag(flag: Boolean)
    fun filterByHaveacflag(flag: Boolean)
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
            mutableStateFlow.value = state.copy(restaurantlist = state.restaurantlist
                .sortedWith(compareByDescending<Restaurant> { it.rating }.thenBy { it.name }))
        }

        override fun sortByAddeddateDesc() {
            mutableStateFlow.value = state.copy(restaurantlist = state.restaurantlist
                .sortedWith(compareByDescending<Restaurant> { it.added_timestamp }.thenBy { it.name }))
        }

        override fun sortByModifieddateDesc() {
            mutableStateFlow.value = state.copy(restaurantlist = state.restaurantlist
                .sortedWith(compareByDescending<Restaurant> { it.modified_timestamp }.thenBy { it.name }))
        }

        override fun filterByString(filter_string: String?) {
            if (!filter_string.isNullOrEmpty()) {
                mutableStateFlow.value = state.copy(
                    restaurantlist = state.restaurantlist.filter { filter_by_search_string(it, filter_string) })
            }
        }

        override fun filterBySummerflag(flag: Boolean) {
            mutableStateFlow.value = state.copy(summerflag = flag,
                restaurantlist = state.restaurantlist.filter {
                    @Suppress("KotlinConstantConditions")
                    if (flag) {
                        it.for_summer == flag
                    } else {
                        true
                    }
                })
        }

        override fun filterByHaveacflag(flag: Boolean) {
            mutableStateFlow.value = state.copy(haveacflag = flag,
                restaurantlist = state.restaurantlist.filter {
                    @Suppress("KotlinConstantConditions")
                    if (flag) {
                        it.have_ac == flag
                    } else {
                        true
                    }
                })
        }

        private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

        fun CharSequence.unaccent(): String {
            val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
            return REGEX_UNACCENT.replace(temp, "")
        }

        private fun filter_by_search_string(it: Restaurant, filter_string: String): Boolean {
            val filter_string2 = filter_string.replace("\\p{Zs}+".toRegex(), "")
            if (it.name.lowercase().unaccent().replace("\\p{Zs}+".toRegex(), "")
                    .contains(filter_string2.lowercase().unaccent()))
            {
                return true
            }
            else if (it.address.lowercase().replace("\\p{Zs}+".toRegex(), "").unaccent()
                    .contains(filter_string2.lowercase().unaccent()))
            {
                return true
            }
            return false
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

fun load_restaurants() {
    Log.i(TAG, "load_restaurants:start")
    restaurantliststore.clear()
    val filter_category_id = globalstore.getFilterCategoryId()
    if (filter_category_id == SPECIAL_CATEGORY_ALL.value.toLong()) {
        orma.selectFromRestaurant().toList().forEach {
            try {
                val r = Restaurant.deep_copy(it)
                restaurantliststore.add(item = r)
                // Log.i(TAG, "load_restaurants: " + r)
            } catch (_: Exception) {
            }
        }
    }
    else if (filter_category_id == SPECIAL_CATEGORY_NOSTORE.value.toLong()) {
        orma.selectFromRestaurant().category_idNotEq(STORE.value.toLong()).toList().forEach {
            try {
                val r = Restaurant.deep_copy(it)
                restaurantliststore.add(item = r)
                // Log.i(TAG, "load_restaurants: " + r)
            } catch (_: Exception) {
            }
        }
    }
    else
    {
        orma.selectFromRestaurant().category_idEq(filter_category_id).toList().forEach {
            try {
                val r = Restaurant.deep_copy(it)
                restaurantliststore.add(item = r)
                // Log.i(TAG, "load_restaurants: " + r)
            } catch (_: Exception) {
            }
        }
    }
    sort_restaurants()
    restaurantliststore.filterBySummerflag(globalstore.getForsummerFilter())
    restaurantliststore.filterByHaveacflag(globalstore.getHaveacFilter())
    restaurantliststore.filterByString(globalstore.getFilterString())
    Log.i(TAG, "load_restaurants:end")
}

fun sort_restaurants()
{
    val id = globalstore.getSorterId()
    if (id == SORTER.NAME.value)
    {
        restaurantliststore.sortByName()
    }
    else if (id == SORTER.ADDRESS.value)
    {
        restaurantliststore.sortByAddress()
    }
    else if (id == SORTER.RATING.value)
    {
        restaurantliststore.sortByRating()
    }
    else if (id == SORTER.ADDED_DATE.value)
    {
        // HINT: show last added restaurant at the top of the list
        restaurantliststore.sortByAddeddateDesc()
    }
    else if (id == SORTER.MODIFIED_DATE.value)
    {
        // HINT: show last modified restaurant at the top of the list
        restaurantliststore.sortByModifieddateDesc()
    }
}
