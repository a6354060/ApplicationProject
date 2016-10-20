package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public abstract class BaseSetupAcitvity extends Activity {
    protected SharedPreferences mSpf;
    private GestureDetector gd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        super.onCreate(savedInstanceState);
        mSpf = getSharedPreferences("config", MODE_PRIVATE);
        /**
         * 处理左右滑动的页面
         */
        gd = new GestureDetector(this, new SimpleOnGestureListener() {
            public boolean onFling(MotionEvent e1, MotionEvent e2,
                                   float velocityX, float velocityY) {

                if (Math.abs(e2.getRawY() - e1.getRawY()) > 200) {
                    Toast.makeText(BaseSetupAcitvity.this, "不能这样划哦!",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (Math.abs(velocityX) < 80) {
                    Toast.makeText(BaseSetupAcitvity.this, "滑动速度不能太慢哦", Toast.LENGTH_SHORT).show();
                    return true;
                }
                // 上一步
                if (e1.getRawX() - e2.getRawX() > 100) {
                    showNextpage();
                }
                // 下一步
                if (e2.getRawX() - e1.getRawX() > 100) {
                    showPreviousPage();
                }
                return true;
            }

            ;
        });
    }

    // 下一步
    public void next(View view) {
        showNextpage();
    }

    ;

    public void previous(View view) {
        showPreviousPage();
    }

    //  子类自己实现下页的动作
    public abstract void showNextpage();

    // 子类自己实现上页的动作
    public abstract void showPreviousPage();

       /**
     * 处理滑动事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gd.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


}
