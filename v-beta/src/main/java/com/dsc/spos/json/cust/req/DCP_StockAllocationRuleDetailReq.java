package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * 服务函数：DCP_StockAllocationRuleDetail
 * 服务说明：分配规则设置查询
 * @author wangzyc 2021-03-15
 */
@Data
public class DCP_StockAllocationRuleDetailReq extends JsonBasicReq {
    private level1ELm request;

    @Data
    public class level1ELm{
        private String keyTxt;      // 关键字 （品号或品名）
        private String ruleType;    // 分配规则（空全部0按数量 1按比例）
        private List<level2ELm> organizationList; // 机构列表
    }

    @Data
    public class level2ELm{
        private String organizationNo; // 机构
    }
}
