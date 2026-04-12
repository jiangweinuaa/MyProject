package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_N_StockAllocationRuleUpdate
 * 服务说明：N_分配规则设置修改
 * @author jinzma
 * @since  2024-04-22
 */
@Data
public class DCP_N_StockAllocationRuleUpdateReq extends JsonBasicReq {
    private Request request;
    @Data
    public class Request {
         private String dealType;
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
        private String organizationNo;
        private String ruleType;
        private List<Channel> channelList;
    }
    @Data
    public class Channel {
        private String channelId;
        private String allocationValue;
    }


}
