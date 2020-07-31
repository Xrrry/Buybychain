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
    private Integer all_sell_count; //所有卖出次数
    private String all_his_sell; //所有卖出记录
    //以下先只传最后一次的信息
    private String sell_id; //卖出信息id
    private String sell_sal_acc; //卖家id
    private String sal_nickname; //卖家昵称
    private String sal_cred; //卖家平均信誉分
    private String sal_cnt; //卖家评价人数
    private String sal_total; //卖家评价总分
    private String sell_time; //卖出时间
    private String sell_cus_acc; //买家id
    private String cus_nickname; //买家昵称
    private String sell_track_num; //快递号
}