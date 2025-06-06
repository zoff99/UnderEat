@file:Suppress("UselessCallOnNotNull", "LocalVariableName", "ConvertToStringTemplate",
    "FunctionName", "UNUSED_VARIABLE", "ClassName", "LiftReturnOrAssignment", "unused"
)

package com.zoffcc.applications.undereat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumTouchTargetEnforcement
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoffcc.applications.sorm.Restaurant
import com.zoffcc.applications.undereat.corefuncs.orma
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.LinkedBlockingQueue

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ComposableNaming")
@Composable
fun add_form(context: Context) {
    var input_for_summer by remember { mutableStateOf(false) }
    var input_name by remember {
        val textFieldValue = TextFieldValue(text = "")
        mutableStateOf(textFieldValue)
    }
    var input_addr by remember {
        val textFieldValue = TextFieldValue(text = "")
        mutableStateOf(textFieldValue)
    }
    var input_comment by remember {
        val textFieldValue = TextFieldValue(text = "")
        mutableStateOf(textFieldValue)
    }
    var input_phonenumber by remember {
        val textFieldValue = TextFieldValue(text = "")
        mutableStateOf(textFieldValue)
    }
    var input_lat by remember {
        val textFieldValue = TextFieldValue(text = "")
        mutableStateOf(textFieldValue)
    }
    var input_lon by remember {
        val textFieldValue = TextFieldValue(text = "")
        mutableStateOf(textFieldValue)
    }


    val cat_list = orma.selectFromCategory().toList()
    val cat_isDropDownExpanded = remember { mutableStateOf(false) }
    val cat_itemPosition = remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState))
    {
        Spacer(modifier = Modifier.height(20.dp))
        Text("Add new Restaurant", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Column {
            //
            //
            // ----------- name -----------
            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
                value = input_name, placeholder = { Text(text = "Name", fontSize = 14.sp) },
                onValueChange = { input_name = it })
            // ----------- name -----------
            //
            //
            // ----------- address -----------
            Spacer(
                modifier = Modifier
                    .width(5.dp)
                    .height(6.dp)
            )
            CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
                Button(
                    modifier = Modifier
                        .height(40.dp)
                        .padding(horizontal = 5.dp),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    onClick = {
                        if ((!input_name.text.isNullOrEmpty()) && (input_addr.text.isNullOrEmpty())) {
                            get_address(search_item = input_name.text,
                                onResult = {
                                    if (it.isNullOrEmpty()) {
                                        Toast.makeText(context, "No Address Found", Toast.LENGTH_SHORT).show()
                                    } else {
                                        input_addr = TextFieldValue(text = it)
                                    }
                                })
                        } else {
                            Toast.makeText(context, "No Name entered or Address Field is already filled out", Toast.LENGTH_SHORT).show()
                        }
                    },
                    content = {
                        Text(
                            modifier = Modifier.padding(0.dp),
                            text = "fill address with Nominatim",
                            style = TextStyle(
                                fontSize = 12.sp,
                            )
                        )
                    }
                )
            }
            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
                value = input_addr, placeholder = { Text(text = "Address", fontSize = 14.sp) },
                onValueChange = { input_addr = it })
            // ----------- address -----------
            //
            //
            // ----------- phone number -----------
            Spacer(
                modifier = Modifier
                    .width(5.dp)
                    .height(6.dp)
            )
            CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
                Button(
                    modifier = Modifier
                        .height(40.dp)
                        .padding(horizontal = 5.dp),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    onClick = {
                        if ((!input_name.text.isNullOrEmpty()) && (input_phonenumber.text.isNullOrEmpty())) {
                            get_phonenumber(search_item = input_name.text,
                                onResult = {
                                    if (it.isNullOrEmpty()) {
                                        Toast.makeText(context, "No Phonenumber Found", Toast.LENGTH_SHORT).show()
                                    } else {
                                        input_phonenumber = TextFieldValue(text = it)
                                    }
                                })
                        } else {
                            Toast.makeText(context, "No Name entered or Phonenumber is already filled out", Toast.LENGTH_SHORT).show()
                        }
                    },
                    content = {
                        Text(
                            modifier = Modifier.padding(0.dp),
                            text = "fill phonenumber with Nominatim",
                            style = TextStyle(
                                fontSize = 12.sp,
                            )
                        )
                    }
                )
            }

            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
                value = input_phonenumber, placeholder = { Text(text = "Phone Number", fontSize = 14.sp) },
                onValueChange = { input_phonenumber = it })
            // ----------- phone number -----------
            //
            //
            // ----------- category -----------
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
                    Text(text = cat_list[cat_itemPosition.value].name, fontSize = 16.sp)
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
                                cat_itemPosition.value = index
                            })
                    }
                }
            }
            // ----------- category -----------
            //
            //
            // ----------- for summer label -----------
            Row {
                Text(text = "ok for summer",
                    modifier = Modifier.padding(start = 12.dp, end = 5.dp).align(Alignment.CenterVertically)
                    )
                Checkbox(
                    checked = input_for_summer,
                    onCheckedChange = { input_for_summer = it },
                    modifier = Modifier.size(60.dp).align(Alignment.CenterVertically),
                    enabled = true
                )
            }
            // ----------- for summer label -----------
            //
            //
            // ----------- comment -----------
            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
                value = input_comment, placeholder = { Text(text = "Comment", fontSize = 14.sp) },
                onValueChange = { input_comment = it })
            // ----------- comment -----------
            //
            //
            // --------- lat lon ---------
            Spacer(
                modifier = Modifier
                    .width(5.dp)
                    .height(6.dp)
            )
            CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
                Button(
                    modifier = Modifier
                        .height(40.dp)
                        .padding(horizontal = 5.dp),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    onClick = {
                        if ((!input_name.text.isNullOrEmpty()) && (input_lat.text.isNullOrEmpty()) && (input_lon.text.isNullOrEmpty())) {
                            get_lat_lon(search_item = input_name.text,
                                onResult = {
                                    if ((it == null) || (it.lat.isNullOrEmpty()) || (it.lon.isNullOrEmpty())) {
                                        Toast.makeText(context, "No GPS Coordinates Found", Toast.LENGTH_SHORT).show()
                                    } else {
                                        input_lat = TextFieldValue(text = it.lat)
                                        input_lon = TextFieldValue(text = it.lon)
                                    }
                                })
                        } else {
                            Toast.makeText(context, "No Name entered or GPS Coordinates are already filled out", Toast.LENGTH_SHORT).show()
                        }
                    },
                    content = {
                        Text(
                            modifier = Modifier.padding(0.dp),
                            text = "fill location with Nominatim",
                            style = TextStyle(
                                fontSize = 12.sp,
                            )
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.width(1.dp).height(10.dp))
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
            IconButton(
                onClick = {
                    try {
                        // Log.i(TAG, "GPS:"+ input_lat.text + " " + input_lon.text)
                        if ((!input_lat.text.isNullOrEmpty()) && (!input_lon.text.isNullOrEmpty())) {
                            val mapuri =
                                Uri.parse("geo:0,0?q=" + input_lat.text + " " + input_lon.text)
                            val mapIntent = Intent(Intent.ACTION_VIEW, mapuri)
                            context.startActivity(mapIntent)
                        }
                    }
                    catch(e: Exception) {
                        e.printStackTrace()
                    }
                },
                modifier = Modifier
                    .randomDebugBorder()
                    .padding(top = 10.dp)
                    .size(60.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .randomDebugBorder()
                        .fillMaxSize()
                        .padding(4.dp),
                    imageVector = Icons.Default.LocationOn,
                    tint = Color.LightGray,
                    contentDescription = "Restaurant GPS Location"
                )
            }
            // --------- lat lon ---------
            //
            //

        }
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            //
            //
            // ------- button add -------
            Button(
                modifier = Modifier
                    .height(50.dp)
                    .padding(horizontal = 15.dp),
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
                            r.active = true
                            r.for_summer = input_for_summer
                            r.category_id = cat_list[cat_itemPosition.value].id
                            r.lat = geo_coord_string_to_longdb(input_lat.text)
                            r.lon = geo_coord_string_to_longdb(input_lon.text)
                            val r_id_new: Long = orma.insertIntoRestaurant(r)
                            restore_mainlist_state()
                            globalstore.updateMainscreenState(MAINSCREEN.MAINLIST)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                content = {
                    Text(
                        text = "Add",
                        style = TextStyle(
                            fontSize = 15.sp,
                        )
                    )
                }
            )
            Spacer(
                modifier = Modifier
                    .width(50.dp)
                    .weight(10F)
            )
            // ------- button add -------
            //
            //
            // ------- button cancel -------
            Button(
                modifier = Modifier
                    .height(50.dp)
                    .padding(horizontal = 15.dp),
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
            // ------- button cancel -------
            //
            //
        }
    }
}

fun get_address(search_item: String,
                onResult: (String) -> Unit
) {
    val queue = LinkedBlockingQueue<String>()
    val t = Thread {
        var full_addr = ""
        val url = HTTP_NOMINATIM_SEARCH_URL +
                Uri.encode(search_item)
        Log.i(TAG, "XXXXX:url:" + url)
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            val data_json = connection.inputStream.bufferedReader().use { it.readText() }
            if (!data_json.isNullOrEmpty()) {
                // Log.i(TAG, data_json)
                val json_array = JSONArray(data_json)
                // Log.i(TAG, data_json)
                val json = json_array.getJSONObject(0)
                // Log.i(TAG, json.toString())
                val address_json = json.getJSONObject("address")
                // Log.i(TAG, address_json.toString())
                val road = address_json.getString("road")
                Log.i(TAG, "addr:1:" + road)
                var house_number = ""
                try {
                     house_number = " " + address_json.getString("house_number")
                } catch (_: Exception) {
                }
                Log.i(TAG, "addr:2:" + house_number)
                val city_district = address_json.getString("city_district")
                Log.i(TAG, "addr:3:" + city_district)
                val postcode = address_json.getString("postcode")
                Log.i(TAG, "addr:4:" + postcode)
                // orma.updateRestaurant().idEq(r_id)
                // load_restaurants()
                full_addr = "" + road + house_number + ", " + postcode + " " + city_district
            }
        }
        catch(e: Exception)
        {
            e.printStackTrace()
        }
        finally {
            try {
                connection.disconnect()
            } catch(e: Exception){
                e.printStackTrace()
            }
        }
        try {
            queue.add(full_addr)
        } catch(e: Exception){
            e.printStackTrace()
            queue.add("")
        }
    }

    t.start()
    t.join()
    try {
        val full_address = queue.take()
        Log.i(TAG, "address is: " + full_address)
        onResult(full_address)
    } catch(e: Exception){
        e.printStackTrace()
        onResult("")
    }
}

fun get_phonenumber(search_item: String,
                    onResult: (String) -> Unit
) {
    val queue = LinkedBlockingQueue<String>()
    val t = Thread {
        var tel = ""
        val url = HTTP_NOMINATIM_SEARCH_URL +
                Uri.encode(search_item)
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
                try {
                    tel = json.getString("phone")
                } catch (_: java.lang.Exception) {
                    tel = json.getString("contact:phone")
                }
                Log.i(
                    TAG,
                    "XXXXXXXX6:" + tel
                )
//                orma.updateRestaurant().idEq(r_id)
//                    .phonenumber(tel).execute()
//                load_restaurants()
            }
        }
        catch(e: Exception)
        {
            e.printStackTrace()
        }
        finally {
            try {
                connection.disconnect()
            } catch(e: Exception){
                e.printStackTrace()
            }
        }
        try {
            queue.add(tel)
        } catch(e: Exception){
            e.printStackTrace()
            queue.add("")
        }
    }

    t.start()
    t.join()
    try {
        val tel = queue.take()
        Log.i(TAG, "phonenumber is: " + tel)
        onResult(tel)
    } catch(e: Exception){
        e.printStackTrace()
        onResult("")
    }
}

data class lat_lon_double(
    val lat: String,
    val lon: String
)

fun get_lat_lon(search_item: String,
                onResult: (lat_lon_double?) -> Unit
) {
    val queue = LinkedBlockingQueue<lat_lon_double>()
    val t = Thread {
        var lat_lon: lat_lon_double? = null
        val url = HTTP_NOMINATIM_SEARCH_URL +
                Uri.encode(search_item)
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
                lat_lon = lat_lon_double(lat.toString(), lon.toString())
                // orma.updateRestaurant().idEq(r_id)
                //    .lat(geo_coord_double_to_longdb(lat))
                //    .lon(geo_coord_double_to_longdb(lon)).execute()
                //load_restaurants()
            }
        }
        catch(e: Exception)
        {
            e.printStackTrace()
        }
        finally {
            try {
                connection.disconnect()
            } catch(e: Exception){
                e.printStackTrace()
            }
        }
        try {
            queue.add(lat_lon)
        } catch(e: Exception){
            e.printStackTrace()
            queue.add(lat_lon_double("", ""))
        }
    }

    t.start()
    t.join()
    try {
        val lat_long = queue.take()
        Log.i(TAG, "lat lon is: " + lat_long)
        onResult(lat_long)
    } catch(e: Exception){
        e.printStackTrace()
        onResult(null)
    }
}
