package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.service.KillProcessService;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class TaskManagerSettingActivity extends Activity implements View.OnClickListener {
    @ViewInject(R.id.rl_show_system_process)
    private RelativeLayout rl_show_system_process;

    @ViewInject(R.id.rl_timer_clean)
    private RelativeLayout rl_timer_clean;

    @ViewInject(R.id.sw_show_system_process)
    private Switch sw_show_system;

    @ViewInject(R.id.sw_clean_process)
    private Switch sw_clean_process;

    private SharedPreferences config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager_setting);
        config = getSharedPreferences("config", MODE_APPEND);
        initUI();
        rl_show_system_process.setOnClickListener(this);
        rl_timer_clean.setOnClickListener(this);


    }

    /**
     * 返回键
     * @param view
     */
    public void back(View view){
        finish();
    }

    private void initUI() {
        ViewUtils.inject(this);
        /**
         * 保存状态
         */
        boolean is_show_system = config.getBoolean("is_show_system", false);
        sw_show_system.setChecked(is_show_system);

        boolean is_timer_clean = config.getBoolean("is_timer_clean", false);
        if(is_timer_clean) {
            sw_clean_process.setChecked(true);
            Intent intent=new Intent(this, KillProcessService.class);
            startService(intent);
        }else {
            sw_clean_process.setChecked(false);
            Intent intent=new Intent(this, KillProcessService.class);
            stopService(intent);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_show_system_process:
                // 显示系统进程
                if (sw_show_system.isChecked()) {

                        sw_show_system.setChecked(false);

                    config.edit().putBoolean("is_show_system", false).commit();
                } else {
                    sw_show_system.setChecked(true);
                    config.edit().putBoolean("is_show_system", true).commit();
                }


                break;
            case R.id.rl_timer_clean:
                // 定时清理进程
                if (sw_clean_process.isChecked()) {
                    sw_clean_process.setChecked(false);
                    config.edit().putBoolean("is_timer_clean", false).commit();
                    Intent intent=new Intent(this, KillProcessService.class);
                    stopService(intent);

                } else {
                    sw_clean_process.setChecked(true);
                    config.edit().putBoolean("is_timer_clean", true).commit();
                    Intent intent=new Intent(this, KillProcessService.class);
                    startService(intent);
                }
                break;

        }
    }
}
