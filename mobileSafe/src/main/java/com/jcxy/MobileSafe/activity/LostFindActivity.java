package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.service.LocationService;

public class LostFindActivity extends Activity {
    private SharedPreferences msp;
    private TextView safeNum;
    private ImageView iv_lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lostfind_activity);
        msp = getSharedPreferences("config", MODE_PRIVATE);
        iv_lock = (ImageView) findViewById(R.id.iv_lock);
        if (msp.getBoolean("safeLock", false)) {
            iv_lock.setImageResource(R.drawable.lock);
        } else {
            iv_lock.setImageResource(R.drawable.unlock);
        }

        safeNum = (TextView) findViewById(R.id.safeNumber);

        String num = msp.getString("safeNumber", "");
        safeNum.setText(num);

        startService(new Intent(this, LocationService.class));

    }

    /**
     * 返回键
     * @param view
     */
    public void back(View view){
        finish();
    }

    public void reset(View view) {
        startActivity(new Intent(this, Setup1Activity.class));
        finish();
    }


}
