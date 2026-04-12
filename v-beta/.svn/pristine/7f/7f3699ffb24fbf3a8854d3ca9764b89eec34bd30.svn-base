package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * 服务函数：DCP_StockAllocationRuleDelete
 * 服务说明：分配规则设置删除
 * @author wangzyc 2021-03-16
 */
@Data
public class DCP_StockAllocationRuleDeleteReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private List<level2Elm> pluList;        // 商品列表
    }

    @Data
    public class level2Elm{
        private String organizationNo;          // 机构
        private String pluNo;                   // 品号
        private String featureNo;               // 特征码
        private String sUnit;                   // 交易单位
    }

}
