package com.jcxy.MobileSafe.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.bean.AppInfo;
import com.jcxy.MobileSafe.db.dao.ApplockDao;
import com.jcxy.MobileSafe.engine.AppInfoUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hp on 2016/10/12.
 */

public class LockFragment extends Fragment {

    private ListView lockListView;
    private TextView tv_lock_count;
    private ApplockDao dao;
    private ArrayList<AppInfo> appLockList;
    private AppInfoUtils apputil;
    private LinearLayout ll_wait_load;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ll_wait_load.setVisibility(View.INVISIBLE);
            lockListView.setVisibility(View.VISIBLE);
            System.out.println("jsldfjsldkfjsdlk");
            if (appLockList != null && appLockList.size() > 0) {
                System.out.println(appLockList.size() + "gggggg");
                adapter = new MyLockAdapter();
                lockListView.setAdapter(adapter);
            }
        }
    };
    private MyLockAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.lock_fragment, null);
        lockListView = (ListView) inflate.findViewById(R.id.lv_lock);
        tv_lock_count = (TextView) inflate.findViewById(R.id.tv_lock_count);
        ll_wait_load = (LinearLayout) inflate.findViewById(R.id.ll_wait_load);
        return inflate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dao = new ApplockDao(getActivity());
        appLockList = new ArrayList<AppInfo>();
        apputil = new AppInfoUtils(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        ll_wait_load.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                List<AppInfo> appInfos = apputil.getAppInfos();
                for (AppInfo a : appInfos) {
                    boolean b = dao.findByPackageName(a.getPackageName());
                    if (b) {
                        appLockList.add(a);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();


    }

    /**
     * 数据适配器
     */
    private class MyLockAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            tv_lock_count.setText("已加锁的有(" + appLockList.size() + ")个");
            return appLockList.size();
        }

        @Override
        public Object getItem(int position) {
            return appLockList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            final View view;
            if (convertView == null) {
                view = View.inflate(getActivity(), R.layout.lock_fragment_item, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_lock_app_icon = (ImageView) view.findViewById(R.id.iv_lock_app_icon);
                viewHolder.tv_lock_app_name = (TextView) view.findViewById(R.id.tv_lock_app_name);
                viewHolder.iv_lock_icon = (ImageView) view.findViewById(R.id.iv_lock_icon);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            final AppInfo appInfo = appLockList.get(position);
            viewHolder.iv_lock_app_icon.setBackground(appInfo.getIcon());

            viewHolder.tv_lock_app_name.setText(appInfo.getAppName());

            viewHolder.iv_lock_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 删除枷锁的数据库
                    dao.delete(appInfo.getPackageName());
                    // 开启位移动画
                    TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1.0f,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                    translateAnimation.setDuration(500);
                    view.startAnimation(translateAnimation);

                    new Thread() {
                        @Override
                        public void run() {
                            SystemClock.sleep(500);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    appLockList.remove(appInfo);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }.start();


                }
            });

            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_lock_app_icon;
        TextView tv_lock_app_name;
        ImageView iv_lock_icon;
    }

}
