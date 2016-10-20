package com.jcxy.MobileSafe.receive;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.db.dao.BlackNumberDao;
import com.jcxy.MobileSafe.service.LocationService;

public class SmsReceiveBordcast extends BroadcastReceiver {

    private static final String TAG = "SmsReceiveBordcast";
    private SharedPreferences sp;
    private DevicePolicyManager dpm;
    private ComponentName who;

    @Override
    public void onReceive(Context context, Intent intent) {
        dpm = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
        who = new ComponentName(context, MyAdminDevices.class);
        sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
        // 判断是否开启手机卫士
        BlackNumberDao dao = new BlackNumberDao(context);
        if (sp.getBoolean("safeLock", false)) {
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : objs) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
                String sender = smsMessage.getOriginatingAddress();
                String body = smsMessage.getMessageBody();

                if(!TextUtils.isEmpty(sender)){
                    int mode = dao.findByNumber(sender);
                    if (mode == 0 || mode == 2) {
                        // 进行短信拦截
                        abortBroadcast();
                    }
                }


                if ("#*location*#".equals(body)) {
                    Log.i(TAG, "返回位置信息.");
                    // 获取位置 放在服务里面去实现。
                    Intent service = new Intent(context, LocationService.class);
                    context.startService(service);
                    abortBroadcast();
                } else if ("#*alarm*#".equals(body)) {
                    Log.i(TAG, "播放报警音乐.");
                    MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
                    player.setVolume(1.0f, 1.0f);
                    player.start();
                    abortBroadcast();
                } else if ("#*wipedata*#".equals(body)) {
                    Log.i(TAG, "远程清除数据.");
                    // 清除所有数据
                    if (dpm.isAdminActive(who)) {
                        dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                    } else {
                        Toast.makeText(context, "必须先在 设置 —》 安全 里激活设备管理器", Toast.LENGTH_SHORT).show();
                    }
                    abortBroadcast();
                } else if ("#*lockscreen*#".equals(body)) {
                    Log.i(TAG, "远程锁屏.");
                    // 判断设备管理器是否是激活状态
                    if (dpm.isAdminActive(who)) {
                        dpm.lockNow();
                        dpm.resetPassword("1001", 0);
                    } else {
                        Toast.makeText(context, "必须先在 设置 —》 安全 里激活设备管理器", Toast.LENGTH_SHORT).show();
                    }
                    abortBroadcast();
                }
            }

        }
    }
}
