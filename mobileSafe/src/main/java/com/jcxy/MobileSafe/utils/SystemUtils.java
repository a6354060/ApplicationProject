package com.jcxy.MobileSafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.wenming.library.processutil.ProcessManager;

import java.util.List;

/**
 * Created by hp on 2016/9/29.
 */

public class SystemUtils {

    /**
     * 得到系统剩余内存大小
     */

     public static Long getFreeMemory(Context context){

         ActivityManager activityManager= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
         ActivityManager.MemoryInfo memoryInfo=new ActivityManager.MemoryInfo();
         activityManager.getMemoryInfo(memoryInfo);
         return memoryInfo.availMem;
     }

    /**
     * 得到系统总共的内存大小
     *
     */

    public static Long getTotalMemory(Context context){

        ActivityManager activityManager= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo=new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        return memoryInfo.totalMem;
    }

    /**
     * 运行的进程个数
     */
    public static int getRunningCount(Context context){

        return ProcessManager.getRunningAppProcesses().size();
    }


    /**
     * 杀死所有运行的进程
     */
    public static void killAllProcess(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo rap : appProcesses) {
            // 要保证自己不能被杀死
            if(!rap.processName.equals(context.getPackageName())){
                activityManager.killBackgroundProcesses(rap.processName);
            }

        }
    }
}
