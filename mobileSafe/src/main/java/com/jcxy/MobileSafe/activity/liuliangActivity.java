package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.bean.AppInfo;
import com.jcxy.MobileSafe.engine.AppInfoUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

public class liuliangActivity extends Activity {
    @ViewInject(R.id.ll_wait_load)
    private LinearLayout ll_wait_load;

    @ViewInject(R.id.lv_flow_manage)
    private ListView lv_flow_manage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liuliang);
        initUI();
        initData();
    }


    private MyAdapter adapter;
    /**
     * 处理消息
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ll_wait_load.setVisibility(View.GONE);
            adapter = new MyAdapter();
            lv_flow_manage.setAdapter(adapter);
        }
    };

    /**
     * 初始化数据
     */
    public List<AppInfo> appInfos;

    private void initData() {
        final AppInfoUtils appInfoUtils = new AppInfoUtils(this);
        ll_wait_load.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                appInfos = appInfoUtils.getAppInfos();
                handler.sendEmptyMessage(0);
            }
        }.start();


    }

    /**
     * 初始化UI
     */

    private void initUI() {
        ViewUtils.inject(this);
    }

    public void back(View view) {
        finish();
    }


    /**
     * 数据适配器
     */
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return appInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return appInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolde viewHolde = null;
            if (convertView == null) {
                convertView = View.inflate(liuliangActivity.this, R.layout.flow_manage_item, null);
                viewHolde = new ViewHolde();
                viewHolde.iv_app_icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
                viewHolde.tv_flow_R = (TextView) convertView.findViewById(R.id.tv_flow_R);
                viewHolde.tv_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
                viewHolde.tv_flow_T = (TextView) convertView.findViewById(R.id.tv_flow_T);
                viewHolde.tv_flow_all_size = (TextView) convertView.findViewById(R.id.tv_flow_all_size);
                convertView.setTag(viewHolde);
            } else {
                Object tag = convertView.getTag();
                if (tag instanceof ViewHolde) {
                    viewHolde = (ViewHolde) tag;
                }
            }

            if (viewHolde != null) {
                AppInfo appInfo = appInfos.get(position);
                // icon
                viewHolde.tv_app_name.setText(appInfo.getAppName());
                viewHolde.iv_app_icon.setImageDrawable(appInfo.getIcon());
                long uidRxPackets = TrafficStats.getUidRxBytes(appInfo.getUid());
                long uidTxPackets = TrafficStats.getUidTxBytes(appInfo.getUid());
                viewHolde.tv_flow_R.setText("下载:" + Formatter.formatFileSize(liuliangActivity.this, uidRxPackets));
                viewHolde.tv_flow_T.setText("上传:" + Formatter.formatFileSize(liuliangActivity.this, uidTxPackets));
                viewHolde.tv_flow_all_size.setText("总共:"+Formatter.formatFileSize(liuliangActivity.this, uidRxPackets + uidTxPackets));
            }
            return convertView;
        }
    }

    static class ViewHolde {
        ImageView iv_app_icon;
        TextView tv_app_name;
        TextView tv_flow_T;
        TextView tv_flow_R;
        TextView tv_flow_all_size;
    }


}
