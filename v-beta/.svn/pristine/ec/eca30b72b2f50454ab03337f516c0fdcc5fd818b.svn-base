package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: DCP_TouchMenuEnable
 * @author: wangzyc
 * @create: 2021-06-15
 */
@Data
public class DCP_TouchMenuEnableReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String oprType;     // 操作类型：1-启用 2禁用
        private List<level2Elm> menuList; // 菜单编码
    }

    @Data
    public class level2Elm{
        private String menuNo;      // 菜单编码
    }
}
