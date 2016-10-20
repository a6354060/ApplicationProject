package com.jcxy.MobileSafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.jcxy.MobileSafe.db.dao.BlackNumberDao;

import java.lang.reflect.Method;

public class BlackNumberService extends Service {
    private static final String TAG = "BlackNumberService";
    private SharedPreferences sp;
    private BlackNumberDao dao;
    private MyReceive receive;
    private MyPhoneListener listener;
    private TelephonyManager tm;
    private ContentObserver observer;
    private ContentResolver resolver;
    private Uri uri;

    public BlackNumberService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sp= getSharedPreferences("config", MODE_PRIVATE);
        dao = new BlackNumberDao(this);
        // 注册广播进行短信拦截 只在4.3之前后效
        registerReceive();

        // 电话拦截
        endCallSafe();

    }

    /**
     * 电话拦截
     */
    private void endCallSafe() {
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyPhoneListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    /**
     * 监听来电状态
     */
    class MyPhoneListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    // 电话响了
                    Log.i(TAG, incomingNumber + "电话来了");
                    int inMode = dao.findByNumber(incomingNumber);
                    if (inMode == 0 || inMode == 1) {

                        System.out.println(incomingNumber+"被拦截了");

                        // 删除呼叫记录
                        deleteCallRecord(incomingNumber);

                        // 拦截电话
                        breakCall();
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    break;

                default:
                    break;
            }

        }

        /**
         * 中断电话
         */
        private void breakCall() {
            System.out.println("拦截情况");
            try {
                System.out.println("try里面拦截情况");
                Class<?> clszz = getClassLoader().loadClass("android.os.ServiceManager");
                System.out.println("clszz"+clszz);
                Method m = clszz.getDeclaredMethod("getService", String.class);
                // IBinder 就是 电话服务提供的接口 给我们使用的
                System.out.println("m"+m);
                IBinder iBinder = (IBinder) m.invoke(null,TELEPHONY_SERVICE);
                // 拿到 实列对象接口
                System.out.println("iBinder"+iBinder);
                ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
                //　 挂断电话
                System.out.println("iTelephony"+iTelephony);
                 iTelephony.endCall();
                System.out.println("endCall后面");




            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    /**
     * 删除呼叫记录
     */
    private void deleteCallRecord(String number) {
         resolver = getContentResolver();
         uri=Uri.parse("content://call_log/calls");
        observer = new MyContentObserver(new Handler(),number);
        resolver.registerContentObserver(uri,true,observer);

    }

    /**
     * 监听电话记录的内容改变情况
     */
    private class MyContentObserver extends ContentObserver{
      private  String Number;
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler,String innumber) {
            super(handler);
            this.Number=innumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            int delete = resolver.delete(uri, "number=?", new String[]{Number});
            Log.i(TAG, "删除了"+Number+"号码的记录");
        }
    }





    /**
     * 注册短信接收广播
     */
    private void registerReceive() {

        receive = new MyReceive();
        IntentFilter intenFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intenFilter.setPriority(Integer.MAX_VALUE);
          if(!sp.getBoolean("safeLock",false)) {
              registerReceiver(receive, intenFilter);
          }
    }




    /**
     * 短信广播接受者
     */
    class MyReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : objs) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj,"utf-8");
                String number = smsMessage.getOriginatingAddress();
                 if(!TextUtils.isEmpty(number)){
                     int mode = dao.findByNumber(number);
                     if (mode == 0 || mode == 2) {
                         // 进行短信拦截
                         abortBroadcast();
                     }
                 }

            }


        }

    }

    @Override
    public void onDestroy() {
        if(!sp.getBoolean("safeLock",false)) {
            // 取消注册
            unregisterReceiver(receive);
        }
            // 取消监听
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);

        super.onDestroy();
    }
}


