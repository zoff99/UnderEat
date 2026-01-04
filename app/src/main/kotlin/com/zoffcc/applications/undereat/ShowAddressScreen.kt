@file:Suppress("LiftReturnOrAssignment", "LocalVariableName", "UNUSED_PARAMETER",
    "SpellCheckingInspection", "ConvertToStringTemplate", "UsePropertyAccessSyntax",
    "ReplaceWithOperatorAssignment", "unused", "KotlinConstantConditions"
)

package com.zoffcc.applications.undereat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShowAddressScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        val restraunt_id = globalstore.getRestaurantId()
        val data = restaurantliststore.get(restraunt_id)
        Spacer(modifier = Modifier.width(1.dp).height(80.dp))
        Row {
            Spacer(modifier = Modifier.weight(5F).height(1.dp))
            restaurant_name_view(data, false, null)
            Spacer(modifier = Modifier.weight(5F).height(1.dp))
        }

        Spacer(modifier = Modifier.width(1.dp).height(30.dp))
        Row {
            Spacer(modifier = Modifier.weight(5F).height(1.dp))
            Text(
                text = data.address.replace(",", ",\n"),
                //softWrap = true,
                modifier = Modifier
                    .randomDebugBorder(),
                textAlign = TextAlign.Start,
                style = TextStyle(fontSize = 28.sp, lineBreak = LineBreak.Paragraph)
            )
            Spacer(modifier = Modifier.weight(5F).height(1.dp))
        }
    }
}
