package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * 服务函数：DCP_N_GoodsShelfQuery
 * 服务说明：N-商品上下架查询
 * @author jinzma
 * @since  2024-04-22
 */
@Data
public class DCP_N_GoodsShelfQueryRes extends JsonRes {
    private Datas datas;
    @Data
    public class Datas {
          private String pluNo;
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
        private String channelName;
        private String status;
        private String onShelfAuto;
        private String onShelfDate;
        private String offShelfAuto;
        private String offShelfDate;
    }

}
