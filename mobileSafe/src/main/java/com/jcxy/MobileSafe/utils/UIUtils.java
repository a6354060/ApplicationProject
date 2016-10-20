package com.jcxy.MobileSafe.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by hp on 2016/9/29.
 */

public class UIUtils {

    public static  void  showToast(final Activity context, final String msg){

        if(Thread.currentThread().getName().equals("main")){
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }else{

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

}
