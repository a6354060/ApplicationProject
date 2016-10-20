package com.jcxy.MobileSafe.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

/**
 * 服务状态的工具类
 *
 * @author hp
 */
public class ServiceStatuUtils {

    public static boolean serviceStatu(Context context, String serviceName) {

        ActivityManager ac = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);

        // 得到运行服务的状态
        List<RunningServiceInfo> services = ac.getRunningServices(100);

        for (RunningServiceInfo rsi : services) {
            // 得到服务的组件名
            ComponentName cn = rsi.service;
            String className = cn.getClassName();
            if (className.equals(serviceName)) {
                // 说明该服务已经运行
                return true;
            }
        }

        return false;
    }
}
