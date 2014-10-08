package com.example.kpp.mykpp001;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.mariux.teleport.lib.TeleportClient;

import java.util.concurrent.CountDownLatch;

//import com.google.android.gms.common.ConnectionResult;

//import com.google.android.gms.common.ConnectionResult;

public class MyActivity extends Activity
        implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MyActivity.class.getName();

    private EditText txtRate;
    private TextView txtMsg;
    private static final int SENSOR_TYPE_HEARTRATE = 65562;
    private Sensor sensor;
    private SensorManager sensorManager;
    private CountDownLatch latch;
    private GoogleApiClient mGoogleApiClient;

    private boolean changed = true;

    TeleportClient teleportClient;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        //-----[ダイアログの設定]
        startDialog();

        //-----[ローディングの描画は別スレッドで行う]
//        Thread thread = new Thread(new Progress());
//        thread.start();

        // 画面をスリープ状態にさせない
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = this.getIntent();
        if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey("keep")) {
            boolean keep = intent.getExtras().getBoolean("keep");
            if (keep) {
                //startactivity only code goes here
            }
        }
        latch = new CountDownLatch(1);
        final Context context = this;
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                ((Button) findViewById(R.id.btn_submit)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendHertRate();
                    }
                });

                txtRate = (EditText) stub.findViewById(R.id.txt_rate);
                //txtRate.setText("少々お待ちください...");

                latch.countDown();
            }
        });

        sensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        sensor = sensorManager.getDefaultSensor(SENSOR_TYPE_HEARTRATE); // using Sensor Lib2 (Samsung Gear Live)

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
    }

    private void startDialog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle("測定中");
        dialog.setMessage("お待ちください...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
    }

    private void endDialog() {
        dialog.dismiss();
        dialog = null;
    }

//    private class Progress implements Runnable {
//
//        public void run() {
//            try {
//                Thread.sleep(10000);//ここで画像を読み込む処理などを書く
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            //-----[読み込み終了の通知]
//            handler.sendEmptyMessage(0);
//        }
//    }
//
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            //-----[ダイアログを閉じる]
//            dialog.dismiss();
//        }
//    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null && intent.getExtras().containsKey("keep")) {
            boolean keep = intent.getExtras().getBoolean("keep");
            if (!keep) {
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager.registerListener(this, this.sensor, 3);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            latch.await();
            if (event.values[0] > 0) {
                Log.d(TAG, "sensor event: " + event.accuracy + " = " + event.values[0]);
//                if (changed) {
                    txtRate.setText(String.valueOf(event.values[0]));
                    endDialog();
                    sensorManager.unregisterListener(this, this.sensor);
                    changed = false;
//                }
            }

        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    private void sendHertRate(){
        //Send empty string to ask phone to refresh weather data
        Log.d(TAG, "sendNotificationToMobile ");
        final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        googleApiClient.connect();
        PutDataMapRequest dataMap = PutDataMapRequest.create("/create");
        dataMap.getDataMap().putString("hert_rate", txtRate.getText().toString());
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(googleApiClient, request);
        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                Log.d(TAG, "onResult " + dataItemResult.toString());
                googleApiClient.disconnect();
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: " + connectionResult);
    }
}
