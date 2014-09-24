package com.example.kpp.mykpp001;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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

import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

//import com.google.android.gms.common.ConnectionResult;

//import com.google.android.gms.common.ConnectionResult;

public class MyActivity extends Activity implements SensorEventListener {

    private static final String TAG = MyActivity.class.getName();

    private TextView txtRate;
    private static final int SENSOR_TYPE_HEARTRATE = 65562;
    private Sensor sensor;
    private SensorManager sensorManager;
    private CountDownLatch latch;
    private GoogleApiClient mGoogleAppiClient;

    TeleportClient teleportClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        // 画面をスリープ状態にさせない
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = this.getIntent();
        if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey("keep")) {
            boolean keep = intent.getExtras().getBoolean("keep");
            if (keep) {
                //startactivity only code goes here
            }
        }
//        sendNotificationToMobile();
//        mGoogleAppiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks()
//                {
//                    @Override
//                    public void onConnected(Bundle connectionHint) {
//                        Log.d(TAG, "onConnected: " + connectionHint);
//                        putDataItem();
//                    }
//                    @Override
//                    public void onConnectionSuspended(int cause) {
//                        Log.d(TAG, "onConnectionSuspended: " + cause);
//                    }
//                })
//                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
//                    @Override
//                    public void onConnectionFailed(ConnectionResult result) {
//                        Log.d(TAG, "onConnectionFailed: " + result);
//                    }
//                })
//                .addApi(Wearable.API)
//                .build();


//        teleportClient = new TeleportClient(this);
//        teleportClient.setOnGetMessageTask(new ShowToastFromOnGetMessageTask());
        latch = new CountDownLatch(1);
        final Context context = this;
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                ((Button) findViewById(R.id.btn_submit)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendNotificationToMobile();
//                        mGoogleAppiClient.connect();
//                        //google api
//                        Log.d(TAG, "google api 1");
//                        // パスを指定して PutDataMapRequest を生成
//                        PutDataMapRequest dataMap = PutDataMapRequest.create("/count");
//                        Log.d(TAG, "google api 2");
//                        // 必要な値を DataMap にセット
//                        dataMap.getDataMap().putInt("HERT_RATE", R.id.txt_rate);
//                        Log.d(TAG, "google api 3");
//                        // PutDataRequest を取得
//                        PutDataRequest request = dataMap.asPutDataRequest();
//                        Log.d(TAG, "google api 4");
//                        // DataItem の生成をリクエスト
//                        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
//                                .putDataItem(mGoogleAppiClient, request);
//                        final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
//                                .addApi(Wearable.API)
//                                .build();
//                        googleApiClient.connect();
//                        String value = "update_request";
//                        value = value +  Calendar.getInstance().getTimeInMillis();
//                        PutDataMapRequest dataMap = PutDataMapRequest.create("/create");
//                        dataMap.getDataMap().putString(Tools.WEAR_ACTION_UPDATE, value);
//                        PutDataRequest request = dataMap.asPutDataRequest();
//                        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(googleApiClient, request);
//                        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
//                            @Override
//                            public void onResult(DataApi.DataItemResult dataItemResult) {
//                                Log.d(TAG, "Sent: " + dataItemResult.toString());
//                                mGoogleAppiClient.disconnect();
//                            }
//                        });



                        //Message
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                final String message = txtRate.toString();
//                                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleAppiClient).await();
//                                for (Node node : nodes.getNodes()) {
//                                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
//                                            mGoogleAppiClient,
//                                            node.getId(),
//                                            message,
//                                            message.getBytes())
//                                            .await();
//                                    if (result.getStatus().isSuccess()) {
//                                        Log.d("onClick", "isSuccess is true!");
//                                    } else {
//                                        Log.d("onClick", "isSuccess is false");
//                                    }
//                                }
//                            }
//                         }).start();


                    }
                });

                txtRate = (TextView) stub.findViewById(R.id.txt_rate);
                txtRate.setText("少々お待ちください...");

                latch.countDown();
            }
        });

        sensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        sensor = sensorManager.getDefaultSensor(SENSOR_TYPE_HEARTRATE); // using Sensor Lib2 (Samsung Gear Live)

//        //tomo
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Wearable.API)
//                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
//                    @Override
//                    public void onConnected(Bundle bundle) {
//                        Log.d(TAG, "Google Api Client connected");
//                        new AsyncTask<Void, Void, Void>() {
//                            @Override
//                            protected Void doInBackground(Void... voids) {
//                               // restoreCurrentCount();
//                                return null;
//                            }
//                        }.execute();
//                    }
//
//                    @Override
//                    public void onConnectionSuspended(int i) {
//                    }
//                }).build();
//        mGoogleApiClient.connect();
        this.mGoogleAppiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        // パスを指定して PutDataMapRequest を生成
                        //tomo
                        //PutDataMapRequest dataMap = PutDataMapRequest.create("/count");

                        // 必要な値を DataMap にセット
                        //tomo
                        // //dataMap.getDataMap().putInt("HERT_RATE", 10);

                        // PutDataRequest を取得
                        //tomo
                        //PutDataRequest request = dataMap.asPutDataRequest();

                        // DataItem の生成をリクエスト
                        //tomo
                        // PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                           //     .putDataItem(mGoogleApiClient, request);
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener()
                {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();
        this.mGoogleAppiClient.connect();
    }


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
                txtRate.setText(String.valueOf(event.values[0]));
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

    private void sendHertRate(Context context) {
        //tomo
//        final String message = "Hello world";//txtRate.toString();
//        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleAppiClient).await();
//        for (Node node : nodes.getNodes()) {
//            MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
//                    mGoogleAppiClient,
//                    node.getId(),
//                    "/hello",
//                    message.getBytes())
//                    .await();
//            if (result.getStatus().isSuccess()) {
//                Log.d("onClick", "isSuccess is true");
//            } else {
//                Log.d("onClick", "isSuccess is false");
//            }
//        }
//        GoogleApiClient mGoogleAppiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
//                    @Override
//                    public void onConnected(Bundle connectionHint) {
//                        Log.d(TAG, "onConnected: " + connectionHint);
//                        // パスを指定して PutDataMapRequest を生成
//                        PutDataMapRequest dataMap = PutDataMapRequest.create("/count");
//
//                        // 必要な値を DataMap にセット
//                        dataMap.getDataMap().putInt("HERT_RATE", R.id.txt_rate);
//
//                        // PutDataRequest を取得
//                        PutDataRequest request = dataMap.asPutDataRequest();
//
//                        // DataItem の生成をリクエスト
//                        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
//                                .putDataItem(mGoogleApiClient, request);
//                    }
//
//                    @Override
//                    public void onConnectionSuspended(int cause) {
//                        Log.d(TAG, "onConnectionSuspended: " + cause);
//                    }
//                })
//                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener()
//        {
//                    @Override
//                    public void onConnectionFailed(ConnectionResult result) {
//                        Log.d(TAG, "onConnectionFailed: " + result);
//                    }
//                })
//                .addApi(Wearable.API)
//                .build();
    }
    private void putDataItem(){
        Log.d(TAG, "putDataItem!");
        Random random = new Random(SystemClock.currentThreadTimeMillis());
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/foo");
        putDataMapRequest.getDataMap().putInt("my_key", random.nextInt());
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleAppiClient, putDataMapRequest.asPutDataRequest());
    }

    private void sendNotificationToMobile(){
        //Send empty string to ask phone to refresh weather data
        Log.d(TAG, "sendNotificationToMobile ");
        final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        googleApiClient.connect();
        String value = "update_request";
        value = value +  Calendar.getInstance().getTimeInMillis();
        PutDataMapRequest dataMap = PutDataMapRequest.create("/create");
        dataMap.getDataMap().putString("key_hrtrate", "kawamoto");
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(googleApiClient, request);
        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                Log.d(TAG, "Sent: " + dataItemResult.toString());
                googleApiClient.disconnect();
            }
        });
    }

}
