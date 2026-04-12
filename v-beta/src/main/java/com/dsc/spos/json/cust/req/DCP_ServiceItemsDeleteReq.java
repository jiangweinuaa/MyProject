package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: 服务项目删除
 * @author: wangzyc
 * @create: 2021-07-20
 */
@Data
public class DCP_ServiceItemsDeleteReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private List<String> itemsNo;
    }
}
