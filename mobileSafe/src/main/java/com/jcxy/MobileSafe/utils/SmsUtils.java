package com.jcxy.MobileSafe.utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

/**
 * Created by hp on 2016/9/29.
 */

public class SmsUtils {

    public interface SmsBackCall {
        public void before(int count);

        public void backing(int process);

    }


    /**
     * 备份短信方法
     */

    public static boolean backUp(Context context, SmsBackCall smsBackCall, ProgressDialog progressDialog,Cursor cursor) {


        if (cursor != null && cursor.getCount() > 0) {

            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(context, "手机没有外部存储", Toast.LENGTH_SHORT).show();
                return false;
            }

            // 设计进度对话框

            int count = cursor.getCount();

            // 保存短信数量
            SharedPreferences config = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            config.edit().putInt("backupCount", count).commit();

            // 备份前
            smsBackCall.before(count);

            int progress = 0;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh时mm分ss秒");
            File file = new File(Environment.getExternalStorageDirectory(), "back.xml");
            OutputStream os = null;
            try {
                os = new FileOutputStream(file);
                XmlSerializer serializer = Xml.newSerializer();
                serializer.setOutput(os, "utf-8");
                serializer.startDocument("utf-8", true);
                serializer.startTag(null, "smss");
                serializer.attribute(null, "size", String.valueOf(count));
                while (cursor.moveToNext()) {
                    serializer.startTag(null, "sms");

                    // 备份电话
                    serializer.startTag(null, "address");
                    serializer.text(cursor.getString(0));
                    serializer.endTag(null, "address");

                    // 备份时间
                    serializer.startTag(null, "date");
                    serializer.text(cursor.getString(1));
                    serializer.endTag(null, "date");

                    // 备份类型
                    serializer.startTag(null, "type");
                    serializer.text(cursor.getString(2));
                    serializer.endTag(null, "type");

                    // 备份内容
                    serializer.startTag(null, "body");
                    serializer.text(Crypto.encrypt("110", cursor.getString(3)));
                    serializer.endTag(null, "body");

                    serializer.endTag(null, "sms");
                    progress++;
                    smsBackCall.backing(progress);
                }


                serializer.endTag(null, "smss");


                serializer.endDocument();
                progressDialog.dismiss();
                os.flush();
                os.close();
                cursor.close();

                return true;


            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        } else {
            SharedPreferences config = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            config.edit().putInt("backupCount", 0).commit();
            return true;
        }


    }


    /**
     * 还原短信
     */
    public static boolean restore(Context context, SmsBackCall smsBackCall, ProgressDialog progressDialog,File file) {

        String address = null;
        String body = null;
        String type = null;
        String date = null;

        int count; // 还原的总数
        int progress = 0; // 还原的进度


        /**
         * 解析XML文件
         */
        XmlPullParser parser = Xml.newPullParser();
        InputStream in = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://sms");
            ContentValues values = new ContentValues();
            in = new FileInputStream(file);
            parser.setInput(in, "utf-8");
            int eventType = parser.getEventType();

            SharedPreferences config = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            count = config.getInt("backupCount", 0);
            smsBackCall.before(count);
            System.out.println("开始还原");
            /**
             * 为了防止重复插入短信 先把短信全删除了 不安全
             */
            //  resolver.delete(uri,null,null);
            while (XmlPullParser.END_DOCUMENT != eventType) {

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("address".equals(parser.getName())) {
                            address = parser.nextText();
                        } else if ("date".equals(parser.getName())) {
                            date = parser.nextText();
                        } else if ("type".equals(parser.getName())) {
                            type = parser.nextText();
                        } else if ("body".equals(parser.getName())) {
                            body = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("sms".equals(parser.getName())) {
                            String decrypt = Crypto.decrypt("110", body);
                            values.put("address", address);
                            values.put("date", date);
                            values.put("type", type);
                            values.put("body", decrypt);

                            /**
                             * 相同的短信 不能重复还原
                             */
                            Cursor cursor = resolver.query(uri, new String[]{"address", "body"}, " address =? and body=? and date=?", new String[]{address, decrypt, date}, null);
                            if (!cursor.moveToNext()) {
                                resolver.insert(uri, values);
                                System.out.println(values);
                            }
                            cursor.close();
                            progress++;
                            smsBackCall.backing(progress);
                        }
                        break;
                }
                // 解析下一个
                eventType = parser.next();

            }
            progressDialog.dismiss();
            System.out.println("还原结束");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("异常" + e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return true;
    }

}
