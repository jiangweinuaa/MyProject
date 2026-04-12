package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_N_GoodsOnlineLogisticsInfoUpdate
 * 服务说明：N-商城商品修改物流与运费
 * @author jinzma
 * @since  2024-05-08
 */
@Data
public class DCP_N_GoodsOnlineLogisticsInfoUpdateReq extends JsonBasicReq {
    private Request request;
    @Data
    public class Request {
        private List<Plu> pluList;
        private LogisticsInfo logisticsInfo;
    }
    @Data
    public class Plu {
        private String pluNo;
    }
    @Data
    public class LogisticsInfo {
        private String shopPickUp;     //是否支持自提0-否1-是
        private String cityDeliver;    //是否支持同城配送0-否1-是
        private String expressDeliver; //是否支持全国快递0-否1-是
        private String freightFree;    //是否包邮0-否1-是
        private String freightTemplateId;  //运费模板编码
        private String isRestaurant;   //是否堂食0-否1-是
    }


}
