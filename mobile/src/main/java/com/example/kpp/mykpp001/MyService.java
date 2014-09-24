package com.example.kpp.mykpp001;

import android.util.Log;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

public class MyService extends WearableListenerService {

    private static final String TAG = MyService.class.getName();

//    @Override
//    public void onMessageReceived(MessageEvent messageEvent) {
//        Log.d("MyService", "onMessageReceived");
//        Log.d("MyService", messageEvent.getPath());
//        Log.d("MyService", new String(messageEvent.getData()));
//    }

//    @Override
//    public void onDataChanged(DataEventBuffer dataEvents) {
//        Log.d("MyService!", "onDataChanged");
//        for (DataEvent event : dataEvents) {
//            if (event.getType() == DataEvent.TYPE_DELETED) {
//                Log.d(TAG, "DataItem deleted: " + event.getDataItem().getUri());
//            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
//                Log.d(TAG, "DataItem changed: " + event.getDataItem().getUri());
//            }
//        }
//    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged");
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
//        dataEvents.close();
//        for (DataEvent event : events) {
//            Uri uri = event.getDataItem().getUri();
//            String path = uri.getPath();
//            if ("/create".equals(path)) {
//                DataMapItem item = DataMapItem.fromDataItem(event.getDataItem());
//                //String weatherText = item.getDataMap().getString(Tools.WEAR_ACTION_UPDATE);//TODO if needed to differ what to do
////                Intent mIntent = new Intent(this, UpdateService.class);
////                UpdateFromWear.this.startService(mIntent);
//            }
//        }
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.d(TAG, "DataItem deleted: " + event.getDataItem().getUri());
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.d(TAG, "DataItem changed: " + event.getDataItem().getUri());
                DataMapItem item = DataMapItem.fromDataItem(event.getDataItem());
                Log.d(TAG, "item value 2");
                String weatherText = item.getDataMap().getString("key_hrtrate");
                Log.d(TAG, "item value :" + weatherText);
            }
        }
    }
}
