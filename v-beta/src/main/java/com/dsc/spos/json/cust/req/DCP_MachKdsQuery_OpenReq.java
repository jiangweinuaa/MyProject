package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服務函數：DCP_MachKdsQuery_Open
 * 服务说明：KDS信息查询
 * @author wangzyc
 * @since  2021/4/13
 */
@Data
public class DCP_MachKdsQuery_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class  level1Elm{
        private String kds; // KDS是否启用 Y/N
    }
}
