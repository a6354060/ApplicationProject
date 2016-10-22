package com.jcxy.MobileSafe.receive;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;
import android.widget.Toast;

import com.jcxy.MobileSafe.bean.TaskInfo;
import com.jcxy.MobileSafe.engine.TaskInfos;

import java.util.Iterator;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class KillAllProcessReceiver extends BroadcastReceiver {
    public KillAllProcessReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        ActivityManager activityManager = (ActivityManager)context.getSystemService(ACTIVITY_SERVICE);
        int count = 0; // 杀死进程的个数
        int size = 0;  // 释放内存的大小
        TaskInfos taskInfos= new TaskInfos(context);
        List<TaskInfo> tasks = taskInfos.getTasks();
        Iterator<TaskInfo> iterator = tasks.iterator();
            TaskInfo sysInfo = null;
            while (iterator.hasNext()) {
                sysInfo = iterator.next();

                    iterator.remove();
                    count++;
                    size += sysInfo.getProcessSize();
                    activityManager.killBackgroundProcesses(sysInfo.getPackName());
            }

        String formatFileSize = Formatter.formatFileSize(context, size);
       Toast.makeText(context, "共清理了" + count + "个进程！ 释放了" + formatFileSize + "内存",Toast.LENGTH_SHORT).show();
    }
}
