package com.example.kpp.mykpp001;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

/**
 * 心拍数情報の追加・編集Activity
 * @author T.Kawamoto
 * @version 1.0
 */
public class MyActivity extends FragmentActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener, LoaderManager.LoaderCallbacks<TransData> {

    private static final String TAG = MyActivity.class.getName();
    // Google API Client
    private GoogleApiClient mGoogleApiClient;
    // 心拍数
    private EditText txtRate;
    private String heartRate = "";
    // 測定日
    private String assayDate = "";
    // ユーザ
    private EditText txtUserId;
    // メッセージ
    private TextView txtMessage;
    // データ保存
    private SharedPreferences pref;

    // 測位用クラス
    private CustomLocationManager mCustomLocationManager;
    private static final int LOCATION_TIME_OUT = 5000;
    //  測位情報
    public String strIdo = "";// 緯度(onCompleteで取得)
    public String strKeido = "";// 経度
    public Long longJikanGPS = (long) 0;// 時間(GPS用)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        // SharedPreferencesのインスタンス
        pref = getSharedPreferences("pre_save",MODE_PRIVATE);

        // テキストフィールド取得
        txtRate = (EditText) findViewById(R.id.txt_rate);
        txtUserId = (EditText) findViewById(R.id.txt_userid);
        txtMessage = (TextView) findViewById(R.id.txt_Message);

        // GoogleApiClientインスタンス
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        // ボタン取得
        Button sendButton = (Button) findViewById(R.id.btn_submit);
        // ボタン押下時処理登録
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendHealthData();
            }
        });

        // 測位用クラス
        mCustomLocationManager = new CustomLocationManager(this);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        //GPS測位開始
        getCurrentPoint();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    /*
     * 送信ボタン押下時処理
     */
    private void sendHealthData() {
        TransData sendData = new TransData();
        Bundle args = new Bundle();
        // 測定結果セット
        sendData.userId = txtUserId.getText().toString();
        sendData.heartRate = txtRate.getText().toString();
        sendData.assayDate = pref.getString("assayDate", "");
        sendData.gpsLatitude = mCustomLocationManager.strIdoC;
        sendData.gpsLongitude = mCustomLocationManager.strKeidoC;

        args.putSerializable("data", sendData);
        // Loaderの呼び出し
        getSupportLoaderManager().restartLoader(0, args, this);
    }

    /*
     * loaderが作成されたときに呼び出される
     */
    @Override
    public Loader<TransData> onCreateLoader(int id, Bundle args) {
        if( null != args ) {
            TransData sendData = (TransData) args.getSerializable("data");
            // HttpAccesser呼び出し
            return new HttpAccesser(this, sendData);
        }
        return null;
    }

    /*
     * AsyncTaskLoader(loadInBackground)の処理が終了したら呼び出される
     */
    @Override
    public void onLoadFinished(Loader<TransData> loader, TransData recvData) {
        if ("0".equals(recvData.status)) {
            txtMessage.setText("送信が完了しました！");
        } else if ("9".equals(recvData.status)) {
            txtMessage.setText("送信に失敗しました。");
        }
    }

    /*
     * AsyncTaskLoaderが破棄されるときに呼び出される
     */
    @Override
    public void onLoaderReset(Loader<TransData> loader) {
        Log.d(TAG, "onLoaderReset");
    }

    /*
     * Google Play services接続時に呼び出される
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
        // Listenerを登録する
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    /*
     * Google Play services接続が失敗したときの処理
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: " + connectionResult);
    }

    /*
     * データが更新時呼び出される
     */
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged");
        for (DataEvent event : dataEvents) {
            // データが消された時
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.d(TAG, "DataItem deleted: " + event.getDataItem().getUri());
            // データが変わった時
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.d(TAG, "DataItem changed: " + event.getDataItem().getUri());
                // DataItemsから取得
                DataMapItem item = DataMapItem.fromDataItem(event.getDataItem());
                heartRate = item.getDataMap().getString("hert_rate");
                assayDate = item.getDataMap().getString("assay_date");
                // データ保存(プリファレンス)
                SharedPreferences.Editor e = pref.edit();
                e.putString("assayDate", assayDate);
                e.commit();

                Log.d(TAG, "hert_rate :" + heartRate);
                Log.d(TAG, "assayDate :" + assayDate);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtRate.setText(heartRate);
                    }
                });
            }
        }
    }

    /*
     * GPS測位開始
     */
    private void getCurrentPoint(){
        mCustomLocationManager.doNowLocationData(LOCATION_TIME_OUT, new CustomLocationManager.LocationCallback() {

            @Override
            public void onTimeout() {
            }

            @Override
            public void onComplete(Location location) {
                strIdo = Double.toString(location.getLatitude());// 緯度
                strKeido = Double.toString(location.getLongitude());// 経度
                longJikanGPS = location.getTime();// 時間
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            // Listenerを削除する
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
