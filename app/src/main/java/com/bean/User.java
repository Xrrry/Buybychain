package com.bean;


import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    @SerializedName("value1")
    private String id;
    @SerializedName("value2")
    private String user_id;
    @SerializedName("value3")
    private String user_type;
}
