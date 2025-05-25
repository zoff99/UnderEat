@file:Suppress("FunctionName", "LocalVariableName", "SpellCheckingInspection",
    "UselessCallOnNotNull",
    "ConvertToStringTemplate", "UnusedReceiverParameter", "CascadeIf", "LiftReturnOrAssignment"
)

package com.zoffcc.applications.undereat

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.zoffcc.applications.sorm.Restaurant
import com.zoffcc.applications.undereat.corefuncs.orma
import com.zoffcc.applications.undereat.ui.theme.UnderEatAppTheme
import kotlinx.coroutines.flow.MutableStateFlow

const val TAG = "UnderEat"

val messages = MutableStateFlow("running tests ...")
val globalstore = createGlobalStore()
const val DEBUG_COMPOSE_UI_UPDATES = true // set "false" for release builds


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UnderEatAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
        val result = corefuncs().init_me(this)
        messages.value = messages.value + result
        load_restaurants()
    }
}

val restaurantliststore = createRestaurantListStore()

@Composable
fun MainScreen() {
    val restaurants by restaurantliststore.stateFlow.collectAsState()
    val state_mainscreen by globalstore.stateFlow.collectAsState()

    Log.i(TAG, "size_list=" + restaurants.restaurantlist.size)

    if (state_mainscreen.mainscreen_state == MAINSCREEN.MAINLIST) {
        globalstore.setEditRestaurantId(-1)
        main_list(restaurants)
    }
    else if (state_mainscreen.mainscreen_state == MAINSCREEN.ADD)
    {
        globalstore.setEditRestaurantId(-1)
        add_form()
    }
    else if (state_mainscreen.mainscreen_state == MAINSCREEN.EDIT)
    {
        edit_form()
    }
}

fun load_restaurants() {
    Log.i(TAG, "load_restaurants:start")
    restaurantliststore.clear()
    orma.selectFromRestaurant().toList().forEach {
        try
        {
            val r = Restaurant.deep_copy(it)
            restaurantliststore.add(item = r)
            Log.i(TAG, "load_restaurants: " + r)
        } catch (_: Exception)
        {
        }
    }
    Log.i(TAG, "load_restaurants:end")
}
