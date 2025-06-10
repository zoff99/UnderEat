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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CompassScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        val restraunt_id = globalstore.getRestaurantId()
        val data = restaurantliststore.get(restraunt_id)
        Spacer(modifier = Modifier.width(1.dp).height(45.dp))
        Row {
            Spacer(modifier = Modifier.weight(5F).height(1.dp))
            restaurant_name_view(data, false)
            Spacer(modifier = Modifier.weight(5F).height(1.dp))
        }
        Spacer(modifier = Modifier.width(1.dp).height(15.dp))
        Compass(data = data, compact = false, fullsize = true)
    }
}
