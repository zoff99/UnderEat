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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.zoffcc.applications.sorm.OrmaDatabase
import com.zoffcc.applications.sorm.OrmaDatabase.run_query_for_single_result
import java.io.File

const val import_file_extension = ".uedb"
const val export_sql_filename = "export" + import_file_extension
const val export_ics_filename = "export.ics"
private const val sql_dump_prefix = "import_5907edf"

// private const val sql_export_dir = "export"
private const val sql_export_db_name = "dbexp"

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ComposableNaming")
@Composable
fun settings_form(context: Context) {
    var show_import_alert by remember { mutableStateOf(false) }
    var input_taxi_number by remember {
        val textFieldValue =
            TextFieldValue(text = if (TAXI_PHONE_NUMBER.isNullOrEmpty()) "" else TAXI_PHONE_NUMBER!!)
        mutableStateOf(textFieldValue)
    }

    if (show_import_alert)
    {
        val dbs_path: String
        var sql_export_filename: String = ""
        try
        {
            dbs_path = context.filesDir.absolutePath
            // val dbs_path: String = context.getExternalFilesDir(null)!!.absolutePath
            sql_export_filename = dbs_path + "/" + export_sql_filename
            Log.i(TAG, "import filename: " + sql_export_filename)
        }
        catch(_: Exception)
        {
        }

        AlertDialog(onDismissRequest = { },
            title = { Text("Import data from file:" + "\n" + sql_export_filename) },
            confirmButton = {
                Button(onClick = {
                    try {
                        // now import the db from sqlite file -------------
                        // val dbs_path: String = context.getDir(sql_export_dir, MODE_PRIVATE).absolutePath
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
                        val sql_03b = "INSERT INTO Restaurant (id, name, category_id, address, area_code, lat, lon, rating, comment, active, for_summer, need_reservation, phonenumber, have_ac, added_timestamp, modified_timestamp, only_evening)" +
                                " " +
                                "select id, name, category_id, address, area_code, lat, lon, rating, comment, active, for_summer, need_reservation, phonenumber, have_ac, added_timestamp, modified_timestamp, only_evening" +
                                " " +
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
                    restore_mainlist_state()
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

    val scrollState = rememberScrollState()
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(4.dp)
        .verticalScroll(scrollState))
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
                // remove values from "lov" table, since those are kind of private settings
                val sql_02b = "DELETE FROM '$sql_export_db_name'.lov;"
                val sql_03 = "DETACH DATABASE $sql_export_db_name;"
                run_query_for_single_result(sql_01)
                run_query_for_single_result(sql_02)
                run_query_for_single_result(sql_02b)
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
        Spacer(modifier = Modifier.height(50.dp))
        Text(modifier = Modifier
            .fillMaxWidth()
            .padding(end = 15.dp, start = 15.dp, bottom = 5.dp),
            text = "Taxi Phone Number",
            fontSize = 18.sp
        )
        TextField(modifier = Modifier
            .fillMaxWidth()
            .padding(end = 15.dp, start = 10.dp, bottom = 5.dp),
            value = input_taxi_number, placeholder = { Text(text = "Taxi Phone Number", fontSize = 14.sp) },
            onValueChange = {
                input_taxi_number = it
                set_taxi_number(input_taxi_number.text)
            })

        Row {
            Column(modifier = Modifier.width(16.dp)) {

            }
            Column {
                Spacer(modifier = Modifier.height(50.dp))
                var git_hash = ""
                try {
                    git_hash = BuildConfig.GIT_HASH
                } catch (_: Exception) {
                }
                Text("git hash: " + git_hash, fontSize = 14.sp)

                var b_type = ""
                try {
                    b_type = BuildConfig.BUILD_TYPE
                } catch (_: Exception) {
                }
                Text("build type: " + b_type, fontSize = 14.sp)

                var version_code = ""
                try {
                    version_code = "" + BuildConfig.VERSION_CODE
                } catch (_: Exception) {
                }
                Text("version: " + version_code, fontSize = 14.sp)


                var debug__sqlite_user_version: String? = "unknown"
                try {
                    debug__sqlite_user_version =
                        run_query_for_single_result("PRAGMA user_version")
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                var debug__sqlite_version: String? = "unknown"
                try {
                    debug__sqlite_version =
                        run_query_for_single_result("SELECT sqlite_version()")
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                var debug__cipher_version: String? = "unknown"
                try {
                    debug__cipher_version =
                        run_query_for_single_result("PRAGMA cipher_version")
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                var debug__cipher_provider: String? = "unknown"
                try {
                    debug__cipher_provider =
                        run_query_for_single_result("PRAGMA cipher_provider")
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                var debug__cipher_provider_version: String? = "unknown"
                try {
                    debug__cipher_provider_version =
                        run_query_for_single_result("PRAGMA cipher_provider_version")
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                //
                var debug_output = ""
                debug_output = debug_output + "--- sorma2 ---" + "\n"
                debug_output = debug_output + "sorma_version=" + OrmaDatabase.getVersion() + "\n"
                debug_output = debug_output + "cipher_version=" + debug__cipher_version + "\n"
                debug_output = debug_output + "sqlite_version=" + debug__sqlite_version + "\n"
                debug_output = debug_output + "cipher_provider=" + debug__cipher_provider + "\n"
                debug_output = debug_output + "cipher_provider_version=" + debug__cipher_provider_version + "\n"
                Text("" + debug_output, fontSize = 14.sp)
            }
        }
    }
}

