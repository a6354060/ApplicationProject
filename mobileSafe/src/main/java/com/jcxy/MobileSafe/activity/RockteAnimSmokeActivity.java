package com.jcxy.MobileSafe.activity;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.service.DeskWindowService;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class RockteAnimSmokeActivity extends Activity {
     private ImageView imageView;
     private AnimationDrawable ad;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.desk_smoke_anim);
		RelativeLayout layout=(RelativeLayout) findViewById(R.id.rl_smoke);
//		imageView =(ImageView) findViewById(R.id.iv_rocket_bg);
//		imageView.setBackgroundResource(R.drawable.rockte_bg_light_anim);
//		ad=(AnimationDrawable) imageView.getBackground();
//		ad.start();
		AlphaAnimation alphaAni= new AlphaAnimation(0, 1);
		alphaAni.setDuration(1200);
		layout.startAnimation(alphaAni);
		
		// 延时1.2秒后完成页面
		  new Handler().postDelayed(new Runnable(){
			@Override
			public void run() {
				finish();
			}
		  }, 1200);
	}
}
