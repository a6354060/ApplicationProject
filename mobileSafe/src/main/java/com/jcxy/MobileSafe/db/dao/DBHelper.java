package com.jcxy.MobileSafe.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hp on 2016/9/18.
 */
public class DBHelper extends SQLiteOpenHelper {


    public DBHelper(Context context) {
        super(context, "blackNumberDb", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql =" create table blackNumber( _id integer primary key autoincrement ,number varchar(20) unique , mode int not null) ";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
