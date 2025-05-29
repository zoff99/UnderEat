@file:Suppress("FunctionName", "LocalVariableName", "SpellCheckingInspection",
    "UselessCallOnNotNull",
    "ConvertToStringTemplate", "UnusedReceiverParameter", "CascadeIf", "LiftReturnOrAssignment",
    "UNUSED_EXPRESSION"
)

package com.zoffcc.applications.undereat

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoffcc.applications.sorm.Restaurant
import com.zoffcc.applications.undereat.corefuncs.orma
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ComposableNaming")
@Composable
fun edit_form() {
    var show_delete_alert by remember { mutableStateOf(false) }
    val restaurant_id = globalstore.getRestaurantId()
    val rest_data = restaurantliststore.get(restaurant_id)
    var input_name by remember {
        val textFieldValue =
            TextFieldValue(text = if (rest_data.name.isNullOrEmpty()) "" else rest_data.name)
        mutableStateOf(textFieldValue)
    }
    var input_addr by remember {
        val textFieldValue =
            TextFieldValue(text = if (rest_data.address.isNullOrEmpty()) "" else rest_data.address)
        mutableStateOf(textFieldValue)
    }
    var input_comment by remember {
        val textFieldValue =
            TextFieldValue(text = if (rest_data.comment.isNullOrEmpty()) "" else rest_data.comment)
        mutableStateOf(textFieldValue)
    }
    var input_phonenumber by remember {
        val textFieldValue = TextFieldValue(text = if (rest_data.phonenumber.isNullOrEmpty()) "" else rest_data.phonenumber)
        mutableStateOf(textFieldValue)
    }
    Log.i(TAG, "RRRRR:lat:" + rest_data.lat)
    var input_lat by remember {
        val textFieldValue = TextFieldValue(text = geo_coord_longdb_to_string(rest_data.lat))
        mutableStateOf(textFieldValue)
    }
    Log.i(TAG, "RRRRR:lon:" + rest_data.lon)
    var input_lon by remember {
        val textFieldValue = TextFieldValue(text = geo_coord_longdb_to_string(rest_data.lon))
        mutableStateOf(textFieldValue)
    }

    val cat_list = orma.selectFromCategory().toList()
    val cat_isDropDownExpanded = remember { mutableStateOf(false) }
    val cat_itemPosition = remember { mutableStateOf(rest_data.category_id.toInt()) }
    val scrollState = rememberScrollState()

    if (show_delete_alert)
    {
        AlertDialog(onDismissRequest = { },
            title = { Text("Delete") },
            confirmButton = {
                Button(onClick = {
                    try {
                        orma.deleteFromRestaurant().idEq(restaurant_id).execute()
                        //
                        globalstore.setEditRestaurantId(-1)
                        load_restaurants()
                        globalstore.updateMainscreenState(MAINSCREEN.MAINLIST)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { show_delete_alert = false }) {
                    Text("No")
                }
            },
            text = { "Really delete this Restaurant ?" })
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState))
    {
        Spacer(modifier = Modifier.height(20.dp))
        Text("Edit Restaurant", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Column {
            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
                value = input_name, placeholder = { Text(text = "Name", fontSize = 14.sp) },
                onValueChange = { input_name = it })
            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
                value = input_addr, placeholder = { Text(text = "Address", fontSize = 14.sp) },
                onValueChange = { input_addr = it })
            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
                value = input_phonenumber, placeholder = { Text(text = "Phone Number", fontSize = 14.sp) },
                onValueChange = { input_phonenumber = it })
            Box {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .randomDebugBorder()
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(10.dp)
                        .clickable {
                            cat_isDropDownExpanded.value = true
                        }
                ) {
                    // Log.i(TAG, "CCCCCCC22:" + cat_itemPosition.value + " ___ " + cat_list)
                    Text(text = cat_list[cat_itemPosition.value - 1].name, fontSize = 16.sp)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "select Category")
                }
                DropdownMenu(
                    expanded = cat_isDropDownExpanded.value,
                    onDismissRequest = {
                        cat_isDropDownExpanded.value = false
                    }) {
                    cat_list.forEachIndexed { index, category_ ->
                        DropdownMenuItem(
                            modifier = Modifier
                                .height(60.dp)
                                .padding(1.dp),
                            text = {
                                Text(text = category_.name, fontSize = 16.sp)
                            },
                            onClick = {
                                cat_isDropDownExpanded.value = false
                                // Log.i(TAG, "CCCCCCC333:" + cat_itemPosition.value + " ___ " + index)
                                cat_itemPosition.value = index + 1
                            })
                    }
                }
            }
            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
                value = input_comment, placeholder = { Text(text = "Comment", fontSize = 14.sp) },
                onValueChange = { input_comment = it })
            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
                value = input_lat, placeholder = { Text(text = "Latitude", fontSize = 14.sp) },
                onValueChange = { input_lat = it })
            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
                value = input_lon, placeholder = { Text(text = "Longitude", fontSize = 14.sp) },
                onValueChange = { input_lon = it })


        }
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            Button(
                modifier = Modifier
                    .randomDebugBorder()
                    .height(50.dp)
                    .weight(100F)
                    .padding(horizontal = 5.dp),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp),
                onClick = {
                    try {
                        if (input_name.text.isNullOrEmpty()) {
                            // input error
                        } else if (input_addr.text.isNullOrEmpty()) {
                            // input error
                        } else {
                            val r = Restaurant()
                            r.name = input_name.text
                            r.address = input_addr.text
                            if (input_comment.text.isNullOrEmpty()) {
                                r.comment = ""
                            } else {
                                r.comment = input_comment.text
                            }
                            if (input_phonenumber.text.isNullOrEmpty()) {
                                r.phonenumber = ""
                            } else {
                                r.phonenumber = input_phonenumber.text
                            }
                            r.lat = geo_coord_string_to_longdb(input_lat.text)
                            r.lon = geo_coord_string_to_longdb(input_lon.text)
                            r.address = input_addr.text
                            r.active = true
                            r.for_summer = false
                            r.category_id = cat_list[cat_itemPosition.value - 1].id

                            orma.updateRestaurant().idEq(restaurant_id)
                                .name(r.name).address(r.address)
                                .lat(r.lat).lon(r.lon)
                                .comment(r.comment).category_id(r.category_id)
                                .phonenumber(r.phonenumber).execute()

                            load_restaurants()

                            @Suppress("CanBeVal")
                            var restaurant_id_copy = restaurant_id
                            if ((input_lat.text.isNullOrEmpty()) || (input_lon.text.isNullOrEmpty()))
                            {
                                get_lat_lon(r.name, r.address, restaurant_id_copy)
                            }
                            //
                            globalstore.setEditRestaurantId(-1)
                            globalstore.updateMainscreenState(MAINSCREEN.MAINLIST)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                content = {
                    Text(
                        text = "Save",
                        style = TextStyle(
                            fontSize = 15.sp,
                        )
                    )
                }
            )
            Spacer(
                modifier = Modifier
                    .randomDebugBorder()
                    .width(2.dp)
                    .weight(1F)
            )
            Button(
                modifier = Modifier
                    .randomDebugBorder()
                    .height(50.dp)
                    .weight(100F)
                    .padding(horizontal = 5.dp),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp),
                onClick = {
                    show_delete_alert = true
                },
                content = {
                    Text(
                        text = "Delete",
                        style = TextStyle(
                            fontSize = 15.sp,
                        )
                    )
                }
            )
            Spacer(
                modifier = Modifier
                    .randomDebugBorder()
                    .width(2.dp)
                    .weight(1F)
            )
            Button(
                modifier = Modifier
                    .randomDebugBorder()
                    .height(50.dp)
                    .weight(100F)
                    .padding(horizontal = 5.dp),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp),
                onClick = {
                    globalstore.updateMainscreenState(MAINSCREEN.MAINLIST)
                },
                content = {
                    Text(
                        text = "Cancel",
                        style = TextStyle(
                            fontSize = 15.sp,
                        )
                    )
                }
            )
        }
    }
}

@Suppress("UNUSED_PARAMETER")
fun get_lat_lon(r_name: String, r_address: String, r_id: Long)
{
    Thread {
        val url = HTTP_NOMINATIM_GET_LAT_LON +
                Uri.encode(r_name)
        Log.i(TAG, "XXXXX:url:" + url)
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            val data_json = connection.inputStream.bufferedReader().use { it.readText() }
            if (!data_json.isNullOrEmpty()) {
                // Log.i(TAG, data_json)
                val json_array = JSONArray(data_json)
                // Log.i(TAG, "XXXXXXXX1:" + json_array.toString())
                val json = json_array.getJSONObject(0)
                // Log.i(TAG, "XXXXXXXX2:" + json.toString())
                val lat = json.getDouble("lat")
                Log.i(TAG, "XXXXXXXX4:" + lat.toString() + " -> " + geo_coord_double_to_longdb(lat))
                val lon = json.getDouble("lon")
                Log.i(TAG, "XXXXXXXX4:" + lon.toString() + " -> " + geo_coord_double_to_longdb(lon))
                Log.i(TAG, "XXXXXXXX5:" + r_id)
                orma.updateRestaurant().idEq(r_id)
                    .lat(geo_coord_double_to_longdb(lat))
                    .lon(geo_coord_double_to_longdb(lon)).execute()
                load_restaurants()
            }
        }
        catch(e: Exception)
        {
            e.printStackTrace()
        }
        finally {
            connection.disconnect()
        }
    }.start()
}

fun geo_coord_string_to_longdb(coord: String): Long
{
    if (coord.isNullOrEmpty())
    {
        return 0
    }
    return ((coord.toDouble()) * 10_000_000).toLong()
}

fun geo_coord_longdb_to_string(coord: Long): String
{
    if (coord == 0L)
    {
        return ""
    }
    return (coord.toDouble() / 10_000_000.toDouble()).toString()
}

fun geo_coord_double_to_longdb(coord: Double): Long
{
    return (coord * 10_000_000).toLong()
}

@Suppress("unused")
fun geo_coord_longdb_to_double(coord: Long): Double
{
    return coord.toDouble() / 10_000_000.toDouble()
}