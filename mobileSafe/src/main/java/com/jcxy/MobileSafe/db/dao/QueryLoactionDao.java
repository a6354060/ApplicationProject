package com.jcxy.MobileSafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 查询归属地的DAO
 *
 * @author hp
 */
public class QueryLoactionDao {

    private static final String PATH1 = "/data/data/com.jcxy.MobileSafe/files/commonnumber.db";
    private static final String PATH = "/data/data/com.jcxy.MobileSafe/files/address.db";

    public QueryLoactionDao() {
    }

    public String queryLoaction(String number) {
        String address = "未知号码";

        if (number.matches("^1[3-8]\\d{9}$")) { // 是个手机号码
            number = number.substring(0, 7);
            SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
            String sql = " select location from data2 where id = ( select outkey from data1 where id=?) ";
            Cursor c = db.rawQuery(sql, new String[]{number});
            if (c != null) {
                if (c.moveToNext())
                    address = c.getString(0);
            }
        } else if (number != null) {
            SQLiteDatabase db1 = SQLiteDatabase.openDatabase(PATH1, null, SQLiteDatabase.OPEN_READONLY);
            switch (number.length()) {
                case 3:
                    address = queryLocation(number, address, db1);
                    if ("未知号码".equals(address)) {
                        address = "报警电话";
                    }
                    break;
                case 4:
                    address = queryLocation(number, address, db1);
                    if ("未知号码".equals(address)) {
                        address = "模拟器号码";
                    }
                    break;
                case 5:
                    address = queryLocation(number, address, db1);
                    if ("未知号码".equals(address)) {
                        address = "服务号码";
                    }
                    break;
                case 6:
                    address = queryLocation(number, address, db1);
                    break;
                case 7:
                    address = queryLocation(number, address, db1);
                    if ("未知号码".equals(address)) {
                        address = "本地电话";
                    }
                    break;
                case 8:
                    address = queryLocation(number, address, db1);
                    if ("未知号码".equals(address)) {
                        address = "本地电话";
                    }
                    break;
                case 10:
                    address = queryLocation(number, address, db1);
                    break;
            }
        }
        return address;
    }

    private String queryLocation(String number, String address, SQLiteDatabase db1) {
        String sql = " select name from commonnumber where number =?";
        Cursor c = db1.rawQuery(sql, new String[]{number});
        if (c != null) {
            if (c.moveToNext())
                address = c.getString(0);
        }
        return address;
    }

}
