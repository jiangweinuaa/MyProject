package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_StockOutCreateInBatchReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_StockOutCreateInBatchReq.LevelElm request;

    @Data
    public class LevelElm{
        @JSONFieldRequired(display = "出通待出货明细")
        private List<DCP_StockOutCreateInBatchReq.DataList> dataList;
    }


    @Data
    public class DataList{

        @JSONFieldRequired(display = "收货组织")
        private String receiptOrg;
        //@JSONFieldRequired(display = "要货模板")
        private String templateNo;
        @JSONFieldRequired(display = "通知单号")
        private String noticeNo;
        @JSONFieldRequired(display = "通知单项次")
        private String noticeItem;
        @JSONFieldRequired(display = "品号")
        private String pluNo;
        //@JSONFieldRequired(display = "特征码")
        private String featureNo;
        //@JSONFieldRequired(display = "商品条码")
        private String pluBarcode;
        @JSONFieldRequired(display = "出货仓库")
        private String warehouse;
        @JSONFieldRequired(display = "出货单位")
        private String pUnit;
        @JSONFieldRequired(display = "出货数量")
        private String pQty;

    }

}
