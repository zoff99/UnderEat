@file:Suppress("LiftReturnOrAssignment", "LocalVariableName", "UNUSED_PARAMETER",
    "SpellCheckingInspection", "ConvertToStringTemplate", "UsePropertyAccessSyntax",
    "ReplaceWithOperatorAssignment", "unused", "KotlinConstantConditions"
)

package com.zoffcc.applications.undereat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoffcc.applications.sorm.Restaurant
import com.zoffcc.applications.undereat.GPSTracker.calculateDistance
import com.zoffcc.applications.undereat.GPSTracker.getBearing
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.pow
import kotlin.math.roundToInt


@SuppressLint("UseKtx")
@Composable
fun RestaurantCard(index: Int, data: Restaurant, context: Context) {
    val state_compactMainlist by globalstore.stateFlow.collectAsState()
    Box(
        modifier = Modifier
            .padding(start = 2.dp, end = 12.dp, bottom = 2.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RectangleShape
            )
            .clickable {
                if (globalstore.getSorterId() == SORTER.DISTANCE.value) {
                    globalstore.setEditRestaurantId(data.id)
                    globalstore.updateMainscreenState(MAINSCREEN.COMPASS)
                }
            }
        ,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(0.dp)
        ) {
            Column(modifier = Modifier.weight(100000F)) {
                val compact = state_compactMainlist.compactMainList
                restaurant_name_view(data, compact)
                var cat_name: String
                try {
                    cat_name =  global_categories[data.category_id]!!
                } catch (_: Exception) {
                    cat_name = "Unknown"
                }
                if (!compact) {
                    Text(
                        text = data.address,
                        softWrap = true,
                        maxLines = 2,
                        modifier = Modifier
                            .randomDebugBorder()
                            .padding(start = 6.dp),
                        textAlign = TextAlign.Start,
                        style = TextStyle(fontSize = 14.sp)
                    )
                    Row(
                        modifier = Modifier
                            .randomDebugBorder()
                            .padding(start = 6.dp)
                    ) {
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SEND)
                                val link_url_encoded = HTTP_MAPS_URL +
                                        Uri.encode(data.name + " " + data.address)
                                // Log.i(TAG, "share_url=" + link_url_encoded)
                                val text = "" + data.name + "\n" + data.address
                                intent.type = "text/plain"
                                intent.putExtra(Intent.EXTRA_SUBJECT, text)
                                intent.putExtra(Intent.EXTRA_TEXT, link_url_encoded)
                                context.startActivity(Intent.createChooser(intent, "Share via"))
                            },
                            modifier = Modifier
                                .randomDebugBorder()
                                .size(25.dp)
                        ) {
                            Icon(
                                modifier = Modifier
                                    .randomDebugBorder()
                                    .fillMaxSize()
                                    .padding(0.dp),
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Restaurant Information"
                            )
                        }
                        Text(
                            text = cat_name,
                            softWrap = true,
                            maxLines = 1,
                            modifier = Modifier
                                .randomDebugBorder()
                                .padding(start = 6.dp)
                                .align(Alignment.CenterVertically),
                            textAlign = TextAlign.Start,
                            style = TextStyle(
                                fontSize = 14.sp,
                            )
                        )
                        if (globalstore.getSorterId() == SORTER.DISTANCE.value) {
                            @Suppress("KotlinConstantConditions")
                            Compass(data, compact)
                        }
                    }
                }

                if (compact) {
                    var rating by remember { mutableIntStateOf(data.rating) }
                    Row {
                        @Suppress("KotlinConstantConditions")
                        StarRatingBar(
                            maxStars = 5,
                            starSizeDp = if (compact) 14.dp else 22.dp,
                            starSpacingDp = 2.dp,
                            isEnabled = false,
                            rating = rating.toFloat(),
                            onRatingChanged = {
                                rating = it.roundToInt()
                            }
                        )
                        if (globalstore.getSorterId() == SORTER.DISTANCE.value) {
                            Spacer(modifier = Modifier.width(5.dp).height(1.dp))
                            @Suppress("KotlinConstantConditions")
                            Compass(data, compact)
                        }
                        summer_label(data, compact)
                        have_ac_label(data, compact)
                    }
                } else {
                    var rating by remember { mutableIntStateOf(data.rating) }
                    Row {
                        @Suppress("KotlinConstantConditions")
                        StarRatingBar(
                            maxStars = 5,
                            starSizeDp = if (compact) 14.dp else 22.dp,
                            starSpacingDp = 2.dp,
                            isEnabled = false,
                            rating = rating.toFloat(),
                            onRatingChanged = {
                                rating = it.roundToInt()
                            }
                        )
                        summer_label(data, compact)
                        have_ac_label(data, compact)
                    }
                }
                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .height(2.dp)
                )
            }
            if (!state_compactMainlist.compactMainList) {
                Spacer(
                    modifier = Modifier
                        .randomDebugBorder()
                        .width(1.dp)
                        .weight(10F)
                )
                // --------- phone number ---------
                Column(modifier = Modifier.width(55.dp)) {
                    if (data.phonenumber.isNullOrEmpty()) {
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .randomDebugBorder()
                                .size(50.dp)
                        ) {
                            Icon(
                                modifier = Modifier
                                    .randomDebugBorder()
                                    .fillMaxSize()
                                    .padding(4000.dp), // TODO: make an empty image. this is just a quick hack
                                imageVector = Icons.Default.Phone,
                                contentDescription = "No Phonenumber available"
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                if (data.phonenumber.isNullOrEmpty()) {
                                    Toast.makeText(
                                        context,
                                        "No Phonenumber for this Restaurant",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    val mapuri = Uri.parse("tel:" + data.phonenumber)
                                    val mapIntent = Intent(Intent.ACTION_DIAL, mapuri)
                                    context.startActivity(mapIntent)
                                }
                            },
                            modifier = Modifier
                                .randomDebugBorder()
                                .size(50.dp)
                        ) {
                            Icon(
                                modifier = Modifier
                                    .randomDebugBorder()
                                    .fillMaxSize()
                                    .padding(4.dp),
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call Restaurant"
                            )
                        }
                    }
                    // --------- phone number ---------
                    IconButton(
                        onClick = {
                            val mapuri = Uri.parse("geo:0,0?q=" + data.name + " " + data.address)
                            val mapIntent = Intent(Intent.ACTION_VIEW, mapuri)
                            context.startActivity(mapIntent)
                        },
                        modifier = Modifier
                            .randomDebugBorder()
                            .size(50.dp)
                    ) {
                        var icon_green = false
                        if ((data.lat != 0L) && (data.lon != 0L)) {
                            icon_green = true
                        }
                        Icon(
                            modifier = Modifier
                                .randomDebugBorder()
                                .fillMaxSize()
                                .padding(4.dp),
                            imageVector = Icons.Default.LocationOn,
                            tint = if (icon_green) Color(1, 130, 5) else Color.LightGray,
                            contentDescription = "Restaurant Location"
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ComposableNaming")
@Composable
private fun summer_label(data: Restaurant, compact: Boolean) {
    var padding_top = 4.dp
    var padding_start = 4.dp
    var icons_size = SwitchDefaults.IconSize
    if (compact) {
        padding_top = 0.dp
        padding_start = 2.dp
        icons_size = SwitchDefaults.IconSize * 0.8f
    }
    if (data.for_summer) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = "ok for summer",
                modifier = Modifier.size(icons_size + padding_top)
                    .padding(start = padding_top, top = padding_start),
                tint = Color.Yellow.copy(green = 0.8f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ComposableNaming")
@Composable
private fun have_ac_label(data: Restaurant, compact: Boolean) {
    var padding_top = 4.dp
    var padding_start = 4.dp
    var icons_size = SwitchDefaults.IconSize * 2.0f
    if (compact) {
        padding_top = 0.dp
        padding_start = 2.dp
        icons_size = SwitchDefaults.IconSize * 1.5f * 0.8f
    }
    if (data.have_ac) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
            Icon(
                imageVector = Icons.Filled.AcUnit,
                contentDescription = "has real A/C",
                modifier = Modifier.size(icons_size + padding_top)
                    .padding(start = padding_top, top = padding_start),
                tint = Color.Blue.copy(green = 0.8f)
            )
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
internal fun restaurant_name_view(data: Restaurant, compact: Boolean) {
    var text_size_compact = 19.sp
    if (data.name.length > 32) {
        text_size_compact = 14.sp
    }
    Text(
        text = data.name,
        softWrap = true,
        maxLines = 2,
        modifier = Modifier
            .randomDebugBorder()
            .padding(start = 4.dp)
            .drawBehind {
                if (data.need_reservation) {
                    val strokeWidthPx = 4.dp.toPx()
                    val verticalOffset = size.height - 2.sp.toPx()
                    drawLine(
                        color = RESERVATION_LINE,
                        strokeWidth = strokeWidthPx,
                        start = Offset(0f, verticalOffset),
                        end = Offset(size.width, verticalOffset)
                    )
                }
            },
        textAlign = TextAlign.Start,
        style = TextStyle(
            fontSize = if (compact) text_size_compact else 20.sp,
        )
    )
}

@Composable
internal fun Compass(data: Restaurant, compact: Boolean, fullsize: Boolean = false) {
    var distance by remember { mutableStateOf("") }
    val state_location by locationstore.stateFlow.collectAsState()
    var relativeBearing by remember { mutableFloatStateOf(0F) }
    if ((gps != null) && (data.lat != 0L) && (data.lon != 0L)) {
        val lat = state_location.lat
        val lon = state_location.lon

        val bearing: Float = (360.0 - getBearing(
            lat, lon,
            geo_coord_longdb_to_double(data.lat),
            geo_coord_longdb_to_double(data.lon)
        )).toFloat()

        @Suppress("ReplaceWithOperatorAssignment")
        val heading: Float = state_location.heading.toFloat()

        relativeBearing = bearing - heading
        //if (relativeBearing < 0) {
        //    relativeBearing = 360 + relativeBearing
        //}

        //Log.i(TAG, "dis11=" + lat + " " + lon + " " +
        //        geo_coord_longdb_to_double(data.lat) + " " + geo_coord_longdb_to_double(data.lon)+
        //" " + data.name)
        val distance_in_meters = calculateDistance(
            lat, lon, 0.0,
            geo_coord_longdb_to_double(data.lat),
            geo_coord_longdb_to_double(data.lon),
            0.0
        )
        if (distance_in_meters.roundToInt() > MAX_DISTANCE) {
            distance = ""
        } else {
            // distance = "" + distance_in_meters.roundToInt() +
            //        " m" + " " + relativeBearing.roundToInt()
            distance = "" + distance_in_meters.roundToInt() + " m"
            // distance = "" + bearing.roundToInt() + " " + heading.roundToInt()
            // Log.i(TAG, "dis=" + distance + " " + data.name + " " + bearing + " " + heading)
        }
    }
    val rotation = smoothRotation(relativeBearing)
    val animatedRotation by animateFloatAsState(
        targetValue = rotation.value,
        animationSpec = tween(
            durationMillis = 400,
            easing = LinearOutSlowInEasing
        )
    )
    if (distance.isNotEmpty()) {
        if (fullsize) {
            Column(modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = Icons.Default.ArrowCircleUp,
                    contentDescription = "Navigation Arrow to Target",
                    modifier = Modifier
                        .randomDebugBorder()
                        .weight(1000F)
                        .fillMaxSize()
                        .rotate(animatedRotation)
                )
                Row {
                    Spacer(modifier = Modifier.weight(5F).height(1.dp))
                    Text(
                        text = distance,
                        softWrap = true,
                        maxLines = 1,
                        textAlign = TextAlign.Start,
                        style = TextStyle(
                            fontSize = 35.sp,
                        )
                    )
                    Spacer(modifier = Modifier.weight(5F).height(1.dp))
                }
                Spacer(modifier = Modifier.width(1.dp).height(60.dp))
            }
        } else {
            if (compact) {
                Row(modifier = Modifier.width(180.dp)) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Navigation Arrow to Target",
                        modifier = Modifier
                            .size(16.dp)
                            .rotate(animatedRotation)
                    )
                    Text(
                        text = distance,
                        softWrap = true,
                        maxLines = 1,
                        modifier = Modifier
                            .randomDebugBorder()
                            .padding(start = 6.dp),
                        textAlign = TextAlign.Start,
                        style = TextStyle(
                            fontSize = 9.sp,
                        )
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "Navigation Arrow to Target",
                    modifier = Modifier
                        .size(30.dp)
                        .rotate(animatedRotation)
                )
                Text(
                    text = distance,
                    softWrap = true,
                    maxLines = 1,
                    modifier = Modifier
                        .randomDebugBorder()
                        .padding(start = 6.dp),
                    textAlign = TextAlign.Start,
                    style = TextStyle(
                        fontSize = 14.sp,
                    )
                )
            }
        }
    }
}

@Composable
private fun smoothRotation(rotation: Float): MutableState<Float> {
    val storedRotation = remember { mutableFloatStateOf(rotation) }

    // Sample data
    // current angle 340 -> new angle 10 -> diff -330 -> +30
    // current angle 20 -> new angle 350 -> diff 330 -> -30
    // current angle 60 -> new angle 270 -> diff 210 -> -150
    // current angle 260 -> new angle 10 -> diff -250 -> +110

    LaunchedEffect(rotation){
        snapshotFlow { rotation  }
            .collectLatest { newRotation ->
                val diff = newRotation - storedRotation.floatValue
                val shortestDiff = when {
                    diff > 180 -> diff - 360
                    diff < -180 -> diff + 360
                    else -> diff
                }
                storedRotation.floatValue = storedRotation.floatValue + shortestDiff
            }
    }

    return storedRotation
}

fun Double.roundTo(numFractionDigits: Int): Double {
    val factor = 10.0.pow(numFractionDigits.toDouble())
    return (this * factor).roundToInt() / factor
}
