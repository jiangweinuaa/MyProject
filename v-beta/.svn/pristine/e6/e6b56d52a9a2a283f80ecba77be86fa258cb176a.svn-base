package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: 预约项目删除
 * @author: wangzyc
 * @create: 2021-07-21
 */
@Data
public class DCP_ReserveItemsDeleteReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private List<level2Elm> itemsNoList;
        private String shopId;
    }

    @Data
    public class level2Elm{
        private String itemsNo;
    }

}
