package com.zoffcc.applications.undereat;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;

import androidx.annotation.Nullable;

public class ShareActivity extends Activity
{
    private static final String TAG = "ShareActivity";

    Intent intent;
    String action;
    String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");

        intent = getIntent();
        Log.i(TAG, "onCreate:intent=" + intent);
        action = intent.getAction();
        type = intent.getType();

        if (Intent.ACTION_SEND.equals(action))
        {
            ClipData cdata = intent.getClipData();
            Log.i(TAG, "onCreate:cdata=" + cdata);
            if (cdata != null)
            {
                int item_count = cdata.getItemCount();
                Log.i(TAG, "onCreate:item_count=" + item_count);
                Log.i(TAG, "onCreate:getDescription=" + cdata.getDescription());
            }

            Uri data = intent.getData();
            Log.i(TAG, "onCreate:data=" + data);
            String dataString = intent.getDataString();
            Log.i(TAG, "onCreate:dataString=" + dataString);
            try
            {
                String shareWith = dataString.substring(dataString.lastIndexOf('/') + 1);
                Log.i(TAG, "onCreate:shareWith=" + shareWith);
            }
            catch (Exception e2)
            {
            }

            Uri data_uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (data_uri != null)
            {
                Log.i(TAG, "onCreate:data_uri=" + data_uri);
            }

            try
            {
                ContentResolver cr = this.getApplicationContext().getContentResolver();
                Cursor metaCursor = cr.query(data_uri, null, null, null, null);
                String fileName = null;
                if (metaCursor != null)
                {
                    try
                    {
                        if (metaCursor.moveToFirst())
                        {
                            String file_path = metaCursor.getString(0);
                            Log.i(TAG, "file_attach_for_send:metaCursor_path:fp=" + file_path);
                            Log.i(TAG, "file_attach_for_send:metaCursor_path:column names=" +
                                        metaCursor.getColumnNames().length);
                            int j;
                            for (j = 0; j < metaCursor.getColumnNames().length; j++)
                            {
                                Log.i(TAG, "file_attach_for_send:metaCursor_path:column name=" +
                                          metaCursor.getColumnName(j));
                                Log.i(TAG,
                                      "file_attach_for_send:metaCursor_path:column data=" + metaCursor.getString(j));
                                if (metaCursor.getColumnName(j).equals(DocumentsContract.Document.COLUMN_DISPLAY_NAME))
                                {
                                    if (metaCursor.getString(j) != null)
                                    {
                                        if (metaCursor.getString(j).length() > 0)
                                        {
                                            fileName = metaCursor.getString(j);
                                            Log.i(TAG, "file_attach_for_send:filename new=" + fileName);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    finally
                    {
                        metaCursor.close();
                    }
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent:intent=" + intent);
    }
}
