@file:Suppress("FunctionName", "LocalVariableName", "SpellCheckingInspection",
    "UselessCallOnNotNull",
    "ConvertToStringTemplate", "UnusedReceiverParameter", "CascadeIf", "LiftReturnOrAssignment",
    "DeprecatedCallableAddReplaceWith", "UseExpressionBody", "DEPRECATION"
)

package com.zoffcc.applications.undereat

import android.content.Context
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

const val TAG = "UnderEat"

const val DEBUG_COMPOSE_UI_UPDATES = false // set "false" for release builds
const val HTTP_MAPS_URL = "https://www.google.com/maps/search/?api=1&query="
const val TAXI_PHONE_NUMBER = "+43 1 31 300"

val globalstore = createGlobalStore()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UnderEatAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(this)
                }
            }
        }
        corefuncs().init_me(this)
        load_restaurants()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (globalstore.getMainscreenState() == MAINSCREEN.MAINLIST) {
            super.onBackPressed()
        }
        else if (globalstore.getMainscreenState() == MAINSCREEN.ADD)
        {
            globalstore.updateMainscreenState(MAINSCREEN.MAINLIST)
        }
        else if (globalstore.getMainscreenState() == MAINSCREEN.EDIT)
        {
            globalstore.updateMainscreenState(MAINSCREEN.MAINLIST)
        }
        else if (globalstore.getMainscreenState() == MAINSCREEN.SETTINGS)
        {
            globalstore.updateMainscreenState(MAINSCREEN.MAINLIST)
        }
        else {
            super.onBackPressed()
        }
    }
}

val restaurantliststore = createRestaurantListStore()

@Composable
fun MainScreen(context: Context) {
    val restaurants by restaurantliststore.stateFlow.collectAsState()
    val state_mainscreen by globalstore.stateFlow.collectAsState()

    Log.i(TAG, "size_list=" + restaurants.restaurantlist.size)

    if (state_mainscreen.mainscreen_state == MAINSCREEN.MAINLIST) {
        globalstore.setEditRestaurantId(-1)
        main_list(restaurants, context)
    }
    else if (state_mainscreen.mainscreen_state == MAINSCREEN.ADD)
    {
        globalstore.setEditRestaurantId(-1)
        add_form()
    }
    else if (state_mainscreen.mainscreen_state == MAINSCREEN.SETTINGS)
    {
        globalstore.setEditRestaurantId(-1)
        settings_form(context)
    }
    else if (state_mainscreen.mainscreen_state == MAINSCREEN.EDIT)
    {
        edit_form()
    }
}

fun load_restaurants() {
    Log.i(TAG, "load_restaurants:start")
    restaurantliststore.clear()
    val filter_category_id = globalstore.getFilterCategoryId()
    if (filter_category_id == -1L) {
        orma.selectFromRestaurant().toList().forEach {
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
    Log.i(TAG, "load_restaurants:end")
}
