package com.zoffcc.applications.undereat

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoffcc.applications.sorm.Restaurant
import com.zoffcc.applications.undereat.corefuncs.orma
import com.zoffcc.applications.undereat.ui.theme.UnderEatAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow

val messages = MutableStateFlow("running tests ...")
val TAG = "UnderEat"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UnderEatAppTheme() {
                SortScreen()
            }
        }
        val result = corefuncs().init_me(this)
        messages.value = messages.value + result
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    val txt by messages.collectAsState()
    Text(
        text = "" + txt,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 21.sp, lineHeight = 25.0.sp)
    )
}

val restaurantliststore = CoroutineScope(SupervisorJob()).createRestaurantListStore()

@Composable
fun SortScreen() {
    val restaurants by restaurantliststore.stateFlow.collectAsState()
    Column(
        //modifier = Modifier.background(MaterialTheme.colorScheme.background),
        content = {
            Text(
                text = "${restaurants.restaurantlist.size} Restaurants",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = TextStyle(
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    //color = MaterialTheme.colorScheme.onBackground
                )
            )
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                content = {
                    Button(
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        //colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.background),
                        //border = BorderStroke(4.dp, MaterialTheme.colorScheme.secondaryContainer),
                        onClick = {
                            // restaurantliststore.clear()
                            // listSorted.addAll(RestaurantList.sortedBy { it.name })
                        },
                        content = {
                            Text(
                                text = "Sort by Name",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    //color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                        }
                    )
                    Button(
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        //colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.background),
                        //border = BorderStroke(4.dp, MaterialTheme.colorScheme.secondaryContainer),
                        onClick = {
                            //listSorted.clear()
                            //listSorted.addAll(RestaurantList.sortedBy { it.city })
                        },
                        content = {
                            Text(
                                text = "Sort by City",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    //color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                        }
                    )
                    Button(
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        //colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.background),
                        //border = BorderStroke(4.dp, MaterialTheme.colorScheme.secondaryContainer),
                        onClick = {
                            //listSorted.clear()
                            //listSorted.addAll(RestaurantList.sortedBy { it.address })
                        },
                        content = {
                            Text(
                                text = "Sort by Address",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    //color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                        }
                    )
                    Button(
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        //colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.background),
                        //border = BorderStroke(4.dp, MaterialTheme.colorScheme.secondaryContainer),
                        onClick = {
                            //listSorted.clear()
                            //listSorted.addAll(RestaurantList.sortedBy { it.addressNumber })
                        },
                        content = {
                            Text(
                                text = "Sort by AddressNumber",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    //color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                        }
                    )
                }
            )
            load_restaurants()
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(start = 2.dp, end = 10.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                content = {
                    itemsIndexed(items = restaurants.restaurantlist,
                        // key = { item : Restaurant -> item.id },
                    ) { index : Int, data : Restaurant ->
                        Card(
                            modifier = Modifier
                                //.background(color = MaterialTheme.colorScheme.background)
                                .padding(2.dp)
                                .clickable {

                                },
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            //colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
                            //border = BorderStroke(
                            //    0.dp,
                            //    MaterialTheme.colorScheme.secondaryContainer
                            //),
                        ) {
                            Text(
                                text = "Position: $index",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    //color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            Text(
                                text = "Name: ${data.name}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    //color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            Text(
                                text = "City: ${data.address}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                style = TextStyle(
                                    fontSize = 17.sp,
                                    //color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                        }
                    }
                }
            )
        }
    )
}

fun load_restaurants() {
    Log.i(TAG , "111111")
    orma.selectFromRestaurant().toList().forEach {
        Log.i(TAG , "xx")
        try
        {
            val r = Restaurant()
            r.name = it.name
            r.address = it.address
            restaurantliststore.add(item = r)
        } catch (_: Exception)
        {
        }
    }
}
