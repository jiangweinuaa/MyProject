package com.dsc.spos.thirdpart.duandian;

import lombok.Data;

import java.util.List;

@Data
public class channelOrderDTO {
    private String channelOrderCode;
    private String channelType = "POS";
    private String orderSource = "DJ_POS";
    private String orderType = "2C";
    private String orderDetailType = "POS_SALE_RECORD";
    private String orderStatus = "PAIED";//传已支付：PAIED
    private String isTempOrder = "0";//是否临时单 传0 否
    private String buyerId;
    private String buyerAccount;
    private String buyerName;
    private String buyerPhone;
    private String receiverName;
    private String receiverPhone;
    private String receiverProvince;
    private String receiverCity;
    private String receiverArea;
    private String receiverAddress;
    private String actualPrice;
    private String channelOrderCreateTime;//时间格式 2023-04-11 14:59:57
    private String channelOrderPayTime;//时间格式 2023-04-11 14:59:57
    private String channelOrderUpdateTime;//时间格式 2023-04-11 14:59:57
    private String siteCode;//门店编码
    private String siteName;
    private String point;
    private List<channelOrderLineDTO> channelOrderLineList;

    /**************逆向销售单，节点不一样 气死人******************/
    private String channelReverseOrderCode;//退订单号
    private String forwardOrderFlag = "2C";//默认传2C
    private String channelReverseOrderType ;//= "RETAIL_ORDER_RETURN_REFUND";//默认传：RETAIL_ORDER_RETURN_REFUND
    private String channelReverseOrderDetailType;// = "UNSUBSCRIBE";//默认传：UNSUBSCRIBE
    private String channelReverseOrderStatus;// = "UNSUBSCRIBE";//默认传：UNSUBSCRIBE
    private String channelReverseOrderCreateTime;//就是该退订单的创建时间
    private String refundAmt;//退款金额
    private List<channelOrderLineDTO> channelReverseOrderList;

    @Data
    public class channelOrderLineDTO
    {
        private String channelOrderLineCode;//鼎捷传项次比如1,2,3等等
        private String channelOrderCode;//和头信息订单号一致，传鼎捷订单号
        private String channelOrderLineType = "CONVENTION";//鼎捷传“CONVENTION”常规
        private String erpGoodsCode;
        private String erpGoodsName;
        private String erpGoodsUnit;
        private String erpGoodsUnitPrice;
        private String goodsQty;
        private String deliveryStatus = "DELIVERED";//鼎捷传“DELIVERED”已配送
        private String actualPayAmt;

        /***********逆向销售单，传入节点**************/
        private String channelReverseOrderCode;//默认传：退订单号
        private String originalGoodsCode;
        private String originalGoodsName;
        private String originalErpGoodsCode;
        private String originalErpGoodsName;
        private String refundAmt;
    }




}
