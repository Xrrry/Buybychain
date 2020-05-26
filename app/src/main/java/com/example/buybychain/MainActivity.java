package com.example.buybychain;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    final Handler handler = new Handler();
    private Button get,post,clear;
    private TextView textView;

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
        setContentView(R.layout.activity_main);
        get = (Button) findViewById(R.id.get);
        post = (Button) findViewById(R.id.post);
        clear = (Button) findViewById(R.id.clear);
        textView = (TextView) findViewById(R.id.Text1);
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAsyn("http://buybychain.cn:8888/getUserCount");
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post("http://buybychain.cn:8888/addUser","user","{\n" +
                        "  \"age\": \"16\",\n" +
                        "  \"name\": \"test\",\n" +
                        "  \"sex\": \"女\"\n" +
                        "}");
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("0000");
                    }
                });
            }
        });
    }


    public void getAsyn(String url) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
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
                            textView.setText(body);
                            Toast.makeText(getApplicationContext(), "get请求成功" ,Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    public void post(String url,String key,String value){
        OkHttpClient client = new OkHttpClient();
//        FormBody body = new FormBody.Builder()
//                .add(key,value)
//                .build();

        RequestBody body = RequestBody.create(MediaType.parse("application/json"),value);

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
                System.out.println("this");
                if(response.isSuccessful()) {
                    System.out.println("thiss");
                    final String body = response.body().string();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(body);
                            Toast.makeText(getApplicationContext(), "post请求成功", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

//    public void getDatasync(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
//                    Request request = new Request.Builder()
//                            .url("http://buybychain.cn:8888/getUserCount")//请求接口。如果需要传参拼接到接口后面。
//                            .build();//创建Request 对象
//                    Response response = null;
//                    response = client.newCall(request).execute();//得到Response 对象
//                    if (response.isSuccessful()) {
////                        Log.d("kwwl","response.code()=="+response.code());
////                        Log.d("kwwl","response.message()=="+response.message());
////                        Log.d("kwwl","res=="+response.body().string());
//                        final String body = response.body().string();
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                textView.setText(body);
//                            }
//                        });
//                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

}
