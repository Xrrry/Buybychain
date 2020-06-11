package com.example.buybychain;

import android.app.Application;
import android.util.DisplayMetrics;

import com.bean.Commodity;
import com.uuzuche.lib_zxing.DisplayUtil;

import lombok.Data;

@Data
public class Buybychain extends Application {
    private String phone;
    private String type;
    private Commodity commodity;
    private boolean isValid = false;

    @Override
    public void onCreate() {
        super.onCreate();
        initDisplayOpinion();
    }

//    public String getPhone() {
//        return phone;
//    }
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }
//    public void setType(String type) {
//        this.type = type;
//    }
//    public String getType() {
//        return type;
//    }
    private void initDisplayOpinion() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        DisplayUtil.density = dm.density;
        DisplayUtil.densityDPI = dm.densityDpi;
        DisplayUtil.screenWidthPx = dm.widthPixels;
        DisplayUtil.screenhightPx = dm.heightPixels;
        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(getApplicationContext(), dm.widthPixels);
        DisplayUtil.screenHightDip = DisplayUtil.px2dip(getApplicationContext(), dm.heightPixels);
    }
}
