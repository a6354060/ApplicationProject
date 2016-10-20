package com.jcxy.MobileSafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by hp on 2016/10/18.
 */

public class CacheBean {

    private String appName;
    private long   appCache;
    private Drawable cacheIcon;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    private String packageName;

    public Drawable getCacheIcon() {
        return cacheIcon;
    }

    public void setCacheIcon(Drawable cacheIcon) {
        this.cacheIcon = cacheIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getAppCache() {
        return appCache;
    }

    public void setAppCache(long appCache) {
        this.appCache = appCache;
    }
}
