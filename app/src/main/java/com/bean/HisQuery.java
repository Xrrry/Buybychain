package com.bean;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class HisQuery {
    private List<HisQueryitem> hisQueryList;
}
