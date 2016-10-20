package com.jcxy.MobileSafe.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class RebootPhoneReceive extends BroadcastReceiver {

    private SharedPreferences sp;

    @Override
    public void onReceive(Context context, Intent intent) {
        sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
        boolean b = sp.getBoolean("safeLock", false);
        // 判断是否开启手机防盗
        if (b) {
            String sim = sp.getString("sim", null);
            if (sim != null) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
                String serialNumber = tm.getSimSerialNumber();
                if (sim.equals(serialNumber)) {
                    // sim序列号卡相等
                    System.out.println("手机安全");
                } else {
                    // sim序列号不卡相等
                    String phone = sp.getString("safeNumber", "");
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone, null, "你的sim卡已经被替换了！", null, null);
                }
            }
        }

    }

}
