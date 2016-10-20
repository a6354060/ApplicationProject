package com.jcxy.MobileSafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.jcxy.MobileSafe.R;

public class SettingItemRelative extends RelativeLayout {

    private static  String NAMESPACE ="http://schemas.android.com/apk/res-auto";
    private TextView title;
    private TextView desc;
    private Switch sw;
    private String mTitle;
    private String mDesc_on;
    private String mDesc_off;

    public SettingItemRelative(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public SettingItemRelative(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTitle = attrs.getAttributeValue(NAMESPACE, "title");// 根据属性名称,获取属性的值
        mDesc_on = attrs.getAttributeValue(NAMESPACE, "desc_on");
        mDesc_off = attrs.getAttributeValue(NAMESPACE, "desc_off");
        initView();
    }

    public SettingItemRelative(Context context) {
        super(context);
        initView();
    }

    // 向该组件填充一个布局
    private void initView() {
        View v = View.inflate(getContext(), R.layout.setting_item, this);
        title = (TextView) v.findViewById(R.id.tv_title);
        desc = (TextView) v.findViewById(R.id.tv_desc_on);
        sw = (Switch) v.findViewById(R.id.sw);
        title.setText(mTitle);
    }

    // 设置标题
    public void setTitle(String t) {
        title.setText(t);
    }

    // 设置描述
    public void setDesc(String des) {
        desc.setText(des);
    }

    // 返回开关状态
    public boolean isChecked() {
        return sw.isChecked();
    }

    // 设置开关状态
    public void setChecked(boolean check) {
        sw.setChecked(check);
        if (check) {
            setDesc(mDesc_on);
        } else {
            setDesc(mDesc_off);
        }

    }

}
