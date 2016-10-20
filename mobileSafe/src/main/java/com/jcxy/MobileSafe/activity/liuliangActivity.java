package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.jcxy.MobileSafe.R;

public class liuliangActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liuliang);
    }

    public void back(View view){
        finish();
    }
}
