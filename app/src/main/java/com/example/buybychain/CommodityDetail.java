package com.example.buybychain;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.SearchDetail;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommodityDetail extends AppCompatActivity {
    final Handler handler = new Handler();
    private TextView com_name;
    private TextView com_else;
    private TextView producer;
    private TextView saler;
    private TextView customer;
    private ImageView im1,im2,im3;

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
        setContentView(R.layout.activity_commodity_detail);
        com_name = findViewById(R.id.com_name);
        com_else = findViewById(R.id.com_else);
        producer = findViewById(R.id.produser);
        saler = findViewById(R.id.saler);
        customer = findViewById(R.id.customer);
        im1 = findViewById(R.id.image1);
        im2 = findViewById(R.id.image2);
        im3 = findViewById(R.id.image3);
        String scanResult = getIntent().getStringExtra("scanResult");
        post("http://buybychain.cn:8888/query",scanResult);

    }

    public void getAsyn(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println(e);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "get请求失败" ,Toast.LENGTH_LONG).show();

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    final String body = response.body().string();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
//                            textView.setText(body);
//                            Toast.makeText(getApplicationContext(), "get请求成功" ,Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
    public void post(String url, String scanResult){
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("out_id",scanResult)
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
                    final SearchDetail searchDetail = gson.fromJson(body,SearchDetail.class);
                    final Long timeStamp = System.currentTimeMillis();  //获取当前时间戳
                    final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    final String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            com_name.setText(searchDetail.getCom_name());
                            com_else.setText("价格：¥" + searchDetail.getCom_price() +
                                    "  类别：" + searchDetail.getCom_cate() + "  产地：" + searchDetail.getCom_place());
                            String outTime = sdf.format(new Date(Long.valueOf(searchDetail.getOut_birthday() + "000")));
                            producer.setText(outTime + "\n生产商：" + searchDetail.getPro_nickname() + "\n出厂");
                            String sellTime = sdf.format(new Date(Long.valueOf(searchDetail.getSell_time() + "000")));
                            saler.setText(sellTime + "\n销售商：" + searchDetail.getSal_nickname() + "\n售出");
                            customer.setText(sd + "\n买家：" + searchDetail.getSell_cus_acc() + "\n查询");
                            im1.setVisibility(View.VISIBLE);
                            im2.setVisibility(View.VISIBLE);
                            im3.setVisibility(View.VISIBLE);
//                            Toast.makeText(getApplicationContext(), "post请求成功", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}
