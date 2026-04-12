package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * 服务函数：DCP_StockAllocationRuleDetail
 * 服务说明：分配规则设置查询
 * @author wangzyc 2021-03-15
 */
@Data
public class DCP_StockAllocationRuleDetailRes extends JsonRes {
    private level1Elm datas;

    @Data
    public class level1Elm{
        private List<level2Elm> pluList;    // 商品列表
    }

    @Data
    public class level2Elm{
        private String organizationNo;      // 机构编号
        private String organizationName;    // 机构名称
        private String pluNo;               // 品号
        private String pluName;             // 品名
        private String featureNo;           // 特征码
        private String featureName;         // 特征码名称
        private String sUnit;               // 交易单位
        private String sUnitName;           // 交易单位名称
        private String listImage;           // 图片名称
        private String ruleType;            // 分配规则
        private List<level3Elm> channelList;// 渠道列表
    }

    @Data
    public class level3Elm{
        private String channelId;           // 渠道ID
        private String channelName;         // 渠道名称
        private String allocationValue;     // 分配你比例/数量
    }
}
