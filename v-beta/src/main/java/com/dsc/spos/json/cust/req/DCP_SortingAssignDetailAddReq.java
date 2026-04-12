package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_SortingAssignDetailAddReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {

        private String billNo;
        private String employeeId;
        private String departId;
        private List<Datas> datas;

    }

    @Getter
    @Setter
    public class Datas {
        private String objectType;
        private String objectId;
        private String routeNo;
        private String rDate;
        private String deliveryDate;
        private String ofNo;
        private String orderType;
        private String orderNo;
        private String warehouse;

        List<Detail> detail;
    }

    @Getter
    @Setter
    public class Detail {
        private String oItem;
        private String orderItem;
        private String pluNo;
        private String featureNo;
        private String category;
        private String pUnit;
        private String noQty;
        private String poQty;
        private String pQty;
        private String baseUnit;
        private String baseQty;
        private String unitRatio;
        private String pTemplateNo;

    }


}
