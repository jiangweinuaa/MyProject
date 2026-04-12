package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.cust.JsonReq;
import lombok.Data;

/**
 * 服务函数：DCP_N_ClassDetail
 * 服务说明：N-销售分组详情
 * @author jinzma
 * @since  2024-04-18
 */
@Data
public class DCP_N_ClassDetailReq extends JsonReq {

    private Request request;

    @Data
    public class Request {
        private String classType;
        private String classNo;
    }

}
