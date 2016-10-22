package com.jcxy.MobileSafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by hp on 2016/9/24.
 */
public class AppInfo {


    private String apkpath;


    public String getApkpath() {
        return apkpath;
    }

    public void setApkpath(String apkpath) {
        this.apkpath = apkpath;
    }

    // 应用的图标
    private Drawable icon;

    // 应用的名字
    private String appName;

    // 应用的包名
    private String packageName;

    // 应用的大小

    private Long appSize;

    // 是否放在手机内存

    private Boolean isRom;

    // 是否是系统应用

    private Boolean isSystemApp;

    // 用户ID
    private int Uid;

    public int getUid() {
        return Uid;
    }

    public void setUid(int uid) {
        Uid = uid;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Long getAppSize() {
        return appSize;
    }

    public void setAppSize(Long appSize) {
        this.appSize = appSize;
    }

    public Boolean getRom() {
        return isRom;
    }

    public void setRom(Boolean rom) {
        isRom = rom;
    }

    public Boolean getSystemApp() {
        return isSystemApp;
    }

    public void setSystemApp(Boolean systemApp) {
        isSystemApp = systemApp;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                ", appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", appSize=" + appSize +
                ", isRom=" + isRom +
                ", isSystemApp=" + isSystemApp +
                '}';
    }

    public AppInfo(String appName, Drawable icon, String packageName, Long appSize, Boolean isRom, Boolean isSystemApp) {
        this.appName = appName;
        this.icon = icon;
        this.packageName = packageName;
        this.appSize = appSize;
        this.isRom = isRom;
        this.isSystemApp = isSystemApp;
    }

    public AppInfo() {
    }
}
