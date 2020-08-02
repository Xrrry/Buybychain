package com.bean;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class SearchDetail {
    private String out_id; //唯一标识
    private String com_id; //模板标识
    private String pro_acc; //厂商id
    private String out_birthday; //出厂时间
    private String pro_nickname; //厂商昵称
    private String com_name; //商品名
    private String com_cate; //商品类型
    private String com_price; //商品价格
    private String com_place; //产品产地
    private String all_his_sell; //所有卖出记录
    private String all_his_query; //所有查询记录
}