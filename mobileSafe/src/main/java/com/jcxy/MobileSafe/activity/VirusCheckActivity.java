package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.bean.Virus;
import com.jcxy.MobileSafe.db.dao.VirusDao;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

public class VirusCheckActivity extends Activity {
    private static final int SCANNING = 1;
    private static final int FINISH = 2;
    @ViewInject(R.id.iv_check_scanning)
    private ImageView iv_check_scanning;

    @ViewInject(R.id.progressBar)
    private ProgressBar progressBar;

    @ViewInject(R.id.ll_scanning_result)
    private LinearLayout ll_scanning_result;

    @ViewInject(R.id.tv_no_virus)
    private TextView tv_no_virus;

    @ViewInject(R.id.tv_virus_desc)
    private TextView tv_virus_desc;

    @ViewInject(R.id.iv_virus_app)
    private ListView iv_virus_app;

    private VirusDao virusDao;
    private PackageManager packageManager;
    private int process;
    private List<Virus> list;
    private RotateAnimation rota;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case SCANNING:
                    Object obj = msg.obj;
                    if (obj instanceof Virus) {
                        tv_virus_desc.setVisibility(View.GONE);
                        Virus v = (Virus) obj;
                        if (v.getIsVirus() != null) {
                            if (list == null) {
                                list = new ArrayList<Virus>();
                            }
                            // 有病毒
                            TextView textView = new TextView(VirusCheckActivity.this);
                            textView.setText(v.getPname() + "有病毒");
                            textView.setTextColor(Color.RED);
                            ll_scanning_result.addView(textView);
                            list.add(v);
                        } else {
                            //　没病毒
                            TextView textView = new TextView(VirusCheckActivity.this);
                            textView.setText(v.getPname() + "扫描安全");
                            ll_scanning_result.addView(textView);
                        }
                        process++;
                        progressBar.setProgress(process);
                    }
                    break;
                case FINISH:
                    // 结束动画
                    iv_check_scanning.clearAnimation();
                    ll_scanning_result.setVisibility(View.GONE);

                    if (list != null) {
                        tv_no_virus.setVisibility(View.GONE);
                        tv_virus_desc.setVisibility(View.VISIBLE);
                        MyVirusAppAdapter adapter=new MyVirusAppAdapter();
                        iv_virus_app.setAdapter(adapter);
                    }else {
                        tv_no_virus.setVisibility(View.VISIBLE);
                    }

                    break;

            }
        }
    };

    /**
     * 数据适配器
     * @param
     */
    private  class MyVirusAppAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv_virus_name=null;
            ImageView iv_virus_img=null;

            if(convertView==null){
                convertView=View.inflate(VirusCheckActivity.this, R.layout.virus_app_item, null);
                 tv_virus_name= (TextView) convertView.findViewById(R.id.tv_virus_name);
                 iv_virus_img= (ImageView) convertView.findViewById(R.id.iv_virus_img);

            }else {
                 tv_virus_name= (TextView) convertView.findViewById(R.id.tv_virus_name);
                 iv_virus_img= (ImageView) convertView.findViewById(R.id.iv_virus_img);
            }

            final Virus virus = list.get(position);
            tv_virus_name.setText(virus.getPname());

             iv_virus_img.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     // 卸载程序
                     Intent intent = new Intent();
                     intent.setAction(Intent.ACTION_DELETE);
                     intent.setData(Uri.parse("package:" +virus.getPackName()));
                     startActivity(intent);
                 }
             });



            return convertView;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virus_check);
        initUI();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        virusDao = new VirusDao();
        packageManager = getPackageManager();
        final List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        progressBar.setMax(installedPackages.size());


        new Thread() {
            @Override
            public void run() {
                for (PackageInfo info : installedPackages) {
                    String sourceDir = info.applicationInfo.sourceDir;
                    System.out.println(sourceDir);
                    String s = virusDao.checkVirus(sourceDir);
                    String programeName = info.applicationInfo.loadLabel(packageManager).toString();
                    String packageName = info.packageName;

                    Virus virus = new Virus();
                    virus.setIsVirus(s);
                    virus.setPname(programeName);
                    virus.setPackName(packageName);
                    Message message = Message.obtain();
                    message.obj = virus;
                    message.what = SCANNING;
                    handler.sendMessage(message);
                }

                Message m1 = Message.obtain();
                m1.what = FINISH;
                handler.sendMessage(m1);
            }
        }.start();


    }

    /**
     * 返回键
     *
     * @param view
     */
    public void back(View view) {
        finish();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        ViewUtils.inject(this);
        /**
         * 动画旋转
         */
        rota = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        rota.setDuration(1500);
        rota.setRepeatCount(Animation.INFINITE);
        iv_check_scanning.startAnimation(rota);
    }
}
