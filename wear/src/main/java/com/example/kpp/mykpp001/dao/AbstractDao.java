package com.example.kpp.mykpp001.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.kpp.mykpp001.MySQLiteOpenHelper;

/**
 * Created by kawamoto on 2014/10/11.
 */
public abstract class  AbstractDao {

    protected MySQLiteOpenHelper dbHelper;
    protected SQLiteDatabase db;

    AbstractDao (Context context) {
        dbHelper = new MySQLiteOpenHelper(context);
    }

    public AbstractDao open() {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }
}
