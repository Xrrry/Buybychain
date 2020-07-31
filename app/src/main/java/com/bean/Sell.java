package com.bean;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Sell {
    @SerializedName("value1")
    private String id;
    @SerializedName("value2")
    private String sell_id;
    @SerializedName("value3")
    private String sell_time;
    @SerializedName("value4")
    private String sell_sal_acc;
    @SerializedName("value5")
    private String sell_cus_acc;
    @SerializedName("value6")
    private String sell_track_num;
}