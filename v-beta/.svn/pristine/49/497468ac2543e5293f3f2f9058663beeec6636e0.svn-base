package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_N_StockAllocationRuleDetail
 * 服务说明：N_分配规则设置查询
 * @author jinzma
 * @since  2024-05-07
 */
@Data
public class DCP_N_StockAllocationRuleDetailReq extends JsonBasicReq {

    private Request request;
    @Data
    public class Request{
        private String pluNo;       // 品号
        private String ruleType;    // 分配规则（空全部0按数量 1按比例）
        private List<Organization> organizationList; // 机构列表
    }

    @Data
    public class Organization{
        private String organizationNo; // 机构
    }
}
