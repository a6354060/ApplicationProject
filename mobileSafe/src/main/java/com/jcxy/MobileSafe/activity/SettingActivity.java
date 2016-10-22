package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.service.AdressService;
import com.jcxy.MobileSafe.service.AppLockService;
import com.jcxy.MobileSafe.service.BlackNumberService;
import com.jcxy.MobileSafe.service.DeskWindowService;
import com.jcxy.MobileSafe.utils.ServiceStatuUtils;
import com.jcxy.MobileSafe.view.SettingItemClickRelative;
import com.jcxy.MobileSafe.view.SettingItemRelative;
import com.wenming.library.DetectService;


public class SettingActivity extends Activity {
    private SettingItemRelative auto_update;
    private SharedPreferences spf;
    private SettingItemRelative sir_address;
    private SettingItemClickRelative scr_prompt;
    private SettingItemRelative sir_desk_window;
    private SettingItemRelative sir_black_number;
    private SettingItemRelative sir_widget;
    private static final String TAG = "SplashActivity";
    private SettingItemRelative appLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        sir_address = (SettingItemRelative) findViewById(R.id.sir_address);
        scr_prompt = (SettingItemClickRelative) findViewById(R.id.reminder_loaction);
        sir_desk_window = (SettingItemRelative) findViewById(R.id.sir_desk_window);
        sir_black_number = (SettingItemRelative) findViewById(R.id.sir_black_number);
        auto_update = (SettingItemRelative) findViewById(R.id.sir_auto_update);
        sir_widget = (SettingItemRelative) findViewById(R.id.sir_widget);
        appLock = (SettingItemRelative) findViewById(R.id.app_lock_service);

        spf = getSharedPreferences("config", MODE_PRIVATE);

        autoUpdateSetting();

        initAddress();

        activeFromAserviceStatu();

        setAdressDisplayStyle();

        promptDialogLoaction();

        setDeskWindow();

        setBlackNumberFunction();

        widget();

        appLock();

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
     * 黑名单功能
     */
    private void setBlackNumberFunction() {
        final Intent intent = new Intent(SettingActivity.this, BlackNumberService.class);
        // 判断以前的状态
        boolean b = spf.getBoolean("blackNumberFunction", false);
        if (b) {
            sir_black_number.setChecked(true);
            startService(intent);
        } else {
            sir_black_number.setChecked(false);
            stopService(intent);
        }

        // 设置点击事件 是否开启桌面小浮窗
        sir_black_number.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断当期的状态
                if (sir_black_number.isChecked()) {
                    // 点击状态
                    sir_black_number.setChecked(false);
                    spf.edit().putBoolean("blackNumberFunction", false).commit();
                    // 关闭浮窗服务
                    stopService(intent);
                } else {
                    // 非点击
                    sir_black_number.setChecked(true);
                    spf.edit().putBoolean("blackNumberFunction", true).commit();
                    // 开启浮窗服务
                    startService(intent);
                }
            }
        });

    }

    /**
     * 自动更新设置
     */
    private void autoUpdateSetting() {


        boolean autoUpdate = spf.getBoolean("update_auto", true);
        if (autoUpdate) {
            // sivUpdate.setDesc("自动更新已开启");
            auto_update.setChecked(true);
        } else {
            // sivUpdate.setDesc("自动更新已关闭");
            auto_update.setChecked(false);
        }

        auto_update.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auto_update.isChecked()) {
                    spf.edit().putBoolean("update_auto", false).commit();
                    auto_update.setChecked(false);
                } else {
                    spf.edit().putBoolean("update_auto", true).commit();
                    auto_update.setChecked(true);
                }
            }
        });
    }

    /**
     * 电话归属地设置
     */
    private void initAddress() {
        boolean b = spf.getBoolean("showAddress", false);
        // 判断以前是否点击过
        if (b) {
            sir_address.setChecked(true);
        } else {
            sir_address.setChecked(false);
        }

        sir_address.setOnClickListener(new OnClickListener() {
            Intent intent = new Intent(SettingActivity.this, AdressService.class);

            @Override
            public void onClick(View v) {
                // 判断是否是点击状态
                if (sir_address.isChecked()) {
                    spf.edit().putBoolean("showAddress", false).commit();
                    sir_address.setChecked(false);
                    // 关闭服务
                    stopService(intent);
                } else {
                    spf.edit().putBoolean("showAddress", true).commit();
                    sir_address.setChecked(true);
                    // 开启服务
                    startService(intent);

                }
            }
        });

    }

    /**
     * 根据归属的服务开启状态来控制归属的开关
     */
    private void activeFromAserviceStatu() {
        boolean statu = ServiceStatuUtils.serviceStatu(this, "com.jcxy.MobileSafe.service.AdressService");
        if (statu) {
            // 已经开启服务
            sir_address.setChecked(true);
        } else {
            // 服务已经关闭
            sir_address.setChecked(false);
        }

    }

    /**
     * 设置归属地显示风格
     */
    private void setAdressDisplayStyle() {
        SettingItemClickRelative scr = (SettingItemClickRelative) findViewById(R.id.scr_style);
        final TextView tv_desc = (TextView) scr.findViewById(R.id.tv_desc);
        final String[] items = new String[]{"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};
        final int hint = spf.getInt("hintStyle", 0);
        tv_desc.setText(items[hint]);
        scr.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出对话框选择风格
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setIcon(R.drawable.main_icon_notification);
                builder.setTitle("电话归属地提示框风格");
                builder.setSingleChoiceItems(items, hint, new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_desc.setText(items[which]);
                        spf.edit().putInt("hintStyle", which).commit();
                        dialog.dismiss();
                    }
                });
                // 显示对话框

                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });

    }

    /**
     * 归属地提示框的位置
     */
    private void promptDialogLoaction() {
        TextView tv_desc = (TextView) scr_prompt.findViewById(R.id.tv_desc);
        tv_desc.setText("设置归属地提示框的显示位置");
        scr_prompt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, PromptDialogLocationActivity.class));
            }
        });

    }

    /**
     * 桌面小浮窗
     */

    private void setDeskWindow() {
        final Intent intent = new Intent(SettingActivity.this, DeskWindowService.class);
        // 判断以前的状态
        boolean b = spf.getBoolean("desk_window", false);
        if (b) {
            sir_desk_window.setChecked(true);
            startService(intent);
        } else {
            sir_desk_window.setChecked(false);
            stopService(intent);
        }

        // 设置点击事件 是否开启桌面小浮窗
        sir_desk_window.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断当期的状态
                if (sir_desk_window.isChecked()) {
                    // 点击状态
                    sir_desk_window.setChecked(false);
                    spf.edit().putBoolean("desk_window", false).commit();
                    // 关闭浮窗服务
                    stopService(intent);
                } else {
                    // 非点击
                    sir_desk_window.setChecked(true);
                    spf.edit().putBoolean("desk_window", true).commit();
                    // 开启浮窗服务
                    startService(intent);
                }
            }
        });

    }

    /**
     * 桌面快捷方式
     */
    private void widget() {


        // 判断以前的状态
        boolean b = spf.getBoolean("widget", false);
        if (b) {
            sir_widget.setChecked(true);

        } else {
            sir_widget.setChecked(false);
        }

        // 设置点击事件 是否开启桌面快捷方式
        sir_widget.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断当期的状态
                if (sir_widget.isChecked()) {
                    // 点击状态
                    sir_widget.setChecked(false);
                    spf.edit().putBoolean("widget", false).commit();
                    // 关闭快捷方式
                    stopWidget();
                } else {
                    // 非点击
                    sir_widget.setChecked(true);
                    spf.edit().putBoolean("widget", true).commit();

                    // 开启快捷方式
                    startWidget();
                }
            }
        });


    }

    /**
     * 程序锁服务功能
     */
    public MyReceiver myReceiver;
    private void appLock() {
        if (!DetectService.isAccessibilitySettingsOn(getApplicationContext()) == true) {
           spf.edit().putBoolean("appLock",false).commit();
        }

        // 判断以前的状态
        boolean b = spf.getBoolean("appLock", false);
        if (b) {
            appLock.setChecked(true);
            // 开启程序锁
            Intent intent = new Intent(SettingActivity.this, AppLockService.class);
            startService(intent);
        } else {
            appLock.setChecked(false);
            // 关闭程序锁
            Intent intent = new Intent(SettingActivity.this, AppLockService.class);
            stopService(intent);
        }

        // 设置点击事件 是否开启程序锁
        appLock.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断当期的状态
                if (appLock.isChecked()) {
                    // 点击状态
                    appLock.setChecked(false);
                    spf.edit().putBoolean("appLock", false).commit();
                    // 关闭程序锁
                    Intent intent = new Intent(SettingActivity.this, AppLockService.class);
                    stopService(intent);

                } else {
                    // 非点击
                    // 开启程序锁
                    // 弹出对话框选择风格
                    final AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                    AlertDialog dialog = builder.create();
                    builder.setTitle("温馨提示");
                    builder.setMessage("开启程序锁必须要到设置->辅助功能 开启无障碍功能，该功能不会影响手机使用。");
                    // 显示对话框
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {


                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                             myReceiver = new MyReceiver();
                            registerReceiver(myReceiver,new IntentFilter("isStartFunction"));
                            Intent intent = new Intent(SettingActivity.this, AppLockService.class);
                            startService(intent);
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.show();



                }
            }
        });


    }

    /**
     * 开启快捷方式
     */
    private void startWidget() {
        // 发送创建快捷方式的广播
        Intent intent = new Intent();
        // 行为是创建快捷方式
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        // 快捷方式的名字
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "安全卫士");
        // 快捷的样子
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));

        // 按下的动作

        Intent dowhantIntent = new Intent();
        dowhantIntent.setAction("masterHome");
        dowhantIntent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, dowhantIntent);

        // 是否可以重复创建
        intent.putExtra("duplicate", false);
        sendBroadcast(intent);
        // Toast.makeText(this, "桌面快捷成功", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "桌面快捷成功");
    }

    /**
     * 关闭快捷方式
     */
    private void stopWidget() {
        // 发送销毁快捷方式的广播


        Intent intent = new Intent();
        // 行为是销毁快捷方式
        intent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
        // 快捷方式的名字
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "卫士快捷");
        // 快捷的样子
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));

        // 按下的动作

        Intent dowhantIntent = new Intent();
        dowhantIntent.setAction("masterHome");
        dowhantIntent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, dowhantIntent);

        // 是否可以重复创建
        intent.putExtra("duplicate", false);
        sendBroadcast(intent);
        //Toast.makeText(this, "桌面快捷销毁", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "桌面销毁成功");

    }

    /**
     * 防止在没设置无障碍功能是按返回键一直跳的问题
     */
    public class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("appLockReceiver");
            if (DetectService.isAccessibilitySettingsOn(getApplicationContext()) == true) {
                 appLock.setChecked(true);
                spf.edit().putBoolean("appLock",true).commit();
            }else {
                System.out.println("stopService");
                Intent intent1 = new Intent(SettingActivity.this, AppLockService.class);
                stopService(intent1);
            }
        }
    }

    @Override
    protected void onStart() {
        sendBroadcast(new Intent("isStartFunction"));
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myReceiver!=null){
        unregisterReceiver(myReceiver);
        myReceiver=null;
    }
  }
}
