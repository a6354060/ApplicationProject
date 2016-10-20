package com.jcxy.MobileSafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.jcxy.MobileSafe.utils.SystemUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class KillProcessService extends Service {
    private Timer timer;
    private BroadcastReceiver receiver;

    public KillProcessService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo rap : appProcesses) {
                activityManager.killBackgroundProcesses(rap.processName);
            }
            // 发送数据更新广播
            sendBroadcast(new Intent("KILL_PROCESS_TIMER"));
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 每个俩个小时清理进程
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SystemUtils.killAllProcess(KillProcessService.this);
                // 发送数据更新广播
                sendBroadcast(new Intent("KILL_PROCESS_TIMER"));

            }
        }, 1000 * 60 * 60 * 2, 1000 * 60 * 60 * 2); // 延时俩小时


        // 当锁屏时候也清理一次
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(receiver!=null){
            unregisterReceiver(receiver);
            receiver=null;
        }
    // 当关闭服务是取消任务调度
        timer.cancel();
    }
}
