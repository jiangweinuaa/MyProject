package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.service.imp.json.DCP_DistriOrderUpdate;
import lombok.Data;

import java.util.List;

@Data
public class DCP_DistriOrderUpdateReq extends JsonBasicReq {
    private DCP_DistriOrderUpdateReq.LevelElm request;

    @Data
    public class LevelElm{

        @JSONFieldRequired(display = "铺货单明细")
        private List<DCP_DistriOrderUpdateReq.OrderList> orderList;
        @JSONFieldRequired(display = "铺货单号")
        private String billNo;
        @JSONFieldRequired(display = "单据日期")
        private String bDate;
        private String rDate;
        private String demandOrgNo;
        @JSONFieldRequired(display = "申请人员")
        private String employeeId;
        @JSONFieldRequired(display = "申请部门")
        private String departId;
        private String memo;

        @JSONFieldRequired
        private String totOqty;
        @JSONFieldRequired
        private String totCqty;
        @JSONFieldRequired
        private String totPqty;
        @JSONFieldRequired
        private String totAmt;
        @JSONFieldRequired
        private String totDistriAmt;
    }

    @Data
    public class OrderList{

        @JSONFieldRequired(display = "需求组织")
        private String demandOrgNo;
        @JSONFieldRequired(display = "需求日期")
        private String rDate;
        @JSONFieldRequired(display = "商品条码")
        private String pluBarcode;
        @JSONFieldRequired(display = "品号")
        private String pluNo;
        @JSONFieldRequired(display = "特征码")
        private String featureNo;
        @JSONFieldRequired(display = "铺货单位")
        private String pUnit;
        @JSONFieldRequired(display = "铺货数量")
        private String pQty;
        private String price;
        private String amt;
        private String distriPrice;
        private String distriAmt;
        private String supplierId;
        private String supplierType;

        private String baseUnit;
        private String baseQty;
        private String unitRatio;
    }

}
