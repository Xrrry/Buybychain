package com.bean;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class Saler {
    @SerializedName("value1")
    private String id;
    @SerializedName("value2")
    private String sal_acc;
    @SerializedName("value3")
    private String sal_nickname;
    @SerializedName("value4")
    private String sal_cred;
    @SerializedName("value5")
    private String sal_cnt;
    @SerializedName("value6")
    private String sal_total;
}