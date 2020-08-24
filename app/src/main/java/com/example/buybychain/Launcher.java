package com.example.buybychain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

public class Launcher extends AppCompatActivity {
    public static Launcher instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        // 隐藏标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTheme(R.style.AppTheme);//恢复原有的样式
        setContentView(R.layout.activity_launcher);

        SharedPreferences sp = getSharedPreferences("login", getApplicationContext().MODE_PRIVATE);
        String phone = sp.getString("phone", null);
        String type = sp.getString("type","1");
        String name = sp.getString("name","未注册用户");
        Timer timer=new Timer();
        TimerTask task1=new TimerTask() {
            public void run() {
                Intent intent = new Intent(Launcher.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };
        TimerTask task2=new TimerTask() {
            public void run() {
                Intent intent = new Intent(Launcher.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };
        TimerTask task3=new TimerTask() {
            public void run() {
                Intent intent = new Intent(Launcher.this, ProducerHomeActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };
        if (phone != null) {
            Buybychain application = (Buybychain) getApplication();
            application.setPhone(phone);
            application.setType(type);
            application.setName(name);
            if(type.equals("1")) {
                timer.schedule(task1,1000); //用户
            }
            else {
                timer.schedule(task3,1000); // 商家/卖家
            }
        }
        else {
            timer.schedule(task2,1000); // 登录
        }
    }
}
