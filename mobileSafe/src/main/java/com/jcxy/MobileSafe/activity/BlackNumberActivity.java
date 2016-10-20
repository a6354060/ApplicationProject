package com.jcxy.MobileSafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jcxy.MobileSafe.R;
import com.jcxy.MobileSafe.bean.BlackNumberInfo;
import com.jcxy.MobileSafe.db.dao.BlackNumberDao;
import com.jcxy.MobileSafe.db.dao.QueryLoactionDao;

import java.util.ArrayList;
import java.util.List;

public class BlackNumberActivity extends Activity {
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
    private final int count = 10; // 每次查询15条数据
    private int page;
    private EditText go_count;
    private TextView tv_page_count;
    private int allCount;
    private TextView tv_deleteAll;
    private ImageView iv_black_edite;


    /**
     * 处理消息
     */
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            listView.setVisibility(View.VISIBLE);
            ll_wait.setVisibility(View.GONE);

            adapter = new MyAdapter(blackNumberInfos);
            listView.setAdapter(adapter);

        }
    };
    private int allPage;
    private PopupWindow pop;
    private View inflate;
    private LinearLayout ll_black_number;
    private FrameLayout fl_black_number;
    final List<Integer> list = new ArrayList<Integer>(); // 判断是否是重复点击


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_number);

        initView();

        initListViewData(count, page);

        OnItemClick();

        dealBlackEdite();
    }

    /**
     * 返回键
     * @param view
     */
    public void back(View view){
        finish();
    }

    /**
     * 编辑PopupWindow 对话框
     */
    private void dealBlackEdite() {

        clickCancel(ll_black_number);
        inflate = View.inflate(BlackNumberActivity.this, R.layout.black_edite_dialog, null);


        iv_black_edite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                add_black_number = (TextView) inflate.findViewById(R.id.tv_add_black_number);
                tv_deleteAll = (TextView) inflate.findViewById(R.id.tv_deleteAll);
                // 必须是同一个对象才能disMiss掉
                if (pop == null) {
                    pop = new PopupWindow(inflate, -2, -2);
                }


                pop.showAsDropDown(v);
                //pop.showAtLocation(v, Gravity.LEFT + Gravity.BOTTOM,50,20);


                if (list.size() >= 1) {
                    dismissPopWindwo();
                    list.clear();
                } else {
                    list.add(0);
                }


                add_black_number.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(BlackNumberActivity.this);
                        final AlertDialog dialog = builder.create();
                        View view = View.inflate(BlackNumberActivity.this, R.layout.add_black_dialog, null);
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
                                if (!TextUtils.isEmpty(black)) {
                                    int mode = -1;
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
                                        Toast.makeText(BlackNumberActivity.this, "请选择拦截模式", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    dao.addNumber(black, mode);
                                    Toast.makeText(BlackNumberActivity.this, "添加黑名单成功", Toast.LENGTH_SHORT).show();
                                    initListViewData(count, page);
                                    dialog.dismiss();

                                } else {
                                    Toast.makeText(BlackNumberActivity.this, "黑名单号码不能为空", Toast.LENGTH_SHORT).show();

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
                        dismissPopWindwo();
                    }
                });

                tv_deleteAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (blackNumberInfos != null && blackNumberInfos.size() > 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(BlackNumberActivity.this);
                            builder.setTitle("全部删除黑名单号码");
                            builder.setMessage("确定全部删除吗？");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    int all = dao.deleteAll();

                                    if (all > 0) {
                                        Toast.makeText(BlackNumberActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                        initListViewData(count, page);
                                        pop.dismiss();

                                    } else {
                                        Toast.makeText(BlackNumberActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    pop.dismiss();
                                }
                            });
                            builder.show();
                        } else {

                            Toast.makeText(BlackNumberActivity.this, "没有数据可删除", Toast.LENGTH_SHORT).show();
                            pop.dismiss();
                        }
                    }

                });


            }

        });


    }

    private void clickCancel(ViewGroup view) {

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (pop != null) {

                    dismissPopWindwo();
                }
            }


            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (pop != null) {
                    dismissPopWindwo();
                }
            }
        });


        if (view.isClickable()) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPopWindwo();

                }
            });
        } else {
            view.setClickable(true);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPopWindwo();
                }
            });

        }
    }

    private void dismissPopWindwo() {
        if (pop != null) {
            if(list.size()>=1){
                list.clear();
            }
            pop.dismiss();
            pop = null;
        }
    }


    /**
     * 上一页
     *
     * @param view
     */
    public void previous(View view) {

        if (page <= 0) {
            Toast.makeText(this, "已经是第一页了", Toast.LENGTH_SHORT).show();
        } else {
            page--;
            tv_page_count.setText("" + page + "/" + allPage);
            initListViewData(count, page);
        }


    }

    /**
     * 下一页
     *
     * @param view
     */
    public void next(View view) {

        if (page >= allPage) {
            Toast.makeText(this, "已经是最后一页了", Toast.LENGTH_SHORT).show();
        } else {
            page++;
            tv_page_count.setText("" + page + "/" + allPage);
            initListViewData(count, page);
        }


    }

    /**
     * 跳转到某页
     *
     * @param view
     */
    public void go(View view) {
        String s = go_count.getText().toString().trim();
        if (!TextUtils.isEmpty(s)) {
            int i = Integer.parseInt(s);
            page = i;
            if (i < 0 || i > allPage) {
                Toast.makeText(this, "请正确输入数字", Toast.LENGTH_SHORT).show();
            } else {
                tv_page_count.setText("" + page + "/" + allPage);
                initListViewData(count, i);
            }
        } else {
            Toast.makeText(this, "输入的数字不能为空", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 初始化数据
     */
    private void initListViewData(final int count, final int page) {
        blackNumberInfos = dao.queryByNum(count, page);
        allCount = dao.queryAllCount();
        if (allCount != 0 && allCount % count == 0) {
            allPage = allCount / count - 1;
        } else {
            allPage = allCount / count;
        }

        tv_page_count.setText(page + "/" + allPage);
        if (blackNumberInfos == null) {

            listView.setVisibility(View.GONE);
            tv_non_black_number.setVisibility(View.VISIBLE);


        } else {
            // 开启线程处理 网路获取数据
            listView.setVisibility(View.VISIBLE);
            ll_wait.setVisibility(View.GONE);
            tv_non_black_number.setVisibility(View.GONE);
            new Thread() {
                @Override
                public void run() {
                    // 只能在主线程中更新UI
                    // 模拟网络获取数据 睡眠 1 秒
                    SystemClock.sleep(500);

                    Message message = Message.obtain();
                    handler.sendMessage(message);

                }
            }.start();


        }


    }

    /**
     * 处理listView的item的事件
     */
    private void OnItemClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {

                before.add(position);
                Bview.add(view);
                Button btn_delete = null;
                if (before.size() == 1 || before.get(0) == before.get(1)) {
                    RelativeLayout rl = (RelativeLayout) view;
                    rl.setBackgroundResource(R.drawable.black_number_item);
                    btn_delete = (Button) rl.findViewById(R.id.btn_delete);
                    btn_delete.setVisibility(View.VISIBLE);
                    if (before.size() >= 2) {
                        before.remove(before.get(0));
                        Bview.remove(Bview.get(0));
                    }

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

                        AlertDialog.Builder builder = new AlertDialog.Builder(BlackNumberActivity.this);
                        builder.setTitle("删除黑名单号码");
                        builder.setMessage("确定删除吗？");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BlackNumberInfo info = blackNumberInfos.get(position);
                                boolean delete = dao.delete(info.getNumber());

                                if (delete) {
                                    blackNumberInfos.remove(info);
                                    allPage = allCount - 1 / count;
                                    Toast.makeText(BlackNumberActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    if ((allPage * count) == (allCount - 1) && (allCount - 1) != 0) {
                                        page--;
                                    }
                                    initListViewData(count, page);

                                } else {
                                    Toast.makeText(BlackNumberActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
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


        listView = (ListView) findViewById(R.id.black_number_listView);
        tv_non_black_number = (TextView) findViewById(R.id.tv_non_black_number);
        fl_black_number = (FrameLayout) findViewById(R.id.fl_black_number);
        ll_wait = (LinearLayout) findViewById(R.id.ll_wait_load);
        go_count = (EditText) findViewById(R.id.et_go_count);
        tv_page_count = (TextView) findViewById(R.id.tv_page_count);
        ll_black_number = (LinearLayout) findViewById(R.id.ll_black_number);
        iv_black_edite = (ImageView) findViewById(R.id.iv_black_edite);

    }

    /**
     * 添加黑名单号码
     */
    private void addBlackNumber() {
        System.out.println("AddBlackNumber");
        add_black_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BlackNumberActivity.this);
                final AlertDialog dialog = builder.create();
                View view = View.inflate(BlackNumberActivity.this, R.layout.add_black_dialog, null);
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
                        if (!TextUtils.isEmpty(black)) {

                            if (cb_phone.isChecked() && cb_sms.isChecked()) {
                                // 电话+短信拦截
                                dao.addNumber(black, 0);
                            } else if (cb_phone.isChecked()) {
                                // 电话拦截
                                dao.addNumber(black, 1);

                            } else {
                                // 短信拦截
                                dao.addNumber(black, 2);
                            }
                            Toast.makeText(BlackNumberActivity.this, "添加黑名单成功", Toast.LENGTH_SHORT).show();
                            // 刷新数据
                            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();

                            initListViewData(count, page);
                            dialog.dismiss();

                        } else {
                            Toast.makeText(BlackNumberActivity.this, "黑名单号码不能为空", Toast.LENGTH_SHORT).show();

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
        tv_deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (blackNumberInfos != null && blackNumberInfos.size() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BlackNumberActivity.this);
                    builder.setTitle("全部删除黑名单号码");
                    builder.setMessage("确定全部删除吗？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            int all = dao.deleteAll();

                            if (all > 0) {
                                Toast.makeText(BlackNumberActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                initListViewData(count, page);

                            } else {
                                Toast.makeText(BlackNumberActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    builder.setNegativeButton("取消", null);
                    builder.show();
                } else {

                    Toast.makeText(BlackNumberActivity.this, "没有数据可删除", Toast.LENGTH_SHORT).show();
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
            View view;
            if (convertView == null) {
                convertView = View.inflate(BlackNumberActivity.this, R.layout.black_number_item, null);
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

    /**
     *
     */


    @Override
    protected void onDestroy() {
        if (pop != null) {
            pop.dismiss();
            pop = null;
        }
        super.onDestroy();
    }
}
