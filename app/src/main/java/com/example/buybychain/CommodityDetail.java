package com.example.buybychain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.Commodity;
import com.bean.HisQuery;
import com.bean.HisQueryitem;
import com.bean.HisSell;
import com.bean.HisSellitem;
import com.bean.PerAllHistory;
import com.bean.Saler;
import com.bean.SearchDetail;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommodityDetail extends AppCompatActivity {
    public static CommodityDetail instance;
    Handler handler = new Handler();
    private ListView listview_1, listview_2;
    private MyAdapter adapter1, adapter2;
    private TextView com_name, com_price, com_type, com_place;
    private TextView pro_time, pro_acc;
    private TextView cus_time, cus_acc;
    private TextView warning;
    private LinearLayout all;
    private List<Map<String, Object>> hisselllist;
    private List<Map<String, Object>> hisquerylist;
    private Map<String, Object> map;
    private Button button;
    private LinearLayout query, big, sell;
    String history = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        // 设置状态栏字体颜色 黑色
        Window window = getWindow();
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            } catch (Exception e) {

            }
        }
        setContentView(R.layout.activity_commodity_detail);
        com_name = findViewById(R.id.com_name);
        com_price = findViewById(R.id.com_price);
        com_type = findViewById(R.id.com_type);
        com_place = findViewById(R.id.com_place);
        pro_time = findViewById(R.id.pro_time);
        pro_acc = findViewById(R.id.pro_acc);
        cus_time = findViewById(R.id.cus_time);
        cus_acc = findViewById(R.id.cus_acc);
        all = findViewById(R.id.pageall);
        query = findViewById(R.id.query);
        big = findViewById(R.id.big);
        sell = findViewById(R.id.sell);
        warning = findViewById(R.id.warning);
        listview_1 = (ListView) this.findViewById(R.id.hissell);
        listview_2 = (ListView) this.findViewById(R.id.hisquery);
        button = findViewById(R.id.submit);
        hisselllist = new ArrayList<Map<String, Object>>();
        hisquerylist = new ArrayList<Map<String, Object>>();
        String scanResult = getIntent().getStringExtra("scanResult");
        history = getIntent().getStringExtra("history");
        Buybychain application = (Buybychain) getApplication();
        if(history.equals("1")){
            post("http://buybychain.cn:8888/queryHistory",scanResult,application.getPhone());
        }
        else {
            post("http://buybychain.cn:8888/query",scanResult,application.getPhone());
        }

    }

    public void post(String url, String scanResult, String user_id){
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("out_id",scanResult)
                .add("user_id",user_id)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "post请求失败" ,Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    final String body = response.body().string();
                    if (body.equals("不存在")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), NoResult.class);
                                startActivity(intent);
                            }
                        });
                    }
                    else{
                        Gson gson = new Gson();
                        final SearchDetail searchDetail = gson.fromJson(body,SearchDetail.class);
                        System.out.println(searchDetail.toString());
                        String his1 = searchDetail.getAll_his_sell();
                        final HisSell hisSell = gson.fromJson(his1, HisSell.class);
                        List<HisSellitem> hisSellList = hisSell.getHisSellList();
                        System.out.println(hisSellList.toString());
                        final HisSellitem hisSellitem = hisSell.getHisSellList().get(hisSell.getHisSellList().size()-1);
                        String his2 = searchDetail.getAll_his_query();
                        HisQuery hisQuery = gson.fromJson(his2, HisQuery.class);
                        List<HisQueryitem> hisQueryList = hisQuery.getHisQueryList();
//                    HisQueryitem hisQueryitem = hisQuery.getHisQueryList().get(hisQuery.getHisQueryList().size()-1);
                        System.out.println(hisQueryList.toString());
                        final Long timeStamp = System.currentTimeMillis();  //获取当前时间戳
                        final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        final String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));
                        for (HisSellitem s : hisSellList) {
                            map = new HashMap<String, Object>();
                            map.put("time", sdf.format(new Date(Long.valueOf(s.getSell_time() + "000"))));
                            map.put("track", "快递单号: " + s.getSell_track_num());
                            map.put("saler", s.getSell_nickname());
                            map.put("customer", s.getCus_nickname() + " (" + phoneTrans(s.getSell_cus_acc()) + ")");
                            hisselllist.add(map);
                        }
                        for (HisQueryitem s : hisQueryList) {
                            map = new HashMap<String, Object>();
                            map.put("time", sdf.format(new Date(Long.valueOf(s.getHis_time() + "000"))));
                            map.put("cus_acc", phoneTrans(s.getHis_cus_acc()));
                            hisquerylist.add(map);
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                com_name.setText(searchDetail.getCom_name());
                                com_price.setText("价格：¥" + searchDetail.getCom_price());
                                com_type.setText("类别：" + searchDetail.getCom_cate());
                                com_place.setText("产地：" + searchDetail.getCom_place());
                                String outTime = sdf.format(new Date(Long.valueOf(searchDetail.getOut_birthday() + "000")));
                                pro_time.setText(outTime);
                                pro_acc.setText(searchDetail.getPro_nickname() + " (" + phoneTrans(searchDetail.getPro_acc()) + ")");
                                cus_time.setText(sd);
                                cus_acc.setText(hisSellitem.getCus_nickname() + " (" + phoneTrans(hisSellitem.getSell_cus_acc()) + ")");
                                String[] form1 = {"time", "track", "saler", "customer"};
                                int[] to1 = {R.id.time, R.id.track, R.id.saler, R.id.customer};
                                adapter1 = new MyAdapter(getApplicationContext(), hisselllist, R.layout.hissellitem, form1, to1);
                                listview_1.setAdapter(adapter1);
                                String[] form2 = {"time", "cus_acc"};
                                int[] to2 = {R.id.time, R.id.cus_acc};
                                adapter2 = new MyAdapter(getApplicationContext(), hisquerylist, R.layout.hisqueryitem, form2, to2);
                                listview_2.setAdapter(adapter2);
                                all.setVisibility(View.VISIBLE);
                                if(hisselllist.size()<hisquerylist.size()) {
                                    System.out.println("warning");
                                    warning.setVisibility(View.VISIBLE);
                                }
                                if (history.equals("1")) {
                                    big.removeView(button);
                                }
                                if (hisselllist.size() == 0) {
                                    big.removeView(sell);
                                }
                                if (hisquerylist.size() == 0) {
                                    big.removeView(query);
                                }
                            }
                        });
                        if(!history.equals("1")) {
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), Comment.class);
                                    intent.putExtra("sal_acc", hisSellitem.getSell_sal_acc());
                                    intent.putExtra("sal_nickname", hisSellitem.getSell_nickname());
                                    intent.putExtra("sal_cnt", hisSellitem.getSal_cnt());
                                    intent.putExtra("sal_total", hisSellitem.getSal_total());
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                }
            }
        });
    }
    public String phoneTrans(String s) {
        return s.substring(0,3) + "****" + s.substring(7,11);
    }
    public class MyAdapter extends SimpleAdapter {
        //上下文
        Context context;
        //private LayoutInflater mInflater;

        public MyAdapter(Context context,
                         List<? extends Map<String, ?>> data, int resource, String[] from,
                         int[] to) {
            super(context, data, resource, from, to);
            this.context = context;
            //this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {
            View view = super.getView(i, convertView, viewGroup);
            LinearLayout item = view.findViewById(R.id.item);
            item.setTag(i);//设置标签
            item.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            return view;
        }
    }
}
