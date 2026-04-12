package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_TransApplyQueryReq extends JsonBasicReq {

    private DCP_TransApplyQueryReq.LevelElm request;

    @Data
    public class LevelElm{
        private String status;

        @JSONFieldRequired(display = "开始日期")
        private String beginDate;

        @JSONFieldRequired(display = "结束日期")
        private String endDate;

        private String keyTxt;
        private String getType;
        private String transType;
        private String applyType;
    }
}