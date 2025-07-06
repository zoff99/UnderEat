@file:Suppress("FunctionName", "LocalVariableName", "SpellCheckingInspection",
    "UselessCallOnNotNull",
    "ConvertToStringTemplate", "UnusedReceiverParameter", "CascadeIf", "LiftReturnOrAssignment",
    "UNUSED_EXPRESSION"
)

package com.zoffcc.applications.undereat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.DateFormat
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoffcc.applications.sorm.Restaurant
import com.zoffcc.applications.undereat.corefuncs.orma
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ComposableNaming", "UseKtx")
@Composable
fun edit_form(context: Context) {
    var show_delete_alert by remember { mutableStateOf(false) }
    val restaurant_id = globalstore.getRestaurantId()
    val rest_data = restaurantliststore.get(restaurant_id)
    var text_added_timestamp by remember { mutableLongStateOf(rest_data.added_timestamp) }
    var text_modified_timestamp by remember { mutableLongStateOf(rest_data.modified_timestamp) }
    var input_for_summer by remember { mutableStateOf(rest_data.for_summer) }
    var input_have_ac by remember { mutableStateOf(rest_data.have_ac) }
    var input_needs_reservation by remember { mutableStateOf(rest_data.need_reservation) }
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
    // Log.i(TAG, "RRRRR:lat:" + rest_data.lat)
    var input_lat by remember {
        val textFieldValue = TextFieldValue(text = geo_coord_longdb_to_string(rest_data.lat))
        mutableStateOf(textFieldValue)
    }
    // Log.i(TAG, "RRRRR:lon:" + rest_data.lon)
    var input_lon by remember {
        val textFieldValue = TextFieldValue(text = geo_coord_longdb_to_string(rest_data.lon))
        mutableStateOf(textFieldValue)
    }
    var rating by remember { mutableIntStateOf(rest_data.rating) }

    val cat_list = orma.selectFromCategory().toList()
    val cat_isDropDownExpanded = remember { mutableStateOf(false) }
    val cat_itemPosition = remember { mutableIntStateOf(rest_data.category_id.toInt()) }
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
                        restore_mainlist_state()
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
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
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
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
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
                    // Log.i(TAG, "CCCCCCC22:" + cat_itemPosition.value + " ___ " + cat_list)
                    Text(text = cat_list[cat_itemPosition.intValue - 1].name, fontSize = 16.sp)
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
                                cat_itemPosition.intValue = index + 1
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
            // ----------- have ac label -----------
            Row {
                Text(text = "has A/C",
                    modifier = Modifier.padding(start = 12.dp, end = 5.dp).align(Alignment.CenterVertically)
                )
                Checkbox(
                    checked = input_have_ac,
                    onCheckedChange = { input_have_ac = it },
                    modifier = Modifier.size(60.dp).align(Alignment.CenterVertically),
                    enabled = true
                )
            }
            // ----------- have ac label -----------
            //
            //
            // ----------- need reservation -----------
            Row {
                Text(text = "needs reservation",
                    modifier = Modifier.padding(start = 12.dp, end = 5.dp).align(Alignment.CenterVertically)
                )
                Checkbox(
                    checked = input_needs_reservation,
                    onCheckedChange = { input_needs_reservation = it },
                    modifier = Modifier.size(60.dp).align(Alignment.CenterVertically),
                    enabled = true
                )
            }
            // ----------- need reservation -----------
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
            // ----------- rating -----------
            Spacer(modifier = Modifier.width(1.dp).height(10.dp))
            StarRatingBar(
                maxStars = 5,
                starSizeDp = 45.dp,
                starSpacingDp = 2.dp,
                isEnabled = true,
                rating = rating.toFloat(),
                onRatingChanged = {
                    rating = it.roundToInt()
                }
            )
            // ----------- rating -----------
            //
            //
            // --------- lat lon ---------
            Spacer(modifier = Modifier.width(1.dp).height(10.dp))
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
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
        Spacer(modifier = Modifier.height(2.dp))
        Row {
            Spacer(modifier = Modifier.width(12.dp))
            Text("added", modifier = Modifier.width(100.dp), fontSize = 14.sp)
            Text(DateFormat.format("yyyy.MM.dd HH:mm:ss", text_added_timestamp).toString(), fontSize = 14.sp)
        }
        Row {
            Spacer(modifier = Modifier.width(12.dp))
            Text("modified", modifier = Modifier.width(100.dp), fontSize = 14.sp)
            Text(DateFormat.format("yyyy.MM.dd HH:mm:ss", text_modified_timestamp).toString(), fontSize = 14.sp)
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
                            r.for_summer = input_for_summer
                            r.have_ac = input_have_ac
                            r.need_reservation = input_needs_reservation
                            r.rating = rating
                            r.category_id = cat_list[cat_itemPosition.intValue - 1].id

                            orma.updateRestaurant().idEq(restaurant_id)
                                .name(r.name).address(r.address)
                                .lat(r.lat).lon(r.lon).rating(rating)
                                .for_summer(r.for_summer)
                                .have_ac(r.have_ac)
                                .need_reservation(r.need_reservation)
                                .comment(r.comment).category_id(r.category_id)
                                .modified_timestamp(System.currentTimeMillis())
                                .phonenumber(r.phonenumber).execute()

                            restore_mainlist_state()
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