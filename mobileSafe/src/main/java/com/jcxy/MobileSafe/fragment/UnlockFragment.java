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

import static com.jcxy.MobileSafe.R.id.ll_wait_load;


public class UnlockFragment extends Fragment {

    private ListView unlockList;
    private View view;
    private TextView tv_unlock_count;
    private MyAdapter adapter;
    private List<AppInfo> appInfos;
    private AppInfoUtils appInfoUtils;
    private LinearLayout ll_wait;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            unlockList.setVisibility(View.VISIBLE);
            ll_wait.setVisibility(View.INVISIBLE);
            adapter = new MyAdapter();
            unlockList.setAdapter(adapter);
        }
    };
    private ArrayList<AppInfo> appNuLockList;
    private ApplockDao dao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 给碎片的布局视图
        view = inflater.inflate(R.layout.un_lock_fragment, null);
        unlockList = (ListView) view.findViewById(R.id.lv_unlock);
        tv_unlock_count = (TextView) view.findViewById(R.id.tv_unlock_count);
        ll_wait = (LinearLayout) view.findViewById(ll_wait_load);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appNuLockList = new ArrayList<AppInfo>();
        appInfoUtils = new AppInfoUtils(getActivity());
        dao = new ApplockDao(getActivity());
    }

    @Override
    public void onStart() {
        unlockList.setVisibility(View.INVISIBLE);
        ll_wait.setVisibility(View.VISIBLE);
        new Thread() {

            @Override
            public void run() {
                appInfos = appInfoUtils.getAppInfos();
                for (AppInfo infos : appInfos) {
                    boolean b = dao.findByPackageName(infos.getPackageName());
                    if (!b) {
                        appNuLockList.add(infos);
                    }
                }

                handler.sendEmptyMessage(0);
            }
        }.start();
        super.onStart();
    }

    /**
     * 数据适配器
     */
    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            tv_unlock_count.setText("未加锁的有(" + appNuLockList.size() + ")个");
            return appNuLockList.size();
        }

        @Override
        public Object getItem(int position) {
            return appNuLockList.get(position);
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
                view = View.inflate(getActivity(), R.layout.unlock_fragmetn_item, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_unlock_app_icon = (ImageView) view.findViewById(R.id.iv_unlock_app_icon);
                viewHolder.tv_unlock_app_name = (TextView) view.findViewById(R.id.tv_unlock_app_name);
                viewHolder.iv_lock_icon = (ImageView) view.findViewById(R.id.iv_lock_icon);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            final AppInfo appInfo = appNuLockList.get(position);

            viewHolder.iv_unlock_app_icon.setBackground(appInfo.getIcon());

            viewHolder.tv_unlock_app_name.setText(appInfo.getAppName());

            viewHolder.iv_lock_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 加入枷锁的数据库
                    dao.addApp(appInfo.getPackageName());
                    // 开启位移动画
                    TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1.0f,
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
                                    appNuLockList.remove(appInfo);
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
        ImageView iv_unlock_app_icon;
        TextView tv_unlock_app_name;
        ImageView iv_lock_icon;
    }

}
