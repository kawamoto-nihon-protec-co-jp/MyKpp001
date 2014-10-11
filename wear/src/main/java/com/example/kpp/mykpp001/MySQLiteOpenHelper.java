package com.example.kpp.mykpp001;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kpp.mykpp001.dao.HeartRateDao;

/**
 * Created by kawamoto on 2014/10/11.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DB = "mysqlite.db";
    private static final int DB_VERSION = 2;

    public MySQLiteOpenHelper(Context context) {
        super(context, DB, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTables(db);
        onCreate(db);
    }

    private void  createTables(SQLiteDatabase db) {
        db.execSQL(HeartRateDao.CREATE_TABLE);
    }

    private void  dropTables(SQLiteDatabase db) {
        db.execSQL(HeartRateDao.DROP_TABLE);
    }
}
