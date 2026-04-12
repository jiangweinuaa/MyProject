package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_TransApplyUpdateReq extends JsonBasicReq {

    private DCP_TransApplyUpdateReq.level1Elm request;

    @Data
    public class level1Elm{
        @JSONFieldRequired(display = "单据日期")
        private String bDate;
        @JSONFieldRequired(display = "申请单号")
        private String billNo;
        @JSONFieldRequired(display = "调拨类型")
        private String transType;
        @JSONFieldRequired(display = "调出组织")
        private String transOutOrgNo;
        private String transOutWarehouse;
        @JSONFieldRequired(display = "调入组织")
        private String transInOrgNo;
        private String transInWarehouse;
        private String approveOrgNo;
        @JSONFieldRequired(display = "是否需收货方确认")
        private String isTranInConfirm;
        private String rDate;
        private String pTemplateNo;
        private String totCqty;
        private String totPoQty;
        private String totAmt;
        private String totDistriAmt;
        @JSONFieldRequired(display = "申请人员ID")
        private String employeeId;
        @JSONFieldRequired(display = "申请部门ID")
        private String departId;
        private String memo;

        @JSONFieldRequired
        private String applyType;

        @JSONFieldRequired
        private List<DCP_TransApplyUpdateReq.Detail> detail;

        private List<DCP_TransApplyUpdateReq.Source> source;
    }

    @Data
    public class Detail{

        private String item;
        //@JSONFieldRequired(display = "商品条码")
        private String pluBarcode;
        @JSONFieldRequired(display = "品号")
        private String pluNo;
        private String featureNo;
        @JSONFieldRequired(display = "申请数量")
        private String poQty;
        @JSONFieldRequired(display = "申请单位")
        private String pUnit;
        @JSONFieldRequired(display = "零售价")
        private String price;
        private String amt;
        @JSONFieldRequired(display = "进货价")
        private String distriPrice;
        private String distriAmt;
        //@JSONFieldRequired(display = "基准单位")
        private String baseUnit;
        private String baseQty;
        private String unitRatio;
        private String reason;
        private String memo;
        private String pickMinQty;
        private String pickMulQty;
    }

    @Data
    public class Source{
        private String item;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String oType;
        private String ofNo;
        private String oItem;
        private String pQty;
        private String pUnit;
        private String pUnitName;
    }

}
