package com.example.kpp.mykpp001.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.kpp.mykpp001.entity.HeartRateEntitty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kawamoto on 2014/10/11.
 */
public class HeartRateDao extends AbstractDao {
    public static final String TABLE_NAME = "heartRate";
    public static final String COL_HEART_RATE_NAME = "heart_rate";
    public static final String COL_CREATE_DATE_NAME = "create_date";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( _id integer primary key autoincrement, heart_rate integer not null, create_date datetime CHECK(create_date like '____-__-__ __:__:__'));";
    public static final String DROP_TABLE = "DROP TABLE " + TABLE_NAME + ";";

    public HeartRateDao(Context context) {
        super(context);
    }

    public long insert(String heartRate) {
        ContentValues values = new ContentValues();
        values.put(COL_HEART_RATE_NAME, heartRate);
        // 現在の時刻を取得
        Date date = new Date();
        // 日付形式を設定
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String sysdate = sdf.format(date);
        values.put(COL_CREATE_DATE_NAME, sysdate);
        return db.insert(TABLE_NAME, null, values);
    }

    public List<HeartRateEntitty> findAllOrderByIdDesc() {
        List<HeartRateEntitty> list = new ArrayList <HeartRateEntitty>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, "_id DESC");
        boolean isEof = cursor.moveToFirst();
        while(isEof){
            HeartRateEntitty entity = new HeartRateEntitty();
            entity.id = cursor.getString(0);
            entity.heartRate = cursor.getString(1);
            entity.createDate = cursor.getString(2);
            list.add(entity);
            isEof = cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

}
