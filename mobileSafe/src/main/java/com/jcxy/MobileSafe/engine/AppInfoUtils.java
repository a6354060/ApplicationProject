package com.jcxy.MobileSafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.jcxy.MobileSafe.bean.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016/9/24.
 */

public class AppInfoUtils {
    private PackageManager pm;
    private List<AppInfo> appInfos;

    public AppInfoUtils(Context context) {
        this.pm = context.getPackageManager();
        this.appInfos = new ArrayList<AppInfo>();
    }

    public List<AppInfo> getAppInfos() {
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        AppInfo appInfo = null;
        if (installedPackages != null) {
            for (PackageInfo packInfo : installedPackages) {
                appInfo = new AppInfo();
                // 获得包名
                appInfo.setPackageName(packInfo.packageName);

                ApplicationInfo applicationInfo = packInfo.applicationInfo;
                // 获得应用图标
                appInfo.setIcon(applicationInfo.loadIcon(pm));

                // 应用名
                appInfo.setAppName(applicationInfo.loadLabel(pm).toString());

                // 应用大小 先拿到APP的原始路径
                String sourceDir = applicationInfo.sourceDir;
                //System.out.println(sourceDir);

                appInfo.setApkpath(sourceDir);

                File file = new File(sourceDir);
                appInfo.setAppSize(file.length());

                //获取到安装应用程序的标记
                int flags = packInfo.applicationInfo.flags;
                // 这里的flags都是2的倍数 如果相等就不会等于零
                if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    // 系统App
                    appInfo.setSystemApp(true);

                } else {
                    // 用户App
                    appInfo.setSystemApp(false);
                }

                // 原理同上
                if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                    //表示在sd卡
                    appInfo.setRom(false);
                } else {
                    //表示内存
                    appInfo.setRom(true);
                }
                appInfos.add(appInfo);

            }

        }

        return appInfos;
    }

}
