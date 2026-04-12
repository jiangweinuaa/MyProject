package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: DCP_TouchMenuQuery
 * @author: wangzyc
 * @create: 2021-06-15
 */
@Data
public class DCP_TouchMenuQueryReq extends JsonBasicReq {

    private level1Elm request;

    @Data
    public class level1Elm{
        private String keyTxt;  // 关键字，模糊检索菜单名称和编号
        private String status;  // 状态：-1未启用100已启用0已禁用
        private String shopId;  // 适用门店id
        private String channelId;   // 适用渠道id
    }

}
