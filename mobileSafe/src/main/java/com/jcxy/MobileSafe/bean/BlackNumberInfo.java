package com.jcxy.MobileSafe.bean;

/**
 * Created by hp on 2016/9/18.
 */
public class BlackNumberInfo {

    private String number; // 表示黑名单的号码
    private int mode;  // 表示拦截的模式 0,表示电话，短信拦截 ，1 表示电话拦截 ，2 表示短信拦截

    public BlackNumberInfo(String number, int mode) {
        this.number = number;
        this.mode = mode;
    }


    public BlackNumberInfo() {
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "BlackNumberInfo{" +
                "number='" + number + '\'' +
                ", mode=" + mode +
                '}';
    }
}
