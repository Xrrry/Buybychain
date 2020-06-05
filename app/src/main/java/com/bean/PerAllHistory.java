package com.bean;


import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class PerAllHistory {
    @SerializedName("value1")
    private String id;
    @SerializedName("value2")
    private String cus_acc;
    @SerializedName("value3")
    private String his_time;
    @SerializedName("value4")
    private String out_id;
    @SerializedName("value5")
    private String com_name;
}
