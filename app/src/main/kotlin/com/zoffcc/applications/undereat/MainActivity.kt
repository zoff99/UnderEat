@file:Suppress("FunctionName", "LocalVariableName", "SpellCheckingInspection",
    "UselessCallOnNotNull",
    "ConvertToStringTemplate", "UnusedReceiverParameter", "CascadeIf", "LiftReturnOrAssignment",
    "DeprecatedCallableAddReplaceWith", "UseExpressionBody", "DEPRECATION", "PrivatePropertyName"
)

package com.zoffcc.applications.undereat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zoffcc.applications.undereat.corefuncs.del_g_opts
import com.zoffcc.applications.undereat.corefuncs.get_g_opts
import com.zoffcc.applications.undereat.corefuncs.orma
import com.zoffcc.applications.undereat.corefuncs.set_g_opts
import com.zoffcc.applications.undereat.ui.theme.UnderEatAppTheme

const val TAG = "UnderEat"

const val DEBUG_COMPOSE_UI_UPDATES = false // set "false" for release builds
const val UNDEREAT_USERAGENT = "UnderEatApp https://github.com/zoff99/UnderEat"
const val HTTP_MAPS_URL = "https://www.google.com/maps/search/?api=1&query="
const val HTTP_NOMINATIM_SEARCH_URL = "https://nominatim.openstreetmap.org/search?limit=1&addressdetails=1&format=json&q="
var TAXI_PHONE_NUMBER: String? = null
var global_categories: MutableMap<Long, String> = mutableMapOf()
const val MAX_DISTANCE = 30_000 // max distance in meters when location will not be used anymore on mainlist
val RESERVATION_LINE = Color(0xff842111)

@SuppressLint("StaticFieldLeak")
var gps: GPSTracker? = null

val globalstore = createGlobalStore()
val locationstore = createLocationStore()

class MainActivity : ComponentActivity() {

    private val ACCESS_FINE_LOCATION_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the permission is already granted
        @Suppress("ControlFlowWithEmptyBody")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                ACCESS_FINE_LOCATION_PERMISSION_CODE
            )
        }

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
        restore_mainlist_state()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (globalstore.getMainscreenState() == MAINSCREEN.MAINLIST) {
            super.onBackPressed()
        }
        else if (globalstore.getMainscreenState() == MAINSCREEN.COMPASS)
        {
            globalstore.updateMainscreenState(MAINSCREEN.MAINLIST)
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

    override fun onResume()
    {
        super.onResume()
        try {
            if (globalstore.getSorterId() == SORTER.DISTANCE.value) {
                gps = GPSTracker(this)
            }
        } catch(_: java.lang.Exception) {
        }
    }

    override fun onPause()
    {
        super.onPause()
        try {
            gps?.stopUsingGPS()
        } catch(_: java.lang.Exception) {
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION_PERMISSION_CODE) {
            @Suppress("ControlFlowWithEmptyBody")
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
            }
        }
    }
}

internal fun restore_mainlist_state() {
    load_taxi_number()
    load_categories()
    load_compact_flag()
    load_filters()
    load_sorter()
    load_filter_string()
    load_forsummer_flag()
    load_haveac_flag()
    load_restaurants()
}

val restaurantliststore = createRestaurantListStore()

@Composable
fun MainScreen(context: Context) {
    val restaurants by restaurantliststore.stateFlow.collectAsState()
    val state_mainscreen by globalstore.stateFlow.collectAsState()

    // Log.i(TAG, "size_list=" + restaurants.restaurantlist.size)

    if (state_mainscreen.mainscreen_state == MAINSCREEN.MAINLIST) {
        globalstore.setEditRestaurantId(-1)
        main_list(restaurants, context)
    }
    else if (state_mainscreen.mainscreen_state == MAINSCREEN.COMPASS)
    {
        CompassScreen()
    }
    else if (state_mainscreen.mainscreen_state == MAINSCREEN.ADD)
    {
        globalstore.setEditRestaurantId(-1)
        add_form(context)
    }
    else if (state_mainscreen.mainscreen_state == MAINSCREEN.SETTINGS)
    {
        globalstore.setEditRestaurantId(-1)
        settings_form(context)
    }
    else if (state_mainscreen.mainscreen_state == MAINSCREEN.EDIT)
    {
        edit_form(context)
    }
}

fun set_taxi_number(taxi_num: String?) {
    if (taxi_num.isNullOrEmpty())
    {
        del_g_opts("TAXI_PHONE_NUMBER")
        TAXI_PHONE_NUMBER = null
    }
    else
    {
        set_g_opts("TAXI_PHONE_NUMBER", taxi_num)
        TAXI_PHONE_NUMBER = taxi_num
    }
}

private fun load_categories() {
    val cat_list = orma.selectFromCategory().toList()
    global_categories.clear()
    cat_list.forEach {
        try {
            global_categories[it.id] = it.name
        } catch (e: Exception) {
            e.printStackTrace()
            global_categories[it.id] = "Unknown"
        }
    }
}

private fun load_taxi_number() {
    TAXI_PHONE_NUMBER = get_g_opts("TAXI_PHONE_NUMBER")
}

fun save_compact_flag() {
    val flag = globalstore.getCompactMainList()
    set_g_opts("CompactMainList", flag.toString())
}

fun save_forsummer_flag() {
    val flag = globalstore.getForsummerFilter()
    set_g_opts("ForsummerFilter", flag.toString())
}

fun save_haveac_flag() {
    val flag = globalstore.getHaveacFilter()
    set_g_opts("HaveacFilter", flag.toString())
}

private fun load_compact_flag() {
    val flag = get_g_opts("CompactMainList")
    if (flag.isNullOrEmpty())
    {
        globalstore.setCompactMainList(false)
    }
    else
    {
        try
        {
            globalstore.setCompactMainList(flag.toBoolean())
        }
        catch(e: Exception)
        {
            e.printStackTrace()
        }

    }
}

private fun load_forsummer_flag() {
    val flag = get_g_opts("ForsummerFilter")
    if (flag.isNullOrEmpty())
    {
        globalstore.setForsummerFilter(false)
    }
    else
    {
        try
        {
            globalstore.setForsummerFilter(flag.toBoolean())
        }
        catch(e: Exception)
        {
            e.printStackTrace()
        }

    }
}

private fun load_haveac_flag() {
    val flag = get_g_opts("HaveacFilter")
    if (flag.isNullOrEmpty())
    {
        globalstore.setHaveacFilter(false)
    }
    else
    {
        try
        {
            globalstore.setHaveacFilter(flag.toBoolean())
        }
        catch(e: Exception)
        {
            e.printStackTrace()
        }

    }
}

fun save_filters() {
    val fid = globalstore.getFilterCategoryId()
    set_g_opts("FilterCategoryId", fid.toString())
}

private fun load_filters() {
    val fid = get_g_opts("FilterCategoryId")
    if (fid.isNullOrEmpty())
    {
        globalstore.setFilterCategoryId(-1)
    }
    else
    {
        try
        {
            globalstore.setFilterCategoryId(fid.toLong())
        }
        catch(e: Exception)
        {
            e.printStackTrace()
        }

    }
}

fun save_sorter() {
    val id = globalstore.getSorterId()
    set_g_opts("SorterId", id.toString())
}

private fun load_sorter() {
    val id = get_g_opts("SorterId")
    if (id.isNullOrEmpty())
    {
        globalstore.setSorterId(SORTER.NAME.value)
    }
    else
    {
        try
        {
            globalstore.setSorterId(id.toLong())
        }
        catch(e: Exception)
        {
            e.printStackTrace()
        }

    }
}

fun save_filter_string() {
    val id = globalstore.getFilterString()
    set_g_opts("FilterString", id)
}

private fun load_filter_string() {
    val id = get_g_opts("FilterString")
    globalstore.setFilterString(id)
}