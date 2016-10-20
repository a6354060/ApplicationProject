package com.jcxy.MobileSafe.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hp on 2016/9/20.
 */
public class GetContactsDao {

    private static ArrayList<Map<String, String>> list;
    private static HashMap<String, String> map;
    private static Context context;

    public GetContactsDao(Context context) {
        this.context = context;
    }

    // 得到联系人
    public static ArrayList<Map<String, String>> getContacts() {
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.android.contacts/raw_contacts"),
                new String[]{"contact_id"}, null, null, null);
        if (cursor != null) {
            list = new ArrayList<Map<String, String>>();
            while (cursor.moveToNext()) {
                String raw_id = cursor.getString(0);
                Cursor data = context.getContentResolver().query(Uri.parse("content://com.android.contacts/data"),
                        new String[]{"mimetype", "data1"}, " raw_contact_id=?", new String[]{raw_id}, null);
                if (data != null) {
                    map = new HashMap<String, String>();
                    while (data.moveToNext()) {
                        String mime = data.getString(0);
                        String value = data.getString(1);
                        if ("vnd.android.cursor.item/phone_v2".equals(mime)&& !TextUtils.isEmpty(value))
                        {
                            map.put("phone", value);
                        } else if ("vnd.android.cursor.item/name".equals(mime)&& !TextUtils.isEmpty(value)) {
                            map.put("name", value);
                        }
                    }
                    list.add(map);
                    data.close();
                }
            }
        }
        cursor.close();

        return list;
    }

}
