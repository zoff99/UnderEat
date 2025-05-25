package com.zoffcc.applications.undereat;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.zoffcc.applications.sorm.OrmaDatabase;
import com.zoffcc.applications.sorm.Restaurant;

import java.io.File;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.zoffcc.applications.sorm.OrmaDatabase.run_multi_sql;
import static com.zoffcc.applications.sorm.OrmaDatabase.set_schema_upgrade_callback;

@SuppressWarnings("ALL")
public class corefuncs
{
    private String path;
    private static final String TAG = "UnderEat:";
    private static String ret = "";

    static OrmaDatabase orma = null;
    final static int ORMA_CURRENT_DB_SCHEMA_VERSION = 2; // increase for database schema changes // minimum is 1
    private final static String MAIN_DB_NAME = "main.db";
    private static boolean PREF__DB_wal_mode = true; // use WAL mode
    private final String PREF__DB_secrect_key = ""; // no encryption

    public static enum Category {
        VIENNA_KITCHEN(1),
        CHINESE(2),
        JAPANESE(3),
        HEURIGEN(4);
        public int value;
        private Category(int value)
        {
            this.value = value;
        }
    }

    static final int N_ITEMS = 10;
    static final int N_OPS = 100;
    final String titlePrefix = "title ";
    final String contentPrefix =
            "content content content\n" + "content content content\n" + "content content content\n" + " ";

    void upgrade_db_schema_do(int old_version, int new_version)
    {
        if (new_version == 1)
        {
            // @formatter:off
            run_multi_sql("CREATE TABLE IF NOT EXISTS \"Category\" (\n" +
                          "  \"id\" INTEGER,\n" +
                          "  \"name\" TEXT UNIQUE NOT NULL,\n" +
                          "  PRIMARY KEY(\"id\" AUTOINCREMENT)\n" +
                          ");\n");

            run_multi_sql("CREATE TABLE IF NOT EXISTS \"Restaurant\" (\n" +
                          "  \"id\" INTEGER,\n" +
                          "  \"name\" TEXT UNIQUE NOT NULL,\n" +
                          "  \"category_id\" INTEGER,\n" +
                          "  \"address\" TEXT NOT NULL,\n" +
                          "  \"area_code\" TEXT,\n" +
                          "  \"lat\" INTEGER,\n" +
                          "  \"lon\" INTEGER,\n" +
                          "  \"rating\" INTEGER,\n" +
                          "  \"comment\" TEXT,\n" +
                          "  \"active\" BOOLEAN DEFAULT \"1\",\n" +
                          "  \"for_summer\" BOOLEAN DEFAULT \"0\",\n" +
                          "  PRIMARY KEY(\"id\" AUTOINCREMENT)\n" +
                          ");");

            run_multi_sql("insert into Category (id, name) values (1, 'Wiener Küche')");
            run_multi_sql("insert into Category (id, name) values (2, 'Chineisch')");
            run_multi_sql("insert into Category (id, name) values (3, 'Japanisch')");
            run_multi_sql("insert into Category (id, name) values (4, 'Heurigen')");
            // @formatter:on
        }

        if (new_version == 2)
        {
            // @formatter:off
            run_multi_sql("ALTER TABLE \"Restaurant\" ADD COLUMN need_reservation BOOLEAN DEFAULT \"1\";\n");
            run_multi_sql("ALTER TABLE \"Restaurant\" ADD COLUMN phonenumber TEXT DEFAULT NULL;\n");
            // @formatter:on
        }
    }

    private OrmaDatabase OrmaDatabase_wrapper(String dbs_path, String pref__db_secrect_key, boolean pref__db_wal_mode)
    {
        set_schema_upgrade_callback(new OrmaDatabase.schema_upgrade_callback()
        {
            @Override
            public void upgrade(int old_version, int new_version)
            {
                Log.i(TAG, "trying to upgrade schema from " + old_version + " to " + new_version);
                upgrade_db_schema_do(old_version, new_version);
            }
        });

        OrmaDatabase orma = new OrmaDatabase(dbs_path, pref__db_secrect_key, pref__db_wal_mode);
        try
        {
            OrmaDatabase.init(ORMA_CURRENT_DB_SCHEMA_VERSION);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return orma;
    }

    String init_me(Context c)
    {
        long time_start = System.currentTimeMillis();

        try
        {
            System.out.println(TAG + "app version:" + BuildConfig.VERSION_NAME);
            ret = ret + "\n" + "app version:" + BuildConfig.VERSION_NAME;

            System.out.println(TAG + "git hash:" + BuildConfig.GIT_HASH);
            ret = ret + "\n" + "git hash:" + BuildConfig.GIT_HASH;

            System.out.println(TAG + "Android API:" + Build.VERSION.SDK_INT);
            ret = ret + "\n" + "Android API:" + Build.VERSION.SDK_INT;
        }
        catch (Exception e)
        {
            try
            {
                ret = ret + "\n" + "git hash:" + BuildConfig.GIT_HASH;
            }
            catch (Exception ignored)
            {
            }
        }

        System.out.println(TAG + "starting ...");
        ret = ret + "\n" + "starting ...";

        // define the path where the db file will be located
        String dbs_path = c.getDir("dbs", MODE_PRIVATE).getAbsolutePath() + "/" + MAIN_DB_NAME;
        // Log.i(TAG, "db:path=" + dbs_path);
        File database_dir = new File(new File(dbs_path).getParent());
        database_dir.mkdirs();

        orma = OrmaDatabase_wrapper(dbs_path, PREF__DB_secrect_key, PREF__DB_wal_mode);
        System.out.println(TAG + "db is open");
        ret = ret + "\n" + "db is open";


        // show some version information


        String debug__cipher_version = "unknown";
        try
        {
            debug__cipher_version = orma.run_query_for_single_result("PRAGMA cipher_version");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        String debug__cipher_provider = "unknown";
        try
        {
            debug__cipher_provider = orma.run_query_for_single_result("PRAGMA cipher_provider");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        String debug__cipher_provider_version = "unknown";
        try
        {
            debug__cipher_provider_version = orma.run_query_for_single_result("PRAGMA cipher_provider_version");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        System.out.println(TAG + "orma version: " + orma.getVersion());
        System.out.println(TAG + "sqlite version: " + OrmaDatabase.get_current_sqlite_version());
        System.out.println(TAG + "sqlcipher version: " + debug__cipher_version);
        System.out.println(TAG + "sqlcipher provider: " + debug__cipher_provider);
        System.out.println(TAG + "sqlcipher p.ver.: " + debug__cipher_provider_version);
        ret = ret + "\n" + "orma version: " + orma.getVersion();
        ret = ret + "\n" + "sqlite version: " + OrmaDatabase.get_current_sqlite_version();
        ret = ret + "\n" + "sqlcipher version: " + debug__cipher_version;
        ret = ret + "\n" + "sqlcipher provider: " + debug__cipher_provider;
        ret = ret + "\n" + "sqlcipher p.ver.: " + debug__cipher_provider_version;

        /*
        try
        {
            orma.deleteFromRestaurant().execute();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        Random rnd = new Random();
        for(int i=0;i<2;i++) {
            try
            {
                Restaurant r = new Restaurant();
                r.name = "Restaurant " + i;
                r.address = "" + rnd.nextInt() + " gasse " + rnd.nextFloat() + " opjopiriejroije wiorjweior jiowejroijiorj ior ";
                r.active = true;
                r.for_summer = false;
                r.category_id = Category.VIENNA_KITCHEN.value;
                orma.insertIntoRestaurant(r);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        */

        try
        {
            List<Restaurant> rl = orma.selectFromRestaurant().toList();
            System.out.println(TAG + "size=" + rl.size() + rl);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        // all finished
        System.out.println(TAG + "finished.");
        ret = ret + "\n" + "finished";

        return ret;
    }
}
