package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * 服务函数：DCP_N_GoodsOnAndOffShelf
 * 服务说明：N-商品上下架
 * @author jinzma
 * @since  2024-04-18
 */
@Data
public class DCP_N_GoodsOnAndOffShelfReq extends JsonBasicReq {

    private Request request;
    @Data
    public class Request {
        private String orgType;
        private String orgId;
        private String oprType;
        private String onShelfAuto;
        private String onShelfDate;
        private String offShelfAuto;
        private String offShelfDate;
        private String billType;

        private List<Plu> pluList;

    }
    @Data
    public class Plu {
        private String pluNo;
        private String pluName;
        private String pluType;
        private List<Shop> shopList;

    }
    @Data
    public class Shop {
        private String shopId;
        private String shopName;
        private List<Channel> channelList;
    }
    @Data
    public class Channel {
        private String channelId;
    }


}


