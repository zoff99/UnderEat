package com.zoffcc.applications.undereat

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoffcc.applications.undereat.ui.theme.UnderEatAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import java.lang.Float.max

val messages = MutableStateFlow("running tests ...")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UnderEatAppTheme() {
                // A surface container using the 'background' color from the theme
                //Surface(
                //    modifier = Modifier.fillMaxSize(),
                //    color = MaterialTheme.colorScheme.background
                //) {
                //    // Greeting()
                    SortScreen()
                //}
            }
        }
        val result = sorma2example().testme(this)
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

private class Restaur(
    val name: String,
    val city: String,
    val address: String,
    val addressNumber: Int
)

private val RestaurantList = mutableStateListOf(
    Restaur(name = "Michael", city = "New York", address = "x street", addressNumber = 1234),
    Restaur("Jorge", "Los Angeles", "street A", 2743),
    Restaur("Adam", "Chicago", "street B", 3045),
    Restaur("Philip", "Houston", "street C", 4432),
    Restaur("Amanda", "Phoenix", "street D", 567),
    Restaur("Madonna", "Philadelphia", "street E", 6834),
    Restaur("Jordan", "San Antonio", "street F", 792),
    Restaur("Jackson", "Some City", "street G", 802),
    Restaur("Potter", "Dallas", "street H", 99),
    Restaur("Harry", "Austin", "street I", 161),
    Restaur("Cruise", "San Francisco", "street J", 1732),
    Restaur("Denzel", "Seattle", "street K", 1743),
    Restaur("Hermione", "Denver", "street L", 134),
    Restaur("Vin Diesel", "Dallas", "street M", 134)
)

@Composable
fun SortScreen() {
    val listSorted = remember {
        mutableStateListOf<Restaur>()
    }
    listSorted.addAll(RestaurantList)
    Column(
        //modifier = Modifier.background(MaterialTheme.colorScheme.background),
        content = {
            Text(
                text = "${RestaurantList.size} Restaurants",
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
                            listSorted.clear()
                            listSorted.addAll(RestaurantList.sortedBy { it.name })
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
                            listSorted.clear()
                            listSorted.addAll(RestaurantList.sortedBy { it.city })
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
                            listSorted.clear()
                            listSorted.addAll(RestaurantList.sortedBy { it.address })
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
                            listSorted.clear()
                            listSorted.addAll(RestaurantList.sortedBy { it.addressNumber })
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(start = 2.dp, end = 10.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                content = {
                    itemsIndexed(listSorted) { index, data ->
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
                                text = "City: ${data.city}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                style = TextStyle(
                                    fontSize = 17.sp,
                                    //color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            Text(
                                text = "Address: ${data.address}: ${data.addressNumber}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    //color = MaterialTheme.colorScheme.onSecondary
                                )
                            )
                        }
                    }
                }
            )
        }
    )
}
