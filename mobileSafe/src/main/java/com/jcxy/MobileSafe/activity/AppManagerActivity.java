package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.bean.AppInfo;
import com.jcxy.MobileSafe.engine.AppInfoUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.stericson.RootTools.RootTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class AppManagerActivity extends Activity implements View.OnClickListener {
    @ViewInject(R.id.lv_applist)
    private ListView lv_applist;

    @ViewInject(R.id.tv_rom_info)
    private TextView tv_rom_info;

    @ViewInject(R.id.tv_sd_info)
    private TextView tv_sd_info;

    @ViewInject(R.id.ll_wait_load)
    private LinearLayout ll_wait_load;

    @ViewInject(R.id.ll_memory)
    private LinearLayout ll_memory;

    @ViewInject(R.id.tv_app_item_top)
    private TextView tv_app_item_top;

    private MyAppListAdapter Adapter;
    /**
     * 所有的安装的应用的消息
     */
    private List<AppInfo> appInfos;

    /**
     * 用户应用信息
     */
    private List<AppInfo> userAppInfo;

    /**
     * 系统应用信息
     */
    private List<AppInfo> systemAppInfo;
    private UninstallReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initView();

        initData();
        /**
         * 注册卸载软件的广播
         */
        receiver = new UninstallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        // 必须指定卸载模式 通过包卸载
        filter.addDataScheme("package");
        registerReceiver(receiver, filter);
    }

    /**
     * 返回键
     * @param view
     */
    public void back(View view){
        finish();
    }

    private PopupWindow popupWindow;
    private AppInfo clickAppInfo;
    /**
     * 消息处理类
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            lv_applist.setVisibility(View.VISIBLE);
            tv_app_item_top.setVisibility(View.VISIBLE);
            ll_wait_load.setVisibility(View.GONE);

            if (systemAppInfo != null && systemAppInfo.size() > 0 && userAppInfo != null && userAppInfo.size() > 0) {
                Adapter = new MyAppListAdapter(systemAppInfo, userAppInfo);
                lv_applist.setAdapter(Adapter);
            }

            /**
             * 监听AppList滚动事件
             */
            lv_applist.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    dismissPopwindow();
                    if (firstVisibleItem == 0) {
                        tv_app_item_top.setText("用户程序:" + userAppInfo.size() + "个");
                    } else if (firstVisibleItem >= userAppInfo.size() + 2) {
                        tv_app_item_top.setText("系统程序:" + systemAppInfo.size() + "个");
                    } else if (firstVisibleItem <= userAppInfo.size()+1) {
                        tv_app_item_top.setText("用户程序:" + userAppInfo.size() + "个");
                    }
                }
            });

            /**
             * 监听点击AppListItem事件
             */
            final View inflate = View.inflate(AppManagerActivity.this, R.layout.pop_app_item, null);

            lv_applist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Object itemAtPosition = lv_applist.getItemAtPosition(position);
                    if (itemAtPosition instanceof AppInfo) {
                        clickAppInfo = (AppInfo) itemAtPosition;
                        // 弹出窗体对话框
                        dismissPopwindow();
                        popupWindow = new PopupWindow(inflate, -2, -2);
// 动画播放有一个前提条件： 窗体必须要有背景资源。 如果窗体没有背景，动画就播放不出来。
                        popupWindow.setBackgroundDrawable(new ColorDrawable(
                                Color.TRANSPARENT));
                        int[] laction = new int[2];
                        // 获取view 距离窗口的高度
                        view.getLocationInWindow(laction);
                        popupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 100, laction[1]);

                        // 动画处理
                        ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f,
                                1.0f, Animation.RELATIVE_TO_SELF, 0,
                                Animation.RELATIVE_TO_SELF, 0.5f);
                        sa.setDuration(200);
                        AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
                        aa.setDuration(200);
                        AnimationSet set = new AnimationSet(false);
                        set.addAnimation(aa);
                        set.addAnimation(sa);
                        inflate.startAnimation(set);


                        LinearLayout ll_uninstall = (LinearLayout) inflate.findViewById(R.id.ll_uninstall);

                        LinearLayout ll_share = (LinearLayout) inflate.findViewById(R.id.ll_share);

                        LinearLayout ll_start_app = (LinearLayout) inflate.findViewById(R.id.ll_start_app);

                        LinearLayout ll_detail = (LinearLayout) inflate.findViewById(R.id.ll_detail);


                        //如果处理很多点击事件 这个方式不错
                        ll_uninstall.setOnClickListener(AppManagerActivity.this);
                        ll_start_app.setOnClickListener(AppManagerActivity.this);
                        ll_share.setOnClickListener(AppManagerActivity.this);
                        ll_detail.setOnClickListener(AppManagerActivity.this);


                    }
                }
            });


        }
    };

    /**
     * 关闭popWindow
     */
    public void dismissPopwindow() {

        if (popupWindow != null && popupWindow.isShowing()) {
        popupWindow.dismiss();
    }
}


    /**
     * 处理窗口的点击事件
      */

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_uninstall:
                // 卸载App
                uninstallApplication();
                dismissPopwindow();
                break;
            case R.id.ll_start_app:
                // 运行app
                startApplication();
                dismissPopwindow();
                break;
            case R.id.ll_share:
                // 分享app
                 shareApp();
                 dismissPopwindow();
                break;
            case R.id.ll_detail:
                // 查看详情
                lookAppDetail();
                dismissPopwindow();
                break;

        }

    }

    /**
     * 查看App详细信息
     */
    private void lookAppDetail() {
        Intent detail_intent = new Intent();
        detail_intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
        detail_intent.setData(Uri.parse("package:" + clickAppInfo.getPackageName()));
        startActivity(detail_intent);
    }

    /**
     * 分享App
     */
    private void shareApp() {
        Intent share_app=new Intent();
        share_app.setAction(Intent.ACTION_SEND);
        share_app.addCategory(Intent.CATEGORY_DEFAULT);
        share_app.setType("text/plain");
        share_app.putExtra("android.intent.extra.SUBJECT", "f分享");
        share_app.putExtra("android.intent.extra.TEXT",
                "Hi！推荐您使用软件：" + clickAppInfo.getPackageName() + "下载地址:" + "https://play.google.com/store/apps/details?id=" + clickAppInfo.getPackageName());
        this.startActivity(Intent.createChooser(share_app, "分享"));
    }


    /**
     * 开启应用程序
     */
    private void startApplication() {
        // 打开这个应用程序的入口activity。
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(clickAppInfo
                .getPackageName());
        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "该应用没有启动界面", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 卸载软件
     */
    private void uninstallApplication() {
        if (!clickAppInfo.getSystemApp()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + clickAppInfo.getPackageName()));
            startActivity(intent);
        }else{
            //系统应用 ，root权限 利用linux命令删除文件。
            if(!RootTools.isRootAvailable()){
                Toast.makeText(this, "卸载系统应用，必须要root权限", Toast.LENGTH_SHORT).show();
                return ;
            }
            try {
                if(!RootTools.isAccessGiven()){
                    Toast.makeText(this, "请授权手机卫士root权限", Toast.LENGTH_SHORT).show();
                    return ;
                }
                RootTools.sendShell("mount -o remount ,rw /system", 3000);
                RootTools.sendShell("rm -r "+clickAppInfo.getApkpath(), 30000);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }



    /**
     * 初始化数据
     */
    private void initData() {

        lv_applist.setVisibility(View.GONE);
        ll_wait_load.setVisibility(View.VISIBLE);
        tv_app_item_top.setVisibility(View.GONE);

        final AppInfoUtils appInfoUtils = new AppInfoUtils(this);
        getMemoryAndSDcard(); // 获取内存信息

        // 开启子线程处理加载数据
        new Thread() {
            @Override
            public void run() {
                // 耗时操作
                appInfos = appInfoUtils.getAppInfos();

                systemAppInfo = new ArrayList<AppInfo>();
                userAppInfo = new ArrayList<AppInfo>();
                for (AppInfo appInfo : appInfos) {

                    if (appInfo.getSystemApp()) {
                        // 系统App
                        systemAppInfo.add(appInfo);
                    } else {
                        //用户App
                        userAppInfo.add(appInfo);
                    }
                }


                handler.sendEmptyMessage(0);

            }
        }.start();


    }


    private class MyAppListAdapter extends BaseAdapter {
        List<AppInfo> userApp;
        List<AppInfo> systemrApp;

        public MyAppListAdapter(List<AppInfo> systemrApp, List<AppInfo> userApp) {
            this.userApp = userApp;
            this.systemrApp = systemrApp;
        }


        @Override
        public int getCount() {
            return userApp.size() + 1 + systemrApp.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            // 处理俩个特殊位置
            if (position == 0) {
                return null;
            } else if (position == userApp.size() + 1) {
                return null;
            }

            if (position > userApp.size() + 1) {
                // 还要减去特殊位置
                return systemrApp.get(position - 1 - (userApp.size() + 1));
            } else {
                return userApp.get(position - 1);
            }

        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (position == 0) {
                // 返回用户APP信息
                TextView tv = new TextView(AppManagerActivity.this);
                //tv.setBackgroundColor(Color.GRAY);
                // tv.setTextColor(Color.WHITE);
//                tv.setText("用户程序：" + userApp.size() + "个");
//                tv.setVisibility(View.GONE);
                tv.setTextSize(1);
                return tv;


            } else if (position == userApp.size() + 1) {
                // 返回系统APP信息
                TextView tv = new TextView(AppManagerActivity.this);
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setText("系统程序：" + systemrApp.size() + "个");
                return tv;

            }


            ViewHolder holder;
            if (convertView != null && convertView instanceof LinearLayout) {

                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(AppManagerActivity.this, R.layout.app_manager_item, null);
                holder = new ViewHolder();
                holder.iv_app_icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
                holder.tv_app_size = (TextView) convertView.findViewById(R.id.tv_app_size);
                holder.tv_app_category = (TextView) convertView.findViewById(R.id.tv_app_category);
                holder.tv_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
                convertView.setTag(holder);

            }
            AppInfo appInfo = null;
            if (position >= userApp.size() + 1) {
                appInfo = systemrApp.get(position - 1 - (userApp.size() + 1));
            } else {
                appInfo = userApp.get(position - 1);
            }


            String appName = appInfo.getAppName();
            if (!TextUtils.isEmpty(appName))
                holder.tv_app_name.setText(appName);

            String appSize = Formatter.formatFileSize(AppManagerActivity.this, appInfo.getAppSize());
            if (!TextUtils.isEmpty(appSize))
                holder.tv_app_size.setText(appSize);

            Drawable icon = appInfo.getIcon();
            if (icon != null)
                holder.iv_app_icon.setImageDrawable(icon);

            if (appInfo.getRom()) {
                // 手机内存
                holder.tv_app_category.setText("手机内存");

            } else {
                // sd卡内存
                holder.tv_app_category.setText("sd卡内存");

            }

            return convertView;
        }


    }

    /**
     * hodler类
     */
    static class ViewHolder {
        ImageView iv_app_icon;
        TextView tv_app_size;
        TextView tv_app_category;
        TextView tv_app_name;
    }

    /**
     * 得到手机内存和sd卡信息
     */
    private void getMemoryAndSDcard() {
        // 得到手机内存消息
        File dataDirectory = Environment.getDataDirectory();
        // SD卡的
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        // 总内存
        long totalSpace = dataDirectory.getTotalSpace();
        // 剩余内存
        long freeSpace = dataDirectory.getFreeSpace();

        // 格式化一下
        String sTotalSpace = Formatter.formatFileSize(this, totalSpace);
        String sFreeSpace = Formatter.formatFileSize(this, freeSpace);

        if (!TextUtils.isEmpty(sTotalSpace) && !TextUtils.isEmpty(sFreeSpace)) {
            tv_rom_info.setText("手机内存:(剩余/总共)" + sFreeSpace + "/" + sTotalSpace);
        }

        // 得到SD卡的信息

        long SDtotalSpace = externalStorageDirectory.getTotalSpace();
        long SDfreeSpace = externalStorageDirectory.getFreeSpace();

        // 格式化一下
        String sSdTotalSpace = Formatter.formatFileSize(this, SDtotalSpace);
        String sSdFreeSpace = Formatter.formatFileSize(this, SDfreeSpace);

        if (!TextUtils.isEmpty(sTotalSpace) && !TextUtils.isEmpty(sFreeSpace)) {
            tv_sd_info.setText("sd卡内存:(剩余/总共)" + sSdFreeSpace + "/" + sSdTotalSpace);
        }
    }

    /**
     * 初始化View
     */
    private void initView() {
        // 通过注解注入
        ViewUtils.inject(this);
    }

    /**
     * 软件卸载时候的广播
     */
    private class UninstallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
             //如果卸载数据包 马上更新数据
            System.out.println("onReceive");
            initData();
        }
    }


    @Override
    protected void onDestroy() {
        dismissPopwindow();
        if(receiver!=null){
            unregisterReceiver(receiver);
            receiver=null;
        }
        super.onDestroy();
    }
}
