package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * 服务函数：DCP_StockAllocationRuleUpdate
 * 服务说明：分配规则设置修改
 * @author wangzyc 2021-03-15
 */
@Data
public class DCP_StockAllocationRuleUpdateReq extends JsonBasicReq {

    private level1ELm request;

    @Data
    public class level1ELm{
        private List<level2ELm> pluList; // 商品列表
        private String dealType;         // 操作类型（0库存不自动分配，1库存自动分配）
    }

    @Data
    public class level2ELm{
        private String organizationNo;  // 机构
        private String pluNo;           // 品号
        private String featureNo;       // 特征码
        private String sUnit;           // 交易单位
        private String ruleType;        // 分配规则
        private List<level3ELm> channelList; // 渠道列表（新增调用时该节点传空）
    }

    @Data
    public class level3ELm{
        private String channelId;       // 渠道id
        private String allocationValue; // 分配比例/数量


    }


}
