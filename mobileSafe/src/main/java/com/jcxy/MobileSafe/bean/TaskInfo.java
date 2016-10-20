package com.jcxy.MobileSafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by hp on 2016/9/29.
 */

public class TaskInfo {

    private String processName;
    private Drawable icon;
    private int processSize;
    private boolean isSystemProcess;
    private  String packName;

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    /**
     *判断cheackBox是否是选上的
     * @return
     */
    private boolean isChecked;


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "processName='" + processName + '\'' +
                ", icon=" + icon +
                ", processSize=" + processSize +
                ", isSystemProcess=" + isSystemProcess +
                '}';
    }

    public TaskInfo(String processName, Drawable icon, int processSize, Boolean isSystemProcess) {
        this.processName = processName;
        this.icon = icon;
        this.processSize = processSize;
        this.isSystemProcess = isSystemProcess;
    }

    public TaskInfo() {
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public int getProcessSize() {
        return processSize;
    }

    public void setProcessSize(int processSize) {
        this.processSize = processSize;
    }

    public boolean getSystemProcess() {
        return isSystemProcess;
    }

    public void setSystemProcess(boolean systemProcess) {
        isSystemProcess = systemProcess;
    }
}
