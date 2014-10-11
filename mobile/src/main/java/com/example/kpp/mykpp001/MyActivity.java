package com.example.kpp.mykpp001;

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
    private String test = "";
    private EditText txtRate;
    private TextView txtTest;

    // ①初期処理(アクティビティの起動時)
    // 必要なコンポーネントなどを作成するための処理を記述
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        // テキストフィールド取得
        txtRate = (EditText) findViewById(R.id.txt_rate);
        txtTest = (TextView) findViewById(R.id.txt_Test);

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
    }

    // ②アクティビティが表示されたとき
    @Override
    protected void onStart() {
        super.onStart();
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
        sendData.data = txtRate.getText().toString();
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
            editText.setText(recvData.data);
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
                test = item.getDataMap().getString("test");
                Log.d(TAG, "hert_rate :" + heartRate);
                Log.d(TAG, "test :" + test);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtRate.setText(heartRate);
                        txtTest.setText(test);
                    }
                });
            }
        }
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
