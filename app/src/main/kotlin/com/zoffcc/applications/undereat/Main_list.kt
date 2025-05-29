@file:Suppress("unused", "UNUSED_ANONYMOUS_PARAMETER", "LocalVariableName",
    "SpellCheckingInspection", "ConvertToStringTemplate", "UselessCallOnNotNull"
)

package com.zoffcc.applications.undereat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoffcc.applications.sorm.Category
import com.zoffcc.applications.undereat.corefuncs.orma
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("ComposableNaming")
@Composable
fun main_list(restaurants: StateRestaurantList, context: Context) {
    val all_cat = Category()
    all_cat.id = -1
    all_cat.name = "* All *"
    //
    val cat_list = ArrayList<Category>()
    cat_list.add(all_cat)
    cat_list.addAll(orma.selectFromCategory().toList())
    //
    val filter_category_id = globalstore.getFilterCategoryId()
    var current_filter_cat_id_pos = 0
    cat_list.forEachIndexed { index, category ->
        if (category.id == filter_category_id) {
            current_filter_cat_id_pos = index
        }
    }
    //
    val cat_isDropDownExpanded = remember { mutableStateOf(false) }
    val cat_itemPosition = remember { mutableStateOf(current_filter_cat_id_pos) }
    val listState = rememberLazyListState()

    Column {
            // Header Row
            Row {
                Button(
                    modifier = Modifier
                        .height(50.dp)
                        .padding(4.dp),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    onClick = {
                        globalstore.updateMainscreenState(MAINSCREEN.ADD)
                    },
                    content = {
                        Text(
                            text = "add",
                            style = TextStyle(
                                fontSize = 12.sp,
                            )
                        )
                    }
                )
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(10F)
                    .align(Alignment.CenterVertically)
                    .padding(2.dp)
                    .clickable {
                        globalstore.updateMainscreenState(MAINSCREEN.SETTINGS)
                    })
                {
                    Text(
                        text = "${restaurants.restaurantlist.size} Restaurants",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(10F)
                            .align(Alignment.CenterVertically)
                            .padding(2.dp),
                        style = TextStyle(
                            fontSize = 17.sp,
                            textAlign = TextAlign.Start,
                        )
                    )
                }
                Button(
                    modifier = Modifier
                        .height(50.dp)
                        .padding(4.dp),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    onClick = {
                        if (TAXI_PHONE_NUMBER.isNullOrEmpty())
                        {
                            Toast.makeText(context, "No Taxi Phonenumber set", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            val mapuri = Uri.parse("tel:" + TAXI_PHONE_NUMBER)
                            val mapIntent = Intent(Intent.ACTION_DIAL, mapuri)
                            context.startActivity(mapIntent)
                        }
                    },
                    content = {
                        Icon(
                            modifier = Modifier
                                .randomDebugBorder()
                                .padding(0.dp),
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call Restaurant"
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            modifier = Modifier.padding(0.dp),
                            text = "Taxi",
                            style = TextStyle(
                                fontSize = 13.sp,
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
                            //Log.i(
                            //    TAG,
                            //    " s2:" + restaurants.restaurantlist.size + " " + restaurants.restaurantlist
                            //)
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
                                        //Log.i(TAG, "CTT1:" + cat_isDropDownExpanded.value + " " + cat_itemPosition.value)
                                        //Log.i(TAG, "CTT2:" + cat_list[cat_itemPosition.value].id + " " + cat_list[cat_itemPosition.value].name)
                                        globalstore.setFilterCategoryId(cat_list[cat_itemPosition.value].id)
                                        load_restaurants()
                                    })
                            }
                        }
                    }

                }
            )
            Row {
                // Data List
                LazyColumn(
                    state = listState,
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
                ScrollBar(lazyListState = listState)
            }
        }
}

@Suppress("UNUSED_PARAMETER", "SimpleRedundantLet")
@Composable
fun ScrollBar(
    lazyListState: LazyListState,
    hidable: Boolean = true,
    color: Color = Color.LightGray,
    width: Int = 15,
    modifier: Modifier = Modifier,
) {
    val height by remember(lazyListState) {
        derivedStateOf {
            val columnHeight = lazyListState.layoutInfo.viewportSize.height
            val totalCnt = lazyListState.layoutInfo.totalItemsCount.takeIf { it > 0 } ?: 1
            val visibleLastIndex = lazyListState.layoutInfo.visibleItemsInfo.lastIndex

            (visibleLastIndex + 1) * (columnHeight.toFloat() / totalCnt)
        }
    }

    val topOffset by remember(lazyListState) {
        derivedStateOf {
            val totalCnt = lazyListState.layoutInfo.totalItemsCount.takeIf { it > 0 } ?: 1
            val visibleCnt =
                lazyListState.layoutInfo.visibleItemsInfo.count().takeIf { it > 0 } ?: 1
            val columnHeight = lazyListState.layoutInfo.viewportSize.height
            val firstVisibleIndex = lazyListState.firstVisibleItemIndex
            val scrollItemHeight = (columnHeight.toFloat() / totalCnt)
            val realItemHeight = (columnHeight.toFloat() / visibleCnt)
            val offset = ((firstVisibleIndex) * scrollItemHeight)
            val firstItemOffset =
                lazyListState.firstVisibleItemScrollOffset / realItemHeight * scrollItemHeight

            offset + firstItemOffset
        }
    }
    val scope = rememberCoroutineScope()
    var isShownScrollBar by remember(lazyListState) {
        mutableStateOf(true)
    }

    if (hidable) {
        var disposeJob: Job? by remember {
            mutableStateOf(null)
        }
        DisposableEffect(topOffset) {
            isShownScrollBar = true
            onDispose {
                disposeJob?.takeIf { it.isActive }?.let {
                    it.cancel()
                }
                disposeJob = scope.launch {
                    delay(1000)
                    isShownScrollBar = false

                }
            }
        }
    }

    val columnSize by remember(lazyListState) {
        derivedStateOf {
            lazyListState.layoutInfo.viewportSize
        }
    }
    AnimatedVisibility(visible = isShownScrollBar, enter = fadeIn(), exit = fadeOut()) {
        Canvas(
            modifier = Modifier
                .size(width = columnSize.width.dp, height = columnSize.height.dp),
            onDraw = {
                drawRect(
                    color,
                    topLeft = Offset(this.size.width - width, topOffset),
                    size = Size(width.toFloat(), height),
                )
            })
    }

}
