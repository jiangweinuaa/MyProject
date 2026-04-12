package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.JsonReq;
import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * 服务函数：DCP_ChannelStockDetail
 * 服务说明：渠道分配库存查询
 * @author wangzyc 2021-03-16
 */
@Data
public class DCP_ChannelStockDetailReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String pluNo;            // 品号
        private String ruleType;         // 分配规则
        private List<level2Elm> organizationList; // 机构列表
        private String keyTxt;          // 关键字（品号，品名）
    }

    @Data
    public class level2Elm{
        private String organizationNo;  // 机构
    }
}
