package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_StockTaskUpdateReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_StockTaskUpdateReq.LevelElm request;


    @Data
    public class LevelElm {
        @JSONFieldRequired(display = "盘点任务单ID")
        private String stockTaskID;
        @JSONFieldRequired(display = "盘点任务单单号")
        private String stockTaskNo;
        @JSONFieldRequired(display = "盘点类型")
        private String docType;
        @JSONFieldRequired(display = "计划日期")
        private String sDate;
        @JSONFieldRequired(display = "盘点日期")
        private String bDate;
        @JSONFieldRequired(display = "创建方式")
        private String createType;
        @JSONFieldRequired(display = "盘点方式")
        private String taskWay;
        @JSONFieldRequired(display = "是否盲盘")
        private String isBTake;
        @JSONFieldRequired(display = "是否调整库存")
        private String isAdjustStock;
        @JSONFieldRequired(display = "漏盘处理方式")
        private String notGoodsMode;
        private String pTemplateNo;
        private String warehouseType;
        @JSONFieldRequired(display = "申请人员")
        private String employeeId;
        @JSONFieldRequired(display = "申请部门")
        private String departId;
        private String memo;
        @JSONFieldRequired(display = "盘点明细列表")
        private List<DCP_StockTaskUpdateReq.Detail> detail;
        @JSONFieldRequired(display = "盘点组织范围")
        private List<DCP_StockTaskUpdateReq.OrgList> orgList;


    }


    @Data
    public class Detail{
        @JSONFieldRequired(display = "序号")
        private String item;
        @JSONFieldRequired(display = "商品编号")
        private String pluNo;
        private String featureNo;
        @JSONFieldRequired(display = "分类编号")
        private String category;
        @JSONFieldRequired(display = "盘点单位")
        private String pUnit;
        @JSONFieldRequired(display = "基准单位")
        private String baseUnit;
        @JSONFieldRequired(display = "换算率")
        private String unitRatio;
        private String sdPrice;
    }

    @Data
    public class OrgList{
        @JSONFieldRequired(display = "组织编号")
        private String organizationNo;
        @JSONFieldRequired(display = "仓库编号")
        private String warehouse;
    }

}
