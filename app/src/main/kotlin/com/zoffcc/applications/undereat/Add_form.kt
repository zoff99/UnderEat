@file:Suppress("UselessCallOnNotNull", "LocalVariableName")

package com.zoffcc.applications.undereat

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoffcc.applications.sorm.Restaurant
import java.lang.Exception

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ComposableNaming")
@Composable
fun add_form() {
    var input_name by remember {
        val textFieldValue = TextFieldValue(text = "")
        mutableStateOf(textFieldValue)
    }
    var input_addr by remember {
        val textFieldValue = TextFieldValue(text = "")
        mutableStateOf(textFieldValue)
    }
    var input_comment by remember {
        val textFieldValue = TextFieldValue(text = "")
        mutableStateOf(textFieldValue)
    }
    var input_phonenumber by remember {
        val textFieldValue = TextFieldValue(text = "")
        mutableStateOf(textFieldValue)
    }

    val cat_list = corefuncs.orma.selectFromCategory().toList()
    val cat_isDropDownExpanded = remember { mutableStateOf(false) }
    val cat_itemPosition = remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize())
    {
        Spacer(modifier = Modifier.height(20.dp))
        Text("Add new Restaurant", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Column {
            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
                value = input_name, placeholder = { Text(text = "Name", fontSize = 14.sp) },
                onValueChange = { input_name = it })
            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
                value = input_addr, placeholder = { Text(text = "Address", fontSize = 14.sp) },
                onValueChange = { input_addr = it })
            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
                value = input_phonenumber, placeholder = { Text(text = "Phone Number", fontSize = 14.sp) },
                onValueChange = { input_phonenumber = it })
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
                            })
                    }
                }
            }
            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
                value = input_comment, placeholder = { Text(text = "Comment", fontSize = 14.sp) },
                onValueChange = { input_comment = it })
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            Button(
                modifier = Modifier
                    .height(50.dp)
                    .padding(horizontal = 15.dp),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp),
                onClick = {
                    try {
                        if (input_name.text.isNullOrEmpty()) {
                            // input error
                        } else if (input_addr.text.isNullOrEmpty()) {
                            // input error
                        } else {
                            val r = Restaurant()
                            r.name = input_name.text
                            r.address = input_addr.text
                            if (input_comment.text.isNullOrEmpty()) {
                                r.comment = ""
                            } else {
                                r.comment = input_comment.text
                            }
                            if (input_phonenumber.text.isNullOrEmpty()) {
                                r.phonenumber = ""
                            } else {
                                r.phonenumber = input_phonenumber.text
                            }
                            r.active = true
                            r.for_summer = false
                            r.category_id = cat_list[cat_itemPosition.value].id
                            corefuncs.orma.insertIntoRestaurant(r)
                            load_restaurants()
                            globalstore.updateMainscreenState(MAINSCREEN.MAINLIST)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                content = {
                    Text(
                        text = "Add",
                        style = TextStyle(
                            fontSize = 15.sp,
                        )
                    )
                }
            )
            Spacer(
                modifier = Modifier
                    .width(50.dp)
                    .weight(10F)
            )
            Button(
                modifier = Modifier
                    .height(50.dp)
                    .padding(horizontal = 15.dp),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp),
                onClick = {
                    globalstore.updateMainscreenState(MAINSCREEN.MAINLIST)
                },
                content = {
                    Text(
                        text = "Cancel",
                        style = TextStyle(
                            fontSize = 15.sp,
                        )
                    )
                }
            )
        }
    }
}