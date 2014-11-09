package com.example.kpp.mykpp001.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.kpp.mykpp001.MySQLiteOpenHelper;

/**
 * AbstractDaoクラス<br/>
 * Daoクラス作成時はこのクラスを継承させます。
 * @author T.Kawamoto
 * @version 1.0
 */
public abstract class  AbstractDao {
    // Helper変数
    protected MySQLiteOpenHelper dbHelper;
    // SQLiteDatabasr変数
    protected SQLiteDatabase db;

    AbstractDao (Context context) {
        dbHelper = new MySQLiteOpenHelper(context);
    }

    /**
     * データベース処理開始
     * @return
     */
    public AbstractDao open() {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    /**
     * データベース処理終了
     * @return
     */
    public void close(){
        dbHelper.close();
    }
}
