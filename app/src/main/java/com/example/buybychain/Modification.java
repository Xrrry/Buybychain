package com.example.buybychain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.mob.tools.utils.DeviceHelper.getApplication;

public class Modification extends AppCompatActivity {
    private TextView ntv;
    Handler handler = new Handler();

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
        setContentView(R.layout.activity_modification);
        Buybychain application = (Buybychain) getApplication();
        ntv = findViewById(R.id.nickname);
        ntv.setHint(application.getName());
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ntv.getText().toString();
                if(name.isEmpty()) {
                    Toast.makeText(Modification.this, "请填写新昵称", Toast.LENGTH_SHORT).show();
                }
                else {
                    Buybychain application = (Buybychain) getApplication();
                    post("http://buybychain.cn:8888/modi",application.getType(), application.getPhone(), name);
                }
            }
        });
    }
    public void post(String url, String type, String user_id, final String nickname){
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("type",type)
                .add("user_id",user_id)
                .add("nickname",nickname)
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
                    System.out.println(body);
                    if(body.equals("更新成功")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "更新成功" ,Toast.LENGTH_LONG).show();
                            }
                        });
                        SharedPreferences sp = getSharedPreferences("login", getApplicationContext().MODE_PRIVATE);
                        Buybychain application = (Buybychain) getApplication();
                        application.setName(nickname);
                        application.setChange(true);
                        sp.edit()
                                .remove("name")
                                .apply();
                        sp.edit()
                                .putString("name",nickname)
                                .apply();
                        finish();
                    }
                }
            }
        });
    }
}
