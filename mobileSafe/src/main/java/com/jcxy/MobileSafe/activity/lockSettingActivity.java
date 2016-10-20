package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.jcxy.MobileSafe.R;

public class lockSettingActivity extends Activity {

    private SharedPreferences config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_setting);
        initUI();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        config = getSharedPreferences("config", MODE_PRIVATE);
    }


    public void back(View view) {
        finish();
    }

    /**
     * 重新设置密码
     *
     * @param view
     */
    public void resetLockPassword(View view) {
        config.edit().remove("applockpassword").commit();
        /**
         * 设置程序锁密码
         */
        Intent intent = new Intent(this, AppLockScreenActivity.class);
        intent.putExtra("topTitle", "ON");
        startActivity(intent);
        finish();
    }

}
