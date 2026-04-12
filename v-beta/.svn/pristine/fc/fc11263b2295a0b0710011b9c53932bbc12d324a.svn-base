package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_BatchingDocCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_BatchingDocCreateReq.LevelElm request;

    @Data
    public class LevelElm{
        @JSONFieldRequired
        private String bDate;
        @JSONFieldRequired
        private String docType;
        @JSONFieldRequired
        private String totPQty;
        @JSONFieldRequired
        private String totCQty;

        private String employeeId;
        @JSONFieldRequired
        private String departId;
        @JSONFieldRequired
        private String oOType;
        @JSONFieldRequired
        private List<Datas> datas;
    }

    @Data
    public class Datas{
        @JSONFieldRequired
        private String item;
        @JSONFieldRequired
        private String pluNo;
        @JSONFieldRequired
        private String pUnit;
        @JSONFieldRequired
        private String pQty;
        private String benCopies;
        private String pItem;
        private String processNo;
        private String sItem;
        private String zItem;
        private String ofNo;
        private String oItem;
        @JSONFieldRequired
        private String fromWarehouse;
        @JSONFieldRequired
        private String toWarehouse;
        @JSONFieldRequired
        private String oOType;
        @JSONFieldRequired
        private String oOfNo;
        private String oOItem;
        @JSONFieldRequired
        private String mPluNo;
        @JSONFieldRequired
        private String batchQty;
        @JSONFieldRequired
        private String mPUnit;
        @JSONFieldRequired
        private String isReplace;
        private String rItem;
        private String replaceRatio;
        @JSONFieldRequired
        private String isBuckle;
        private List<BatchList> batchList;

    }

    @Data
    public class BatchList{
        private String item;
        private String sharePQty;
        private String batch;
        private String fromWarehouse;
        private String toWarehouse;
        private String baseUnit;
        private String baseQty;
        private String location;

    }

}
