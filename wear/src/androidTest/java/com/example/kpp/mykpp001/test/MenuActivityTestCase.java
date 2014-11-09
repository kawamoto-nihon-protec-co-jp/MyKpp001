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
//        //リストビューにはアイテムクリックリスナーが設定されている
//        assertTrue(listView.hasOnClickListeners());
        //リストビューにはアダプタが設定されている
        assertNotNull(listView.getAdapter());
//        WearableListView.ViewHolder holder;
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
////                listView.once
////                listView.setId(0);
//             //   listView.getAdapter(0);
////                listView.getChildAt(0).setTag(0);
//                listView.performClick();
////                listView.callOnClick();
////                listView.performLongClick();
////                listView.getAdapter().getItemId(0);
////                listView.findViewHolderForItemId(0);
//            }
//        });
//        int cnt =listView.getAdapter().getItemCount();
//        this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
//        getInstrumentation().waitForIdleSync();
//        assertTrue(getInstrumentation().checkMonitorHit(monitor, 1));
//        Intent target = getStartedActivityIntent();
//        String ret = target.getComponent().getClassName();
//        assertEquals(MyActivity.class.getName(), ret);
//
//        int request_code = getStartedActivityRequest();
//        assertEquals(100, request_code);
    }
}
