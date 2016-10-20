package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.db.dao.QueryLoactionDao;

public class NumQueryPageActivity extends Activity {

    private EditText et_numphone;
    private TextView tv_result;
    private QueryLoactionDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_num_query_page);
        dao = new QueryLoactionDao();
        et_numphone = (EditText) findViewById(R.id.et_phoneNum);
        tv_result = (TextView) findViewById(R.id.tv_result);

        et_numphone.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String loaction = dao.queryLoaction(s.toString());
                tv_result.setText(loaction);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * 返回键
     * @param view
     */
    public void back(View view){
        finish();
    }

    /**
     * 查询号码归属地
     *
     * @param view
     */
    public void query(View view) {

        String number = et_numphone.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(1000);
            // 判断是否是震动
            if (vibrator.hasVibrator()) {
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.tran_shake);
                et_numphone.startAnimation(animation);
            }
            return;
        }

        String loaction = dao.queryLoaction(number);
        tv_result.setText(loaction);
    }


}
