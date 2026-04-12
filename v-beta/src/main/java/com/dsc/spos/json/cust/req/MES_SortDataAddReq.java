package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class MES_SortDataAddReq extends JsonBasicReq
{

    private levelRequest request;

    @Data
    public class levelRequest
    {
        private String eId;//
        private String organizationNo;//
        private String docNo;//
        private String requireNo;//
        private String bDate;//
        private String rTamplateNo;//
        private String department;//
        private String name;//
        private String phone;//
        private String address;//
        private String sDate;//
        private String sTime;//
        private List<level2Elm> dataList;
    }

    @Data
    public class level2Elm
    {
        private String pluNo;//
        private String featureNo;//
        private String aQty;//
        private String transferOut;//
        private String transferIn;//
        private String unit;//
        private String item;//
        private String rDate;//
        private String price;//
        private String amt;//
        private String taxRate;//
        private String taxAmount;//
        private String sourceType;//
        private String sourceNo;//
        private String oItem;//
        private String oCompany;//
    }





}
