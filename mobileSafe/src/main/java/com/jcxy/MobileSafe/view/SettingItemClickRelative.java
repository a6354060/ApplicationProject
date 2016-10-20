package com.jcxy.MobileSafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jcxy.MobileSafe.R;

public class SettingItemClickRelative extends RelativeLayout {

    private static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private TextView title;
    private String mTitle;

    public SettingItemClickRelative(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public SettingItemClickRelative(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTitle = attrs.getAttributeValue(NAMESPACE, "title");// 根据属性名称,获取属性的值

        initView();
    }

    public SettingItemClickRelative(Context context) {
        super(context);
        initView();
    }

    // 向该组件填充一个布局
    private void initView() {
        View v = View.inflate(getContext(), R.layout.setting_item_click, this);
        title = (TextView) v.findViewById(R.id.tv_title);
        title.setText(mTitle);
    }

    // 设置标题
    public void setTitle(String t) {
        title.setText(t);
    }

}
