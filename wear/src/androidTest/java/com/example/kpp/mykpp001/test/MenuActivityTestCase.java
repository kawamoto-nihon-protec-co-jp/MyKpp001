package com.example.kpp.mykpp001.test;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.wearable.view.WearableListView;
import android.test.ActivityUnitTestCase;

import com.example.kpp.mykpp001.MenuActivity;
import com.example.kpp.mykpp001.MyActivity;

/**
 * メニューActivity Unitテスト
 * @author T.Kawamoto
 * @version 1.0
 */
public class MenuActivityTestCase extends ActivityUnitTestCase<MenuActivity> {


    public MenuActivityTestCase() {
        super(MenuActivity.class);
    }


    public void testOpenHeartRateActivity() throws Exception {
        Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(
                MyActivity.class.getName(), null, false);
        Intent intent = new Intent();
        MenuActivity activity = startActivity(intent, null, null);
        final WearableListView listView = (WearableListView)activity.findViewById(com.example.kpp.mykpp001.R.id.list);
        //メインアクティビティはリストビューを持っている
        assertNotNull(listView);
        //リストビューにはアダプタが設定されている
        assertNotNull(listView.getAdapter());
    }
}
