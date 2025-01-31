package com.example.buybychain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ProducerHomeActivity extends AppCompatActivity implements ProducerHomeFragment.OnFragmentInteractionListener, ProducerMyFragment.OnFragmentInteractionListener {

    private RadioGroup mTabRadioGroup;
    private SparseArray<Fragment> mFragmentSparseArray;
    public static ProducerHomeActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        if (LoginActivity.instance != null) {
            LoginActivity.instance.finish();
        }
        if (Launcher.instance != null) {
            Launcher.instance.finish();
        }
        if (HomeActivity.instance != null) {
            HomeActivity.instance.finish();
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
        setContentView(R.layout.activity_producer_home);

        findViewById(R.id.scan_my).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProducerHomeActivity.this, MyCamera.class);
                startActivityForResult(intent, 1);
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("image/*");
//                startActivityForResult(intent, 2);
            }
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    ProducerHomeActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            // readContacts();
        }
        initView();
    }


    private void initView() {
        mTabRadioGroup = findViewById(R.id.tabs_rg);
        mFragmentSparseArray = new SparseArray<>();
        mFragmentSparseArray.append(R.id.home_tab, ProducerHomeFragment.newInstance("1","2"));
        mFragmentSparseArray.append(R.id.my_tab, ProducerMyFragment.newInstance("1","2"));
        mTabRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 具体的fragment切换逻辑可以根据应用调整，例如使用show()/hide()
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        mFragmentSparseArray.get(checkedId)).commit();
                switch (checkedId){
                    case R.id.home_tab:
//                        Toast.makeText(getApplicationContext(),"home",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.my_tab:
//                        Toast.makeText(getApplicationContext(), "my", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        // 默认显示第一个
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                mFragmentSparseArray.get(R.id.home_tab)).commit();
//        findViewById(R.id.scan_my).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(Style3Activity.this, SignActivity.class));
//            }
//        });
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
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(ProducerHomeActivity.this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(ProducerHomeActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(ProducerHomeActivity.this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            Toast.makeText(ProducerHomeActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
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
    public void onFragmentInteraction(Uri uri) {

    }
}
