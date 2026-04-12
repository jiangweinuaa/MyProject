package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服务函数：DCP_ISVWeComStaffQuery
 * 服务说明：查询企微员工列表
 * @author jinzma
 * @since  2023-09-13
 */
@Data
public class DCP_ISVWeComStaffQueryReq extends JsonBasicReq {
    private levelElm request;
    @Data
    public class levelElm{
        private String keyTxt;
        private String accountType;
    }
}
