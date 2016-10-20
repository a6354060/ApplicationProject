package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.fragment.LockFragment;
import com.jcxy.MobileSafe.fragment.UnlockFragment;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class AppLockActivity extends Activity implements View.OnClickListener {

    @ViewInject(R.id.tv_app_unlock)
    private TextView tv_app_unlock;

    @ViewInject(R.id.tv_app_lock)
    private TextView tv_app_lock;
    private LockFragment lockF;
    private UnlockFragment unLock;
    private FragmentManager fragmentManager;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);

    }

    @Override
    protected void onStart() {
        super.onStart();
        initUI();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        ViewUtils.inject(this);
        tv_app_lock.setOnClickListener(this);
        tv_app_unlock.setOnClickListener(this);
        fragmentManager = getFragmentManager();
        lockF = new LockFragment();
        unLock = new UnlockFragment();
        ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fl_app, unLock).commit();
        System.out.println("initUI");
    }

    /**
     * 设置界面
     */
    public  void lockSetting(View view){
       startActivity(new Intent(this,lockSettingActivity.class));
        finish();
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
     * 处理点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        // 开启事物

        switch (v.getId()) {
            case R.id.tv_app_lock:
                ft = fragmentManager.beginTransaction();
                tv_app_lock.setBackgroundResource(R.drawable.tab_right_pressed);
                tv_app_unlock.setBackgroundResource(R.drawable.tab_left_default);
                // 意思是 把Fragment放到对应的Id中
                ft.replace(R.id.fl_app, lockF).commit();
                System.out.println("lockFragment");
                break;
            case R.id.tv_app_unlock:
                ft = fragmentManager.beginTransaction();
                tv_app_lock.setBackgroundResource(R.drawable.tab_right_default);
                tv_app_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
                ft.replace(R.id.fl_app, unLock).commit();
                System.out.println("UnlockFragment");
                break;


        }


    }
}
