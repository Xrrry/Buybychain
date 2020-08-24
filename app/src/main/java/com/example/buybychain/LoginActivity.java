package com.example.buybychain;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.bean.PerAllHistory;
import com.bean.SearchDetail;
import com.bean.User;
import com.google.gson.Gson;
import com.mob.MobSDK;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.util.Base64;
import java.io.UnsupportedEncodingException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static LoginActivity instance;
    private EditText etPhoneNumber;    // 电话号码
    private Button sendVerificationCode;  // 发送验证码
    private EditText etVerificationCode;  // 验证码
    private Button nextStep;        // 下一步
    private String phoneNumber;     // 电话号码
    private String verificationCode;  // 验证码
    private Handler Myhandler = new Handler();
    private boolean flag;  // 操作是否成功
    private String type = null;
    Handler mhandler = new Handler();
    private Boolean isNew = false;
    private String name = null;
    private Boolean f = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        if (Launcher.instance != null) {
            Launcher.instance.finish();
        }
        if (HomeActivity.instance != null) {
            HomeActivity.instance.finish();
        }
        if (ProducerHomeActivity.instance != null) {
            ProducerHomeActivity.instance.finish();
        }
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
        setContentView(R.layout.activity_login);

//        SharedPreferences sp = getSharedPreferences("login", getApplicationContext().MODE_PRIVATE);
//        String phone = sp.getString("phone", null);
//        if (phone != null) {
//            Buybychain application = (Buybychain) getApplicationContext();
//            application.setPhone(phone);
//            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//            startActivity(intent);
//            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
//        }

        etPhoneNumber = (EditText) findViewById(R.id.editText);
        sendVerificationCode = (Button) findViewById(R.id.button2);
        etVerificationCode = (EditText) findViewById(R.id.editText2);
        sendVerificationCode.setOnClickListener(this);
        nextStep = (Button) findViewById(R.id.submit);
        nextStep.setOnClickListener(this);

        MobSDK.submitPolicyGrantResult(true, null);
        EventHandler eventHandler = new EventHandler(){    // 操作回调
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);   // 注册回调接口

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button2:
                if (!TextUtils.isEmpty(etPhoneNumber.getText())) {
                    if (etPhoneNumber.getText().length() == 11) {
                        phoneNumber = etPhoneNumber.getText().toString();
                        SMSSDK.getVerificationCode("86", phoneNumber); // 发送验证码给号码的 phoneNumber 的手机
                        etVerificationCode.requestFocus();
                        post1("http://buybychain.cn:8888/loginSearch",phoneNumber);
                        System.out.println("onpost1");
                        Myhandler.post(new Runnable() {
                            @Override
                            public void run() {
                                sendVerificationCode.setClickable(false);
                            }
                        });
                    }
                    else {
                        Toast.makeText(this, "请输入完整的电话号码", Toast.LENGTH_SHORT).show();
                        etPhoneNumber.requestFocus();
                    }
                } else {
                    Toast.makeText(this, "请输入电话号码", Toast.LENGTH_SHORT).show();
                    etPhoneNumber.requestFocus();
                }
                break;

            case R.id.submit:
                if (!TextUtils.isEmpty(etVerificationCode.getText())) {
                    if (etVerificationCode.getText().length() == 6) {
                        verificationCode = etVerificationCode.getText().toString();
                        SMSSDK.submitVerificationCode("86", phoneNumber, verificationCode);
                        flag = false;
                    } else {
                        Toast.makeText(this, "请输入完整的验证码", Toast.LENGTH_SHORT).show();
                        etVerificationCode.requestFocus();
                    }
                } else {
                    Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
                    etVerificationCode.requestFocus();
                }
                break;

            default:
                break;
        }
    }

    public String transformJson(String s){
        String string = s.substring(6);
        string = string.replace("{","{\"").replace("}","\"}");
        string = string.replaceAll("=","\":\"").replaceAll(", ","\",\"");
        return string;
    }

    public void post1(String url, final String phone){
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("phone",phone)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mhandler.post(new Runnable() {
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
                    if (!body.equals("不存在")) {
                        System.out.println("checkisnotnew");
                        Gson gson = new Gson();
                        String b = transformJson(body);
                        User user = gson.fromJson(b,User.class);
                        type = user.getUser_type();
                        System.out.println(type);
                        post3("http://buybychain.cn:8888/searchname", phone, type);
                    }
                    else {
                        System.out.println("checkisnew");
                        isNew = true;
                    }
                }
            }
        });
    }

    public void post2(String acc, String name, String url){
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("acc", acc)
                .add("name", name)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mhandler.post(new Runnable() {
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
                    if (body.equals("上传成功")) {
                        mhandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "新用户注册成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                            }
                        });
                    }
                }
            }
        });
    }

    public void post3(String url, String acc, String type){
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("phone", acc)
                .add("type", type)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mhandler.post(new Runnable() {
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
                    name = body;
                }
            }
        });
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;

            if (result == SMSSDK.RESULT_COMPLETE) {
                // 如果操作成功
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    // 校验验证码，返回校验的手机和国家代码
                    EditText ed1 = (EditText) findViewById(R.id.editText);
                    final String phone = ed1.getText().toString();
                    final Buybychain application = (Buybychain) getApplication();
                    application.setPhone(phone);
                    SharedPreferences sp = getSharedPreferences("login", getApplicationContext().MODE_PRIVATE);
                    sp.edit()
                            .putString("phone", phone)
                            .apply();
                    if(!isNew) {
                        System.out.println("isnotnew");
                        System.out.println(name);
                        application.setType(type);
                        application.setName(name);
                        sp.edit()
                                .putString("type", type)
                                .putString("name", name)
                                .apply();
                        application.setType(type);
                        if(type.equals("1")) {
                            Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                        }
                        else if (type.equals("2")||type.equals("3")){
                            Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ProducerHomeActivity.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                        }
                    }
                    else {
                        System.out.println("isnew");
                        Long timeStamp = System.currentTimeMillis();  //获取当前时间戳
                        String sd = String.valueOf(timeStamp);
                        String newname = "用户";
                        try {
                            newname = newname.concat(Base64.getEncoder().encodeToString(sd.substring(0,10).getBytes("UTF-8")));
                            newname = newname.replaceAll("=","");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        System.out.println(newname);
                        sp.edit()
                                .putString("type","1")
                                .putString("name", newname)
                                .apply();
                        application.setName(newname);
                        application.setType("1");
                        post2(phone, newname, "http://buybychain.cn:8888/newcus");
                    }
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    // 获取验证码成功，true为智能验证，false为普通下发短信
                    Toast.makeText(getApplicationContext(), "验证码已发送", Toast.LENGTH_SHORT).show();
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    // 返回支持发送验证码的国家列表
                }
            } else {
                // 如果操作失败
                if (flag) {
                    Toast.makeText(getApplicationContext(), "验证码获取失败，请重新获取", Toast.LENGTH_SHORT).show();
                    etPhoneNumber.requestFocus();
                } else {
                    ((Throwable) data).printStackTrace();
                    Toast.makeText(getApplicationContext(), "手机号格式错误", Toast.LENGTH_SHORT).show();
                }
            }
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler(); // 注销回调接口
    }

}
