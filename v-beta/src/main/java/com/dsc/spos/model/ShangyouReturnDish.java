package com.dsc.spos.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商有云管家申请退单，商品请求结构
 */
@Data
public class ShangyouReturnDish
{

    /**
     *菜品名称 必传
     */
    public  String name;
    /**
     *单价 必传
     */
    public BigDecimal price;
    /**
     *数量 必传
     */
    public  int num;
    /**
     *退款总额 必传
     */
    public  BigDecimal totalPrice;

}
