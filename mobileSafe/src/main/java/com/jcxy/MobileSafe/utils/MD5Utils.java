package com.jcxy.MobileSafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    public static String MD5encode(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] b = digest.digest(password.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte c : b) {
                int i = c & 0xff;
                String s = Integer.toHexString(i);
                if (s.length() < 2) {
                    s = "0" + s;
                }
                sb.append(s);
            }
            return sb.toString().toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取到文件的MD5(病毒特征码)
     * @param sourceDir
     * @return
     * 主动防御
     */
    public static String getFileMd5(String sourceDir) {

        File file = new File(sourceDir);

        try {
            FileInputStream fis = new FileInputStream(file);

            byte[] buffer =  new byte[1024];

            int len = -1;
            //获取到数字摘要
            MessageDigest messageDigest = MessageDigest.getInstance("md5");

            while((len = fis.read(buffer))!= -1){

                messageDigest.update(buffer, 0, len);

            }
            byte[] result = messageDigest.digest();

            StringBuffer sb = new StringBuffer();

            for(byte b : result){
                int number = b&0xff; // 加盐 +1 ;
                String hex = Integer.toHexString(number);
                if(hex.length()==1){
                    sb.append("0"+hex);
                }else{
                    sb.append(hex);
                }
            }
            return sb.toString();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
