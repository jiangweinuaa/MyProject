package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_DemandPreAllotCalculateReq extends JsonBasicReq
{

    @JSONFieldRequired(display = "请求参数")
    private DCP_DemandPreAllotCalculateReq.levelRequest request;

    @Data
    public class levelRequest{
        private String dateType;
        @JSONFieldRequired(display = "起始日期")
        private String beginDate;
        @JSONFieldRequired(display = "截止日期")
        private String endDate;
        @JSONFieldRequired(display = "需求类型")
        private String orderType;
        @JSONFieldRequired(display = "是否查询有模板需求")
        private String includeTemplate;
        @JSONFieldRequired(display = "出货组织编号")
        private String distriOrgNo;
        private String templateNo;
        private String keyTxt;
        @JSONFieldRequired(display = "预配模式")
        private String allotType;
        @JSONFieldRequired(display = "查询方式")
        private String queryType;
    }

}
