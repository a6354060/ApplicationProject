package com.jcxy.MobileSafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.jcxy.MobileSafe.activity.AppLockScreenActivity;
import com.jcxy.MobileSafe.db.dao.ApplockDao;
import com.wenming.library.BackgroundUtil;
import com.wenming.library.processutil.ProcessManager;
import com.wenming.library.processutil.models.AndroidAppProcess;

import java.util.List;


public class AppLockService extends Service {
    private ApplockDao dao;
    private boolean flag = false;
    private ActivityManager activityManager;
    private List<String> list;
    private String tempPackageName;
    private OptimizeBroadCast receiver;

    private Handler handler =new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(getApplicationContext(),"当锁屏时应用会再次锁住",Toast.LENGTH_SHORT).show();
        }
    };
    private SharedPreferences config;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class MyObserver extends ContentObserver{

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
             list=dao.findAll();
            System.out.println("contentChanged");
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
         config = getSharedPreferences("config", MODE_PRIVATE);
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
       // 注册内容提供者
        this.getContentResolver().registerContentObserver(Uri.parse("content://appLock/update"),true,new MyObserver(new Handler()));

        dao = new ApplockDao(this);
        list = dao.findAll();

        // 发送广播
        receiver = new OptimizeBroadCast();
        IntentFilter filter = new IntentFilter();
        // 屏幕锁屏时的
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        // 屏幕打开时
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction("TEMPUNLOCKAPP");
        registerReceiver(receiver, filter);
        startAppLock();
    }


    /**
     * 开始锁屏服务
     */
    private void startAppLock() {
        new Thread() {
            @Override
            public void run() {
                flag = true;
                while (flag) {
                    List<AndroidAppProcess> runningAppProcesses = ProcessManager.getRunningAppProcesses();
                    for ( AndroidAppProcess ap: runningAppProcesses) {
                        String packageName = ap.getPackageName();
                        if(BackgroundUtil.isForeground(AppLockService.this,BackgroundUtil.BKGMETHOD_GETACCESSIBILITYSERVICE,packageName))
                        {
                            if (list.contains(packageName)) {
                                // 是锁屏应用 开启锁屏页面
                                // 发送广播临时对该应暂停监控
                                if (packageName.equals(tempPackageName)) {
                                    // 密码输入正确的 放行
                                } else {
                                    Intent intent = new Intent(AppLockService.this, AppLockScreenActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("packageName", packageName);
                                    startActivity(intent);
                                    handler.sendEmptyMessage(0);
                                }
                            }
                            break;
                        }

                    }


                }

            }
        }.start();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 优化广播
     */
    private class OptimizeBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            System.out.println(action);

            switch (action) {
                case "TEMPUNLOCKAPP":
                    tempPackageName = intent.getStringExtra("packageName");
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    tempPackageName = null;
                    // 关闭程序锁服务
                    flag=false;
                    break;
                case Intent.ACTION_SCREEN_ON:
                    // 打开程序锁服务
                    startAppLock();
                    break;

            }

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

            /**
             * 退出循环
             */
            unregisterReceiver(receiver);
            flag = false;


    }
}
