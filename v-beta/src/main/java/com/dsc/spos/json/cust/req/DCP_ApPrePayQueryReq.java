package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ApPrePayQueryReq extends JsonBasicReq
{
    @JSONFieldRequired
    private DCP_ApPrePayQueryReq.levelRequest request;

    @Data
    public class levelRequest{

        private String status;
        @JSONFieldRequired
        private String accountId;
        private String taskId;
        private String beginDate;
        private String endDate;
        private String keyTxt;

        //   "status": "-1 未启用 100 已启用 0 已禁用",
        //        "accountId": "string",
        //        "taskId": "8：预付待抵单；9：员工借支待抵单",
        //        "beginDate": "string",
        //        "endDate": "string",
        //        "keyTxt": "string"

    }

}
