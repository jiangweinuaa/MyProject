package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_N_GoodsOnlinePreSaleInfoUpdate
 * 服务说明：N-商城商品修改预订与发货  (Yapi没有规格)
 * @author jinzma
 * @since  2024-04-18
 */
@Data
public class DCP_N_GoodsOnlinePreSaleInfoUpdateReq extends JsonBasicReq {

    private Request request;

    @Data
    public class Request {
        private List<level1Plu> pluList;
        private level1PreSaleInfo preSaleInfo;

    }
    @Data
    public class level1Plu {
        private String pluNo;
    }
    @Data
    public class level1PreSaleInfo {
        private String preSale;//是否预订，需提前预订0-否1-是
        private String deliveryDateType;//发货时机类型1：付款成功后发货2：指定日期发货
        private String deliveryDateType2;//发货时间类型1：小时 2：天
        private String deliveryDateValue;//付款后%S天后发货
        private String deliveryDate;//预计发货日期
        private String deliveryStartDate; //预计发货开始日期
        private String deliveryEndDate; //预计发货截止日期。
    }




}
