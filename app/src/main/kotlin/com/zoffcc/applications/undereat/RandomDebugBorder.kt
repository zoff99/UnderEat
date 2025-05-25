@file:Suppress("UnusedReceiverParameter")

package com.zoffcc.applications.undereat

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import java.util.Random

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