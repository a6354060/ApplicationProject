package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.utils.MD5Utils;

public class HomeActivity extends Activity {

    private GridView gv;
    private String[] name = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};
    private int[] photo = new int[]{R.drawable.home_safe, R.drawable.home_callmsgsafe, R.drawable.home_apps,
            R.drawable.home_taskmanager, R.drawable.home_netmanager, R.drawable.home_trojan,
            R.drawable.home_sysoptimize, R.drawable.home_tools, R.drawable.home_settings};
    private SharedPreferences mSpf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        mSpf = getSharedPreferences("config", MODE_PRIVATE);

        gv = (GridView) findViewById(R.id.gd_list);
        gv.setAdapter(new MyAdapter());

        /**
         * 主界面选择
         */
        gv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // 手机防盗
                        showPasswordAlertDialog();
                        break;
                    case 1:
                        // 通讯卫士
                        startActivity(new Intent(HomeActivity.this, BlackNumberActivity.class));
                        break;
                    case 2:
                        // 软件管理
                        startActivity(new Intent(HomeActivity.this, AppManagerActivity.class));
                        break;
                    case 3:
                        // 进程管理
                        startActivity(new Intent(HomeActivity.this, TaskManagerActivity.class));
                        break;
                    case 4:
                        // 流量统计
                        startActivity(new Intent(HomeActivity.this, liuliangActivity.class));
                        break;
                    case 5:
                        // 病毒查杀
                        startActivity(new Intent(HomeActivity.this, VirusCheckActivity.class));
                        break;
                    case 6:
                        //缓存清理
                        startActivity(new Intent(HomeActivity.this, CacheCleanActivity.class));
                        break;
                    case 7:
                        // 高级工具
                        startActivity(new Intent(HomeActivity.this, AtoolsActivity.class));
                        break;
                    case 8:
                        // 设置中心
                        Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }


            }
        });

    }

    /**
     * 显示密码对话框
     */
    protected void showPasswordAlertDialog() {
        // 1 判断是否是第一次登陆
        String pass = mSpf.getString("password", null);
        if (TextUtils.isEmpty(pass)) {
            // 没设置 就显示密码设置对话框
            showSetPassDialog();
        } else {
            // 已经登陆 showInput密码对话框
            showInputDialog();
        }

    }

    // 显示输入密码对话框
    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View v = View.inflate(this, R.layout.showinputpassdialog, null);
        dialog.setView(v);
        dialog.show();

        Button ok = (Button) v.findViewById(R.id.ok);
        Button cancel = (Button) v.findViewById(R.id.cancel);

        final EditText pass = (EditText) v.findViewById(R.id.password);
        // 为ok按钮添加监听事件

        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 确定
                // 必须在点击事件里面 获取值
                String p = pass.getText().toString();
                if ((!TextUtils.isEmpty(p))) {
                    String md5s = MD5Utils.MD5encode(p);
                    if (md5s.equals(mSpf.getString("password", null))) {
                        // 密码一致 保存密码 进入手机防盗页面
                        Toast.makeText(HomeActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                        boolean flag = mSpf.getBoolean("flag", false);

                        if (flag) {// 判断是否进入过设置向导页面
                            // 设置过了
                            startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
                        } else {
                            // 没设置过 进入引导页面
                            startActivity(new Intent(HomeActivity.this, Setup1Activity.class));

                        }
                        dialog.dismiss();
                    } else {
                        // 不一致 提示密码不一致
                        Toast.makeText(HomeActivity.this, "俩次密码不一致！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "输入框内容不能为空", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // cancel是事件
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消
                dialog.dismiss();

            }
        });


    }

    // 显示设置密码对话框
    private void showSetPassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View v = View.inflate(this, R.layout.showsetpassdialog, null);
        dialog.setView(v);
        dialog.show();

        Button ok = (Button) v.findViewById(R.id.ok);
        Button cancel = (Button) v.findViewById(R.id.cancel);

        final EditText pass = (EditText) v.findViewById(R.id.password);
        final EditText passConfirm = (EditText) v.findViewById(R.id.passwordConfirm);
        // 为ok按钮添加监听事件

        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 确定
                // 必须在点击事件里面 获取值
                String p = pass.getText().toString();
                String pc = passConfirm.getText().toString();
                if ((!TextUtils.isEmpty(p)) && (!TextUtils.isEmpty(pc))) {
                    if (p.equals(pc)) {
                        // 密码一致 保存密码 进入手机防盗页面
                        String md5Password = MD5Utils.MD5encode(p);
                        mSpf.edit().putString("password", md5Password).commit();
                        Toast.makeText(HomeActivity.this, "设置密码成功！请再次点击登陆", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        // 不一致 提示密码不一致
                        Toast.makeText(HomeActivity.this, "俩次密码不一致！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "输入框内容不能为空", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // cancel是事件
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消
                dialog.dismiss();

            }
        });

    }


    /**
     * 网格适配器
     *
     * @author hp
     */
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO 自动生成的方法存根
            return name.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO 自动生成的方法存根
            return name[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO 自动生成的方法存根
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = View.inflate(HomeActivity.this, R.layout.home_item, null);
            ImageView iv = (ImageView) v.findViewById(R.id.iv_item);
            TextView tv = (TextView) v.findViewById(R.id.tv_item);
            iv.setImageResource(photo[position]);
            tv.setText(name[position]);
            return v;

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }



/*
    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
 */
}
