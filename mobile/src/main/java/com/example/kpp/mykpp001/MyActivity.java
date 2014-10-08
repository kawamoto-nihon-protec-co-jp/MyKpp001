package com.example.kpp.mykpp001;

import android.content.Context;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.mariux.teleport.lib.TeleportClient;

import java.util.List;


public class MyActivity extends FragmentActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener, LoaderManager.LoaderCallbacks<TransData> {

    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = MyActivity.class.getName();

    private String heartRate = "";
    private EditText txtRate;

    TeleportClient teleportClient;

    private static boolean startedWatch = false;
    private static boolean startedService = false;

    private final Context mContext = null;
    private boolean mResolvingError = false;

    private static final int REQUEST_RESOLVE_ERROR = 1001;

    public static String rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        txtRate = (EditText) findViewById(R.id.txt_rate);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        Button sendButton = (Button) findViewById(R.id.btn_submit);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendHealthData();
            }
        });

    }

    private void sendHealthData() {
        TransData sendData = new TransData();
        Bundle args = new Bundle();
        sendData.data = txtRate.getText().toString();
        args.putSerializable("data", sendData);
        getSupportLoaderManager().initLoader(0, args, this);
    }

    public Loader<TransData> onCreateLoader(int id, Bundle args) {
        if( null != args ) {
            TransData sendData = (TransData) args.getSerializable("data");

            return new HttpAccesser(this, sendData);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<TransData> loader, TransData recvData) {
        // AsyncTaskLoaderの処理が終了したら呼び出される
        if(null == recvData) {
            return;
        }
        else {
            EditText editText;
            editText = (EditText) findViewById(R.id.editText);
            editText.setText(recvData.data);
        }
    }

    @Override
    public void onLoaderReset(Loader<TransData> loader) {
        // AsyncTaskLoaderが破棄されるときに呼び出される
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: " + connectionResult);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

//        if (!mResolvingError) {
//            mGoogleApiClient.connect();
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (!mResolvingError) {
//            Wearable.DataApi.removeListener(mGoogleApiClient, this);
//            mGoogleApiClient.disconnect();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

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
                heartRate = item.getDataMap().getString("hert_rate");
                Log.d(TAG, "hert_rate :" + heartRate);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtRate.setText(heartRate);
                    }
                });

            }
        }
    }
}
