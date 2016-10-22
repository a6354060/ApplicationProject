package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.bean.TaskInfo;
import com.jcxy.MobileSafe.engine.TaskInfos;
import com.jcxy.MobileSafe.utils.SystemUtils;
import com.jcxy.MobileSafe.utils.UIUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class TaskManagerActivity extends Activity implements View.OnClickListener {

    @ViewInject(R.id.tv_memory)
    private TextView tv_memory;

    @ViewInject(R.id.tv_running_task)
    private TextView tv_running_task;

    @ViewInject(R.id.lv_task)
    private ListView lv_task;

    @ViewInject(R.id.ll_wait_load)
    private LinearLayout ll_wait_load;

    @ViewInject(R.id.tv_app_item_top)
    private TextView tv_app_item_top;

    @ViewInject(R.id.btn_all_select)
    private Button btn_all_select;

    @ViewInject(R.id.btn_back_select)
    private Button btn_back_select;

    @ViewInject(R.id.btn_clean)
    private Button btn_clean;

    @ViewInject(R.id.btn_setting)
    private Button btn_setting;

    private static MyTaskAdapter adapter;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            taskCount = taskInfos.getTaskCount();

            getRunningProcessCount(taskCount);
            // 处理消息
            if (tasks == null) {
                return;
            }
            adapter = new MyTaskAdapter();
            lv_task.setAdapter(adapter);

            ll_wait_load.setVisibility(View.INVISIBLE);



            /**
             *为按钮添加监听器
             */
            btn_all_select.setOnClickListener(TaskManagerActivity.this);
            btn_back_select.setOnClickListener(TaskManagerActivity.this);
            btn_clean.setOnClickListener(TaskManagerActivity.this);
            btn_setting.setOnClickListener(TaskManagerActivity.this);

            /**
             * 监听TaskList滚动事件
             */
            lv_task.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (firstVisibleItem == 0) {
                        tv_app_item_top.setText("用户进程:" + userTaskInfo.size() + "个");
                    } else if (firstVisibleItem >= userTaskInfo.size() + 2) {
                        tv_app_item_top.setText("系统进程:" + systemTaskInfo.size() + "个");
                    } else if (firstVisibleItem <= userTaskInfo.size() + 1) {
                        tv_app_item_top.setText("用户进程:" + userTaskInfo.size() + "个");
                    }
                }
            });

            lv_task.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object o = lv_task.getItemAtPosition(position);
                    if (o != null && o instanceof TaskInfo) {
                        ViewHolder h = (ViewHolder) view.getTag();
                        TaskInfo info = (TaskInfo) o;
                        // 自己的进程不能被杀死
                        if(getPackageName().equals(info.getProcessName())){
                              return;
                         }

                        if (info.isChecked()) {
                            info.setChecked(false);
                            h.cb_task_stat.setChecked(false);

                        } else {
                            info.setChecked(true);
                            h.cb_task_stat.setChecked(true);
                        }

                    }


                }
            });

        }
    };


    private List<TaskInfo> tasks;
    private ArrayList<TaskInfo> systemTaskInfo;
    private ArrayList<TaskInfo> userTaskInfo;
    private TaskInfos taskInfos;
    private Long freeMemory;
    private Long totalMemory;
    private int taskCount;
    private SharedPreferences config;


    /**
     * 处理点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_all_select:
                //全选处理
                selectAll();
                break;
            case R.id.btn_back_select:
                // 反选处理
                backSelect();
                break;
            case R.id.btn_clean:
                cleanProcess();
                // 清理进程处理
                break;
            case R.id.btn_setting:
                //设置处理
                OpenSetting();
                break;
        }


    }

    /**
     * 任务管理设置
     */
    private void OpenSetting() {
        Intent intent = new Intent(this, TaskManagerSettingActivity.class);
        startActivity(intent);
    }

    /**
     * 清理进程
     */
    private void cleanProcess() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int count = 0; // 杀死进程的个数
        int size = 0;  // 释放内存的大小
        boolean is_show_system = config.getBoolean("is_show_system", false);
        // 判断是否显示系统进程
        if(is_show_system) {
            //用户进程
            Iterator<TaskInfo> iterator1 = userTaskInfo.iterator();
            TaskInfo useInfo = null;
            /**
             * 当我们迭代集合是 是不能修改集合的大小的 我们可以用迭代器本身进行删除里面进行了了处理
             * 如果只删除一个迭代删除也行
             */
            while (iterator1.hasNext()) {
                useInfo = iterator1.next();
                if (useInfo.isChecked()) {

                    iterator1.remove();
                    count++;
                    size += useInfo.getProcessSize();
                    activityManager.killBackgroundProcesses(useInfo.getPackName());

                }
            }
            // 系统进程
            Iterator<TaskInfo> iterator = systemTaskInfo.iterator();
            TaskInfo sysInfo = null;
            while (iterator.hasNext()) {
                sysInfo = iterator.next();
                if (sysInfo.isChecked()) {
                    iterator.remove();
                    count++;
                    size += sysInfo.getProcessSize();
                    activityManager.killBackgroundProcesses(sysInfo.getPackName());
                }
            }
        }else {
            //用户进程
            Iterator<TaskInfo> iterator1 = userTaskInfo.iterator();
            TaskInfo useInfo = null;
            /**
             * 当我们迭代集合是 是不能修改集合的大小的 我们可以用迭代器本身进行删除里面进行了了处理
             * 如果只删除一个迭代删除也行
             */

            while (iterator1.hasNext()) {
                useInfo = iterator1.next();
                if (useInfo.isChecked()) {

                    iterator1.remove();
                    count++;
                    size += useInfo.getProcessSize();
                    activityManager.killBackgroundProcesses(useInfo.getPackName());

                }
            }


        }
        String formatFileSize = Formatter.formatFileSize(this, size);
        UIUtils.showToast(this, "共清理了" + count + "个进程！ 释放了" + formatFileSize + "内存");
        freeMemory += size;
        getMemory(freeMemory, totalMemory);
        taskCount -= count;
        getRunningProcessCount(taskCount);
        adapter.notifyDataSetChanged();
    }

    /**
     * 反选所有
     */
    private void backSelect() {

        for (TaskInfo info : userTaskInfo) {
            if (info.getPackName().equals(getPackageName()))
                continue;
            info.setChecked(!info.isChecked());
        }

        for (TaskInfo i : systemTaskInfo) {
            i.setChecked(!i.isChecked());
        }
        // 数据更新了 通知Adapter更新UI
        adapter.notifyDataSetChanged();
    }

    /**
     * 选择所有
     */
    private void selectAll() {
        boolean is_show_system = config.getBoolean("is_show_system", false);
        for (TaskInfo info : userTaskInfo) {
            if (info.getPackName().equals(getPackageName()))
                continue;
            info.setChecked(true);
        }

      if(is_show_system) {
          for (TaskInfo i : systemTaskInfo) {
              i.setChecked(true);
          }
      }
        // 数据更新了 通知Adapter更新UI
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        initUI();

        ininData();
    }


    /**
     * 返回键
     * @param view
     */
    public void back(View view){
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 初始化数据
     */
    private void ininData() {
        freeMemory = SystemUtils.getFreeMemory(this);
        totalMemory = SystemUtils.getTotalMemory(this);
        getMemory(freeMemory, totalMemory);
        taskInfos = new TaskInfos(this);
        ll_wait_load.setVisibility(View.VISIBLE);
        //初初始化适配器
        new Thread() {
            @Override
            public void run() {
                tasks = taskInfos.getTasks();
                systemTaskInfo = new ArrayList<TaskInfo>();
                userTaskInfo = new ArrayList<TaskInfo>();
                for (TaskInfo taskInfo : tasks) {

                    if (taskInfo.getSystemProcess()) {
                        // 系统App
                        systemTaskInfo.add(taskInfo);
                    } else {
                        //用户App
                        userTaskInfo.add(taskInfo);
                    }
                }


                handler.sendEmptyMessage(0);
            }
        }.start();


    }

    private void getRunningProcessCount(int taskCount) {

        if (!TextUtils.isEmpty(String.valueOf(taskCount))) {
            tv_running_task.setText("运行中的进程:" + taskCount + "个");
        }
    }

    /**
     * 得到内存消息
     */
    private void getMemory(long freeMemory, long totalMemory) {

        String free = Formatter.formatFileSize(this, freeMemory);
        String total = Formatter.formatFileSize(this, totalMemory);

        if (!TextUtils.isEmpty(free) && !TextUtils.isEmpty(total)) {
            tv_memory.setText("内存:剩余/总共 " + free + "/" + total);
        }
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        ViewUtils.inject(this);
        config = getSharedPreferences("config", MODE_PRIVATE);


    }


    /**
     * task适配器
     */

    private class MyTaskAdapter extends BaseAdapter {

        /**
         * 控制listView显示的个数
         *
         * @return
         */
        @Override
        public int getCount() {
            if (config.getBoolean("is_show_system", false)) {
                return userTaskInfo.size() + 1 + systemTaskInfo.size() + 1;
            } else {
                return userTaskInfo.size() + 1;
            }

        }

        @Override
        public Object getItem(int position) {
            // 处理俩个特殊位置
            if (position == 0) {
                return null;
            } else if (position == userTaskInfo.size() + 1) {
                return null;
            }

            if (position > userTaskInfo.size() + 1) {
                // 还要减去特殊位置
                return systemTaskInfo.get(position - 1 - (userTaskInfo.size() + 1));
            } else {
                return userTaskInfo.get(position - 1);
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
                TextView tv = new TextView(TaskManagerActivity.this);
                tv.setTextSize(1);
                return tv;


            } else if (position == userTaskInfo.size() + 1) {
                // 返回系统APP信息
                TextView tv = new TextView(TaskManagerActivity.this);
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setText("系统进程：" + systemTaskInfo.size() + "个");
                return tv;

            }


            ViewHolder holder;
            if (convertView != null && convertView instanceof LinearLayout) {

                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(TaskManagerActivity.this, R.layout.task_info_item, null);
                holder = new ViewHolder();
                holder.iv_task_icon = (ImageView) convertView.findViewById(R.id.iv_task_icon);
                holder.tv_task_size = (TextView) convertView.findViewById(R.id.tv_task_size);
                holder.cb_task_stat = (CheckBox) convertView.findViewById(R.id.cb_task_stat);
                holder.tv_task_name = (TextView) convertView.findViewById(R.id.tv_task_name);
                convertView.setTag(holder);

            }
            TaskInfo taskInfo = null;
            if (position > userTaskInfo.size() + 1) {
                taskInfo = systemTaskInfo.get(position - 1 - (userTaskInfo.size() + 1));
            } else {
                taskInfo = userTaskInfo.get(position - 1);
            }

            // 自己的进程不能被杀死
            if (getPackageName().equals(taskInfo.getPackName())) {
                holder.cb_task_stat.setVisibility(View.GONE);
            }

            //设置勾选状态
            if (taskInfo.isChecked()) {
                holder.cb_task_stat.setChecked(true);
            } else {
                holder.cb_task_stat.setChecked(false);
            }
            String appName = taskInfo.getProcessName();
            if (!TextUtils.isEmpty(appName))
                holder.tv_task_name.setText(appName);

            String appSize = Formatter.formatFileSize(TaskManagerActivity.this, taskInfo.getProcessSize());
            if (!TextUtils.isEmpty(appSize))
                holder.tv_task_size.setText("占用内存:" + appSize);

            Drawable icon = taskInfo.getIcon();
            if (icon != null)
                holder.iv_task_icon.setImageDrawable(icon);


            return convertView;
        }


    }

    /**
     * hodler类
     */
    static class ViewHolder {
        ImageView iv_task_icon;
        TextView tv_task_size;
        TextView tv_task_name;
        CheckBox cb_task_stat;
    }


    /**
     * 接受定时器发来的广播
     */
   public static class MyTaskReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
              if (adapter!=null){
                  adapter.notifyDataSetChanged();
              }

        }
    }

}
