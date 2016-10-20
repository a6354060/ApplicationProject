package com.jcxy.MobileSafe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.view.SettingItemRelative;

public class Setup2Activity extends BaseSetupAcitvity {

    private SettingItemRelative sir;
    private TelephonyManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup2);
        sir = (SettingItemRelative) findViewById(R.id.sir_bind_sim);
        // 判断是sim卡的绑定状态
        String s = mSpf.getString("sim", "");
        if (s.equals("")) {
            sir.setChecked(false);
        } else {
            sir.setChecked(true);
        }

    }

    // 绑定SIM卡
    public void bindSIM(View view) {
        if (sir.isChecked()) {
            mSpf.edit().remove("sim").commit();
            sir.setChecked(false);
            Toast.makeText(this, "取消绑定sim卡", Toast.LENGTH_SHORT).show();
        } else {
            tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            // 得到手机sim卡的唯一序列号
            String serialNumber = tm.getSimSerialNumber();
            mSpf.edit().putString("sim", serialNumber).commit();
            sir.setChecked(true);
            Toast.makeText(this, "成功绑定sim卡", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void showNextpage() {
        if (TextUtils.isEmpty(mSpf.getString("sim", ""))) {
            Toast.makeText(this, "必须点击绑定sim卡", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this, Setup3Activity.class));
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
        finish();
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(this, Setup1Activity.class));
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
        finish();
    }


}
