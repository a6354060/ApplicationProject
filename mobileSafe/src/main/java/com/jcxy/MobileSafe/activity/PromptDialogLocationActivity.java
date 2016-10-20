package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.jcxy.MobileSafe.R;

/**
 * 控制归属地框的位置
 *
 * @author hp
 */
public class PromptDialogLocationActivity extends Activity {
    private TextView tv_prompt_top;
    private TextView tv_prompt_bottom;
    private WindowManager wdm;
    private WindowManager.LayoutParams params;
    private View view;
    private SharedPreferences spf;
    private int Wheight;
    private int Wwidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt_location);
        wdm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Wheight = wdm.getDefaultDisplay().getHeight();
        Wwidth = wdm.getDefaultDisplay().getWidth();
        params = new WindowManager.LayoutParams();
        spf = getSharedPreferences("config", MODE_PRIVATE);
        tv_prompt_top = (TextView) findViewById(R.id.tv_prompt_top);
        tv_prompt_bottom = (TextView) findViewById(R.id.tv_prompt_bottom);
    }

    @Override // 界面可见时调用
    protected void onStart() {
        super.onStart();
        showLocationDialog("双击居中");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(wdm!=null&&view!=null){
            wdm.removeView(view);
            view=null;
        }
    }

    /**
     * 显示归属地对话框
     *
     * @param loaction
     */
    private void showLocationDialog(String loaction) {
        // "半透明","活力橙","卫士蓝","金属灰","苹果绿"
        int[] resid = new int[]{R.drawable.call_locate_white, R.drawable.call_locate_orange,
                R.drawable.call_locate_blue, R.drawable.call_locate_gray, R.drawable.call_locate_green};
        // 需要权限

        view = View.inflate(this, R.layout.dialog_show_call_adress, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_show_adress);
        tv.setText(loaction);

        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.format = PixelFormat.TRANSLUCENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.gravity = Gravity.LEFT + Gravity.TOP;

        // 根据自己的选择来确定背景
        int hint = spf.getInt("hintStyle", 0);
        view.setBackgroundResource(resid[hint]);

        int lastX = spf.getInt("lastX", 0);
        int lastY = spf.getInt("lastY", 0);

        // 重新进入时判断
        if (lastY < tv_prompt_top.getHeight() + 5) {
            tv_prompt_top.setVisibility(View.INVISIBLE); // 设置不可见
            tv_prompt_bottom.setVisibility(View.VISIBLE);
        }
        if ((Wheight - (lastY + view.getHeight() + 5)) < tv_prompt_bottom.getHeight()) {
            tv_prompt_bottom.setVisibility(View.INVISIBLE);
            tv_prompt_top.setVisibility(View.VISIBLE); // 设置不可见
        }

        // 设置浮窗的位置, 基于左上方的偏移量
        params.x = lastX;
        params.y = lastY;
        wdm.addView(view, params);

        // 为 归属地提示框加点击接触事件
        view.setOnTouchListener(new OnTouchListener() {
            private int startX;
            private int startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 根据运动事件处理
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 按下
                        startX = (int) event.getRawX(); // 从屏幕开始计算
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 移动
                        int newX = (int) event.getRawX();
                        int newY = (int) event.getRawY();

                        // 计算偏移量
                        int dx = newX - startX;
                        int dy = newY - startY;

                        // 计算浮窗的位置
                        params.x += dx;
                        params.y += dy;

                        // 不让窗口浮出窗外
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        // 更新浮窗
                        wdm.updateViewLayout(view, params);

                        if (params.y < tv_prompt_top.getHeight() + 5) {
                            tv_prompt_top.setVisibility(View.INVISIBLE); // 设置不可见
                            tv_prompt_bottom.setVisibility(View.VISIBLE);
                        }

                        if ((Wheight - (params.y + view.getHeight() + 5)) < tv_prompt_bottom.getHeight()) {
                            tv_prompt_bottom.setVisibility(View.INVISIBLE);
                            tv_prompt_top.setVisibility(View.VISIBLE); // 设置不可见
                        }

                        // 更新初始化值
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        // 送开
                        Editor edit = spf.edit();
                        edit.putInt("lastX", params.x);
                        edit.putInt("lastY", params.y);
                        edit.commit();
                        break;
                    default:
                        break;
                }
                return false; // 返回 true 表示该事件处理完毕 如果下面还有事件处理就返回false
            }
        });

        final long[] times = new long[2]; // 数组的长度表示点击的次数
        /**
         * 双击居中事件
         */
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 该算法比较有意思
                System.arraycopy(times, 1, times, 0, times.length - 1);
                times[times.length - 1] = SystemClock.uptimeMillis(); // 给最后一个赋值
                if ((times[times.length - 1] - times[0]) < 500) { // 表示连续点击时间的差值
                    // 处理双击事件
                    params.x = Wwidth / 2 - view.getWidth() / 2;
                    params.y = Wheight / 2 - view.getHeight();
                    wdm.updateViewLayout(view, params);
                    Editor edit = spf.edit();
                    edit.putInt("lastX", params.x);
                    edit.putInt("lastY", params.y);
                    edit.commit();
                }
            }
        });

    }

}
