package com.jcxy.MobileSafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.receive.MyAppWidget;
import com.jcxy.MobileSafe.utils.SystemUtils;

import java.util.Timer;
import java.util.TimerTask;

public class MyAppWidgetService extends Service {
    private Timer timer;
    private TimerTask task;
    private AppWidgetManager widgetManager;

    public MyAppWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 定时清理进程
         *  更新AppWidget
         *
         */
        widgetManager = AppWidgetManager.getInstance(this);
        task = new TimerTask() {
            @Override
            public void run() {
                // 处理业务逻辑
                SystemUtils.killAllProcess(MyAppWidgetService.this);
                //更新小部件
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.my_app_widget);

                views.setTextViewText(R.id.process_count, "运行中的进程:" + SystemUtils.getRunningCount(MyAppWidgetService.this) + "个");
                views.setTextViewText(R.id.process_memory, "可用内存:" + Formatter.formatFileSize(MyAppWidgetService.this, SystemUtils.getFreeMemory(MyAppWidgetService.this)));

                // 点击 就发送个广播
                Intent intent = new Intent();
                // 设置个隐示意图
                intent.setAction("KILL_ALL_PROCESS_BROADCAST");

                // 不确定意图 这里发送个广播
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
                ComponentName provider = new ComponentName(getApplicationContext(), MyAppWidget.class);
                //更新桌面
                widgetManager.updateAppWidget(provider, views);


            }
        };
        timer = new Timer();
        timer.schedule(task, 0, 5000);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (widgetManager != null) {
            widgetManager = null;
        }

    }
}
