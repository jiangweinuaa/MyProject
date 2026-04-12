package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_WoStatusQueryRes extends JsonRes {

    private List<Datas> datas;


    @Data
    public class Datas {
        private String status;
        private String accountID;
        private String account;
        private String year;
        private String period;
        private String cost_Calculation;
        private String item;
        private String organizationNo;
        private String org_Name;
        private String batchTaskNo;
        private String bDate;
        private String doc_Type;
        private String productStatus;
        private String pluNo;
        private String pluName;
        private String spec;
        private String pqty;
        private String invQtyEndMth;
        private String setQty;
        private String isRework;
        private String costCloseDate;
        private String taskCloseDate;
        private String pmcloseStatus;
    }
}
