package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: 服务项目启用/禁用
 * @author: wangzyc
 * @create: 2021-07-20
 */
@Data
public class DCP_ServiceItemsEnableReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String oprType;
        private List<level2Elm> itemsNoList;
    }
    @Data
    public class level2Elm{
        private String itemsNo; // 项目编号
    }
}
