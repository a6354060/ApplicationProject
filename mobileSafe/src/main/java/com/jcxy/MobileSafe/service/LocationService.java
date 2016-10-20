package com.jcxy.MobileSafe.service;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationService extends Service {

    private MyLoactionListense listener;
    private LocationManager lm;
    private SharedPreferences sp;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences("config", MODE_PRIVATE);
        // 获取经纬度
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        // 获取经纬度的提供者
        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(true);// Altitude 高度的意思
        criteria.setCostAllowed(true); // 是否收费
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = lm.getBestProvider(criteria, true);
        listener = new MyLoactionListense();
        if(provider==null){
            provider="gps";
        }
        lm.requestLocationUpdates(provider,0,0,listener);




    }

    @Override
    public void onDestroy() {
        // 移除监听
        super.onDestroy();

            lm.removeUpdates(listener);



    }

    private class MyLoactionListense implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            System.out.println("onLocationChanged");
            double longitude = location.getLongitude(); // 获得经度
            double latitude = location.getLatitude(); // 获得纬度
            double altitude = location.getAltitude();// 获得海拔
            float accuracy = location.getAccuracy();
            sp.edit().putString("location", "经度：" + longitude + "\n纬度:" + latitude + "\n 高度" + altitude).commit();
            stopSelf();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO 自动生成的方法存根
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO 自动生成的方法存根

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO 自动生成的方法存根

        }

    }

}
