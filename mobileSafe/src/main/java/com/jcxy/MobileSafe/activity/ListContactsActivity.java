package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.db.dao.GetContactsDao;

import java.util.ArrayList;
import java.util.Map;

public class ListContactsActivity extends Activity {
    private ListView lv;
    private ArrayList<Map<String, String>> contacts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listcontact);
        lv = (ListView) findViewById(R.id.lv_contacts);
        GetContactsDao dao=new GetContactsDao(this);
         contacts =dao.getContacts();
        lv.setAdapter(new SimpleAdapter(this, contacts, R.layout.contactitem, new String[]{"phone", "name"},
                new int[]{R.id.tv_contact_phone, R.id.tv_contact_name}));
        // 设置listView 的点击事件
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> m = contacts.get(position);
                String phone = m.get("phone");
                Intent data = new Intent();
                data.putExtra("phone", phone);
                setResult(RESULT_OK, data);
                finish();
            }
        });

    }




}
