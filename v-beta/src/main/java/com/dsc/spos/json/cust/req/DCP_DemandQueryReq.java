package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_DemandQueryReq extends JsonBasicReq
{

    @JSONFieldRequired(display = "请求参数")
    private DCP_DemandQueryReq.LevelRequest request;

    @Data
    public class LevelRequest{

        @JSONFieldRequired(display = "需求类型")
        private String orderType;
        @JSONFieldRequired(display = "日期类型")
        private String dateType;
        @JSONFieldRequired(display = "开始日期")
        private String beginDate;
        @JSONFieldRequired(display = "结束日期")
        private String endDate;
        @JSONFieldRequired(display = "采购类型")
        private String purType;
        private String purCenter;
        private String orderNo;
        private String[] objectList;
        private String isCheckRestrictGroup;
        private String closeStatus;
        private String queryType;
    }

}
