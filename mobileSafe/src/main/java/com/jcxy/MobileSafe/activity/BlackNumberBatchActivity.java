package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.bean.BlackNumberInfo;
import com.jcxy.MobileSafe.db.dao.BlackNumberDao;
import com.jcxy.MobileSafe.db.dao.QueryLoactionDao;

import java.util.ArrayList;
import java.util.List;

public class BlackNumberBatchActivity extends Activity {
    private ListView listView;
    private QueryLoactionDao qdao;
    private BlackNumberDao dao;
    private List<BlackNumberInfo> blackNumberInfos;

    private List<Integer> before = new ArrayList<Integer>();
    private List<View> Bview = new ArrayList<View>();
    private MyAdapter adapter;
    private TextView add_black_number;
    private TextView tv_non_black_number;
    private LinearLayout ll_wait;
    private final int amount = 10; // 每次查询15条数据
    private int startIndex;
    private EditText go_count;
    private TextView tv_page_count;
    private int allCount;
    private TextView tv_deleteAll;

    /**
     * 处理消息
     */
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {


            if (blackNumberInfos == null) {

                blackNumberInfos = dao.findByBatch(startIndex, amount);
            } else {
                List<BlackNumberInfo> batch = dao.findByBatch(startIndex, amount);
                if (batch != null) {
                    blackNumberInfos.addAll(batch);
                }
            }

            if (blackNumberInfos != null) {
                listView.setVisibility(View.VISIBLE);
                tv_non_black_number.setVisibility(View.GONE);
            } else {
                listView.setVisibility(View.GONE);
                tv_non_black_number.setVisibility(View.VISIBLE);

            }
            if (adapter == null) {
                adapter = new MyAdapter(blackNumberInfos);
                listView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_number_batch);

        initView();

        initListViewData();

        addBlackNumber();

        deleteAllBlackNumber();

        OnItemClick();
    }


    /**
     * 初始化数据
     */
    private void initListViewData() {
        new Thread() {
            @Override
            public void run() {
                // 只能在主线程中更新UI
                // 模拟网络获取数据 睡眠 1 秒
                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    /**
     * 处理listView的item的事件
     */
    private void OnItemClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                before.add(position);
                Bview.add(view);
                Button btn_delete = null;
                if (before.size() == 1 || before.get(0) == before.get(1)) {
                    RelativeLayout rl = (RelativeLayout) view;
                    rl.setBackgroundResource(R.drawable.black_number_item);
                    btn_delete = (Button) rl.findViewById(R.id.btn_delete);
                    btn_delete.setVisibility(View.VISIBLE);
                } else {

                    RelativeLayout view1 = (RelativeLayout) Bview.get(0);
                    view1.setBackgroundResource(R.drawable.black_number_item1);
                    Button button = (Button) view1.findViewById(R.id.btn_delete);
                    button.setVisibility(View.GONE);

                    // 当期的View
                    RelativeLayout relativeLayout = (RelativeLayout) view;
                    btn_delete = (Button) relativeLayout.findViewById(R.id.btn_delete);
                    btn_delete.setVisibility(View.VISIBLE);

                    relativeLayout.setBackgroundResource(R.drawable.black_number_item);

                    before.remove(before.get(0));
                    Bview.remove(view1);


                }
                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 删除黑名单

                        AlertDialog.Builder builder = new AlertDialog.Builder(BlackNumberBatchActivity.this);
                        builder.setTitle("删除黑名单号码");
                        builder.setMessage("确定删除吗？");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BlackNumberInfo info = blackNumberInfos.get(position);
                                boolean delete = dao.delete(info.getNumber());

                                if (delete) {
                                    Toast.makeText(BlackNumberBatchActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    blackNumberInfos.remove(info);
                                    initListViewData();
                                } else {
                                    Toast.makeText(BlackNumberBatchActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        builder.setNegativeButton("取消", null);
                        builder.show();

                    }
                });

            }
        });
    }

    /**
     * 初始化视图
     */
    private void initView() {

        qdao = new QueryLoactionDao();

        dao = new BlackNumberDao(this);
        allCount = dao.queryAllCount();

        listView = (ListView) findViewById(R.id.black_number_listView);
        tv_non_black_number = (TextView) findViewById(R.id.tv_non_black_number);
        add_black_number = (TextView) findViewById(R.id.tv_add_black_number);
        ll_wait = (LinearLayout) findViewById(R.id.ll_wait_load);
        go_count = (EditText) findViewById(R.id.et_go_count);
        tv_page_count = (TextView) findViewById(R.id.tv_page_count);
        tv_deleteAll = (TextView) findViewById(R.id.tv_deleteAll);

        /**
         *
         * @param view
         * @param scrollState  表示滚动的状态
         *
         *                     AbsListView.OnScrollListener.SCROLL_STATE_IDLE 闲置状态
         *                     AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL 手指触摸的时候的状态
         *                     AbsListView.OnScrollListener.SCROLL_STATE_FLING 惯性
         */
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {


            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //当滚动时候调用
                switch (scrollState) {

                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:

                        int position = listView.getLastVisiblePosition();
                        if (position == blackNumberInfos.size() - 1) {
                            // 移动到最后了
                            startIndex += amount;
                            if (startIndex > allCount) {
                                Toast.makeText(BlackNumberBatchActivity.this, "已经到最后一条数据了", Toast.LENGTH_SHORT).show();
                            }


                            initListViewData();

                        }

                        break;

                }


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


    }

    /**
     * 添加黑名单号码
     */
    private void addBlackNumber() {
        add_black_number.setClickable(true);
        System.out.println("AddBlackNumber");
        add_black_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BlackNumberBatchActivity.this);
                final AlertDialog dialog = builder.create();
                View view = View.inflate(BlackNumberBatchActivity.this, R.layout.add_black_dialog, null);
                dialog.setView(view);
                Button btn_black_confirm = (Button) view.findViewById(R.id.btn_black_confirm);
                Button btn_black_cancel = (Button) view.findViewById(R.id.btn_black_cancel);
                final EditText black_number = (EditText) view.findViewById(R.id.et_black_number);
                final CheckBox cb_phone = (CheckBox) view.findViewById(R.id.cb_phone);
                final CheckBox cb_sms = (CheckBox) view.findViewById(R.id.cb_sms);
                // 确定按钮
                btn_black_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 添加到黑名单
                        String black = black_number.getText().toString().trim();
                        int mode = -1;
                        if (!TextUtils.isEmpty(black)) {

                            if (cb_phone.isChecked() && cb_sms.isChecked()) {
                                // 电话+短信拦截
                                mode = 0;
                            } else if (cb_phone.isChecked()) {
                                // 电话拦截
                                mode = 1;

                            } else if (cb_sms.isChecked()) {
                                // 短信拦截
                                mode = 2;
                            } else {
                                Toast.makeText(BlackNumberBatchActivity.this, "请选择拦截模式", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                            blackNumberInfo.setNumber(black);
                            blackNumberInfo.setMode(mode);
                            blackNumberInfos.add(0, blackNumberInfo);
                            //把电话号码和拦截模式添加到数据库。
                            dao.addNumber(black, mode);

                            if (adapter == null) {
                                adapter = new MyAdapter(blackNumberInfos);
                                listView.setAdapter(adapter);
                            } else {
                                adapter.notifyDataSetChanged();
                            }


                            Toast.makeText(BlackNumberBatchActivity.this, "添加黑名单成功", Toast.LENGTH_SHORT).show();
                            // 刷新数据
                            dialog.dismiss();

                        } else {
                            Toast.makeText(BlackNumberBatchActivity.this, "黑名单号码不能为空", Toast.LENGTH_SHORT).show();

                        }

                    }
                });

                // 取消按钮
                btn_black_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss(); //取消对话框
                    }
                });

                dialog.show();

            }
        });


    }

    /**
     * 删除全部黑名单
     */
    private void deleteAllBlackNumber() {
        tv_deleteAll.setClickable(true);
        tv_deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (blackNumberInfos != null && blackNumberInfos.size() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BlackNumberBatchActivity.this);
                    builder.setTitle("全部删除黑名单号码");
                    builder.setMessage("确定全部删除吗？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            int all = dao.deleteAll();

                            if (all > 0) {
                                Toast.makeText(BlackNumberBatchActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                if (adapter == null) {
                                    adapter = new MyAdapter(blackNumberInfos);
                                    listView.setAdapter(adapter);
                                } else {
                                    adapter.notifyDataSetChanged();
                                }

                            } else {
                                Toast.makeText(BlackNumberBatchActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    builder.setNegativeButton("取消", null);
                    builder.show();
                } else {

                    Toast.makeText(BlackNumberBatchActivity.this, "没有数据可删除", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    /**
     * 数据适配器
     */
    class MyAdapter extends BaseAdapter {

        private String all = "短信+电话拦截";
        private String phone = "电话拦截";
        private String sms = "短信拦截";
        private List<BlackNumberInfo> blackNumberInfos;

        public MyAdapter(List<BlackNumberInfo> blackNumberInfos) {
            this.blackNumberInfos = blackNumberInfos;
        }

        @Override
        public int getCount() {
            return blackNumberInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return blackNumberInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            BlackNumberInfo info = blackNumberInfos.get(position);
            if (convertView == null) {
                convertView = View.inflate(BlackNumberBatchActivity.this, R.layout.black_number_item, null);
                holder = new ViewHolder();

                holder.tv_black_number = (TextView) convertView.findViewById(R.id.tv_black_number);
                holder.tv_black_location = (TextView) convertView.findViewById(R.id.tv_black_location);
                holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_black_mode);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();

            }
            String number = info.getNumber();
            holder.tv_black_number.setText(number);
            int mode = info.getMode();
            if (mode == 0) {
                holder.tv_mode.setText(all);
            } else if (mode == 1) {
                holder.tv_mode.setText(phone);
            } else {
                holder.tv_mode.setText(sms);
            }

            String loaction = qdao.queryLoaction(number);
            holder.tv_black_location.setText(loaction);

            return convertView;

        }

        class ViewHolder {
            private TextView tv_black_number;
            private TextView tv_mode;
            private TextView tv_black_location;
        }

    }

}
