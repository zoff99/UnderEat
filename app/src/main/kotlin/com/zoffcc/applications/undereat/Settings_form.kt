@file:Suppress("FunctionName", "LocalVariableName", "SpellCheckingInspection",
    "UselessCallOnNotNull",
    "ConvertToStringTemplate", "UnusedReceiverParameter", "CascadeIf", "LiftReturnOrAssignment",
    "UNUSED_EXPRESSION"
)

package com.zoffcc.applications.undereat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.webkit.MimeTypeMap
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
import androidx.core.content.FileProvider
import com.zoffcc.applications.sorm.OrmaDatabase.run_query_for_single_result
import java.io.File

const val export_sql_filename = "export.uedb"
private const val sql_dump_prefix = "import_5907edf"

// private const val sql_export_dir = "export"
private const val sql_export_db_name = "dbexp"

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
                        // val dbs_path: String = context.getDir(sql_export_dir, MODE_PRIVATE).absolutePath
                        val dbs_path: String = context.filesDir.absolutePath
                        // val dbs_path: String = context.getExternalFilesDir(null)!!.absolutePath
                        val sql_export_filename: String = dbs_path + "/" + export_sql_filename
                        Log.i(TAG, "import filename: " + sql_export_filename)
                        val sql_01 = "ATTACH DATABASE '$sql_export_filename' AS $sql_dump_prefix KEY '';"
                        val sql_02 = "SELECT * from $sql_dump_prefix.Restaurant;"
                        // HINT: !!! keep these columns updated with current schema definition!!!
                        // @formatter:off
                        val sql_02a = "DELETE FROM Category;"
                        val sql_02b = "INSERT INTO Category (id, name) " +
                                "select id, name " +
                                "from $sql_dump_prefix.Category;"
                        //
                        val sql_03a = "DELETE FROM Restaurant;"
                        val sql_03b = "INSERT INTO Restaurant (id, name, category_id, address, area_code, lat, lon, rating, comment, active, for_summer, need_reservation, phonenumber) " +
                                "select id, name, category_id, address, area_code, lat, lon, rating, comment, active, for_summer, need_reservation, phonenumber " +
                                "from $sql_dump_prefix.Restaurant;"
                        val sql_04 = "DETACH DATABASE $sql_dump_prefix;"

                        Log.i(TAG, "share_local_file:001")

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
                // val dbs_path: String = context.getDir(sql_export_dir, MODE_PRIVATE).absolutePath
                val dbs_path: String = context.filesDir.absolutePath
                // val dbs_path: String = context.getExternalFilesDir(null)!!.absolutePath
                val sql_export_filename: String = dbs_path + "/" + export_sql_filename
                try {
                    File(sql_export_filename).delete()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Log.i(TAG, "export filename: " + sql_export_filename)
                val sql_01 = "ATTACH DATABASE '$sql_export_filename' AS $sql_export_db_name KEY '';"
                val sql_02 = "SELECT sqlcipher_export('$sql_export_db_name');"
                val sql_03 = "DETACH DATABASE $sql_export_db_name;"
                run_query_for_single_result(sql_01)
                run_query_for_single_result(sql_02)
                run_query_for_single_result(sql_03)

                val file_uri = FileProvider.getUriForFile(
                    context, "com.zoffcc.applications.undereat.std_fileprovider",
                    File(sql_export_filename))
                Log.i(TAG, "share_local_file:file_uri : " + file_uri)

                val intent = Intent(Intent.ACTION_SEND, file_uri)
                intent.putExtra(Intent.EXTRA_STREAM, file_uri)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                val myMime = MimeTypeMap.getSingleton()
                var mimeType = myMime.getMimeTypeFromExtension(
                    MimeTypeMap.getFileExtensionFromUrl(sql_export_filename)
                )
                if (mimeType == null) {
                    mimeType = "application/octet-stream"
                }

                Log.i(TAG, "share_local_file:mime type: " + mimeType)
                intent.setDataAndType(file_uri, mimeType)
                try {
                    context.startActivity(Intent.createChooser(intent, "Share"))
                } catch (e2: Exception) {
                    e2.printStackTrace()
                }
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