package com.example.kpp.mykpp001.dao;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by kawamoto on 2014/10/11.
 */
public class HeartRateDao extends AbstractDao {
    public static final String TABLE_NAME = "heartRate";
    public static final String CREATE_TABLE = "create table " + TABLE_NAME + " ( _id integer primary key autoincrement, heart_rate integer not null, create_date datetime CHECK(create_date like '____-__-__ __:__:__'));";
    public static final String DROP_TABLE = "drop table " + TABLE_NAME + ";";

    HeartRateDao(Context context) {
        super(context);
    }

    private Cursor findAllOrderByIdDesc() {
        return db.query(TABLE_NAME, null, null, null, null, null, "desc _id");
    }

}
