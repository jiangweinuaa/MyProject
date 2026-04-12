package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class MES_ComposeDisCreateReq extends JsonBasicReq
{
        /**
         * null
         */
        private levelRequest request;

        @Data
        public class DetailList
        {
                private String erpItem;
                private String subPluNo;
                private String unit;
                private String qty;
                private String featureNo;
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
                private String erpDocNo;
                private String type;
                private String bDate;
                private String pluNo;
                private String featureNo;
                private String pUnit;
                private String pQty;
                private String baseUnit;
                private String baseQty;
                private String warehouseNo;
                private String batchNo;
                private String createBy;
                private String createByName;
                private String createTime;
                private List<DetailList> detailList;
        }
}