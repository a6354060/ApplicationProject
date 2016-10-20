package com.jcxy.MobileSafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016/10/13.
 */

public class ApplockDao {
    private ApplockDBHelper applockDBHelper;
     private  Context context;
    public ApplockDao(Context context) {

        this.applockDBHelper = new ApplockDBHelper(context);
        this.context=context;
    }


    /**
     * 添加appLock
     */

    public void addApp(String packageName) {
        SQLiteDatabase db = applockDBHelper.getWritableDatabase();
         context.getContentResolver().notifyChange(Uri.parse("content://appLock/update"), new ContentObserver(new Handler()) {
             @Override
             public void onChange(boolean selfChange) {
                 super.onChange(selfChange);
             }
         });
        ContentValues values = new ContentValues();
        values.put("packageName", packageName);
        db.insert("info", null, values);
        db.close();
    }

    /**
     * 删除appLock
     */
    public void delete(String packageName) {
        SQLiteDatabase db = applockDBHelper.getWritableDatabase();
        db.delete("info", "packageName=?", new String[]{packageName});
        context.getContentResolver().notifyChange(Uri.parse("content://appLock/update"), new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
            }
        });
        db.close();
    }

    /**
     * 查找applock
     */
    public boolean findByPackageName(String packageName) {
        SQLiteDatabase db = applockDBHelper.getWritableDatabase();
        Cursor cursor = db.query("info", new String[]{"packageName"}, "packageName=?", new String[]{packageName}, null, null, null);
        if (cursor.moveToNext()) {
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();

        return false;
    }


    /**
     * 查找所有
     */
    public List<String> findAll() {
        SQLiteDatabase db = applockDBHelper.getWritableDatabase();
        Cursor cursor = db.query("info", new String[]{"packageName"}, null, null, null, null, null);
        List<String> list = new ArrayList<String>();
        while(cursor.moveToNext()) {
            String string = cursor.getString(0);
            list.add(string);
        }
        cursor.close();
        db.close();

        return list;
    }
}
