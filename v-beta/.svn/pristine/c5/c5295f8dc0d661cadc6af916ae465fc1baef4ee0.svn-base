package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_N_StockAllocationRuleDetail
 * 服务说明：N_分配规则设置查询
 * @author jinzma
 * @since  2024-05-07
 */
@Data
public class DCP_N_StockAllocationRuleDetailRes extends JsonRes {
    private Datas datas;
    @Data
    public class Datas {
        private List<Plu> pluList;
    }
    @Data
    public class Plu {
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String sUnit;
        private String sUnitName;
        private List<Shop> shopList;
    }
    @Data
    public class Shop {
        private String organizationNo;
        private String organizationName;
        private String ruleType;
        private List<Channel> channelList;
    }
    @Data
    public class Channel {
        private String channelId;
        private String channelName;
        private String allocationValue;
    }

}
