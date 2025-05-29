@file:Suppress("LiftReturnOrAssignment", "LocalVariableName", "UNUSED_PARAMETER",
    "SpellCheckingInspection", "ConvertToStringTemplate"
)

package com.zoffcc.applications.undereat

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoffcc.applications.sorm.Restaurant
import com.zoffcc.applications.undereat.corefuncs.orma


@Composable
fun RestaurantCard(index: Int, data: Restaurant, context: Context) {
    OutlinedCard(
        modifier = Modifier
            .padding(start = 2.dp, end = 6.dp)
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
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(0.dp)
        ) {
            Column(modifier = Modifier.weight(100000F)) {
                var cat_name: String
                try {
                    cat_name =
                        orma.selectFromCategory().idEq(data.category_id).get(0).name
                } catch (_: Exception) {
                    cat_name = "unknown"
                }
                Text(
                    text = data.name,
                    softWrap = true,
                    maxLines = 2,
                    modifier = Modifier
                        .randomDebugBorder()
                        .padding(start = 4.dp),
                    textAlign = TextAlign.Start,
                    style = TextStyle(
                        fontSize = 20.sp,
                    )
                )
                Text(
                    text = data.address,
                    softWrap = true,
                    maxLines = 2,
                    modifier = Modifier
                        .randomDebugBorder()
                        .padding(start = 6.dp),
                    textAlign = TextAlign.Start,
                    style = TextStyle(
                        fontSize = 14.sp,
                    )
                )
                Row(modifier = Modifier
                    .randomDebugBorder()
                    .padding(start = 6.dp)) {
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
                            context.startActivity(Intent.createChooser(intent,"Share via"))
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
                }
            }
            Spacer(
                modifier = Modifier
                    .randomDebugBorder()
                    .width(1.dp)
                    .weight(10F)
            )
            Column(modifier = Modifier.width(55.dp)) {
                if (data.phonenumber.isNullOrEmpty())
                {
                    IconButton(onClick = {},
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
                }
                else
                {
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
                    if ((data.lat != 0L) && (data.lon != 0L))
                    {
                        icon_green = true
                    }
                    Icon(
                        modifier = Modifier
                            .randomDebugBorder()
                            .fillMaxSize()
                            .padding(4.dp),
                        imageVector = Icons.Default.LocationOn,
                        tint = if (icon_green) Color(1,130,5) else Color.LightGray,
                        contentDescription = "Restaurant Location"
                    )
                }
            }
        }
    }
}