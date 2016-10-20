package com.jcxy.MobileSafe.receive;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.jcxy.MobileSafe.service.MyAppWidgetService;

/**
 * Implementation of App Widget functionality.
 */
public class MyAppWidget extends AppWidgetProvider {


    /**
     * 第一次创建时调用
     *
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        /**
         * 开始服务
         */
        Intent intent = new Intent(context, MyAppWidgetService.class);
        context.startService(intent);
        System.out.println("onEnabled");


    }



    /**
     * 当小部件全部消失时调用
     *
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        /**
         * 停止服务
         */
        Intent intent = new Intent(context, MyAppWidgetService.class);
        context.stopService(intent);

        System.out.println("onDisabled");

    }
}

