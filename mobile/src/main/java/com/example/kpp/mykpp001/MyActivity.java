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


public class MyActivity extends FragmentActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener, LoaderManager.LoaderCallbacks<TransData> {

    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = MyActivity.class.getName();

    private String heartRate = "";
    private String assayDate = "";
    private String test = "";
    private EditText txtRate;
    private TextView txtTest;
    private EditText txtUserId;
    private SharedPreferences pref;

    /** 測位用クラス */
    private CustomLocationManager mCustomLocationManager;
    private static final int LOCATION_TIME_OUT = 5000;
    /** 測位情報 */
    public String strIdo = "";			// 緯度(onCompleteで取得)
    public String strKeido = "";		// 経度
    public Long longJikanGPS = (long) 0;// 時間(GPS用)
    TextView tvGps;		// GPS表示用

    // ①初期処理(アクティビティの起動時)
    // 必要なコンポーネントなどを作成するための処理を記述
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        // SharedPreferencesのインスタンス
        pref = getSharedPreferences("pre_save",MODE_PRIVATE);

        // テキストフィールド取得
        txtRate = (EditText) findViewById(R.id.txt_rate);
        txtTest = (TextView) findViewById(R.id.txt_Test);
        txtUserId = (EditText) findViewById(R.id.txt_userid);

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

    // ②アクティビティが表示されたとき
    @Override
    protected void onStart() {
        super.onStart();
        //GPS測位開始
        getCurrentPoint();
        Log.d(TAG, "onStart");
    }

    // ③アクティビティとユーザーとのやり取りが可能になるとき
    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    // 送信ボタン押下時処理
    private void sendHealthData() {
        TransData sendData = new TransData();
        Bundle args = new Bundle();
        // 心拍数セット
        sendData.userId = txtUserId.getText().toString();
        sendData.heartRate = txtRate.getText().toString();
        sendData.assayDate = pref.getString("assayDate", "");
        sendData.gpsLatitude = mCustomLocationManager.strIdoC;
        sendData.gpsLongitude = mCustomLocationManager.strKeidoC;

        args.putSerializable("data", sendData);
        // Loaderの呼び出し
        getSupportLoaderManager().restartLoader(0, args, this);
    }

    // loaderが作成されたときに呼び出される
    public Loader<TransData> onCreateLoader(int id, Bundle args) {
        if( null != args ) {
            TransData sendData = (TransData) args.getSerializable("data");
            // HttpAccesser呼び出し
            return new HttpAccesser(this, sendData);
        }
        return null;
    }

    // AsyncTaskLoader(loadInBackground)の処理が終了したら呼び出される
    @Override
    public void onLoadFinished(Loader<TransData> loader, TransData recvData) {
        // 受け渡った値の処理
        if(null == recvData) {
            return;
        }
        else {
            EditText editText;
            editText = (EditText) findViewById(R.id.editText);
            editText.setText(recvData.heartRate);
        }
    }

    // loaderがリセットされた時に呼び出される。
    @Override
    public void onLoaderReset(Loader<TransData> loader) {
        Log.d(TAG, "onLoaderReset");
        // AsyncTaskLoaderが破棄されるときに呼び出される
    }

    // Google Play services接続時に呼び出される
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

    // Google Play services接続が失敗したときの処理
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: " + connectionResult);
    }

    // データが更新時呼び出される
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

//                test = item.getDataMap().getString("test");
                Log.d(TAG, "hert_rate :" + heartRate);
                Log.d(TAG, "test :" + assayDate);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtRate.setText(heartRate);
                        txtTest.setText(assayDate);
                    }
                });
            }
        }
    }

    //GPS測位開始
    private void getCurrentPoint(){
        mCustomLocationManager.getNowLocationData(LOCATION_TIME_OUT, new CustomLocationManager.LocationCallback() {

            @Override
            public void onTimeout() {
                //Toast.makeText(getApplicationContext(), R.string.toast_timeout, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(Location location) {
                strIdo = Double.toString(location.getLatitude());	// 緯度
                strKeido = Double.toString(location.getLongitude());// 経度
                longJikanGPS = location.getTime();					// 時間

                //tvGps.setTextColor(Color.rgb(255, 165, 0));
            }
        });

    }

    // オプションメニューの作成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    // メニューのアイテムが押された時に呼ばれる
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ④別のActivityが開始されている時(アクティビティが前面でなくなる前)
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            // Listenerを削除する
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    // ⑤Activityが終了(アクティビティが不可視になった後)
    @Override
    protected void onStop() {
        super.onStop();
    }
}
