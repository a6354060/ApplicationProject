package com.jcxy.MobileSafe.activity;

import android.content.Intent;
import android.os.Bundle;

import com.jcxy.MobileSafe.R;

public class Setup1Activity extends BaseSetupAcitvity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup1);
    }

    @Override
    public void showNextpage() {
        startActivity(new Intent(this, Setup2Activity.class));
        // 俩个页面的切换动画
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
        finish();
    }

    @Override
    public void showPreviousPage() {

    }


}
