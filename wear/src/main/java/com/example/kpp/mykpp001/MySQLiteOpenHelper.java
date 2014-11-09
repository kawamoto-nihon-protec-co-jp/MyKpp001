package com.example.kpp.mykpp001;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kpp.mykpp001.dao.HeartRateDao;

/**
 * SQLite操作Helperクラス
 * @author T.Kawamoto
 * @version 1.0
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    // DB名
    private static final String DB = "mysqlite.db";
    // バージョン管理
    private static final int DB_VERSION = 2;

    public MySQLiteOpenHelper(Context context) {
        super(context, DB, null, DB_VERSION);
    }

    /*
     * データベースを始めて作成したときに呼び出される
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    /*
     * データベースのバージョンを上げたときに呼び出される
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTables(db);
        onCreate(db);
    }

    /*
     * テーブル作成
     * 使用するテーブルは全て記載する
     */
    private void  createTables(SQLiteDatabase db) {
        db.execSQL(HeartRateDao.CREATE_TABLE);
    }

    /*
     * テーブル削除
     * 使用するテーブルは全て記載する
     */
    private void  dropTables(SQLiteDatabase db) {
        db.execSQL(HeartRateDao.DROP_TABLE);
    }
}
