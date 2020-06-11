package com.example.buybychain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.bean.Commodity;
import com.bean.PerAllHistory;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChooseCom extends AppCompatActivity {

    private ListView listview_1;
    private MyAdapter adapter;
    private List<Map<String, Object>> list;
    private Map<String, Object> map;
    private String[] ss;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_choose_com);
        Buybychain application = (Buybychain) getApplication();
        listview_1 = (ListView) this.findViewById(R.id.Listview_1);
        list = new ArrayList<Map<String, Object>>();
        post("http://buybychain.cn:8888/proAllCom","15555555555");
    }

    public void post(String url, String pro_acc){
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("pro_acc",pro_acc)
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
                    Gson gson = new Gson();
                    ss = body.split("Tuple");
                    System.out.println(ss.length);
                    for (int i=1;i<ss.length;i++) {
                        String s = ss[i].substring(1);
                        Commodity commodity = gson.fromJson(s,Commodity.class);
                        map = new HashMap<String, Object>();
                        map.put("com_name", commodity.getCom_name());
                        map.put("com_tandl", commodity.getCom_cate().concat(" · ").concat(commodity.getCom_place()));
                        map.put("com_price",commodity.getCom_price());
                        list.add(map);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] form = {"com_name", "com_tandl", "com_price"};
                            int[] to = {R.id.com_name, R.id.com_tandl, R.id.com_price};
                            adapter = new MyAdapter(getApplicationContext(), list, R.layout.comitem, form, to);
                            listview_1.setAdapter(adapter);
                        }
                    });
                }
            }
        });
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
            RelativeLayout item = view.findViewById(R.id.item);
            item.setTag(i);//设置标签
            item.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int a = (Integer) v.getTag();
                    Gson gson = new Gson();
                    Buybychain application = (Buybychain) getApplication();
                    String s = ss[a+1].substring(1);
                    Commodity commodity = gson.fromJson(s,Commodity.class);
                    System.out.println(commodity.toString());
                    application.setCommodity(commodity);
                    application.setValid(true);
                    finish();
                }
            });
            return view;
        }
    }
}
