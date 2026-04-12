package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_KdsCookStatusUpdate_OpenReq extends JsonBasicReq
{

    private level1Elm request;


    @Data
    public class level1Elm
    {
        private String shopId; // 门店编号
        private String machineId; // 机台编号
        private String cookId;
        private String cookStatus;
    }

}
