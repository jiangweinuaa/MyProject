package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class MES_StockOutApplicationCreateReq extends JsonBasicReq
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
        }

        @Data
        public class levelRequest
        {
                private String eId;
                private String organizationNo;
                private String erpStockOutNo;
                private String bDate;
                private String warehouseNo;
                private String memo;
                private String tot_Qty;
                private String tot_CQty;
                private String createBy;
                private String createByName;
                private String createTime;
                private String mes_recipient;
                private String mes_department;
                private List<DetailList> detailList;
        }
}