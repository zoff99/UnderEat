package com.zoffcc.applications.undereat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val TearDropShape = RoundedCornerShape(
    topStartPercent = 50,
    topEndPercent = 50,
    bottomEndPercent = 10,
    bottomStartPercent = 50
)

@Suppress("unused")
@Composable
fun TearDrop(text: String? = "",
             fontSize: TextUnit = 30.sp,
             fontWeight: FontWeight? = FontWeight.Normal,
             color: Color = Color.Magenta,
             modifier: Modifier = Modifier
                 .wrapContentWidth()) {
        Box(contentAlignment = Alignment.Center,
            modifier = Modifier.graphicsLayer(shape = TearDropShape).background(color, TearDropShape)
        ) {
            Text(text = if (text.isNullOrEmpty()) "" else text,
                fontWeight = fontWeight, fontSize = fontSize,
                modifier = Modifier.padding(6.dp))
        }
}