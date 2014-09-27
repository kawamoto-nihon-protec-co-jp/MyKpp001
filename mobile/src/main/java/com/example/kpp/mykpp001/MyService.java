package com.example.kpp.mykpp001;

import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

public class MyService extends WearableListenerService {

    private static final String TAG = MyService.class.getName();

    private TextView txtRate;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged");
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.d(TAG, "DataItem deleted: " + event.getDataItem().getUri());
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.d(TAG, "DataItem changed: " + event.getDataItem().getUri());
                DataMapItem item = DataMapItem.fromDataItem(event.getDataItem());
                Log.d(TAG, "item value 2");
                String hertRate = item.getDataMap().getString("hert_rate");
                Log.d(TAG, "hert_rate :" + hertRate);
                Intent intent = new Intent(this, MyActivity2.class);
                intent.putExtra(
                        "txt_rate",
                        hertRate);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }
}
