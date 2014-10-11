package com.example.kpp.mykpp001;

/**
 * Created by kawamoto on 2014/10/11.
 */

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kpp.mykpp001.dao.HeartRateDao;

import java.util.ArrayList;
import java.util.List;

public class RateRirekiActivity extends ListActivity {

    static ArrayAdapter<String> adapter;

    private ListView listview;
    private Button delbtn;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_rireki);

        //DB接続
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(HeartRateDao.TABLE_NAME, new String[] {"_id", "heart_rate", "create_date"}, null, null, null, null, "_id DESC");

        //ListView初期化
        List<String> sampleList = new ArrayList<String>();
//        ListView list_rate = (ListView)findViewById(R.id.android.list);

        boolean isEof = cursor.moveToFirst();
        while(isEof){
//            adapter.add(cursor.getString(1));
            sampleList.add("    " + cursor.getString(2) + "        " + cursor.getString(1));
            isEof = cursor.moveToNext();
        }
        adapter = new ArrayAdapter<String>(this, R.layout.rate_list,sampleList);

        //DBクローズ
        cursor.close();
        db.close();

        this.setListAdapter(adapter);

        //ボタンを登録
        delbtn = (Button)findViewById(R.id.btn_del);
        delbtn.setOnClickListener(new ClickListener());
    }

    //ボタンクリック時のリスナ
    class ClickListener implements OnClickListener {
        public void onClick(View v) {
            //テキストのインスタンスを取得
            TextView txt_rate = (TextView) findViewById(R.id.txt_rate);
            String strText = txt_rate.getText().toString();

            if (strText.length() != 0) {
                //まずテキストを削除
                txt_rate.setText("");
                //リストに追加
                ListView list = (ListView) findViewById(android.R.id.list);
                adapter.insert(strText, 0);
                list.setAdapter(adapter);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
