package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_SortingAssignCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;


    @Getter
    @Setter
    public class Request {

        @JSONFieldRequired
        private String bDate;
        @JSONFieldRequired
        private String oType;
        private String employeeId;
        private String departId;

        @JSONFieldRequired
        private List<Datas> datas;

    }

    @Getter
    @Setter
    public class Datas {
        @JSONFieldRequired
        private String objectType;
        @JSONFieldRequired
        private String objectId;

        private String routeNo;

        private String rDate;
        @JSONFieldRequired
        private String deliveryDate;
        @JSONFieldRequired
        private String ofNo;

        private String orderType;

        private String orderNo;
        @JSONFieldRequired
        private String warehouse;

        @JSONFieldRequired
        private List<Detail> detail;

    }

    @Getter
    @Setter
    public class Detail {
        @JSONFieldRequired
        private String oItem;

        private String orderItem;
        @JSONFieldRequired
        private String pluNo;

        private String featureNo;

        private String category;
        @JSONFieldRequired
        private String pUnit;
        @JSONFieldRequired
        private String noQty;
        @JSONFieldRequired
        private String poQty;
        @JSONFieldRequired
        private String pQty;
        @JSONFieldRequired
        private String baseUnit;
        @JSONFieldRequired
        private String baseQty;
        @JSONFieldRequired
        private String unitRatio;

        private String pTemplateNo;

    }


}
