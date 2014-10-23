package com.example.kpp.mykpp001;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class CustomLocationManager implements LocationListener {
    private LocationManager mLocationManager;
    private LocationCallback mLocationCallback;
    private Handler mHandler = new Handler();
    private static final int NETWORK_TIMEOUT = 5000; // Wi-Fiタイムアウト時間
    private static final int GPS_TIME_OUT = 180000;  // GPSタイムアウト時間

    public String strIdoC = "";             // 緯度
    public String strKeidoC = "";           // 経度
    public Long longJikan = (long) 0;       // 時間


    public CustomLocationManager(Context context) {
        mLocationManager = (LocationManager) context.getSystemService(Activity.LOCATION_SERVICE);
    }

    public void getNowLocationData(int delayMillis, LocationCallback locationCallback) {
        this.mLocationCallback = locationCallback;
        //mHandler.postDelayed(gpsTimeOutRun, delayMillis);
        //startLocation(LocationManager.GPS_PROVIDER);
        mHandler.postDelayed(networkTimeOutRun, delayMillis);
        startLocation(LocationManager.NETWORK_PROVIDER);
    }

//del 2013.07.02 未使用のため削除
//     private static final int mAccuracy = Criteria.ACCURACY_COARSE;// 位置情報取得の精度
//     private static final int mBearingAccuracy = Criteria.NO_REQUIREMENT;// 方位精度
//     private static final int mHorizontalAccuracy = Criteria.ACCURACY_LOW;// 緯度経度の取得
//     private static final int mVerticalAccuracy = Criteria.NO_REQUIREMENT;// 高度の取得
//     private static final int mPowerLevel = Criteria.POWER_HIGH;// 電力消費レベルの設定
//     private static final int mSpeedAccuracy = Criteria.NO_REQUIREMENT;// 速度の精度
//     private static final boolean isAltitude = false;// 高度の取得の有無
//     private static final boolean isBearing = false;// 方位を取得の有無
//     private static final boolean isCostAllowed = false;// 位置情報取得に関して金銭的なコストの許可
//     private static final boolean isSpeed = false;// 速度を出すかどうか
//
//     private String getBestProvider(boolean enabledOnly) {
//
//     Criteria criteria = new Criteria();
//
//     criteria.setAccuracy(mAccuracy);
//     criteria.setBearingAccuracy(mBearingAccuracy);
//     criteria.setAltitudeRequired(isAltitude);
//     criteria.setBearingRequired(isBearing);
//     criteria.setCostAllowed(isCostAllowed);
//     criteria.setHorizontalAccuracy(mHorizontalAccuracy);
//     criteria.setPowerRequirement(mPowerLevel);
//     criteria.setSpeedAccuracy(mSpeedAccuracy);
//     criteria.setSpeedRequired(isSpeed);
//     criteria.setVerticalAccuracy(mVerticalAccuracy);
//
//     return mLocationManager.getBestProvider(criteria, enabledOnly);
//
//     }

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

    public boolean checkGpsMode() {
        boolean gps = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;
    }

    public void startLocation(String provider) {
        mLocationManager.requestLocationUpdates(provider, 0, 0, this);
    }

    public void removeUpdate() {
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
Log.i("DEBUG", "CustomLocationManager.onLocationChanged");
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
