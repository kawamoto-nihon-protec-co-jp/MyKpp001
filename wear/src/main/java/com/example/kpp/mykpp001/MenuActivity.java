package com.example.kpp.mykpp001;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * メニューActivity（最初に表示される画面）
 * @author T.Kawamoto
 * @version 1.0
 */
public class MenuActivity extends Activity implements WearableListView.ClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // メニュー作成
        List<String> labels = new ArrayList<String>();
        labels.add("心拍数");
        WearableListView listView = (WearableListView)findViewById(R.id.list);
        listView.setAdapter(new Adapter(this, labels));
        listView.setClickListener(this);
    }

    /*
     * メニュー押下時
     */
    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
//        int i = (Integer)viewHolder.itemView.getTag();
        // Activity呼び出し
//        if (i == 0) {
            Intent intent = new Intent(this, MyActivity.class);
            startActivity(intent);
//        }
    }

    @Override
    public void onTopEmptyRegionClick() {
    }

    /**
     * Adapter定義
     */
    private static class Adapter extends WearableListView.Adapter {

        private Context context;
        private LayoutInflater layoutInflater;
        private List<String> labels;

        public Adapter(Context context, List<String> labels) {
            this.context = context;
            this.layoutInflater = LayoutInflater.from(this.context);
            this.labels = labels;
        }

        /*
         * 表示する要素のViewを作成
         */
        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new WearableListView.ViewHolder(
                    layoutInflater.inflate(R.layout.list_item, null));
        }

        /*
         * Viewにリストをセット
         */
        @Override
        public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int i) {
            TextView textView = (TextView)viewHolder.itemView.findViewById(R.id.label);
            textView.setText(labels.get(i));
            viewHolder.itemView.setTag(i);
        }

        /*
         * 要素数を取得
         */
        @Override
        public int getItemCount() {
            return labels.size();
        }
    }
}
