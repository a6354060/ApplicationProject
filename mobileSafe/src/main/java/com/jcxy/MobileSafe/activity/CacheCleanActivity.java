package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.bean.CacheBean;
import com.jcxy.MobileSafe.engine.AppInfoUtils;
import com.jcxy.MobileSafe.utils.UIUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class CacheCleanActivity extends Activity {
    private static final int UPDATE_CACHE_INFO = 1;
    private static final int UPDATE_ADAPTER = 2;
    private static final int UPDATE = 3;
    private ListView lv_cache;
    private Button btn_clean_all;
    private AppInfoUtils appInfoUtils;
    private List<PackageInfo> installedPackages;
    private PackageManager packageManager;
    private Method method;
    private List<CacheBean> list;
    private LinearLayout ll_wait_load;
    private TextView tv_cache_info;
    private long cacheMemory;
    private MyAdapter adapter;

    private Handler handler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);


            if (msg.what == UPDATE_CACHE_INFO) {
                tv_cache_info.setText("有" + list.size() + "个软件有缓存，总缓存大小为:" + Formatter.formatFileSize(CacheCleanActivity.this, cacheMemory));
            }
            if (msg.what == UPDATE_ADAPTER) {
                if (adapter == null) {
                    ll_wait_load.setVisibility(View.GONE);
                    adapter = new MyAdapter();
                    lv_cache.setAdapter(adapter);
                } else {
                    ll_wait_load.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clean);
        initUI();
        btn_clean_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 清理所有缓存
                 */
                Method[] methods = PackageManager.class.getMethods();
                for (Method method : methods) {
                    if ("freeStorageAndNotify".equals(method.getName())) {
                        try {
                            method.invoke(packageManager, Integer.MAX_VALUE, new IPackageDataObserver() {
                                @Override
                                public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

                                }

                                @Override
                                public IBinder asBinder() {
                                    return null;
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                list.clear();
                cacheMemory=0;
                if (adapter == null) {
                    ll_wait_load.setVisibility(View.GONE);
                    adapter = new MyAdapter();
                    adapter.notifyDataSetChanged();
                } else {
                    ll_wait_load.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
                UIUtils.showToast(CacheCleanActivity.this, "清理完成");
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        list.clear();
        cacheMemory=0;
        initDate();

    }

    public void back(View view) {
        finish();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        lv_cache = (ListView) findViewById(R.id.lv_cache);
        btn_clean_all = (Button) findViewById(R.id.btn_clean_all);
        ll_wait_load = (LinearLayout) findViewById(R.id.ll_wait_load);
        tv_cache_info = (TextView) findViewById(R.id.tv_cache_info);

        list = new ArrayList<CacheBean>();
        packageManager = getPackageManager();
        installedPackages = packageManager.getInstalledPackages(0);
        try {
            //通过反射获取到当前的方法
            method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 初始化数据
     */
    private void initDate() {
        /**
         * 清理所有监听的
         */

        ll_wait_load.setVisibility(View.VISIBLE);

        /**
         * 找到所有带缓存的应用
         */
        new Thread() {
            @Override
            public void run() {
                for (PackageInfo info : installedPackages) {
                    getCacheSize(info);
                }
            }
        }.start();

    }

    private void getCacheSize(PackageInfo info) {
        try {
            method.invoke(packageManager, info.packageName, new MyObserver(info));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MyObserver extends IPackageStatsObserver.Stub {

        private PackageInfo info;
        private CacheBean cacheBean;
        private String packageName;

        public MyObserver(PackageInfo info) {
            this.packageName = info.packageName;
            this.info = info;
        }

        public MyObserver() {
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            long cacheSize = pStats.cacheSize;

            if (cacheSize > 1024 * 12) {
                cacheBean = new CacheBean();
                cacheBean.setAppCache(cacheSize);
                cacheBean.setCacheIcon(info.applicationInfo.loadIcon(packageManager));
                cacheBean.setAppName(info.applicationInfo.loadLabel(packageManager).toString());
                cacheBean.setPackageName(packageName);
                cacheMemory += cacheSize;
                list.add(cacheBean);
                Message obtain = Message.obtain();
                obtain.what = UPDATE_ADAPTER;
                handler.sendMessage(obtain);
            }

        }
    }

    /**
     * 适配器
     */
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            Message obtain = Message.obtain();
            obtain.what = UPDATE_CACHE_INFO;
            handler.sendMessage(obtain);
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(CacheCleanActivity.this, R.layout.cache_item, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_cache_icon = (ImageView) convertView.findViewById(R.id.iv_cache_icon);
                viewHolder.tv_cache_size = (TextView) convertView.findViewById(R.id.tv_cache_size);
                viewHolder.tv_cache_name = (TextView) convertView.findViewById(R.id.tv_cache_name);
                viewHolder.iv_clean_cache = (ImageView) convertView.findViewById(R.id.iv_clean_cache);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final CacheBean cacheBean = list.get(position);
            viewHolder.iv_cache_icon.setImageDrawable(cacheBean.getCacheIcon());
            viewHolder.tv_cache_size.setText(Formatter.formatFileSize(CacheCleanActivity.this, cacheBean.getAppCache()));
            viewHolder.tv_cache_name.setText(cacheBean.getAppName());
            /**
             * 清理缓存的监听
             */
            viewHolder.iv_clean_cache.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * 跳到应用详情页面
                     */
                    Intent detail_intent = new Intent();
                    detail_intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
                    detail_intent.setData(Uri.parse("package:" + cacheBean.getPackageName()));
                    startActivity(detail_intent);
                }
            });
            return convertView;
        }
    }


    static class ViewHolder {
        ImageView iv_cache_icon;
        TextView tv_cache_size;
        ImageView iv_clean_cache;
        TextView tv_cache_name;
    }

}
