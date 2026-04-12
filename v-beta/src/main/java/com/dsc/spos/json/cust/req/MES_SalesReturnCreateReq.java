package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class MES_SalesReturnCreateReq extends JsonBasicReq
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
                private String unit;
                private String featureNo;
                private String pQty;
                private String baseQty;
                private String baseUnit;
                private String warehouseNo;
                private String batchNo;
        }

        @Data
        public class levelRequest
        {
                private String eId;
                private String organizationNo;
                private String erpSaleNo;
                private String bDate;
                private String customer;
                private String salesMan;
                private String salesManTel;
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