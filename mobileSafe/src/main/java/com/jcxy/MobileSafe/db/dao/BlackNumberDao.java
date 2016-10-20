package com.jcxy.MobileSafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jcxy.MobileSafe.bean.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by hp on 2016/9/18.
 */
public class BlackNumberDao {
    private DBHelper dbHelper;


    public BlackNumberDao(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    /**
     * @param number 要添加的黑名单号码
     * @return
     */
    public boolean addNumber(String number, int mode) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Random ran = new Random();
        ContentValues values = new ContentValues();
        long rowId = 0;
        values.put("number", number);
        values.put("mode", mode);
        rowId = db.insert("blackNumber", null, values);
        if (rowId != -1) {
            db.close();
            return true;
        } else {
            db.close();
            return false;
        }


    }

    /**
     * @param number 要删除的黑名单号码
     * @return
     */
    public boolean delete(String number) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int blackNumber = db.delete("blackNumber", " number=?", new String[]{number});
        if (blackNumber != 0) {
            db.close();
            return true;
        } else {
            db.close();
            return false;
        }
    }

    /**
     * @return 影响的行数
     */
    public int deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int number = db.delete("blackNumber", null, null);
        db.close();
        return number;
    }


    /**
     * @param number 修改的号码
     * @param mode   修改的模式
     * @return
     */
    public boolean update(String number, int mode, String oldNumber, int oldMode) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
        int update = db.update("blackNumber", values, " number =?,mode=?", new String[]{oldNumber, oldMode + ""});
        if (update == 0) {
            db.close();
            return false;
        } else {
            db.close();
            return true;
        }
    }

    /**
     * @param number 要查询的黑名单号码 的模式
     * @return
     */
    public int findByNumber(String number) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("blackNumber", new String[]{"number", "mode"}, " number=?", new String[]{number}, null, null, null);

        int mode = -1;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                mode = cursor.getInt(1);
            }
            cursor.close();
            db.close();
        }

        return mode;
    }

    /**
     * 查询所有的黑名单号码
     *
     * @return
     */
    public List<BlackNumberInfo> queryAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("blackNumber", new String[]{"number", "mode"}, null, null, null, null, null);
        List<BlackNumberInfo> blackNumberInfos = getBlackNumberInfos(db, cursor);

        return blackNumberInfos;
    }

    /**
     * 根据cousor 查询
     *
     * @param db
     * @param cursor
     * @return
     */
    private List<BlackNumberInfo> getBlackNumberInfos(SQLiteDatabase db, Cursor cursor) {
        BlackNumberInfo blackNumberInfo = null;
        List<BlackNumberInfo> blackNumberInfos = null;
        if (cursor != null && cursor.getCount() > 0) {
            blackNumberInfos = new ArrayList<BlackNumberInfo>();
            while (cursor.moveToNext()) {
                blackNumberInfo = new BlackNumberInfo();
                String num = cursor.getString(0);
                blackNumberInfo.setNumber(num);
                int mode = cursor.getInt(1);
                blackNumberInfo.setMode(mode);
                blackNumberInfos.add(blackNumberInfo);
            }
            cursor.close();
            db.close();
        }
        return blackNumberInfos;
    }

    /**
     * 根据传入的数量 查询指定的数据 如果没那么多就返回数据库全部
     *
     * @param count 查询多少数据
     * @return
     */
    public List<BlackNumberInfo> queryByNum(int count, int page) {
        int allCount = queryAllCount();
        if (count >= allCount) {
            return queryAll();
        } else  if (count>0){
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String sql = " select number,mode from blackNumber limit ? , ? ";
            Cursor cursor = db.rawQuery(sql, new String[]{page * count + "", count + ""});
            List<BlackNumberInfo> blackNumberInfos = getBlackNumberInfos(db, cursor);
            System.out.println(blackNumberInfos);
            return blackNumberInfos;

        }else{

            return null;
        }


    }


    /**
     * 查询总共多少行
     *
     * @return
     */
    public int queryAllCount() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("blackNumber", new String[]{"count(*)"}, null, null, null, null, null, null);
        int allCount = 0;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                allCount = cursor.getInt(0);
            }
            cursor.close();
            db.close();
        }

        return allCount;
    }

    /**
     *
     * @param startIndex 开始的位置
     * @param amount 查询的数量
     * @return
     */
    public List<BlackNumberInfo> findByBatch(int startIndex,int amount){

        int allCount = queryAllCount();
        if (amount >= allCount) {
            return queryAll();
        } else if (amount>0){
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String sql = " select number,mode from blackNumber limit ? , ? ";
            Cursor cursor = db.rawQuery(sql, new String[]{startIndex + "", amount + ""});
            List<BlackNumberInfo> blackNumberInfos = getBlackNumberInfos(db, cursor);
            return blackNumberInfos;
        }else {
            return null;
        }

    }


}
