package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: 服务项目查询
 * @author: wangzyc
 * @create: 2021-07-20
 */
@Data
public class DCP_ServiceItemsQueryReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String keyTxt;  // 关键词，项目名称或编号
        private String status;  // 状态
    }
}
