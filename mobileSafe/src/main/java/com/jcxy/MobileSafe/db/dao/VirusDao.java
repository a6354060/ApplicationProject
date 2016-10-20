package com.jcxy.MobileSafe.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jcxy.MobileSafe.utils.MD5Utils;

/**
 * Created by hp on 2016/10/11.
 */

public class VirusDao {
    private static final String PATH = "/data/data/com.jcxy.MobileSafe/files/antivirus.db";

    public String checkVirus(String resource) {

        SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);

        String fileMd5 = MD5Utils.getFileMd5(resource);
        System.out.println(fileMd5);

        Cursor cursor = database.query("datable", new String[]{"desc"}, "md5=?", new String[]{fileMd5}, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {

            if (cursor.moveToNext()) {
                String string = cursor.getString(0);
                return string;
            }
            cursor.close();
        }


        return null;
    }


    /**
     * 查找病毒
     */
    public boolean findByMD5(String MD5){
        SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = database.query("datable", null, "md5=?", new String[]{MD5}, null, null, null);
        if (cursor != null && cursor.getCount() > 1) {
            cursor.close();
            return true;
        }
        return false;
    }

    /**
     * 跟新病毒库
     */

    public boolean ubdateVirus(String md5,String desc){
        // 判读病毒是否已经存在
          if(findByMD5(md5)){
              // 已经存在
              return false;
          }else {
              // 不存在 进行添加
              SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READWRITE);
              ContentValues values=new ContentValues();
              values.put("md5",md5);
              values.put("desc",desc);
              values.put("type",6);
              values.put("name","Android.Hack.i22hkt.c");
              long insert = database.insert("datable", null, values);
              if(insert!=-1){
                  return true;
              }
          }


        return  false;
    }

}
