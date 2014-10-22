package com.example.kpp.mykpp001;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.TextView;

import com.example.kpp.mykpp001.dao.HeartRateDao;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.CountDownLatch;

public class MyActivity extends Activity
        implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MyActivity.class.getName();
    static SQLiteDatabase mydb;

    private TextView txtRate;
    private TextView txtPreRate;
    private static final int SENSOR_TYPE_HEARTRATE = 65562;
    private Sensor sensor;
    private SensorManager sensorManager;
    private CountDownLatch latch;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences pref;
    private int cnt = 0;

    private ProgressDialog dialog;

    // ①初期処理
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        // ダイアログの表示
        startDialog();

        // 画面をスリープ状態にさせない
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // SharedPreferencesのインスタンス
        pref = getSharedPreferences("pre_save",MODE_PRIVATE);

        // 同期化支援機能
        latch = new CountDownLatch(1);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            // 別レイアウトを挿入
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                // ボタン押下時処理登録
                ((Button) findViewById(R.id.btn_submit)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 心拍数をタブレットへ送信
                        sendHertRate();
                    }
                });
                ((Button) findViewById(R.id.btn_rireki)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Activity呼び出し
                        Intent intent = new Intent(MyActivity.this, RateRirekiActivity.class);
                        startActivity(intent);
                    }
                });

                // テキストフィールド取得
                txtRate = (TextView) stub.findViewById(R.id.txt_rate);
                txtPreRate = (TextView) stub.findViewById(R.id.txt_preRate);
                // 前回の心拍数取得
                txtPreRate.setText("前回：" + pref.getString("rate","No Data"));

                // ラッチのカウントを減算し、カウントがゼロに達すると待機中のスレッドをすべて解放します。
                latch.countDown();
            }
        });

        // SensorManagerのインスタンス
        sensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        sensor = sensorManager.getDefaultSensor(SENSOR_TYPE_HEARTRATE); // using Sensor Lib2 (Samsung Gear Live)

        // GoogleApiClientインスタンス
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
    }

    // ②アクティビティが表示されたとき
    @Override
    protected void onStart() {
        super.onStart();
        // センサー処理を開始
        sensorManager.registerListener(this, this.sensor, 3);
    }

    // ③アクティビティとユーザーとのやり取りが可能になるとき
    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        Log.d(TAG, "onResume");
    }

    // ダイアログ表示
    private void startDialog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle("測定中");
        dialog.setMessage("お待ちください...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
    }

    // ダイアログ終了
    private void endDialog() {
        dialog.dismiss();
        dialog = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");
        if (intent.getExtras() != null && intent.getExtras().containsKey("keep")) {
            boolean keep = intent.getExtras().getBoolean("keep");
            if (!keep) {
                Log.d(TAG, "finish");
                finish();
            }
        }
    }

    // センサーの値が変わったときに呼び出される(自動生成されるメソッド)
    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            // 処理待ち(ラッチのカウントダウンがゼロになるまで現在のスレッドを待機させます。)
            // カウントがゼロに達すると、メソッドは値 true で復帰します
            latch.await();

            // 心拍数計測値セット
            if (event.values[0] > 0) {
                Log.d(TAG, "sensor event: " + event.accuracy + " = " + event.values[0] + txtPreRate
                );
                txtRate.setText(String.valueOf((int)event.values[0]));

                // データ保存(プリファレンス)
                SharedPreferences.Editor e = pref.edit();
                e.putString("rate",txtRate.getText().toString());
                e.commit();

                // 心拍数の測定結果登録
                HeartRateDao dao = new HeartRateDao(this);
                dao.open();
                dao.insert(txtRate.getText().toString());
                dao.close();

                // ダイアログ終了
                endDialog();

                // センサー処理の停止
                sensorManager.unregisterListener(this, this.sensor);
            }

        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    // センサー精度の変更を行うときに利用(自動生成されるメソッド)
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // 値の送信（DataItems）
    private void sendHertRate(){
        // DataMapインスタンスを生成する
        PutDataMapRequest dataMap = PutDataMapRequest.create("/create");
        dataMap.getDataMap().putString("hert_rate", txtRate.getText().toString());
        cnt++;
        dataMap.getDataMap().putString("test", "tomomi" + cnt);
        // データを送信する
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);
        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                Log.d(TAG, "onResult " + dataItemResult.toString());
            }
        });
    }

    // Google Play services接続時に呼び出される
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    // Google Play services接続が失敗したときの処理
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: " + connectionResult);
    }

    // ④別のActivityが開始されている時
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    // ⑤Activityが終了
    @Override
    protected void onStop() {
        super.onStop();
        // センサー処理の停止
        sensorManager.unregisterListener(this);
        cnt = 0;
    }
}
