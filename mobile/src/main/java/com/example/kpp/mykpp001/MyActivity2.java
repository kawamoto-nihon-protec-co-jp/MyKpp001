package com.example.kpp.mykpp001;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by kawamoto on 2014/09/25.
 */
public class MyActivity2 extends Activity {
    private TextView txtRate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        txtRate = (TextView) findViewById(R.id.txt_rate);
        Bundle extras = getIntent().getExtras();
        String rate = extras.getString("txt_rate");
        txtRate.setText(rate);
    }
}
