package com.jcxy.MobileSafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.jcxy.MobileSafe.R;

public class Setup4Activity extends BaseSetupAcitvity {
    private CheckBox cb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup4);
        cb = (CheckBox) findViewById(R.id.startSafe);
        boolean b = mSpf.getBoolean("safeLock", false);
        if (b) {
            cb.setChecked(true);
            cb.setText("您已开启手机防盗");
        } else {
            cb.setChecked(false);
            cb.setText("您还没开启手机防盗");
        }

        cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb.setText("您已开启手机防盗");
                    mSpf.edit().putBoolean("safeLock", true).commit();
                    Toast.makeText(Setup4Activity.this, "成功开启手机防盗", Toast.LENGTH_SHORT).show();
                } else {
                    cb.setText("您还没有开启手机防盗");
                    mSpf.edit().putBoolean("safeLock", false).commit();
                    Toast.makeText(Setup4Activity.this, "取消手机防盗", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void showNextpage() {
        mSpf.edit().putBoolean("flag", true).commit();
        startActivity(new Intent(this, LostFindActivity.class));
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
        finish();
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(this, Setup3Activity.class));
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
        finish();
    }


}
