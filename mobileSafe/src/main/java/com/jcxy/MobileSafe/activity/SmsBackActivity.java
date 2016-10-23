package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.utils.SmsUtils;
import com.jcxy.MobileSafe.utils.UIUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsBackActivity extends Activity {
    private static final int BACK_SMS = 1;
    private static final int RESTORE_SMS = 2;
    private static final int UPDATE = 3;
    private static final int UPDATE1 = 4;
    @ViewInject(R.id.tv_prompt)
    private TextView tvPrompt;

    @ViewInject(R.id.ll_sms_backup)
    private LinearLayout llSmsBackup;

    @ViewInject(R.id.ll_sms_restore)
    private LinearLayout llSmsRestore;

    private SharedPreferences config;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BACK_SMS:
                    tvPrompt.setText("备份完成！本次共备份" + config.getInt("backupCount", 0) + "条短信");
                    break;
                case RESTORE_SMS:
                    tvPrompt.setText("还原成功！本次共还原" + config.getInt("backupCount", 0) + "条短信");
                    if(Build.VERSION.SDK_INT>=19) {
                        if (!packageName.equals(defaultSmsPackage)) {
                            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSmsPackage);
                            startActivity(intent);
                        }
                    }
                    break;

                case UPDATE:
                    progressDialog.show();
                    break;
                case UPDATE1:
                    progressDialog1.show();
                    break;
            }


        }
    };
    private String defaultSmsPackage;
    private String packageName;
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialog1;
    boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_back);
        initUI();
        if(Build.VERSION.SDK_INT>=19){
            defaultSmsPackage = Telephony.Sms.getDefaultSmsPackage(SmsBackActivity.this);
            packageName = getPackageName();
        }

        initData();
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
     * 初始化数据
     */
    private void initData() {

        if (new File(Environment.getExternalStorageDirectory(), "back.xml").exists()) {
            tvPrompt.setText("上次备份时间" + config.getString("backDate", ""));
        }
        /**
         * 备份短信
         */
        llSmsBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackSms();
            }
        });

        llSmsRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 判断当前的用户的SDK版本的大小
                 */
                if(Build.VERSION.SDK_INT>=19){
                      final String defaultSmsPackage1 = Telephony.Sms.getDefaultSmsPackage(SmsBackActivity.this);
                      if (!packageName.equals(defaultSmsPackage1)) {
                          smsPrompt();
                      } else {
                          RestoreSms();
                      }
                  }else {
                      RestoreSms();
                  }



            }
        });

    }


    /**
     * 还原提示对话框
     */

    private void smsPrompt() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示");
        String message = "您需要临时将\"手机卫士\"设置为默认短信应用，才能恢复短信。    \r\n" +
                "恢复成功后，为了保障短信的的正常收发，还需要将\"短信\"设置为默认短信应用.当点击是后再点击还原短信就可以还原了！";
        builder.setMessage(message);
        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println(defaultSmsPackage + ",smsprom前================");
                if (!packageName.equals(defaultSmsPackage)) {
                    Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName);
                    startActivity(intent);
                }
            }
        });
        builder.show();
    }


    /**
     * 还原短信
     */
    private void RestoreSms() {

        final File file = new File(Environment.getExternalStorageDirectory(), "back.xml");
        if (!file.exists()) {
            Toast.makeText(this, "您还没有备份短信", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog1 = new ProgressDialog(this);
        progressDialog1.setTitle("还原短信");
        progressDialog1.setMessage("正在还原中...");
        progressDialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog1.show();
        new Thread() {
            @Override
            public void run() {
                boolean result = SmsUtils.restore(SmsBackActivity.this, new SmsUtils.SmsBackCall() {
                    @Override
                    public void before(int count) {
                        progressDialog1.setMax(count);
                    }

                    @Override
                    public void backing(int process) {
                        progressDialog1.setProgress(process);
                        Message obtain = Message.obtain();
                        obtain.what = UPDATE1;
                        handler.sendMessage(obtain);
                        progressDialog1.show();

                    }
                }, progressDialog1,file);

                if (result) {
                    UIUtils.showToast(SmsBackActivity.this, "还原成功");
                } else {
                    UIUtils.showToast(SmsBackActivity.this, "还原失败");
                }
                Message msg = Message.obtain();
                msg.what = RESTORE_SMS;
                handler.sendMessage(msg);
            }
        }.start();

    }

    /**
     * 备份短信
     */
    private void BackSms() {
        ContentResolver resolver = this.getContentResolver();
        final Cursor cursor = resolver.query(Uri.parse("content://sms"), new String[]{"address", "date", "type", "body"}, null, null, null);
         if(cursor == null||cursor.getCount() == 0){
             Toast.makeText(this, "没有短信可备份", Toast.LENGTH_SHORT).show();
             return;
         }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("备份短信");
        progressDialog.setMessage("正在备份中...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //progressDialog.show();
        new Thread() {
            @Override
            public void run() {
                boolean result = SmsUtils.backUp(SmsBackActivity.this, new SmsUtils.SmsBackCall() {
                    @Override
                    public void before(int count) {
                        progressDialog.setMax(count);
                    }

                    @Override
                    public void backing(int process) {
                        progressDialog.setProgress(process);
                        Message obtain = Message.obtain();
                        obtain.what = UPDATE;
                        handler.sendMessage(obtain);

                    }
                }, progressDialog,cursor);

                if (result) {
                    UIUtils.showToast(SmsBackActivity.this, "备份成功");
                    // 保存备份时间
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
                    String format = simpleDateFormat.format(new Date());
                    System.out.println(format);
                    config.edit().putString("backDate", format).commit();
                    Message msg = Message.obtain();
                    msg.what = BACK_SMS;
                    handler.sendMessage(msg);

                } else {
                    boolean showing = progressDialog.isShowing();
                    if (showing) {
                        progressDialog.dismiss();
                    }
                    UIUtils.showToast(SmsBackActivity.this, "备份失败");
                }


            }
        }.start();
    }

    /**
     * 初始化ui
     */
    private void initUI() {
        ViewUtils.inject(this);
        config = getSharedPreferences("config", MODE_PRIVATE);
    }
}
