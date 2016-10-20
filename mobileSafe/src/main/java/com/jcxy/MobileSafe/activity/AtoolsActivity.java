package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.jcxy.MobileSafe.R;

public class AtoolsActivity extends Activity {

    private SharedPreferences config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
         config = getSharedPreferences("config", MODE_PRIVATE);
    }

    /**
     * 返回键
     *
     * @param view
     */
    public void back(View view) {
        finish();
    }

    /**
     * 开始归属地查询页面
     *
     * @param view
     */
    public void queryPage(View view) {
        startActivity(new Intent(this, NumQueryPageActivity.class));
    }

    /**
     * 短信备份
     *
     * @param view
     */
    public void backUpSms(View view) {

        // 开启另一个Activity 处理短信备份和还原
        Intent intent = new Intent(this, SmsBackActivity.class);
        startActivity(intent);
    }

    /**
     * 程序锁
     */
    public void appLock(View view) {
        String applockpassword = config.getString("applockpassword", "");

        if(TextUtils.isEmpty(applockpassword)){
            /**
             * 设置程序锁密码
             */
            Intent intent = new Intent(this, AppLockScreenActivity.class);
            intent.putExtra("topTitle","ON");
            startActivity(intent);

        }else {
            Intent intent = new Intent(this, AppLockScreenActivity.class);
            intent.putExtra("topTitle","OFF");
            startActivity(intent);
        }

    }

}
