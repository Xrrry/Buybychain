package com.example.buybychain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.View;
import android.view.Window;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.bean.Commodity;
import com.bean.PerAllHistory;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpOut extends AppCompatActivity {
    Handler handler = new Handler();
    private String com_id;

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
        setContentView(R.layout.activity_up_out);
        findViewById(R.id.scan_my).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpOut.this, MyCamera.class);
                startActivityForResult(intent, 1);
            }
        });
        findViewById(R.id.choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpOut.this, ChooseCom.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long timeStamp = System.currentTimeMillis(); //获取当前时间戳
                String out_time = String.valueOf(timeStamp).substring(0,10);
                MaterialEditText out_id = findViewById(R.id.out_id);
                post("http://buybychain.cn:8888/upOut",out_time,out_id.getText().toString());
            }
        });
    }

    public void post(String url, String out_time,String out_id){
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("out_id",out_id)
                .add("pro_acc","15555555555")
                .add("com_id",com_id)
                .add("out_time",out_time)
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
                    if(body.equals("上传成功")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "上传成功" ,Toast.LENGTH_LONG).show();
                            }
                        });
                        finish();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    final String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(UpOut.this, "扫描成功", Toast.LENGTH_SHORT).show();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MaterialEditText com_id = findViewById(R.id.out_id);
                            com_id.setText(result);
                        }
                    });
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(UpOut.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
        if (requestCode == 2) {
            if (data != null) {
                Uri uri = data.getData();
                ContentResolver cr = getContentResolver();
                try {
                    Bitmap mBitmap = MediaStore.Images.Media.getBitmap(cr, uri);//显得到bitmap图片

                    CodeUtils.analyzeBitmap(String.valueOf(mBitmap), new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            Toast.makeText(UpOut.this, "解析结果:" + result, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            Toast.makeText(UpOut.this, "解析二维码失败", Toast.LENGTH_SHORT).show();
                        }
                    });

                    if (mBitmap != null) {
                        mBitmap.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");
        Buybychain application = (Buybychain) getApplication();
        if(application.isValid()) {
            application.setValid(false);
            final Commodity commodity = application.getCommodity();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UpOut.this, "已选择模板", Toast.LENGTH_SHORT).show();
                    MaterialEditText com_name = findViewById(R.id.com_name);
                    MaterialEditText com_type = findViewById(R.id.com_type);
                    MaterialEditText com_locate = findViewById(R.id.com_locate);
                    MaterialEditText com_price = findViewById(R.id.com_price);
                    com_name.setText(commodity.getCom_name());
                    com_type.setText(commodity.getCom_cate());
                    com_locate.setText(commodity.getCom_place());
                    com_price.setText(commodity.getCom_price());
                    com_id = commodity.getCom_id();
                }
            });
        }
    }
}
