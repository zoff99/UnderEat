@file:Suppress("unused", "UNUSED_ANONYMOUS_PARAMETER")

package com.zoffcc.applications.undereat

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("ComposableNaming")
@Composable
fun main_list(restaurants: StateRestaurantList, context: Context) {
    Column(
        content = {
            // Header Row
            Row {
                Text(
                    text = "${restaurants.restaurantlist.size} Restaurants",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(10F)
                        .align(Alignment.CenterVertically)
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
                    RestaurantCard(index, data, context)
                }
            }
        }
    )
}