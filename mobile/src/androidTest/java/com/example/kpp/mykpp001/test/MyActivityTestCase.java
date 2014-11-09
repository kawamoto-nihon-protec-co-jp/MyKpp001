package com.example.kpp.mykpp001.test;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.example.kpp.mykpp001.MyActivity;

/**
 * MyActivity Unitテスト
 * @author T.Kawamoto
 * @version 1.0
 */
public class MyActivityTestCase extends ActivityInstrumentationTestCase2<MyActivity> {
    private Activity activity;

    private Instrumentation instrumentation;

    public MyActivityTestCase() {
        super(MyActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
        instrumentation = getInstrumentation();
        setActivityInitialTouchMode(false);
    }

    public void testRateText_initialize() throws Exception {
        EditText txtRate = (EditText)activity.findViewById(com.example.kpp.mykpp001.R.id.txt_rate);
        assertEquals("初期値は空文字", "", txtRate.getText().toString());
    }

}
