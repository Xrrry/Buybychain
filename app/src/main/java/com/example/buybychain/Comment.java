package com.example.buybychain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.Saler;
import com.google.gson.Gson;
import com.hedgehog.ratingbar.RatingBar;

import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Comment extends AppCompatActivity {
    private float ratingStar = 0;
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
        setContentView(R.layout.activity_comment);
        final Intent intent = getIntent();
        final String sal_acc = intent.getStringExtra("sal_acc");
        final String nickname = intent.getStringExtra("sal_nickname");
        final TextView textView = findViewById(R.id.saler);
        handler.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(nickname);
            }
        });
        RatingBar mRatingBar = (RatingBar) findViewById(R.id.ratingbar);
        mRatingBar.setOnRatingChangeListener(
                new RatingBar.OnRatingChangeListener() {
                    @Override
                    public void onRatingChange(float RatingCount) {
                        ratingStar = RatingCount;
                        System.out.println(ratingStar);
                    }
                }
        );
        Button button = findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float cnt = Float.valueOf(intent.getStringExtra("sal_cnt")) + 1;
                float total = Float.valueOf(intent.getStringExtra("sal_total")) + ratingStar;
                int cred = Math.round(total/cnt*100);
                String sal_cnt = String.valueOf(Math.round(cnt));
                String sal_total = String.valueOf(Math.round(total));
                String sal_cred = String.valueOf(cred);
                post("http://buybychain.cn:8888/comment",sal_acc, nickname, sal_cred, sal_cnt, sal_total);
            }
        });
    }
    public void post(String url, String sal_acc, String sal_nickname, String sal_cred, String sal_cnt, String sal_total){
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("sal_acc",sal_acc)
                .add("sal_nickname",sal_nickname)
                .add("sal_cred", sal_cred)
                .add("sal_cnt", sal_cnt)
                .add("sal_total",sal_total)
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
                    if(body.equals("更新成功")){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "评价成功" ,Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                }
            }
        });
    }
    public String phoneTrans(String s) {
        return s.substring(0,3) + "****" + s.substring(7,11);
    }
}
