package com.bean;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class HisSellitem {
    @SerializedName("value1")
    private String id;
    @SerializedName("value2")
    private String sell_id;
    @SerializedName("value3")
    private String sell_time;
    @SerializedName("value4")
    private String sell_sal_acc;
    @SerializedName("value5")
    private String sell_nickname;
    @SerializedName("value6")
    private String sal_cred;
    @SerializedName("value7")
    private String sell_cus_acc;
    @SerializedName("value8")
    private String cus_nickname;
    @SerializedName("value9")
    private String sell_track_num;
}