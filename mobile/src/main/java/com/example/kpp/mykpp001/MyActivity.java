package com.example.kpp.mykpp001;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.mariux.teleport.lib.TeleportClient;


public class MyActivity extends Activity
//        implements DataApi.DataListener, ConnectionCallbacks, OnConnectionFailedListener
{

    private static final String TAG = MyActivity.class.getName();

    private TextView txtRate;
    private GoogleApiClient mGoogleApiClient;
    TeleportClient teleportClient;

    private static boolean startedWatch = false;
    private static boolean startedService = false;

    private final Context mContext = null;
    private boolean mResolvingError = false;

    private static final int REQUEST_RESOLVE_ERROR = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        Intent intent = getIntent();

//        if (success) {
//            Intent intent = new Intent(this, MyActivity.class);
//            startActivity(intent);
//            finish();
//        } else {
//            mPasswordView.setError(getString(R.string.error_incorrect_password));
//            mPasswordView.requestFocus();
//        }

        //teleportClient = new TeleportClient(this);
        //teleportClient.setOnGetMessageTask(new ShowHRFromOnGetMessageTask());

        txtRate = (TextView) findViewById(R.id.txt_rate);

//        if (AppConfig.AUTONOMOUS_OPERATION) {
//            startService(new Intent(DataService.class.getName()));
//            startedService = true;
//        }

        //tomo
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Wearable.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();

    }

//    public class ShowHRFromOnGetMessageTask extends TeleportClient.OnGetMessageTask {
//
//        @Override
//        protected void onPostExecute(String path) {
//            //Toast.makeText(getApplicationContext(), "Message - " + path, Toast.LENGTH_SHORT).show();
//            try {
//                double value = Double.valueOf(path);
//                txtRate.setText(path);
//            } catch(Exception e) {
//                //not a heart rate value, discard
//            }
//            //let's reset the task (otherwise it will be executed only once)
//            teleportClient.setOnGetMessageTask(new ShowHRFromOnGetMessageTask());
//        }
//    }

//    @Override
//    public void onConnected(Bundle connectionHint) {
//        if (Log.isLoggable(TAG, Log.DEBUG)) {
//            Log.d(TAG, "Connected to Google Api Service");
//        }
//        mResolvingError = false;
//        Wearable.DataApi.addListener(mGoogleApiClient, this);
//    }

//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }

//    @Override
//    public void onConnectionFailed(ConnectionResult result) {
//        if (mResolvingError) {
//            // Already attempting to resolve an error.
//            return;
//        } else if (result.hasResolution()) {
//            try {
//                mResolvingError = true;
//                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
//            } catch (IntentSender.SendIntentException e) {
//                // There was an error with the resolution intent. Try again.
//                mGoogleApiClient.connect();
//            }
//        } else {
//            Log.e(TAG, "Connection to Google API client has failed");
//            mResolvingError = false;
//            Wearable.DataApi.removeListener(mGoogleApiClient, this);
//        }
//    }

//    @Override
//    public void onDataChanged(DataEventBuffer dataEvents) {
//        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
//        dataEvents.close();
//
//        for (DataEvent event : events) {
//            if (event.getType() == DataEvent.TYPE_DELETED) {
//                Log.d(TAG, "DataItem deleted: " + event.getDataItem().getUri());
//            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
//                Log.d(TAG, "DataItem changed: " + event.getDataItem().getUri());
//            }
//        }
//        Wearable.DataApi.addListener(mGoogleApiClient,this);
//    }

    @Override
    protected void onStart() {
        super.onStart();
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
        if (id == R.id.action_startstop_watch) {
            if (teleportClient != null) {
                if (startedWatch){
                    teleportClient.sendMessage(AppConfig.STOP_ACTIVITY, null);
                    startedWatch = false;
                }
                else {
                    teleportClient.sendMessage(AppConfig.START_ACTIVITY, null);
                    startedWatch = true;
                }
            }
            return true;
        }
        if (id == R.id.action_startstop_service) {
            if(startedService) {
                stopService(new Intent(MyService.class.getName()));
                startedService = false;
            }
            else {
                startService(new Intent(MyService.class.getName()));
                startedService = true;
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
