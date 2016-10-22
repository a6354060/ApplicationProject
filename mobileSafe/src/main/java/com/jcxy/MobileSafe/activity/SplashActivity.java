package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.utils.StreamUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import net.youmi.android.AdManager;
import net.youmi.android.offers.OffersManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class SplashActivity extends Activity {

    protected static final int REQUIRE_UPDATE = 1;
    protected static final int URL_ERROR = 2;
    protected static final int NET_ERROR = 3;
    protected static final int JSON_ERROR = 4;

    protected static final int ENTER_HOME = 5;
    private TextView tv;
    private TextView tv_progress;
    private String mVersionName;
    private int mVersionCode;
    private String mDescription;
    private String mDownloadUrl;

    private Handler handler = new Handler() {
        // 处理消息
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REQUIRE_UPDATE:
                    // 跳出更新对话框
                    showUpdateDialog();
                    break;
                case URL_ERROR:
                    Toast.makeText(getApplicationContext(), "链接错误,请检查你的链接！", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case NET_ERROR:
                    Toast.makeText(getApplicationContext(), "网络错误,请检查你的网络是否连接！", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case JSON_ERROR:
                    Toast.makeText(getApplicationContext(), "数据解析错误！", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case ENTER_HOME:
                    enterHome();
                    break;
                default:
                    break;
            }

        }
    };
    private PackageManager manager;
    private PackageInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tv = (TextView) findViewById(R.id.iv_version);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rel_root);
         manager = getPackageManager();
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        tv.setText("版本名:" + getVersionName());
        SharedPreferences spf = getSharedPreferences("config", MODE_PRIVATE);

        // 加载归属地数据库到包中
        loadLoactionDB("address.db");
        loadLoactionDB("commonnumber.db");
        loadLoactionDB("antivirus.db");

        /**
         * 跟新病毒数据库
         */
        // updateVirusDB();

        if (spf.getBoolean("update_auto", true)) {
            checkVersionUpdate(); // 检查更新
        } else {
            handler.sendEmptyMessageDelayed(ENTER_HOME, 2500);
        }
//        /**
//         * 闪屏动画
//         */
//        AlphaAnimation animation = new AlphaAnimation(0.2f, 1);
//        animation.setDuration(2500);
//        rl.startAnimation(animation);


        AdManager.getInstance(this).init("2a0eec27e9661ca0", "6578b13306d5908b", false, true);

        OffersManager.getInstance(this).onAppLaunch();

    }

    /**
     * 跟新病毒数据库
     */
    private void updateVirusDB() {
        /**
         * 从服务器下载更新病毒的JSON数据
         */
        new Thread(){
            @Override
            public void run() {
                HttpUtils httpUtils = new HttpUtils();
                String url = "http://175.8.19.121/virus.json";
                httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {

//                        String result = responseInfo.result;
//                        Gson gson = new Gson();
//                        result=result.substring(1, result.length()-1);
//                        result=result.replace("\\", "");
//                        VirusJson virusJson = gson.fromJson(result, VirusJson.class);
//                        VirusDao virusDao = new VirusDao();
//                        boolean b = virusDao.ubdateVirus(virusJson.getMd5(), virusJson.getDesc());
//                        if (b) {
//                            System.out.println("病毒更新成功");
//                        }
//
                  }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        System.out.println(msg);
                    }
                });
            }
        }.start();

    }

    /**
     * 数据库的导入
     */
    private void loadLoactionDB(final String name) {
        new Thread(){
            @Override
            public void run() {
                File f = new File(getFilesDir(), name);
                if (f.exists()) {
                    return;
                }
                InputStream in = null;
                FileOutputStream out = null;
                try {
                    // 在data/data/包名/中创建文件
                    File file = new File(getFilesDir(), name);
                    in = this.getClass().getResourceAsStream("/assets/" + name);
                    out = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len); // 文件不能多写 否则会出错的
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();

    }

    /**
     * 获取版本名
     *
     * @return
     */
    private String getVersionName() {
        return info.versionName;
    }

    /**
     * 获取版本号
     *
     * @return
     */
    private int getVersionCode() {
        return info.versionCode;
    }

    /**
     * 检查版本更新
     */
    private void checkVersionUpdate() {
        // 开启子线程去访问网络 防止ANR(android not Response)
        final long start = System.currentTimeMillis();

        new Thread() {
            HttpURLConnection conn = null;
            Message mess = null;

            public void run() {
                // 从服务器获取更新的json数据
                try {
                    mess = Message.obtain(); // 发送的消息
                    URL url = new URL("http://192.168.0.102/update.json");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);// 连接超时
                    conn.setReadTimeout(5000); // 读取超时
                    conn.connect();
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        // 响应成功 获取输入流
                        InputStream in = conn.getInputStream();
                        String result = StreamUtils.StringOfStream(in);
                        JSONObject jo = new JSONObject(result);
                        mVersionName = jo.getString("versionName");
                        mVersionCode = jo.getInt("versionCode");
                        mDescription = jo.getString("description");
                        mDownloadUrl = jo.getString("downloadUrl");
                        if (mVersionCode > getVersionCode()) {
                            // 需要更新
                            mess.what = REQUIRE_UPDATE;
                        } else {
                            enterHome();
                        }

                    }

                } catch (MalformedURLException e) {
                    // url异常
                    e.printStackTrace();
                    mess.what = URL_ERROR;
                } catch (IOException e) {
                    // 网络异常
                    e.printStackTrace();
                    mess.what = NET_ERROR;
                } catch (JSONException e) {
                    // 数据解析错误
                    e.printStackTrace();
                    mess.what = JSON_ERROR;
                } finally {
                    // 发送消息
                    long end = System.currentTimeMillis() - start;
                    if ((end) < 2500) {
                        try {
                            Thread.sleep(2500 - end);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (conn != null) {
                        conn.disconnect();
                        conn = null;
                    }
                    handler.sendMessage(mess);
                }
            }

        }.start();
    }

    /**
     * 显示更新对话框
     */
    protected void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("更新版本号:" + mVersionName);
        builder.setMessage(mDescription);
        builder.setPositiveButton("立即更新", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 更新操作 下载apk文件
                downloadApk();
            }
        });

        // 不更新
        builder.setNegativeButton("下次再说", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        // 当对话框取消时调用
        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });
        builder.show();
    }

    // 下载apk文件
    protected void downloadApk() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            final String target = Environment.getExternalStorageDirectory().getAbsolutePath() + "/update.apk";
            HttpUtils utils = new HttpUtils();
            utils.download(mDownloadUrl, target, new RequestCallBack<File>() {
                @Override
                public void onStart() {
                    super.onStart();
                    System.out.println(target);
                    Toast.makeText(SplashActivity.this, "开始下载", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    // 弹出通知栏
                    tv_progress = (TextView) SplashActivity.this.findViewById(R.id.tv_progress);
                    tv_progress.setVisibility(View.VISIBLE);
                    tv_progress.setText("下载进度:" + (current / total) * 100 + "%");
                    Toast.makeText(SplashActivity.this, "正在下载", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(ResponseInfo<File> arg0) {
                    Toast.makeText(SplashActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(arg0.result), "application/vnd.android.package-archive");
                    startActivityForResult(intent, 0);

                }

                @Override
                public void onFailure(HttpException arg0, String arg1) {
                    Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                }

            });
        }

    }

    // 进入主页面
    protected void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish(); // 在任务栈中移除
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        enterHome();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OffersManager.getInstance(this).onAppExit();
    }
}
