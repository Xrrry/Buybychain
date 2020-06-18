package com.example.buybychain;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpSell extends AppCompatActivity {

    private Handler handler = new Handler();

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
        setContentView(R.layout.activity_up_sell);

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialEditText com_id = findViewById(R.id.com_id);
                MaterialEditText cus_id = findViewById(R.id.cus_id);
                MaterialEditText track_id = findViewById(R.id.track_id);
                String com = com_id.getText().toString();
                String cus = cus_id.getText().toString();
                String track = track_id.getText().toString();
                if(com.isEmpty()||cus.isEmpty()||track.isEmpty()) {
                    Toast.makeText(UpSell.this, "请填写完整", Toast.LENGTH_SHORT).show();
                }
                else {
                    findViewById(R.id.submit).setClickable(false);
                    Long timeStamp = System.currentTimeMillis(); //获取当前时间戳
                    String out_time = String.valueOf(timeStamp).substring(0,10);
                    System.out.println(out_time);
                    post("http://buybychain.cn:8888/upSell",com, cus, out_time, track);
                }
            }
        });
    }

    public void post(String url, String sell_id, String sell_cus_acc, String sell_time, String sell_track_num){
        OkHttpClient client = new OkHttpClient();
        final Buybychain application = (Buybychain) getApplication();
        FormBody body = new FormBody.Builder()
                .add("sell_id",sell_id)
                .add("sell_time",sell_time)
                .add("sell_sal_acc",application.getPhone())
                .add("sell_cus_acc",sell_cus_acc)
                .add("sell_track_num",sell_track_num)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
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
                    if (body.equals("上传成功")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "上传成功" ,Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                    }
                    else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "上传失败" ,Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        });
    }
}
