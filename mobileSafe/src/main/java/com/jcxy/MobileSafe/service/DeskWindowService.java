package com.jcxy.MobileSafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.activity.RockteAnimSmokeActivity;
import com.jcxy.MobileSafe.engine.TaskInfos;

public class DeskWindowService extends Service {
	private WindowManager wm;
	private WindowManager.LayoutParams params;
	private ImageView imageView;
	private SharedPreferences spf;
	private View view;
	private int Wheight;
	private int Wwidth;
	private RockteAnimSmokeActivity activity;
	private LayoutParams Lparams;
	private View lightView;
	private ImageView LightImageView;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		activity = new RockteAnimSmokeActivity();
		spf = getSharedPreferences("config", MODE_PRIVATE);
		startWindow();
		super.onCreate();
	}

	/**
	 * 开始浮窗
	 */
	private void startWindow() {
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		params = new WindowManager.LayoutParams();
        Display display = wm.getDefaultDisplay();
        Wheight = display.getHeight();
		Wwidth = display.getWidth();
		
		view = View.inflate(this, R.layout.desk_progress_ball, null);
		lightView = View.inflate(this, R.layout.desk_window_rockte, null);
		imageView = (ImageView) view.findViewById(R.id.iv_ball);
		LightImageView = (ImageView) lightView.findViewById(R.id.iv_rocket_bg);
		imageView.setBackgroundResource(R.drawable.desk);
		
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		params.format = PixelFormat.TRANSLUCENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.gravity = Gravity.LEFT + Gravity.TOP; // 显示在左上角
		
		Lparams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT, params.type, params.flags, params.format);
		Lparams.gravity=Gravity.BOTTOM;
		wm.addView(view, params); // 添加到窗口

		// 给view 添加接触事件

		imageView.setOnTouchListener(new OnTouchListener() {

			private int startX;
			private int startY;
			private AnimationDrawable ad;
			private Message msg;
			private AnimationDrawable lightAd;
			private static final int FINISH = 1;
			private static final int DEALING = 2;
			private static final int NO_FLY = 3;
			
			// handler 处理发射
			private Handler mHandler = new Handler() {
				public void handleMessage(android.os.Message msg) {
					switch (msg.what) {
					case DEALING:
						// 飞行中
						params.y = (Integer) msg.obj;
						wm.updateViewLayout(view, params);
						break;
					case FINISH:
						// 飞完
						new MyAsyncTask().execute();
						// 恢复以前点击时的浮窗
						int j = spf.getInt("rockteLastY", 0);
						params.x=0;
						params.y=j;
						imageView.setBackgroundResource(R.drawable.desk);
						wm.updateViewLayout(view, params);
						break;
					default:
						break;
					}
				};
			};

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 根据运动事件处理
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// 按下
					// 开启一个透明的Activity 发射火箭
					imageView.setBackgroundResource(R.drawable.rockte_anim);
					ad = (AnimationDrawable) imageView.getBackground();
					ad.start();
					wm.updateViewLayout(view, params);
					
					// 添加光环
					LightImageView.setBackgroundResource(R.drawable.rockte_bg_light_anim);
					lightAd=(AnimationDrawable)LightImageView.getBackground();
                    lightAd.start();
                    wm.addView(lightView, Lparams);
                    
					startX = (int) event.getRawX(); // 从屏幕开始计算
					startY = (int) event.getRawY();
					
					// 保存位置 以便下次出现在同样的位置
					Editor edit = spf.edit();
					edit.putInt("rockteLastX", params.x);
					edit.putInt("rockteLastY", params.y);
					edit.commit();
					
					break;
				case MotionEvent.ACTION_MOVE:
					// 移动
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					// 计算偏移量
					int dx = newX - startX;
					int dy = newY - startY;
					// 计算浮窗的位置
					params.x += dx;
					params.y += dy;
					// 不让窗口浮出窗外
					if (params.x < 0) {
						params.x = 0;
					}
					if (params.y < 0) {
						params.y = 0;
					}
					// 更新浮窗
					wm.updateViewLayout(view, params);
					// 更新初始化值
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					// 送开
					if(params.x>150 && Wwidth-params.x>150 && Wheight-params.y-imageView.getHeight()<260){
						sendRocket();
						// 开始烟雾动画	
						Intent intent=new Intent(DeskWindowService.this, RockteAnimSmokeActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}else{
					   imageView.setBackgroundResource(R.drawable.desk);
						if(params.x>Wwidth/2){
							params.x=Wwidth;
						}else {
							params.x=0;
						}
						wm.updateViewLayout(view, params); 
					}
					// 结束光环
					wm.removeView(lightView);
					
					break;
				default:
					break;
				}
				return true;
			}


			/**
			 * 发射火箭
			 */

			protected void sendRocket() {
				// 设置火箭居中
				params.x = Wwidth / 2 - view.getWidth() / 2;
				wm.updateViewLayout(view, params);
				new Thread() {
					@Override
					public void run() {
						int pos = Wheight;// 移动总距离
						System.out.println(Wheight);
						int k = pos / 10;
						for (int i = 0; i <= 10; i++) {

							// 等待一段时间再更新位置,用于控制火箭速度
							try {
								Thread.sleep(80);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
       
							int y = pos - k * i;
                            if(i==9) y=0;
							msg = Message.obtain();
							msg.obj = y;
							msg.what = DEALING;
							mHandler.sendMessage(msg);
						}
						msg = Message.obtain();
						msg.what = FINISH;
						mHandler.sendMessage(msg);

					}
				}.start();
			}

		});

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopWindow();
	}

	/**
	 * 关闭浮窗
	 */
	private void stopWindow() {
		if (wm != null && view != null) {
			wm.removeView(view);
			view = null; // 让垃圾收集器回收
		}
	}

	private class MyAsyncTask extends AsyncTask<Void,Void,String>{
		@Override
		protected String doInBackground(Void... params) {
			String s = new TaskInfos(getApplicationContext()).killAllProcess();
			return s;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			String[] split = s.split(":");
			int memeory=Integer.valueOf(split[0]);
			int count=Integer.valueOf(split[1]);
			Toast.makeText(getApplicationContext(), "共清理了" + count + "个进程，释放了" + Formatter.formatFileSize(getApplicationContext(), memeory) + "内存", Toast.LENGTH_SHORT).show();
		}
	}

}
