/* SPDX-License-Identifier: GPL-3.0-or-later
 * [sorma2], Java part of sorma2
 * Copyright (C) 2024 Zoff <zoff@zoff.cc>
 */

package com.zoffcc.applications.sorm;

import com.zoffcc.applications.sorm.Log;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static com.zoffcc.applications.sorm.OrmaDatabase.*;


@Table
public class Restaurant
{
    private static final String TAG = "DB.Restaurant";
    @PrimaryKey(autoincrement = true, auto = true)
    public long id;

    @Column(indexed = true, helpers = Column.Helpers.ALL)
    public String name;

    @Column(indexed = true, helpers = Column.Helpers.ALL)
    public long category_id;

    @Column(indexed = true, helpers = Column.Helpers.ALL)
    public String address;

    @Column(indexed = true, helpers = Column.Helpers.ALL)
    public String area_code;

    @Column(indexed = true, helpers = Column.Helpers.ALL)
    public long lat;

    @Column(indexed = true, helpers = Column.Helpers.ALL)
    public long lon;

    @Column(indexed = true, helpers = Column.Helpers.ALL)
    public int rating;

    @Column(indexed = true, helpers = Column.Helpers.ALL)
    public String comment;

    @Column(indexed = true, helpers = Column.Helpers.ALL)
    public boolean active;

    @Column(indexed = true, helpers = Column.Helpers.ALL)
    public boolean for_summer;

    @Column(indexed = true, helpers = Column.Helpers.ALL)
    public String phonenumber;

    @Column(indexed = true, helpers = Column.Helpers.ALL)
    public boolean need_reservation;

    @Column(indexed = true, helpers = Column.Helpers.ALL)
    public boolean have_ac;

    @Column(indexed = true, helpers = Column.Helpers.ALL)
    public long added_timestamp;

    @Column(indexed = true, helpers = Column.Helpers.ALL)
    public long modified_timestamp;

    @Column(indexed = true, helpers = Column.Helpers.ALL)
    public boolean only_evening;

    public static Restaurant deep_copy(Restaurant in)
    {
        Restaurant out = new Restaurant();
        out.id = in.id;
        out.name = in.name;
        out.category_id = in.category_id;
        out.address = in.address;
        out.area_code = in.area_code;
        out.lat = in.lat;
        out.lon = in.lon;
        out.rating = in.rating;
        out.comment = in.comment;
        out.active = in.active;
        out.for_summer = in.for_summer;
        out.phonenumber = in.phonenumber;
        out.need_reservation = in.need_reservation;
        out.have_ac = in.have_ac;
        out.added_timestamp = in.added_timestamp;
        out.modified_timestamp = in.modified_timestamp;
        out.only_evening = in.only_evening;

        return out;
    }

    @Override
    public String toString()
    {
        return "id=" + id + ", name=" + name + ", category_id=" + category_id + ", address=" + address + ", area_code=" + area_code + ", lat=" + lat + ", lon=" + lon + ", rating=" + rating + ", comment=" + comment + ", active=" + active + ", for_summer=" + for_summer + ", phonenumber=" + phonenumber + ", need_reservation=" + need_reservation + ", have_ac=" + have_ac + ", added_timestamp=" + added_timestamp + ", modified_timestamp=" + modified_timestamp + ", only_evening=" + only_evening;
    }



    String sql_start = "";
    String sql_set = "";
    String sql_where = "where 1=1 "; // where
    String sql_orderby = ""; // order by
    String sql_limit = ""; // limit
    List<OrmaBindvar> bind_where_vars = new ArrayList<>();
    int bind_where_count = 0;
    List<OrmaBindvar> bind_set_vars = new ArrayList<>();
    int bind_set_count = 0;

    public List<Restaurant> toList()
    {
        List<Restaurant> list = new ArrayList<>();
        orma_global_sqltolist_lock.lock();
        PreparedStatement statement = null;
        try
        {
            final String sql = this.sql_start + " " + this.sql_where + " " + this.sql_orderby + " " + this.sql_limit;
            log_bindvars_where(sql, bind_where_count, bind_where_vars);
            final long t1 = System.currentTimeMillis();
            statement = sqldb.prepareStatement(sql);
            if (!set_bindvars_where(statement, bind_where_count, bind_where_vars))
            {
                try
                {
                    statement.close();
                }
                catch (Exception ignored)
                {
                }
                return null;
            }
            ResultSet rs = statement.executeQuery();
            final long t2 = System.currentTimeMillis();
            if (ORMA_LONG_RUNNING_TRACE)
            {
                if ((t2 - t1) > ORMA_LONG_RUNNING_MS)
                {
                    Log.i(TAG, "long running (" + (t2 - t1)+ " ms) sql=" + sql);
                }
            }
            final long t3 = System.currentTimeMillis();
            while (rs.next())
            {
                Restaurant out = new Restaurant();
                out.id = rs.getLong("id");
                out.name = rs.getString("name");
                out.category_id = rs.getLong("category_id");
                out.address = rs.getString("address");
                out.area_code = rs.getString("area_code");
                out.lat = rs.getLong("lat");
                out.lon = rs.getLong("lon");
                out.rating = rs.getInt("rating");
                out.comment = rs.getString("comment");
                out.active = rs.getBoolean("active");
                out.for_summer = rs.getBoolean("for_summer");
                out.phonenumber = rs.getString("phonenumber");
                out.need_reservation = rs.getBoolean("need_reservation");
                out.have_ac = rs.getBoolean("have_ac");
                out.added_timestamp = rs.getLong("added_timestamp");
                out.modified_timestamp = rs.getLong("modified_timestamp");
                out.only_evening = rs.getBoolean("only_evening");

                list.add(out);
            }
            final long t4 = System.currentTimeMillis();
            if (ORMA_LONG_RUNNING_TRACE)
            {
                if ((t4 - t3) > ORMA_LONG_RUNNING_MS)
                {
                    Log.i(TAG, "long running (" + (t4 - t3)+ " ms) fetch=" + sql);
                }
            }
            try
            {
                rs.close();
            }
            catch (Exception ignored)
            {
            }

            try
            {
                statement.close();
            }
            catch (Exception ignored)
            {
            }
        }
        catch (Exception e)
        {
            Log.i(TAG, "ERR:toList:001:" + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            try
            {
                statement.close();
            }
            catch (Exception ignored)
            {
            }
            orma_global_sqltolist_lock.unlock();
        }

        return list;
    }


    public long insert()
    {
        long ret = -1;

        orma_global_sqlinsert_lock.lock();
        PreparedStatement insert_pstmt = null;
        try
        {
            String insert_pstmt_sql = null;

            // @formatter:off
            insert_pstmt_sql ="insert into \"" + this.getClass().getSimpleName() + "\"" +
                    "("
                    + "\"name\""
                    + ",\"category_id\""
                    + ",\"address\""
                    + ",\"area_code\""
                    + ",\"lat\""
                    + ",\"lon\""
                    + ",\"rating\""
                    + ",\"comment\""
                    + ",\"active\""
                    + ",\"for_summer\""
                    + ",\"phonenumber\""
                    + ",\"need_reservation\""
                    + ",\"have_ac\""
                    + ",\"added_timestamp\""
                    + ",\"modified_timestamp\""
                    + ",\"only_evening\""
                    + ")" +
                    "values" +
                    "("
                    + "?1"
                    + ",?2"
                    + ",?3"
                    + ",?4"
                    + ",?5"
                    + ",?6"
                    + ",?7"
                    + ",?8"
                    + ",?9"
                    + ",?10"
                    + ",?11"
                    + ",?12"
                    + ",?13"
                    + ",?14"
                    + ",?15"
                    + ",?16"
                    + ")";

            insert_pstmt = sqldb.prepareStatement(insert_pstmt_sql);
            insert_pstmt.clearParameters();

            insert_pstmt.setString(1, this.name);
            insert_pstmt.setLong(2, this.category_id);
            insert_pstmt.setString(3, this.address);
            insert_pstmt.setString(4, this.area_code);
            insert_pstmt.setLong(5, this.lat);
            insert_pstmt.setLong(6, this.lon);
            insert_pstmt.setInt(7, this.rating);
            insert_pstmt.setString(8, this.comment);
            insert_pstmt.setBoolean(9, this.active);
            insert_pstmt.setBoolean(10, this.for_summer);
            insert_pstmt.setString(11, this.phonenumber);
            insert_pstmt.setBoolean(12, this.need_reservation);
            insert_pstmt.setBoolean(13, this.have_ac);
            insert_pstmt.setLong(14, this.added_timestamp);
            insert_pstmt.setLong(15, this.modified_timestamp);
            insert_pstmt.setBoolean(16, this.only_evening);
            // @formatter:on

            if (ORMA_TRACE)
            {
                Log.i(TAG, "sql=" + insert_pstmt);
            }

            final long t1 = System.currentTimeMillis();
            orma_semaphore_lastrowid_on_insert.acquire();
            final long t2 = System.currentTimeMillis();
            if (ORMA_LONG_RUNNING_TRACE)
            {
                if ((t2 - t1) > ORMA_LONG_RUNNING_MS)
                {
                    Log.i(TAG, "insertInto"+this.getClass().getSimpleName()+" acquire running long (" + (t2 - t1)+ " ms)");
                }
            }

            final long t3 = System.currentTimeMillis();
            insert_pstmt.executeUpdate();
            final long t4 = System.currentTimeMillis();
            if (ORMA_LONG_RUNNING_TRACE)
            {
                if ((t4 - t3) > ORMA_LONG_RUNNING_MS)
                {
                    Log.i(TAG, "insertInto"+this.getClass().getSimpleName()+" sql running long (" + (t4 - t3)+ " ms)");
                }
            }

            final long t5 = System.currentTimeMillis();
            insert_pstmt.close();
            final long t6 = System.currentTimeMillis();
            if (ORMA_LONG_RUNNING_TRACE)
            {
                if ((t6 - t5) > ORMA_LONG_RUNNING_MS)
                {
                    Log.i(TAG, "insertInto"+this.getClass().getSimpleName()+" statement close running long (" + (t6 - t5)+ " ms)");
                }
            }

            final long t7 = System.currentTimeMillis();
            ret = get_last_rowid_pstmt();
            final long t8 = System.currentTimeMillis();
            if (ORMA_LONG_RUNNING_TRACE)
            {
                if ((t8 - t7) > ORMA_LONG_RUNNING_MS)
                {
                    Log.i(TAG, "insertInto"+this.getClass().getSimpleName()+" getLastRowId running long (" + (t8 - t7)+ " ms)");
                }
            }

            orma_semaphore_lastrowid_on_insert.release();
        }
        catch (Exception e)
        {
            orma_semaphore_lastrowid_on_insert.release();
            Log.i(TAG, "ERR:insert:001:" + e.getMessage());
            throw new RuntimeException(e);
        }
        finally
        {
            try
            {
                insert_pstmt.close();
            }
            catch (Exception ignored)
            {
            }
            orma_global_sqlinsert_lock.unlock();
        }

        return ret;
    }

    public Restaurant get(int i)
    {
        this.sql_limit = " limit " + i + ",1 ";
        return this.toList().get(0);
    }

    public void execute()
    {
        orma_global_sqlexecute_lock.lock();
        PreparedStatement statement = null;
        try
        {
            final String sql = this.sql_start + " " + this.sql_set + " " + this.sql_where;
            log_bindvars_where_and_set(sql, bind_where_count, bind_where_vars, bind_set_count, bind_set_vars);
            statement = sqldb.prepareStatement(sql);
            if (!set_bindvars_where_and_set(statement, bind_where_count, bind_where_vars, bind_set_count, bind_set_vars))
            {
                try
                {
                    statement.close();
                }
                catch (Exception ignored)
                {
                }
                orma_semaphore_lastrowid_on_insert.release();
                return;
            }
            statement.executeUpdate();
            try
            {
                statement.close();
            }
            catch (Exception ignored)
            {
            }
        }
        catch (Exception e2)
        {
            Log.i(TAG, "ERR:execute:001:" + e2.getMessage());
            e2.printStackTrace();
        }
        finally
        {
            try
            {
                statement.close();
            }
            catch (Exception ignored)
            {
            }
            orma_global_sqlexecute_lock.unlock();
        }
    }

    public int count()
    {
        int ret = 0;

        orma_global_sqlcount_lock.lock();
        PreparedStatement statement = null;
        try
        {
            this.sql_start = "SELECT count(*) as count FROM \"" + this.getClass().getSimpleName() + "\"";

            final String sql = this.sql_start + " " + this.sql_where + " " + this.sql_orderby + " " + this.sql_limit;
            log_bindvars_where(sql, bind_where_count, bind_where_vars);
            statement = sqldb.prepareStatement(sql);
            if (!set_bindvars_where(statement, bind_where_count, bind_where_vars))
            {
                try
                {
                    statement.close();
                }
                catch (Exception ignored)
                {
                }
                return 0;
            }
            ResultSet rs = statement.executeQuery();
            if (rs.next())
            {
                ret = rs.getInt("count");
            }
            try
            {
                rs.close();
            }
            catch (Exception ignored)
            {
            }

            try
            {
                statement.close();
            }
            catch (Exception ignored)
            {
            }
        }
        catch (Exception e)
        {
            Log.i(TAG, "ERR:count:001:" + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            try
            {
                statement.close();
            }
            catch (Exception ignored)
            {
            }
            orma_global_sqlcount_lock.unlock();
        }

        return ret;
    }

    public Restaurant limit(int rowcount)
    {
        this.sql_limit = " limit " + rowcount + " ";
        return this;
    }

    public Restaurant limit(int rowcount, int offset)
    {
        this.sql_limit = " limit " + offset + " , " + rowcount;
        return this;
    }

    // ----------------------------------- //
    // ----------------------------------- //
    // ----------------------------------- //


    // ----------------- Set funcs ---------------------- //
    public Restaurant id(long id)
    {
        if (this.sql_set.equals(""))
        {
            this.sql_set = " set ";
        }
        else
        {
            this.sql_set = this.sql_set + " , ";
        }
        this.sql_set = this.sql_set + " \"id\"=?" + (BINDVAR_OFFSET_SET + bind_set_count) + " ";
        bind_set_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, id));
        bind_set_count++;
        return this;
    }

    public Restaurant name(String name)
    {
        if (this.sql_set.equals(""))
        {
            this.sql_set = " set ";
        }
        else
        {
            this.sql_set = this.sql_set + " , ";
        }
        this.sql_set = this.sql_set + " \"name\"=?" + (BINDVAR_OFFSET_SET + bind_set_count) + " ";
        bind_set_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, name));
        bind_set_count++;
        return this;
    }

    public Restaurant category_id(long category_id)
    {
        if (this.sql_set.equals(""))
        {
            this.sql_set = " set ";
        }
        else
        {
            this.sql_set = this.sql_set + " , ";
        }
        this.sql_set = this.sql_set + " \"category_id\"=?" + (BINDVAR_OFFSET_SET + bind_set_count) + " ";
        bind_set_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, category_id));
        bind_set_count++;
        return this;
    }

    public Restaurant address(String address)
    {
        if (this.sql_set.equals(""))
        {
            this.sql_set = " set ";
        }
        else
        {
            this.sql_set = this.sql_set + " , ";
        }
        this.sql_set = this.sql_set + " \"address\"=?" + (BINDVAR_OFFSET_SET + bind_set_count) + " ";
        bind_set_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, address));
        bind_set_count++;
        return this;
    }

    public Restaurant area_code(String area_code)
    {
        if (this.sql_set.equals(""))
        {
            this.sql_set = " set ";
        }
        else
        {
            this.sql_set = this.sql_set + " , ";
        }
        this.sql_set = this.sql_set + " \"area_code\"=?" + (BINDVAR_OFFSET_SET + bind_set_count) + " ";
        bind_set_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, area_code));
        bind_set_count++;
        return this;
    }

    public Restaurant lat(long lat)
    {
        if (this.sql_set.equals(""))
        {
            this.sql_set = " set ";
        }
        else
        {
            this.sql_set = this.sql_set + " , ";
        }
        this.sql_set = this.sql_set + " \"lat\"=?" + (BINDVAR_OFFSET_SET + bind_set_count) + " ";
        bind_set_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, lat));
        bind_set_count++;
        return this;
    }

    public Restaurant lon(long lon)
    {
        if (this.sql_set.equals(""))
        {
            this.sql_set = " set ";
        }
        else
        {
            this.sql_set = this.sql_set + " , ";
        }
        this.sql_set = this.sql_set + " \"lon\"=?" + (BINDVAR_OFFSET_SET + bind_set_count) + " ";
        bind_set_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, lon));
        bind_set_count++;
        return this;
    }

    public Restaurant rating(int rating)
    {
        if (this.sql_set.equals(""))
        {
            this.sql_set = " set ";
        }
        else
        {
            this.sql_set = this.sql_set + " , ";
        }
        this.sql_set = this.sql_set + " \"rating\"=?" + (BINDVAR_OFFSET_SET + bind_set_count) + " ";
        bind_set_vars.add(new OrmaBindvar(BINDVAR_TYPE_Int, rating));
        bind_set_count++;
        return this;
    }

    public Restaurant comment(String comment)
    {
        if (this.sql_set.equals(""))
        {
            this.sql_set = " set ";
        }
        else
        {
            this.sql_set = this.sql_set + " , ";
        }
        this.sql_set = this.sql_set + " \"comment\"=?" + (BINDVAR_OFFSET_SET + bind_set_count) + " ";
        bind_set_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, comment));
        bind_set_count++;
        return this;
    }

    public Restaurant active(boolean active)
    {
        if (this.sql_set.equals(""))
        {
            this.sql_set = " set ";
        }
        else
        {
            this.sql_set = this.sql_set + " , ";
        }
        this.sql_set = this.sql_set + " \"active\"=?" + (BINDVAR_OFFSET_SET + bind_set_count) + " ";
        bind_set_vars.add(new OrmaBindvar(BINDVAR_TYPE_Boolean, active));
        bind_set_count++;
        return this;
    }

    public Restaurant for_summer(boolean for_summer)
    {
        if (this.sql_set.equals(""))
        {
            this.sql_set = " set ";
        }
        else
        {
            this.sql_set = this.sql_set + " , ";
        }
        this.sql_set = this.sql_set + " \"for_summer\"=?" + (BINDVAR_OFFSET_SET + bind_set_count) + " ";
        bind_set_vars.add(new OrmaBindvar(BINDVAR_TYPE_Boolean, for_summer));
        bind_set_count++;
        return this;
    }

    public Restaurant phonenumber(String phonenumber)
    {
        if (this.sql_set.equals(""))
        {
            this.sql_set = " set ";
        }
        else
        {
            this.sql_set = this.sql_set + " , ";
        }
        this.sql_set = this.sql_set + " \"phonenumber\"=?" + (BINDVAR_OFFSET_SET + bind_set_count) + " ";
        bind_set_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, phonenumber));
        bind_set_count++;
        return this;
    }

    public Restaurant need_reservation(boolean need_reservation)
    {
        if (this.sql_set.equals(""))
        {
            this.sql_set = " set ";
        }
        else
        {
            this.sql_set = this.sql_set + " , ";
        }
        this.sql_set = this.sql_set + " \"need_reservation\"=?" + (BINDVAR_OFFSET_SET + bind_set_count) + " ";
        bind_set_vars.add(new OrmaBindvar(BINDVAR_TYPE_Boolean, need_reservation));
        bind_set_count++;
        return this;
    }

    public Restaurant have_ac(boolean have_ac)
    {
        if (this.sql_set.equals(""))
        {
            this.sql_set = " set ";
        }
        else
        {
            this.sql_set = this.sql_set + " , ";
        }
        this.sql_set = this.sql_set + " \"have_ac\"=?" + (BINDVAR_OFFSET_SET + bind_set_count) + " ";
        bind_set_vars.add(new OrmaBindvar(BINDVAR_TYPE_Boolean, have_ac));
        bind_set_count++;
        return this;
    }

    public Restaurant added_timestamp(long added_timestamp)
    {
        if (this.sql_set.equals(""))
        {
            this.sql_set = " set ";
        }
        else
        {
            this.sql_set = this.sql_set + " , ";
        }
        this.sql_set = this.sql_set + " \"added_timestamp\"=?" + (BINDVAR_OFFSET_SET + bind_set_count) + " ";
        bind_set_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, added_timestamp));
        bind_set_count++;
        return this;
    }

    public Restaurant modified_timestamp(long modified_timestamp)
    {
        if (this.sql_set.equals(""))
        {
            this.sql_set = " set ";
        }
        else
        {
            this.sql_set = this.sql_set + " , ";
        }
        this.sql_set = this.sql_set + " \"modified_timestamp\"=?" + (BINDVAR_OFFSET_SET + bind_set_count) + " ";
        bind_set_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, modified_timestamp));
        bind_set_count++;
        return this;
    }

    public Restaurant only_evening(boolean only_evening)
    {
        if (this.sql_set.equals(""))
        {
            this.sql_set = " set ";
        }
        else
        {
            this.sql_set = this.sql_set + " , ";
        }
        this.sql_set = this.sql_set + " \"only_evening\"=?" + (BINDVAR_OFFSET_SET + bind_set_count) + " ";
        bind_set_vars.add(new OrmaBindvar(BINDVAR_TYPE_Boolean, only_evening));
        bind_set_count++;
        return this;
    }


    // ----------------- Eq/Gt/Lt funcs ----------------- //
    public Restaurant idEq(long id)
    {
        this.sql_where = this.sql_where + " and \"id\"=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, id));
        bind_where_count++;
        return this;
    }

    public Restaurant idNotEq(long id)
    {
        this.sql_where = this.sql_where + " and \"id\"<>?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, id));
        bind_where_count++;
        return this;
    }

    public Restaurant idLt(long id)
    {
        this.sql_where = this.sql_where + " and \"id\"<?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, id));
        bind_where_count++;
        return this;
    }

    public Restaurant idLe(long id)
    {
        this.sql_where = this.sql_where + " and \"id\"<=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, id));
        bind_where_count++;
        return this;
    }

    public Restaurant idGt(long id)
    {
        this.sql_where = this.sql_where + " and \"id\">?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, id));
        bind_where_count++;
        return this;
    }

    public Restaurant idGe(long id)
    {
        this.sql_where = this.sql_where + " and \"id\">=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, id));
        bind_where_count++;
        return this;
    }

    public Restaurant idBetween(long id1, long id2)
    {
        this.sql_where = this.sql_where + " and \"id\">?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " and id<?" + (BINDVAR_OFFSET_WHERE + 1 + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, id1));
        bind_where_count++;
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, id2));
        bind_where_count++;
        return this;
    }

    public Restaurant idIsNull()
    {
        this.sql_where = this.sql_where + " and \"id\" IS NULL ";
        return this;
    }

    public Restaurant idIsNotNull()
    {
        this.sql_where = this.sql_where + " and \"id\" IS NOT NULL ";
        return this;
    }

    public Restaurant nameEq(String name)
    {
        this.sql_where = this.sql_where + " and \"name\"=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, name));
        bind_where_count++;
        return this;
    }

    public Restaurant nameNotEq(String name)
    {
        this.sql_where = this.sql_where + " and \"name\"<>?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, name));
        bind_where_count++;
        return this;
    }

    public Restaurant nameIsNull()
    {
        this.sql_where = this.sql_where + " and \"name\" IS NULL ";
        return this;
    }

    public Restaurant nameIsNotNull()
    {
        this.sql_where = this.sql_where + " and \"name\" IS NOT NULL ";
        return this;
    }

    public Restaurant nameLike(String name)
    {
        this.sql_where = this.sql_where + " and \"name\" LIKE ?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ESCAPE '\\' ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, name));
        bind_where_count++;
        return this;
    }

    public Restaurant nameNotLike(String name)
    {
        this.sql_where = this.sql_where + " and \"name\" NOT LIKE ?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ESCAPE '\\' ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, name));
        bind_where_count++;
        return this;
    }

    public Restaurant category_idEq(long category_id)
    {
        this.sql_where = this.sql_where + " and \"category_id\"=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, category_id));
        bind_where_count++;
        return this;
    }

    public Restaurant category_idNotEq(long category_id)
    {
        this.sql_where = this.sql_where + " and \"category_id\"<>?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, category_id));
        bind_where_count++;
        return this;
    }

    public Restaurant category_idLt(long category_id)
    {
        this.sql_where = this.sql_where + " and \"category_id\"<?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, category_id));
        bind_where_count++;
        return this;
    }

    public Restaurant category_idLe(long category_id)
    {
        this.sql_where = this.sql_where + " and \"category_id\"<=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, category_id));
        bind_where_count++;
        return this;
    }

    public Restaurant category_idGt(long category_id)
    {
        this.sql_where = this.sql_where + " and \"category_id\">?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, category_id));
        bind_where_count++;
        return this;
    }

    public Restaurant category_idGe(long category_id)
    {
        this.sql_where = this.sql_where + " and \"category_id\">=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, category_id));
        bind_where_count++;
        return this;
    }

    public Restaurant category_idBetween(long category_id1, long category_id2)
    {
        this.sql_where = this.sql_where + " and \"category_id\">?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " and category_id<?" + (BINDVAR_OFFSET_WHERE + 1 + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, category_id1));
        bind_where_count++;
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, category_id2));
        bind_where_count++;
        return this;
    }

    public Restaurant category_idIsNull()
    {
        this.sql_where = this.sql_where + " and \"category_id\" IS NULL ";
        return this;
    }

    public Restaurant category_idIsNotNull()
    {
        this.sql_where = this.sql_where + " and \"category_id\" IS NOT NULL ";
        return this;
    }

    public Restaurant addressEq(String address)
    {
        this.sql_where = this.sql_where + " and \"address\"=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, address));
        bind_where_count++;
        return this;
    }

    public Restaurant addressNotEq(String address)
    {
        this.sql_where = this.sql_where + " and \"address\"<>?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, address));
        bind_where_count++;
        return this;
    }

    public Restaurant addressIsNull()
    {
        this.sql_where = this.sql_where + " and \"address\" IS NULL ";
        return this;
    }

    public Restaurant addressIsNotNull()
    {
        this.sql_where = this.sql_where + " and \"address\" IS NOT NULL ";
        return this;
    }

    public Restaurant addressLike(String address)
    {
        this.sql_where = this.sql_where + " and \"address\" LIKE ?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ESCAPE '\\' ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, address));
        bind_where_count++;
        return this;
    }

    public Restaurant addressNotLike(String address)
    {
        this.sql_where = this.sql_where + " and \"address\" NOT LIKE ?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ESCAPE '\\' ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, address));
        bind_where_count++;
        return this;
    }

    public Restaurant area_codeEq(String area_code)
    {
        this.sql_where = this.sql_where + " and \"area_code\"=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, area_code));
        bind_where_count++;
        return this;
    }

    public Restaurant area_codeNotEq(String area_code)
    {
        this.sql_where = this.sql_where + " and \"area_code\"<>?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, area_code));
        bind_where_count++;
        return this;
    }

    public Restaurant area_codeIsNull()
    {
        this.sql_where = this.sql_where + " and \"area_code\" IS NULL ";
        return this;
    }

    public Restaurant area_codeIsNotNull()
    {
        this.sql_where = this.sql_where + " and \"area_code\" IS NOT NULL ";
        return this;
    }

    public Restaurant area_codeLike(String area_code)
    {
        this.sql_where = this.sql_where + " and \"area_code\" LIKE ?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ESCAPE '\\' ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, area_code));
        bind_where_count++;
        return this;
    }

    public Restaurant area_codeNotLike(String area_code)
    {
        this.sql_where = this.sql_where + " and \"area_code\" NOT LIKE ?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ESCAPE '\\' ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, area_code));
        bind_where_count++;
        return this;
    }

    public Restaurant latEq(long lat)
    {
        this.sql_where = this.sql_where + " and \"lat\"=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, lat));
        bind_where_count++;
        return this;
    }

    public Restaurant latNotEq(long lat)
    {
        this.sql_where = this.sql_where + " and \"lat\"<>?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, lat));
        bind_where_count++;
        return this;
    }

    public Restaurant latLt(long lat)
    {
        this.sql_where = this.sql_where + " and \"lat\"<?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, lat));
        bind_where_count++;
        return this;
    }

    public Restaurant latLe(long lat)
    {
        this.sql_where = this.sql_where + " and \"lat\"<=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, lat));
        bind_where_count++;
        return this;
    }

    public Restaurant latGt(long lat)
    {
        this.sql_where = this.sql_where + " and \"lat\">?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, lat));
        bind_where_count++;
        return this;
    }

    public Restaurant latGe(long lat)
    {
        this.sql_where = this.sql_where + " and \"lat\">=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, lat));
        bind_where_count++;
        return this;
    }

    public Restaurant latBetween(long lat1, long lat2)
    {
        this.sql_where = this.sql_where + " and \"lat\">?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " and lat<?" + (BINDVAR_OFFSET_WHERE + 1 + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, lat1));
        bind_where_count++;
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, lat2));
        bind_where_count++;
        return this;
    }

    public Restaurant latIsNull()
    {
        this.sql_where = this.sql_where + " and \"lat\" IS NULL ";
        return this;
    }

    public Restaurant latIsNotNull()
    {
        this.sql_where = this.sql_where + " and \"lat\" IS NOT NULL ";
        return this;
    }

    public Restaurant lonEq(long lon)
    {
        this.sql_where = this.sql_where + " and \"lon\"=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, lon));
        bind_where_count++;
        return this;
    }

    public Restaurant lonNotEq(long lon)
    {
        this.sql_where = this.sql_where + " and \"lon\"<>?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, lon));
        bind_where_count++;
        return this;
    }

    public Restaurant lonLt(long lon)
    {
        this.sql_where = this.sql_where + " and \"lon\"<?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, lon));
        bind_where_count++;
        return this;
    }

    public Restaurant lonLe(long lon)
    {
        this.sql_where = this.sql_where + " and \"lon\"<=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, lon));
        bind_where_count++;
        return this;
    }

    public Restaurant lonGt(long lon)
    {
        this.sql_where = this.sql_where + " and \"lon\">?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, lon));
        bind_where_count++;
        return this;
    }

    public Restaurant lonGe(long lon)
    {
        this.sql_where = this.sql_where + " and \"lon\">=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, lon));
        bind_where_count++;
        return this;
    }

    public Restaurant lonBetween(long lon1, long lon2)
    {
        this.sql_where = this.sql_where + " and \"lon\">?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " and lon<?" + (BINDVAR_OFFSET_WHERE + 1 + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, lon1));
        bind_where_count++;
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, lon2));
        bind_where_count++;
        return this;
    }

    public Restaurant lonIsNull()
    {
        this.sql_where = this.sql_where + " and \"lon\" IS NULL ";
        return this;
    }

    public Restaurant lonIsNotNull()
    {
        this.sql_where = this.sql_where + " and \"lon\" IS NOT NULL ";
        return this;
    }

    public Restaurant ratingEq(int rating)
    {
        this.sql_where = this.sql_where + " and \"rating\"=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Int, rating));
        bind_where_count++;
        return this;
    }

    public Restaurant ratingNotEq(int rating)
    {
        this.sql_where = this.sql_where + " and \"rating\"<>?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Int, rating));
        bind_where_count++;
        return this;
    }

    public Restaurant ratingLt(int rating)
    {
        this.sql_where = this.sql_where + " and \"rating\"<?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Int, rating));
        bind_where_count++;
        return this;
    }

    public Restaurant ratingLe(int rating)
    {
        this.sql_where = this.sql_where + " and \"rating\"<=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Int, rating));
        bind_where_count++;
        return this;
    }

    public Restaurant ratingGt(int rating)
    {
        this.sql_where = this.sql_where + " and \"rating\">?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Int, rating));
        bind_where_count++;
        return this;
    }

    public Restaurant ratingGe(int rating)
    {
        this.sql_where = this.sql_where + " and \"rating\">=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Int, rating));
        bind_where_count++;
        return this;
    }

    public Restaurant ratingBetween(int rating1, int rating2)
    {
        this.sql_where = this.sql_where + " and \"rating\">?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " and rating<?" + (BINDVAR_OFFSET_WHERE + 1 + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Int, rating1));
        bind_where_count++;
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Int, rating2));
        bind_where_count++;
        return this;
    }

    public Restaurant ratingIsNull()
    {
        this.sql_where = this.sql_where + " and \"rating\" IS NULL ";
        return this;
    }

    public Restaurant ratingIsNotNull()
    {
        this.sql_where = this.sql_where + " and \"rating\" IS NOT NULL ";
        return this;
    }

    public Restaurant commentEq(String comment)
    {
        this.sql_where = this.sql_where + " and \"comment\"=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, comment));
        bind_where_count++;
        return this;
    }

    public Restaurant commentNotEq(String comment)
    {
        this.sql_where = this.sql_where + " and \"comment\"<>?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, comment));
        bind_where_count++;
        return this;
    }

    public Restaurant commentIsNull()
    {
        this.sql_where = this.sql_where + " and \"comment\" IS NULL ";
        return this;
    }

    public Restaurant commentIsNotNull()
    {
        this.sql_where = this.sql_where + " and \"comment\" IS NOT NULL ";
        return this;
    }

    public Restaurant commentLike(String comment)
    {
        this.sql_where = this.sql_where + " and \"comment\" LIKE ?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ESCAPE '\\' ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, comment));
        bind_where_count++;
        return this;
    }

    public Restaurant commentNotLike(String comment)
    {
        this.sql_where = this.sql_where + " and \"comment\" NOT LIKE ?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ESCAPE '\\' ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, comment));
        bind_where_count++;
        return this;
    }

    public Restaurant activeEq(boolean active)
    {
        this.sql_where = this.sql_where + " and \"active\"=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Boolean, active));
        bind_where_count++;
        return this;
    }

    public Restaurant activeNotEq(boolean active)
    {
        this.sql_where = this.sql_where + " and \"active\"<>?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Boolean, active));
        bind_where_count++;
        return this;
    }

    public Restaurant activeIsNull()
    {
        this.sql_where = this.sql_where + " and \"active\" IS NULL ";
        return this;
    }

    public Restaurant activeIsNotNull()
    {
        this.sql_where = this.sql_where + " and \"active\" IS NOT NULL ";
        return this;
    }

    public Restaurant for_summerEq(boolean for_summer)
    {
        this.sql_where = this.sql_where + " and \"for_summer\"=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Boolean, for_summer));
        bind_where_count++;
        return this;
    }

    public Restaurant for_summerNotEq(boolean for_summer)
    {
        this.sql_where = this.sql_where + " and \"for_summer\"<>?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Boolean, for_summer));
        bind_where_count++;
        return this;
    }

    public Restaurant for_summerIsNull()
    {
        this.sql_where = this.sql_where + " and \"for_summer\" IS NULL ";
        return this;
    }

    public Restaurant for_summerIsNotNull()
    {
        this.sql_where = this.sql_where + " and \"for_summer\" IS NOT NULL ";
        return this;
    }

    public Restaurant phonenumberEq(String phonenumber)
    {
        this.sql_where = this.sql_where + " and \"phonenumber\"=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, phonenumber));
        bind_where_count++;
        return this;
    }

    public Restaurant phonenumberNotEq(String phonenumber)
    {
        this.sql_where = this.sql_where + " and \"phonenumber\"<>?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, phonenumber));
        bind_where_count++;
        return this;
    }

    public Restaurant phonenumberIsNull()
    {
        this.sql_where = this.sql_where + " and \"phonenumber\" IS NULL ";
        return this;
    }

    public Restaurant phonenumberIsNotNull()
    {
        this.sql_where = this.sql_where + " and \"phonenumber\" IS NOT NULL ";
        return this;
    }

    public Restaurant phonenumberLike(String phonenumber)
    {
        this.sql_where = this.sql_where + " and \"phonenumber\" LIKE ?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ESCAPE '\\' ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, phonenumber));
        bind_where_count++;
        return this;
    }

    public Restaurant phonenumberNotLike(String phonenumber)
    {
        this.sql_where = this.sql_where + " and \"phonenumber\" NOT LIKE ?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ESCAPE '\\' ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_String, phonenumber));
        bind_where_count++;
        return this;
    }

    public Restaurant need_reservationEq(boolean need_reservation)
    {
        this.sql_where = this.sql_where + " and \"need_reservation\"=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Boolean, need_reservation));
        bind_where_count++;
        return this;
    }

    public Restaurant need_reservationNotEq(boolean need_reservation)
    {
        this.sql_where = this.sql_where + " and \"need_reservation\"<>?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Boolean, need_reservation));
        bind_where_count++;
        return this;
    }

    public Restaurant need_reservationIsNull()
    {
        this.sql_where = this.sql_where + " and \"need_reservation\" IS NULL ";
        return this;
    }

    public Restaurant need_reservationIsNotNull()
    {
        this.sql_where = this.sql_where + " and \"need_reservation\" IS NOT NULL ";
        return this;
    }

    public Restaurant have_acEq(boolean have_ac)
    {
        this.sql_where = this.sql_where + " and \"have_ac\"=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Boolean, have_ac));
        bind_where_count++;
        return this;
    }

    public Restaurant have_acNotEq(boolean have_ac)
    {
        this.sql_where = this.sql_where + " and \"have_ac\"<>?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Boolean, have_ac));
        bind_where_count++;
        return this;
    }

    public Restaurant have_acIsNull()
    {
        this.sql_where = this.sql_where + " and \"have_ac\" IS NULL ";
        return this;
    }

    public Restaurant have_acIsNotNull()
    {
        this.sql_where = this.sql_where + " and \"have_ac\" IS NOT NULL ";
        return this;
    }

    public Restaurant added_timestampEq(long added_timestamp)
    {
        this.sql_where = this.sql_where + " and \"added_timestamp\"=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, added_timestamp));
        bind_where_count++;
        return this;
    }

    public Restaurant added_timestampNotEq(long added_timestamp)
    {
        this.sql_where = this.sql_where + " and \"added_timestamp\"<>?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, added_timestamp));
        bind_where_count++;
        return this;
    }

    public Restaurant added_timestampLt(long added_timestamp)
    {
        this.sql_where = this.sql_where + " and \"added_timestamp\"<?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, added_timestamp));
        bind_where_count++;
        return this;
    }

    public Restaurant added_timestampLe(long added_timestamp)
    {
        this.sql_where = this.sql_where + " and \"added_timestamp\"<=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, added_timestamp));
        bind_where_count++;
        return this;
    }

    public Restaurant added_timestampGt(long added_timestamp)
    {
        this.sql_where = this.sql_where + " and \"added_timestamp\">?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, added_timestamp));
        bind_where_count++;
        return this;
    }

    public Restaurant added_timestampGe(long added_timestamp)
    {
        this.sql_where = this.sql_where + " and \"added_timestamp\">=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, added_timestamp));
        bind_where_count++;
        return this;
    }

    public Restaurant added_timestampBetween(long added_timestamp1, long added_timestamp2)
    {
        this.sql_where = this.sql_where + " and \"added_timestamp\">?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " and added_timestamp<?" + (BINDVAR_OFFSET_WHERE + 1 + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, added_timestamp1));
        bind_where_count++;
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, added_timestamp2));
        bind_where_count++;
        return this;
    }

    public Restaurant added_timestampIsNull()
    {
        this.sql_where = this.sql_where + " and \"added_timestamp\" IS NULL ";
        return this;
    }

    public Restaurant added_timestampIsNotNull()
    {
        this.sql_where = this.sql_where + " and \"added_timestamp\" IS NOT NULL ";
        return this;
    }

    public Restaurant modified_timestampEq(long modified_timestamp)
    {
        this.sql_where = this.sql_where + " and \"modified_timestamp\"=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, modified_timestamp));
        bind_where_count++;
        return this;
    }

    public Restaurant modified_timestampNotEq(long modified_timestamp)
    {
        this.sql_where = this.sql_where + " and \"modified_timestamp\"<>?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, modified_timestamp));
        bind_where_count++;
        return this;
    }

    public Restaurant modified_timestampLt(long modified_timestamp)
    {
        this.sql_where = this.sql_where + " and \"modified_timestamp\"<?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, modified_timestamp));
        bind_where_count++;
        return this;
    }

    public Restaurant modified_timestampLe(long modified_timestamp)
    {
        this.sql_where = this.sql_where + " and \"modified_timestamp\"<=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, modified_timestamp));
        bind_where_count++;
        return this;
    }

    public Restaurant modified_timestampGt(long modified_timestamp)
    {
        this.sql_where = this.sql_where + " and \"modified_timestamp\">?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, modified_timestamp));
        bind_where_count++;
        return this;
    }

    public Restaurant modified_timestampGe(long modified_timestamp)
    {
        this.sql_where = this.sql_where + " and \"modified_timestamp\">=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, modified_timestamp));
        bind_where_count++;
        return this;
    }

    public Restaurant modified_timestampBetween(long modified_timestamp1, long modified_timestamp2)
    {
        this.sql_where = this.sql_where + " and \"modified_timestamp\">?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " and modified_timestamp<?" + (BINDVAR_OFFSET_WHERE + 1 + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, modified_timestamp1));
        bind_where_count++;
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Long, modified_timestamp2));
        bind_where_count++;
        return this;
    }

    public Restaurant modified_timestampIsNull()
    {
        this.sql_where = this.sql_where + " and \"modified_timestamp\" IS NULL ";
        return this;
    }

    public Restaurant modified_timestampIsNotNull()
    {
        this.sql_where = this.sql_where + " and \"modified_timestamp\" IS NOT NULL ";
        return this;
    }

    public Restaurant only_eveningEq(boolean only_evening)
    {
        this.sql_where = this.sql_where + " and \"only_evening\"=?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Boolean, only_evening));
        bind_where_count++;
        return this;
    }

    public Restaurant only_eveningNotEq(boolean only_evening)
    {
        this.sql_where = this.sql_where + " and \"only_evening\"<>?" + (BINDVAR_OFFSET_WHERE + bind_where_count) + " ";
        bind_where_vars.add(new OrmaBindvar(BINDVAR_TYPE_Boolean, only_evening));
        bind_where_count++;
        return this;
    }

    public Restaurant only_eveningIsNull()
    {
        this.sql_where = this.sql_where + " and \"only_evening\" IS NULL ";
        return this;
    }

    public Restaurant only_eveningIsNotNull()
    {
        this.sql_where = this.sql_where + " and \"only_evening\" IS NOT NULL ";
        return this;
    }


    // ----------------- OrderBy funcs ------------------ //
    public Restaurant orderByIdAsc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"id\" ASC ";
        return this;
    }

    public Restaurant orderByIdDesc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"id\" DESC ";
        return this;
    }

    public Restaurant orderByNameAsc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"name\" ASC ";
        return this;
    }

    public Restaurant orderByNameDesc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"name\" DESC ";
        return this;
    }

    public Restaurant orderByCategory_idAsc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"category_id\" ASC ";
        return this;
    }

    public Restaurant orderByCategory_idDesc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"category_id\" DESC ";
        return this;
    }

    public Restaurant orderByAddressAsc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"address\" ASC ";
        return this;
    }

    public Restaurant orderByAddressDesc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"address\" DESC ";
        return this;
    }

    public Restaurant orderByArea_codeAsc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"area_code\" ASC ";
        return this;
    }

    public Restaurant orderByArea_codeDesc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"area_code\" DESC ";
        return this;
    }

    public Restaurant orderByLatAsc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"lat\" ASC ";
        return this;
    }

    public Restaurant orderByLatDesc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"lat\" DESC ";
        return this;
    }

    public Restaurant orderByLonAsc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"lon\" ASC ";
        return this;
    }

    public Restaurant orderByLonDesc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"lon\" DESC ";
        return this;
    }

    public Restaurant orderByRatingAsc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"rating\" ASC ";
        return this;
    }

    public Restaurant orderByRatingDesc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"rating\" DESC ";
        return this;
    }

    public Restaurant orderByCommentAsc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"comment\" ASC ";
        return this;
    }

    public Restaurant orderByCommentDesc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"comment\" DESC ";
        return this;
    }

    public Restaurant orderByActiveAsc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"active\" ASC ";
        return this;
    }

    public Restaurant orderByActiveDesc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"active\" DESC ";
        return this;
    }

    public Restaurant orderByFor_summerAsc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"for_summer\" ASC ";
        return this;
    }

    public Restaurant orderByFor_summerDesc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"for_summer\" DESC ";
        return this;
    }

    public Restaurant orderByPhonenumberAsc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"phonenumber\" ASC ";
        return this;
    }

    public Restaurant orderByPhonenumberDesc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"phonenumber\" DESC ";
        return this;
    }

    public Restaurant orderByNeed_reservationAsc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"need_reservation\" ASC ";
        return this;
    }

    public Restaurant orderByNeed_reservationDesc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"need_reservation\" DESC ";
        return this;
    }

    public Restaurant orderByHave_acAsc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"have_ac\" ASC ";
        return this;
    }

    public Restaurant orderByHave_acDesc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"have_ac\" DESC ";
        return this;
    }

    public Restaurant orderByAdded_timestampAsc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"added_timestamp\" ASC ";
        return this;
    }

    public Restaurant orderByAdded_timestampDesc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"added_timestamp\" DESC ";
        return this;
    }

    public Restaurant orderByModified_timestampAsc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"modified_timestamp\" ASC ";
        return this;
    }

    public Restaurant orderByModified_timestampDesc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"modified_timestamp\" DESC ";
        return this;
    }

    public Restaurant orderByOnly_eveningAsc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"only_evening\" ASC ";
        return this;
    }

    public Restaurant orderByOnly_eveningDesc()
    {
        if (this.sql_orderby.equals(""))
        {
            this.sql_orderby = " order by ";
        }
        else
        {
            this.sql_orderby = this.sql_orderby + " , ";
        }
        this.sql_orderby = this.sql_orderby + " \"only_evening\" DESC ";
        return this;
    }



}

