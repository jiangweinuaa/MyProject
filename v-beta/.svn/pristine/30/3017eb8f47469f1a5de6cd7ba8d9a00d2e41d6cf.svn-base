package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_DemandToStockOutNocticeProcessReq extends JsonBasicReq {

    @JSONFieldRequired(display = "请求参数")
    private DCP_DemandToStockOutNocticeProcessReq.LevelItem request;

    @Data
    public class LevelItem{

        @JSONFieldRequired(display = "审核选项")
        private String confirmType;
        @JSONFieldRequired(display = "生单方式")
        private String billCreateType;

        @JSONFieldRequired(display = "转配明细列表")
        private List<DCP_DemandToStockOutNocticeProcessReq.OrderList> orderList;
    }

    @Data
    public class OrderList{
        @JSONFieldRequired(display = "需求单号")
        private String orderNo;
        @JSONFieldRequired(display = "需求明细")
        private String orderItem;
        @JSONFieldRequired(display = "需求类型")
        private String orderType;
        @JSONFieldRequired(display = "需求对象类型")
        private String objectType;
        @JSONFieldRequired(display = "需求对象ID")
        private String objectId;
        @JSONFieldRequired(display = "出货组织编号")
        private String deliverOrgNo;
        @JSONFieldRequired(display = "品号")
        private String pluNo;
        @JSONFieldRequired(display = "特征码")
        private String featureNo;
        //@JSONFieldRequired(display = "商品条码")
        private String pluBarCode;
        @JSONFieldRequired(display = "出货仓库")
        private String wareHouse;
        @JSONFieldRequired(display = "出货单位")
        private String pUnit;
        @JSONFieldRequired(display = "出货数量")
        private String pQty;
        private String poQty;
        @JSONFieldRequired(display = "交易价")
        private String price;
        @JSONFieldRequired(display = "零售价")
        private String retailPrice;
        //@JSONFieldRequired(display = "模板编号")
        private String templateNo;

    }
}
