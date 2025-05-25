@file:Suppress("FunctionName", "LocalVariableName", "SpellCheckingInspection",
    "UselessCallOnNotNull", "UNUSED_ANONYMOUS_PARAMETER", "UNUSED_PARAMETER",
    "ConvertToStringTemplate", "UnusedReceiverParameter", "CascadeIf", "LiftReturnOrAssignment"
)

package com.zoffcc.applications.undereat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoffcc.applications.sorm.Restaurant
import com.zoffcc.applications.undereat.corefuncs.orma
import com.zoffcc.applications.undereat.ui.theme.UnderEatAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Random

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

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ComposableNaming")
@Composable
private fun edit_form() {
    var show_delete_alert by remember { mutableStateOf(false) }
    val restaurant_id = globalstore.getRestaurantId()
    val rest_data = restaurantliststore.get(restaurant_id)
    var input_name by remember {
        val textFieldValue = TextFieldValue(text = if (rest_data.name.isNullOrEmpty()) "" else rest_data.name)
        mutableStateOf(textFieldValue)
    }
    var input_addr by remember {
        val textFieldValue = TextFieldValue(text = if (rest_data.address.isNullOrEmpty()) "" else rest_data.address)
        mutableStateOf(textFieldValue)
    }
    var input_comment by remember {
        val textFieldValue = TextFieldValue(text = if (rest_data.comment.isNullOrEmpty()) "" else rest_data.comment)
        mutableStateOf(textFieldValue)
    }

    val cat_list = orma.selectFromCategory().toList()
    val cat_isDropDownExpanded = remember {mutableStateOf(false)}
    val cat_itemPosition = remember {mutableStateOf(rest_data.category_id.toInt())}

    if (show_delete_alert)
    {
        AlertDialog(onDismissRequest = { },
            title = { Text("Delete") },
            confirmButton = {
                Button(onClick = {
                    try {
                        orma.deleteFromRestaurant().idEq(rest_data.id).execute()
                        //
                        globalstore.setEditRestaurantId(-1)
                        load_restaurants()
                        globalstore.updateMainscreenState(MAINSCREEN.MAINLIST)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = {show_delete_alert = false}) {
                    Text("No")
                }
            },
            text = { "Really delete this Restaurant ?" })
    }

    Column(modifier = Modifier.fillMaxSize())
    {
        Spacer(modifier = Modifier.height(20.dp))
        Text("Edit Restaurant", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Column {
            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
                value = input_name, placeholder = {Text(text = "Name", fontSize = 14.sp)},
                onValueChange = {input_name = it})
            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
                value = input_addr, placeholder = {Text(text = "Address", fontSize = 14.sp)},
                onValueChange = {input_addr = it})
            Box {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = CenterVertically,
                    modifier = Modifier
                        .randomDebugBorder()
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(10.dp)
                        .clickable {
                            cat_isDropDownExpanded.value = true
                        }
                ) {
                    Log.i(TAG, "CCCCCCC22:" + cat_itemPosition.value + " ___ " + cat_list)
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
                                Log.i(TAG, "CCCCCCC333:" + cat_itemPosition.value + " ___ " + index)
                                cat_itemPosition.value = index + 1
                            })
                    }
                }
            }
            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
                value = input_comment, placeholder = {Text(text = "Comment", fontSize = 14.sp)},
                onValueChange = {input_comment = it})


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
                        if (input_name.text.isNullOrEmpty())
                        {
                            // input error
                        }
                        else if (input_addr.text.isNullOrEmpty())
                        {
                            // input error
                        }
                        else {
                            val r = Restaurant()
                            r.name = input_name.text
                            r.address = input_addr.text
                            if (input_comment.text.isNullOrEmpty())
                            {
                                r.comment = ""
                            }
                            else {
                                r.comment = input_comment.text
                            }
                            r.active = true
                            r.for_summer = false
                            Log.i(TAG, "CCCCCCC444_save:" + cat_itemPosition.value + " ___ " + cat_list)
                            r.category_id = cat_list[cat_itemPosition.value - 1].id
                            orma.updateRestaurant().name(r.name).address(r.address).comment(r.comment).category_id(r.category_id).execute()
                            //
                            globalstore.setEditRestaurantId(-1)
                            load_restaurants()
                            globalstore.updateMainscreenState(MAINSCREEN.MAINLIST)
                        }
                    } catch (e: java.lang.Exception) {
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


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ComposableNaming")
@Composable
private fun add_form() {
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

    val cat_list = orma.selectFromCategory().toList()
    val cat_isDropDownExpanded = remember {mutableStateOf(false)}
    val cat_itemPosition = remember {mutableStateOf(0)}

    Column(modifier = Modifier.fillMaxSize())
    {
        Spacer(modifier = Modifier.height(20.dp))
        Text("Add new Restaurant", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Column {
            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
                value = input_name, placeholder = {Text(text = "Name", fontSize = 14.sp)},
                onValueChange = {input_name = it})
            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
                value = input_addr, placeholder = {Text(text = "Address", fontSize = 14.sp)},
                onValueChange = {input_addr = it})
            Box {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = CenterVertically,
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
            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
                value = input_comment, placeholder = {Text(text = "Comment", fontSize = 14.sp)},
                onValueChange = {input_comment = it})


        }
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            Button(
                modifier = Modifier
                    .height(50.dp)
                    .padding(horizontal = 15.dp),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp),
                onClick = {
                    try {
                        if (input_name.text.isNullOrEmpty())
                        {
                            // input error
                        }
                        else if (input_addr.text.isNullOrEmpty())
                        {
                            // input error
                        }
                        else {
                            val r = Restaurant()
                            r.name = input_name.text
                            r.address = input_addr.text
                            if (input_comment.text.isNullOrEmpty())
                            {
                                r.comment = ""
                            }
                            else {
                                r.comment = input_comment.text
                            }
                            r.active = true
                            r.for_summer = false
                            r.category_id = cat_list[cat_itemPosition.value].id
                            orma.insertIntoRestaurant(r)
                            load_restaurants()
                            globalstore.updateMainscreenState(MAINSCREEN.MAINLIST)
                        }
                    } catch (e: java.lang.Exception) {
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
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
private fun main_list(restaurants: StateRestaurantList) {
    Column(
        content = {
            // Header Row
            Row {
                Text(
                    text = "${restaurants.restaurantlist.size} Restaurants",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(10F)
                        .align(CenterVertically)
                        .padding(2.dp),
                    style = TextStyle(
                        fontSize = 20.sp,
                        textAlign = TextAlign.Start,
                    )
                )
                Button(
                    modifier = Modifier
                        .height(50.dp)
                        .padding(2.dp),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    onClick = {
                        globalstore.updateMainscreenState(MAINSCREEN.ADD)
                    },
                    content = {
                        Text(
                            text = "add",
                            style = TextStyle(
                                fontSize = 15.sp,
                            )
                        )
                    }
                )
            }
            // Button Row
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                content = {
                    Button(
                        modifier = Modifier.padding(2.dp),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        onClick = {
                            restaurantliststore.sortByName()
                            Log.i(
                                TAG,
                                " s1:" + restaurants.restaurantlist.size + " " + restaurants.restaurantlist
                            )
                        },
                        content = {
                            Text(
                                text = "Name",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                )
                            )
                        }
                    )
                    Button(
                        modifier = Modifier.padding(2.dp),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        onClick = {
                            restaurantliststore.sortByAddress()
                            Log.i(
                                TAG,
                                " s2:" + restaurants.restaurantlist.size + " " + restaurants.restaurantlist
                            )
                        },
                        content = {
                            Text(
                                text = "Address",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                )
                            )
                        }
                    )
                }
            )
            // Data List
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(start = 2.dp, end = 10.dp)
                    .randomDebugBorder(),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                itemsIndexed(items = restaurants.restaurantlist,
                    key = { index, item -> item.id }
                ) { index, data ->
                    RestaurantCard(index, data)
                }
            }
        }
    )
}

@Composable
fun RestaurantCard(index: Int, data: Restaurant) {
    OutlinedCard(
        modifier = Modifier
            .padding(1.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                globalstore.setEditRestaurantId(data.id)
                globalstore.updateMainscreenState(MAINSCREEN.EDIT)
            },
        shape = RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 4.dp,
            bottomEnd = 4.dp,
            bottomStart = 4.dp,
        ),
        elevation =  CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = CenterVertically,
            modifier = Modifier
                .padding(1.dp)
        ) {
            Column(modifier = Modifier.weight(100000F)) {
                var cat_name: String
                try {
                    cat_name = orma.selectFromCategory().idEq(data.category_id).get(0).name
                } catch (_: Exception) {
                    cat_name = "unknown"
                }
                Text(
                    text = data.name ,
                    softWrap = true,
                    maxLines = 1,
                    modifier = Modifier
                        .randomDebugBorder()
                        .padding(3.dp),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 18.sp,
                    )
                )
                Text(
                    text = data.address ,
                    softWrap = true,
                    maxLines = 1,
                    modifier = Modifier
                        .randomDebugBorder()
                        .padding(3.dp),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 18.sp,
                    )
                )
                Text(
                    text = cat_name,
                    softWrap = true,
                    maxLines = 1,
                    modifier = Modifier
                        .randomDebugBorder()
                        .padding(3.dp),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 13.sp,
                    )
                )
            }
            Spacer(modifier = Modifier
                .randomDebugBorder()
                .width(1.dp)
                .weight(10F))
            IconButton(
                onClick = {},
                modifier = Modifier
                    .randomDebugBorder()
                    .size(60.dp)
            ) {
                Icon(modifier = Modifier
                    .randomDebugBorder()
                    .fillMaxSize()
                    .padding(4.dp),
                    imageVector = Icons.Default.LocationOn, contentDescription = "Localized description")
            }
        }
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

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.randomDebugBorder(): Modifier {
    return if (DEBUG_COMPOSE_UI_UPDATES)
    {
        Modifier
            .padding(3.dp)
            .border(
                width = 4.dp,
                color = Color(
                    Random().nextInt(255),
                    Random().nextInt(255),
                    Random().nextInt(255)
                ),
                shape = RectangleShape
            )
    }
    else
    {
        Modifier
    }
}

