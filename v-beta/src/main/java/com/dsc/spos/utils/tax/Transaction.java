package com.dsc.spos.utils.tax;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 用于Dcptransaction表值的汇总插入
 */
@Data
@EqualsAndHashCode(exclude = {"qty", "distriAmount", "category"})
public class Transaction {
    //分组条件    品号+特征码+单位+税别+税率+单价含税+单价
    private String pluNo;
    private String featureNo;
    private String unit;
    private String taxCode;
    private String taxRate;
    private String taxcalType;
    private String inclTax;
    private double distriPrice;
//    private double price;

    private String category;
    private double qty;
    //    private BigDecimal amount;
    private BigDecimal distriAmount;


}
