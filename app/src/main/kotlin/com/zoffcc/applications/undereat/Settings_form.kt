@file:Suppress("FunctionName", "LocalVariableName", "SpellCheckingInspection",
    "UselessCallOnNotNull",
    "ConvertToStringTemplate", "UnusedReceiverParameter", "CascadeIf", "LiftReturnOrAssignment",
    "UNUSED_EXPRESSION"
)

package com.zoffcc.applications.undereat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoffcc.applications.sorm.OrmaDatabase.run_query_for_single_result
import java.io.File

@SuppressLint("ComposableNaming")
@Composable
fun settings_form(context: Context) {
    var show_import_alert by remember { mutableStateOf(false) }

    if (show_import_alert)
    {
        AlertDialog(onDismissRequest = { },
            title = { Text("Import data") },
            confirmButton = {
                Button(onClick = {
                    try {
                        // now import the db from sqlite file -------------
                        val dbs_path: String = context.getDir("export", MODE_PRIVATE).absolutePath
                        val sql_export_filename: String = dbs_path + "/" + "export.sqlite"
                        Log.i(TAG, "import filename: " + sql_export_filename)
                        val sql_01 = "ATTACH DATABASE '$sql_export_filename' AS import_5907edf KEY '';"
                        val sql_02 = "SELECT * from import_5907edf.Restaurant;"
                        // HINT: !!! keep these columns updated with current schema definition!!!
                        // @formatter:off
                        val sql_02a = "DELETE FROM Category;"
                        val sql_02b = "INSERT INTO Category (id, name) " +
                                "select id, name " +
                                "from import_5907edf.Category;"
                        //
                        val sql_03a = "DELETE FROM Restaurant;"
                        val sql_03b = "INSERT INTO Restaurant (id, name, category_id, address, area_code, lat, lon, rating, comment, active, for_summer, need_reservation, phonenumber) " +
                                "select id, name, category_id, address, area_code, lat, lon, rating, comment, active, for_summer, need_reservation, phonenumber " +
                                "from import_5907edf.Restaurant;"
                        val sql_04 = "DETACH DATABASE import_5907edf;"
                        // @formatter:on
                        run_query_for_single_result(sql_01)
                        run_query_for_single_result(sql_02)
                        run_query_for_single_result(sql_02a)
                        run_query_for_single_result(sql_02b)
                        run_query_for_single_result(sql_03a)
                        run_query_for_single_result(sql_03b)
                        run_query_for_single_result(sql_04)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    load_restaurants()
                    show_import_alert = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { show_import_alert = false }) {
                    Text("No")
                }
            },
            text = { "Really import data ?" })
    }


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(4.dp))
    {
        Spacer(modifier = Modifier.height(50.dp))
        Button(
            modifier = Modifier
                .height(50.dp)
                .padding(horizontal = 15.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = ButtonDefaults.buttonElevation(4.dp),
            onClick = {
                // now dump the DB to file in SQL format -------------
                val dbs_path: String = context.getDir("export", MODE_PRIVATE).absolutePath
                val sql_export_filename: String = dbs_path + "/" + "export.sqlite"
                try {
                    File(sql_export_filename).delete()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Log.i(TAG, "export filename: " + sql_export_filename)
                val sql_01 = "ATTACH DATABASE '$sql_export_filename' AS export KEY '';"
                val sql_02 = "SELECT sqlcipher_export('export');"
                val sql_03 = "DETACH DATABASE export;"
                run_query_for_single_result(sql_01)
                run_query_for_single_result(sql_02)
                run_query_for_single_result(sql_03)
            },
            content = {
                Text(
                    text = "Export Database",
                    style = TextStyle(
                        fontSize = 15.sp,
                    )
                )
            }
        )
        Spacer(modifier = Modifier.height(50.dp))
        Button(
            modifier = Modifier
                .height(50.dp)
                .padding(horizontal = 15.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = ButtonDefaults.buttonElevation(4.dp),
            onClick = {
                show_import_alert = true
            },
            content = {
                Text(
                    text = "Import Database",
                    style = TextStyle(
                        fontSize = 15.sp,
                    )
                )
            }
        )
    }
}