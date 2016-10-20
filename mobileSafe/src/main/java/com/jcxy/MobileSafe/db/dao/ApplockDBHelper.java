package com.jcxy.MobileSafe.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hp on 2016/10/13.
 */

public class ApplockDBHelper extends SQLiteOpenHelper {

    public ApplockDBHelper(Context context) {
        super(context, "appInfo", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql =" create table info( _id integer primary key autoincrement ,packageName varchar(20) ) ";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
