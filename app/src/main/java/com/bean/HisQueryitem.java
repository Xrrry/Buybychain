package com.bean;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class HisQueryitem {
    @SerializedName("value1")
    private String id;
    @SerializedName("value2")
    private String his_out_id;
    @SerializedName("value3")
    private String his_time;
    @SerializedName("value4")
    private String his_cus_acc;
}