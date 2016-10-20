package com.jcxy.MobileSafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jcxy.MobileSafe.R;

public class Setup3Activity extends BaseSetupAcitvity {
    private EditText et_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup3);
        et_phone = (EditText) findViewById(R.id.et_safephone);
        String safeNumber = mSpf.getString("safeNumber", null);
        if (!TextUtils.isEmpty(safeNumber)) {
            et_phone.setText(safeNumber);
        }
    }

    public void selectContacts(View view) {
        Intent intent = new Intent(this, ListContactsActivity.class);
        startActivityForResult(intent, 0);
        //finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String num = data.getStringExtra("phone");
            num = num.replaceAll("-", "").replaceAll(" ", "");
            mSpf.edit().putString("safeNumber", num).commit();
            et_phone.setText(num);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void showNextpage() {
        String num = et_phone.getText().toString().trim();
        if (!TextUtils.isEmpty(num)) {
            mSpf.edit().putString("safeNumber", num).commit();
            et_phone.setText(num);
        } else {
            Toast.makeText(this, "请选择您的安全号码:", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mSpf.getString("safeNumber", null) == null) {
            Toast.makeText(this, "请选择您的安全号码:", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(new Intent(this, Setup4Activity.class));
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
        finish();

    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(this, Setup2Activity.class));
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
        finish();

    }


}
