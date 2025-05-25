@file:Suppress("FunctionName")

package com.zoffcc.applications.undereat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoffcc.applications.sorm.Restaurant
import com.zoffcc.applications.undereat.corefuncs.orma
import com.zoffcc.applications.undereat.ui.theme.UnderEatAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Random

val TAG = "UnderEat"

val messages = MutableStateFlow("running tests ...")
const val DEBUG_COMPOSE_UI_UPDATES = true // set "false" for release builds


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UnderEatAppTheme() {
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
    var state_mainscreen by remember { mutableStateOf(MAINSCREEN.MAINLIST.value) }
    val restaurants by restaurantliststore.stateFlow.collectAsState()
    Log.i(TAG, "size_list=" + restaurants.restaurantlist.size)

    if (state_mainscreen == MAINSCREEN.MAINLIST.value) {
        Column(
            content = {
                // Header Row
                Row() {
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
                            state_mainscreen = MAINSCREEN.ADD.value
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
    else if (state_mainscreen == MAINSCREEN.ADD.value)
    {
        Column(modifier = Modifier.fillMaxSize())
        {
            Text("Add new Restaurant")
            Row() {
                Button(
                    modifier = Modifier
                        .height(50.dp)
                        .padding(horizontal = 15.dp),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    onClick = {
                        state_mainscreen = MAINSCREEN.MAINLIST.value
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
                Spacer(modifier = Modifier
                    .width(50.dp)
                    .weight(10F))
                Button(
                    modifier = Modifier
                        .height(50.dp)
                        .padding(horizontal = 15.dp),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    onClick = {
                        state_mainscreen = MAINSCREEN.MAINLIST.value
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantCard(index: Int, data: Restaurant) {
    OutlinedCard(
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {},
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
                .padding(4.dp)
        ) {
            Text(
                text = data.name + " " + data.address,
                modifier = Modifier
                    .padding(3.dp).randomDebugBorder(),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.randomDebugBorder().width(1.dp).weight(10F))
            IconButton(
                onClick = {},
                modifier = Modifier.randomDebugBorder().size(50.dp)
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = "Localized description")
            }
        }
    }
}

fun load_restaurants() {
    Log.i(TAG, "load_restaurants:start")
    orma.selectFromRestaurant().toList().forEach {
        try
        {
            val r = Restaurant()
            r.id = it.id
            r.name = it.name
            r.address = it.address
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

