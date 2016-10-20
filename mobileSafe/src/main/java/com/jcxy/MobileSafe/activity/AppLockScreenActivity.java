package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class AppLockScreenActivity extends Activity implements OnClickListener {

    private EditText et_pwd;
    private Button bt_0;
    private Button bt_1;
    private Button bt_2;
    private Button bt_3;
    private Button bt_4;
    private Button bt_5;
    private Button bt_6;
    private Button bt_7;
    private Button bt_8;
    private Button bt_9;
    private Button bt_clean_all;
    private Button bt_delete;
    private Button bt_ok;
    private String packageName;
    private TextView top;
    private String topTitle;
    private SharedPreferences config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock_screen);
        top = (TextView) findViewById(R.id.tv_top_title);
        config = getSharedPreferences("config", MODE_PRIVATE);
        initUI();

        topTitle = getIntent().getStringExtra("topTitle");
        // 是TRUE设置隐私锁
        if ("ON".equals(topTitle)) {
            top.setText("请设置隐私锁密码");
        }


    }


    /**
     * 返回键
     * @param view
     */
    public void back(View view){
        if(TextUtils.isEmpty(topTitle)){
            // 当用户输入后退健 的时候。我们进入到桌面
            Intent intent = new Intent();
            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addCategory("android.intent.category.MONKEY");
            startActivity(intent);
        }else {
            finish();
        }

    }

    private void initUI() {

        Intent intent = getIntent();

        if (intent != null) {
            packageName = intent.getStringExtra("packageName");
        }

        et_pwd = (EditText) findViewById(R.id.et_pwd);

        // 隐藏当前的键盘
        et_pwd.setInputType(InputType.TYPE_NULL);

        bt_0 = (Button) findViewById(R.id.bt_0);
        bt_1 = (Button) findViewById(R.id.bt_1);
        bt_2 = (Button) findViewById(R.id.bt_2);
        bt_3 = (Button) findViewById(R.id.bt_3);
        bt_4 = (Button) findViewById(R.id.bt_4);
        bt_5 = (Button) findViewById(R.id.bt_5);
        bt_6 = (Button) findViewById(R.id.bt_6);
        bt_7 = (Button) findViewById(R.id.bt_7);
        bt_8 = (Button) findViewById(R.id.bt_8);
        bt_9 = (Button) findViewById(R.id.bt_9);

        bt_ok = (Button) findViewById(R.id.bt_ok);

        bt_ok.setOnClickListener(this);

        bt_clean_all = (Button) findViewById(R.id.bt_clean_all);

        bt_delete = (Button) findViewById(R.id.bt_delete);
        // 清空
        bt_clean_all.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                et_pwd.setText("");
            }
        });
        // 删除
        bt_delete.setOnClickListener(new OnClickListener() {

            private String str;

            @Override
            public void onClick(View v) {

                str = et_pwd.getText().toString();

                if (str.length() == 0) {
                    return;
                }

                et_pwd.setText(str.substring(0, str.length() - 1));

            }
        });

        bt_0.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_0.getText().toString());
            }
        });
        bt_1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_1.getText().toString());
            }
        });
        bt_2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_2.getText().toString());
            }
        });
        bt_3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_3.getText().toString());
            }
        });
        bt_4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_4.getText().toString());
            }
        });
        bt_5.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_5.getText().toString());
            }
        });
        bt_6.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_6.getText().toString());
            }
        });
        bt_7.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_7.getText().toString());
            }
        });
        bt_8.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_8.getText().toString());
            }
        });
        bt_9.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_9.getText().toString());
            }
        });
    }

    /**
     * 处理文本
     *
     * @param button
     */
    private void dealText(Button button) {

    }

    private List<String> temp = new ArrayList<String>();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_ok:
                String result = et_pwd.getText().toString();
                if ("ON".equals(topTitle)) {//需要重新设置界面密码
                    if (TextUtils.isEmpty(result)) {
                        UIUtils.showToast(AppLockScreenActivity.this, "密码不能为空");
                        break;
                    }
                    et_pwd.setText("");
                    top.setText("请再次输入密码");
                    temp.add(result);
                    if (temp.size() == 2 && temp.get(0).equals(temp.get(1))) {
                        config.edit().putString("applockpassword", result).commit();
                        temp.clear();
                        temp = null;
                        startActivity(new Intent(AppLockScreenActivity.this, AppLockActivity.class));
                        finish();
                    } else if (temp.size() == 2 && !temp.get(0).equals(temp.get(1))) {
                        temp.remove(1);
                        UIUtils.showToast(AppLockScreenActivity.this, "俩次密码不一致");
                        break;
                    }
                } else if ("OFF".equals(topTitle)) { //不需要重新设置界面密码

                    if (TextUtils.isEmpty(result)) {
                        UIUtils.showToast(AppLockScreenActivity.this, "密码不能为空");
                        break;
                    } else if (result.equals(config.getString("applockpassword", ""))) { //密码正确
                        startActivity(new Intent(AppLockScreenActivity.this, AppLockActivity.class));
                        finish();
                    } else {
                        UIUtils.showToast(AppLockScreenActivity.this, "俩次密码不一致");
                       break;
                    }

                } else if (config.getString("applockpassword", "").equals(result)) {
                    // 如果密码正确。说明是自己人
                    /**
                     * 是自己家人。不要拦截他
                     */
                    System.out.println("密码输入正确");

                    Intent intent = new Intent();
                    // 发送广播。停止保护
                    intent.setAction("TEMPUNLOCKAPP");
                    // 跟狗说。现在停止保护短信
                    intent.putExtra("packageName", packageName);

                    sendBroadcast(intent);

                    finish();

                } else {
                    UIUtils.showToast(AppLockScreenActivity.this, "密码错误");
                }

                break;

        }

    }

    @Override
    public void onBackPressed() {
        // 当用户输入后退健 的时候。我们进入到桌面
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }

}

