package com.jcxy.MobileSafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.text.format.Formatter;
import android.widget.Toast;

import com.jcxy.MobileSafe.bean.TaskInfo;
import com.wenming.library.processutil.ProcessManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016/9/29.
 */

public class TaskInfos {
    private Context context;
    private ActivityManager manager;
    private PackageManager pm;
    private TaskInfo info;
    List<TaskInfo> taskInfoList ;
    private List<ActivityManager.RunningAppProcessInfo> list;

    public TaskInfos(Context context) {
        this.context = context;
        manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        pm = context.getPackageManager();
        list = new ArrayList<ActivityManager.RunningAppProcessInfo>();
    }

    /**
     * 得到所有的运行的进程
     */

    public List<TaskInfo> getTasks() {

        list =ProcessManager.getRunningAppProcessInfo(context);

        if (list != null && list.size() > 0) {
            taskInfoList = new ArrayList<TaskInfo>();
            for (ActivityManager.RunningAppProcessInfo rap : list) {
                try {

                    info = new TaskInfo();
                    // 进程的名字
                    String processName = rap.processName;
                    PackageInfo packageInfo = pm.getPackageInfo(processName, 0);

                    String packageName = packageInfo.applicationInfo.packageName;
                    info.setPackName(packageName);

                    // 设置进程占用的大小
                    Debug.MemoryInfo[] processMemoryInfo = manager.getProcessMemoryInfo(new int[]{rap.pid});
                    // 乘以1024是吧KB转换为B 方便格式化
                    int totalMem = processMemoryInfo[0].getTotalPrivateDirty() * 1024;
                    info.setProcessSize(totalMem);


                    int flags = packageInfo.applicationInfo.flags;
                    //ApplicationInfo.FLAG_SYSTEM 表示系统应用程序
                    if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        //系统应用
                        info.setSystemProcess(true);
                    } else {
//					用户应用
                        info.setSystemProcess(false);

                    }

                    // 设置图标
                    Drawable drawable = packageInfo.applicationInfo.loadIcon(pm);
                    info.setIcon(drawable);
                    // 设置进程的名字
                    String label = packageInfo.applicationInfo.loadLabel(pm).toString();
                    info.setProcessName(label);
                    info.setChecked(false);
                    taskInfoList.add(info);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }

        return taskInfoList;
    }


    /**
     * 得到运行的进程的数量
     */

    public int getTaskCount() {
        return taskInfoList.size();
    }

    /**
     * 得到用户进程的运行个数
     */

    public int getUserTaskCount() {

        return 0;
    }

    public  void killAllProcess(){
        List<TaskInfo> tasks = getTasks();
        int memeory=0;
        int count=0;
        ActivityManager manager= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for ( TaskInfo info: tasks) {
            String packName = info.getPackName();
            if(context.getPackageName().equals(packName)){
                continue;
            }
            memeory+=info.getProcessSize();
            count++;
            manager.killBackgroundProcesses(packName);
        }
        Toast.makeText(context,"共清理了"+count+"个进程,释放内存"+ Formatter.formatFileSize(context,memeory),Toast.LENGTH_SHORT).show();
        manager=null;
        tasks=null;
    }
}
