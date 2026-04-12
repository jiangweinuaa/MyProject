package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class MES_PurchaseReturnCreateReq extends JsonBasicReq
{
        /**
         * null
         */
        private levelRequest request;

        @Data
        public class DetailList
        {
                private String erpItem;
                private String pluNo;
                private String pluName;
                private String pUnit;
                private String featureNo;
                private String pQty;
                private String baseQty;
                private String baseUnit;
                private String warehouseNo;
                private String batchNo;
                private String sourceStockInNo;
                private String sourceStockInItem;
        }

        @Data
        public class levelRequest
        {
                private String eId;
                private String organizationNo;
                private String erpReturnNo;
                private String bDate;
                private String supplier;
                private String purchaser;
                private String department;
                private String memo;
                private String tot_Qty;
                private String tot_CQty;
                private String createBy;
                private String createByName;
                private String createTime;
                private List<DetailList> detailList;
        }
}