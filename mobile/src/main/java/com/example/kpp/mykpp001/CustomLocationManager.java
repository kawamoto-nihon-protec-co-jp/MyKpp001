package com.example.kpp.mykpp001;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

/**
 * GPS処理クラス
 * @author T.Kawamoto
 * @version 1.0
 */
public class CustomLocationManager implements LocationListener {
    // 緯度
    public String strIdoC = "";
    // 経度
    public String strKeidoC = "";
    // 時間
    public Long longJikan = (long) 0;

    // GPSタイムアウト時間
    private static final int GPS_TIME_OUT = 180000;
    private LocationManager mLocationManager;
    private LocationCallback mLocationCallback;

    private Handler mHandler = new Handler();


    public CustomLocationManager(Context context) {
        mLocationManager = (LocationManager) context.getSystemService(Activity.LOCATION_SERVICE);
    }

    /**
     * GPS情報の取得
     * @param delayMillis
     * @param locationCallback
     */
    public void doNowLocationData(int delayMillis, LocationCallback locationCallback) {
        this.mLocationCallback = locationCallback;
        mHandler.postDelayed(networkTimeOutRun, delayMillis);
        startLocation(LocationManager.NETWORK_PROVIDER);
    }

    private Runnable gpsTimeOutRun = new Runnable() {

        @Override
        public void run() {
            removeUpdate();
            if (mLocationCallback != null) {
                mLocationCallback.onTimeout();
            }
        }
    };

    private Runnable networkTimeOutRun = new Runnable() {

        @Override
        public void run() {
            removeUpdate();
            mHandler.postDelayed(gpsTimeOutRun, GPS_TIME_OUT);
            startLocation(LocationManager.GPS_PROVIDER);
        }
    };

    private void startLocation(String provider) {
        mLocationManager.requestLocationUpdates(provider, 0, 0, this);
    }

    private void removeUpdate() {
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        strIdoC = Double.toString(location.getLatitude());// 緯度
        strKeidoC = Double.toString(location.getLongitude());// 経度
        longJikan = location.getTime();// 時間

        mHandler.removeCallbacks(gpsTimeOutRun);
        mHandler.removeCallbacks(networkTimeOutRun);
        if (this.mLocationCallback != null) {
            this.mLocationCallback.onComplete(location);
        }
        removeUpdate();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public static interface LocationCallback {

        public void onComplete(Location location);

        public void onTimeout();
    }
}
