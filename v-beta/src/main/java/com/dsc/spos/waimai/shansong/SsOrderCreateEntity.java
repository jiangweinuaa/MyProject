package com.dsc.spos.waimai.shansong;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @description:
 * @author: wangzyc
 * @create: 2022-01-25
 */
@Data
@Accessors(chain = true)
public class SsOrderCreateEntity {
    private String cityName; // 城市名称
    private Integer appointType; // 0立即单，1预约单
    private String appointmentDate; // 预约时间  ,指的是预约取件时间,只支持两个小时以后两天以内
    private String storeId; // 店铺ID 不传递店铺ID订单就认为默认店铺下单
    private String travelWay; // 可指定的交通工具方式 默认为0：未指定；
    private String deliveryType; // 帮我取\帮我送 1.帮我送 2.帮我取 ；默认为1
    private Sender sender; // 发件人信息
    private List<receiver> receiverList; // 收件人信息

    @Data
    @Accessors(chain = true)
    public class Sender{
        private String fromAddress; // 寄件地址
        private String fromAddressDetail; // 寄件详细地址
        private String fromSenderName; // 寄件人姓名
        private String fromMobile; // 寄件联系人
        private String fromLatitude; // 寄件人维度
        private String fromLongitude; // 寄件人经度
    }

    @Data
    @Accessors(chain = true)
    public class receiver{
        private String orderNo; // 第三方平台流水号
        private String toAddress; // 手贱地址
        private String toAddressDetail; // 收件详细地址
        private String toLatitude; // 收件纬度
        private String toLongitude; // 收件纬度
        private String toReceiverName; // 收件人姓名
        private String toMobile; // 收件联系人
        private Integer goodType; // 收件联系人
        private Integer weight; // 物品重量
        private String remarks; // 	备注
//        private String insurance; // 保险费用
//        private String insuranceProId; // 保险产品ID
//        private String additionFee; // 保险产品ID
//        private String orderingSourceType; // 保险产品ID
//        private String orderingSourceNo; // 物品来源流水号
    }

}
