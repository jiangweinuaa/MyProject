package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_N_GoodsMustOrder
 * 服务说明：N-商品必点设置
 * @author jinzma
 * @since  2024-04-17
 */
@Data
public class DCP_N_GoodsMustOrderReq extends JsonBasicReq {
    private Request request;

    @Data
    public class Request {
        private String orgType;
        private String orgId;
        private List<Plu> pluList;
    }

    @Data
    public class Plu {
        private String pluNo;
        private String pluType;
        private String classNo;
        private String remindType;
    }

}
