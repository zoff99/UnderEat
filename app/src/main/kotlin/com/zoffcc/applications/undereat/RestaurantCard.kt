@file:Suppress("LiftReturnOrAssignment", "LocalVariableName", "UNUSED_PARAMETER",
    "SpellCheckingInspection"
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoffcc.applications.sorm.Restaurant

@Composable
fun RestaurantCard(index: Int, data: Restaurant, context: Context) {
    OutlinedCard(
        modifier = Modifier
            .padding(2.dp)
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
                .padding(1.dp)
        ) {
            Column(modifier = Modifier.weight(100000F)) {
                var cat_name: String
                try {
                    cat_name =
                        corefuncs.orma.selectFromCategory().idEq(data.category_id).get(0).name
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
                        fontSize = 18.sp,
                    )
                )
                Text(
                    text = data.address,
                    softWrap = true,
                    maxLines = 2,
                    modifier = Modifier
                        .randomDebugBorder()
                        .padding(start = 4.dp),
                    textAlign = TextAlign.Start,
                    style = TextStyle(
                        fontSize = 16.sp,
                    )
                )
                Text(
                    text = cat_name,
                    softWrap = true,
                    maxLines = 1,
                    modifier = Modifier
                        .randomDebugBorder()
                        .padding(start = 4.dp),
                    textAlign = TextAlign.Start,
                    style = TextStyle(
                        fontSize = 13.sp,
                    )
                )
            }
            Spacer(
                modifier = Modifier
                    .randomDebugBorder()
                    .width(1.dp)
                    .weight(10F)
            )
            Column(modifier = Modifier.width(55.dp)) {
                IconButton(
                    onClick = {
                        if (data.phonenumber.isNullOrEmpty())
                        {
                            Toast.makeText(context, "No Phonenumber for this Restaurant", Toast.LENGTH_SHORT).show()
                        }
                        else {
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
                IconButton(
                    onClick = {
                        val mapuri = Uri.parse("geo:0,0?q=" + data.address)
                        val mapIntent = Intent(Intent.ACTION_VIEW, mapuri)
                        context.startActivity(mapIntent)
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
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Restaurant Location"
                    )
                }
            }
        }
    }
}