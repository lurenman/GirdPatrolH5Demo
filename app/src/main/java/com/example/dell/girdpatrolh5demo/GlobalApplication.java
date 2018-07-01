package com.example.dell.girdpatrolh5demo;

import android.app.Application;

import com.example.dell.girdpatrolh5demo.service.LocationService;

/**
 * 创建日期：2018/6/13
 * 作者:baiyang
 */
public class GlobalApplication extends Application {
    public static GlobalApplication instance;
    public LocationService locationService;
    public double latitude = 0;
    public double longtitude = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
    }

    public static GlobalApplication getInstance() {
        return instance;
    }
}
