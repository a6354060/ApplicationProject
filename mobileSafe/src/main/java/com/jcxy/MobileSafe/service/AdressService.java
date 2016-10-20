package com.jcxy.MobileSafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.db.dao.QueryLoactionDao;

/**
 * 监听电话状态的服务
 *
 * @author hp
 */
public class AdressService extends Service {

    private static final String TAG = "AdressService";
    protected int startX;
    protected int startY;
    private TelephonyManager tm;
    private MyListener listener;
    private WindowManager wdm = null;
    private BroadcastReceiver receiver;
    private View view;
    private SharedPreferences spf;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 窗口管理器 用于向窗口添加一个显示框
        Log.i(TAG, "获取归属地服务已开启");

        // 动态注册广播接受者
        spf = getSharedPreferences("config", MODE_PRIVATE);
        receiver = new PhoneOutCallReceive();
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL));

        // 监听电话状态
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    /**
     * 显示归属地对话框
     *
     * @param loaction
     */
    private void showLocationDialog(String loaction) {
        // "半透明","活力橙","卫士蓝","金属灰","苹果绿"
        int[] resid = new int[]{R.drawable.call_locate_white, R.drawable.call_locate_orange,
                R.drawable.call_locate_blue, R.drawable.call_locate_gray, R.drawable.call_locate_green};
        // 需要权限
        wdm = (WindowManager) getSystemService(WINDOW_SERVICE);

        view = View.inflate(AdressService.this, R.layout.dialog_show_call_adress, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_show_adress);
        tv.setText(loaction);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.format = PixelFormat.TRANSLUCENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.gravity = Gravity.LEFT + Gravity.TOP;

        // 根据自己的选择来确定背景
        int hint = spf.getInt("hintStyle", 0);
        view.setBackgroundResource(resid[hint]);

        int lastX = spf.getInt("lastX", 0);
        int lastY = spf.getInt("lastY", 0);

        // 设置浮窗的位置, 基于左上方的偏移量
        params.x = lastX;
        params.y = lastY;
        wdm.addView(view, params);

        // 为 归属地提示框加点击接触事件
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 根据运动事件处理
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 按下
                        startX = (int) event.getRawX(); // 从屏幕开始计算
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 移动
                        int newX = (int) event.getRawX();
                        int newY = (int) event.getRawY();

                        // 计算偏移量

                        int dx = newX - startX;
                        int dy = newY - startY;

                        // 计算浮窗的位置
                        params.x += dx;
                        params.y += dy;

                        // 不让窗口浮出窗外
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        // 更新浮窗
                        wdm.updateViewLayout(view, params);

                        // 更新初始化值
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        // 送开
                        Editor edit = spf.edit();
                        edit.putInt("lastX", params.x);
                        edit.putInt("lastY", params.y);
                        edit.commit();
                        break;
                    default:
                        break;
                }

                return true; // 表示该事件处理完毕
            }
        });

    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "获取归属地服务已销毁");
        unregisterReceiver(receiver);
        tm.listen(listener, PhoneStateListener.LISTEN_NONE); // 取消监听
        super.onDestroy();
    }

    private class MyListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    // 电话响了
                    Log.i(TAG, incomingNumber + "电话来了");
                    QueryLoactionDao dao = new QueryLoactionDao();
                    String loaction = dao.queryLoaction(incomingNumber);
                    // 在电话界面上显示来电归属地
                    showLocationDialog(loaction);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i(TAG, "电话闲置");
                    System.out.println("电话闲置");
                    if (wdm != null) {
                        wdm.removeView(view); // 从窗口移除
                    }
                    // 电话闲置
                    break;

                default:
                    break;
            }

        }

    }

    /**
     * 电话外拨接受者
     *
     * @author hp
     */
    private class PhoneOutCallReceive extends BroadcastReceiver {

        private static final String TAG = "PhoneOutCallReceive";

        @Override
        public void onReceive(Context context, Intent intent) {

            String data = getResultData();
            Log.i(TAG, "外拨号码：" + data);
            // 获得归属地 地址
            QueryLoactionDao dao = new QueryLoactionDao();
            String loaction = dao.queryLoaction(data);
            showLocationDialog(loaction);
        }

    }

}
