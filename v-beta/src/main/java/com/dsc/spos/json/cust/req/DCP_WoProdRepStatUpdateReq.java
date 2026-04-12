package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/03/27
 */
@Getter
@Setter
public class DCP_WoProdRepStatUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "")
        private List<WorkList> workList;
        @JSONFieldRequired(display = "法人组织")
        private String corp;
        @JSONFieldRequired(display = "日期")
        private String bDate;
        @JSONFieldRequired(display = "")
        private String status;
        @JSONFieldRequired(display = "单据编号")
        private String porderNo;
    }

    @Getter
    @Setter
    public class WorkList {
        private String reportedQty;
        @JSONFieldRequired(display = "项次")
        private String item;
        @JSONFieldRequired(display = "成本中心编码")
        private String costCenterNo;
        private String stdHours;
        private String endWipEqQty;
        private String actHours;
        @JSONFieldRequired(display = "商品编码")
        private String pluNo;
        private String invAmount;
        private String spec;
        @JSONFieldRequired(display = "成本中心")
        private String costCenter;
        private String endWipEqRate;
        private String stdMachineHrs;
        private String invQty;
        private String actMachineHrs;
        private String pluName;
        private String remarks;
        @JSONFieldRequired(display = "工单编号")
        private String taskId;
        private String endWipQty;
    }

}