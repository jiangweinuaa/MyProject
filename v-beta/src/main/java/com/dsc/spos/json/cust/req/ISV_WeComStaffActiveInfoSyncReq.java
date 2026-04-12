package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服务函数：ISV_WeComStaffActiveInfoSync
 * 服务说明：更新企微员工激活信息
 * @author jinzma
 * @since  2023-09-12
 */
@Data
public class ISV_WeComStaffActiveInfoSyncReq extends JsonBasicReq {
    private levelElm request;
    @Data
    public class levelElm{
        private String corpId;
    }
}
