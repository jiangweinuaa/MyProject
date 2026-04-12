package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服务函数：DCP_N_ClassQuery
 * 服务说明：N-销售分组查询
 * @author jinzma
 * @since  2024-04-18
 */
@Data
public class DCP_N_ClassQueryReq extends JsonBasicReq {

    private Request request;

    @Data
    public class Request {
        private String status;
        private String keyTxt;
        private String shopId;
        private String channelId;
        private String appType;
        private String classType;
        private String levelId;
        private String appNo; // 入参新增appNo，根据CRM_CHANNEL.APPNO下属所有channelId匹配销售分组适用渠道（包括全部） By 2021/6/1
        private String isPublic;  //分类性质：1共用分组2.指定分组
    }

}
