package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_PurTemplateUpdateReq  extends JsonBasicReq {

    private DCP_PurTemplateUpdateReq.levelElm request;

    @Data
    public class levelElm{

        private String purTemplateNo;

        private String supplier;

        private String purType;

        private String purCenter;

        private String timeType;

        private String timeValue;

        private String preDays;

        private String memo;

        private String status;

        private List<DCP_PurTemplateUpdateReq.Name_lang> Name_lang;
        private List<DCP_PurTemplateUpdateReq.Plu> pluList;
        private List<DCP_PurTemplateUpdateReq.Org> orgList;

    }

    @Data
    public class Name_lang{
        private String langType;
        private String name;
    }

    @Data
    public class Plu{
        private String item;
        private String pluno;
        private String taxCode;
        private String purUnit;
        private String priceType;
        private String purBasePrice;
        private String minRate;
        private String maxRate;
        private String mulPqty;
        private String minPqty;
        private String status;

        private List<DCP_PurTemplateUpdateReq.Price> priceList;
    }

    @Data
    public class  Price{

        private String seq;
        private String bQty;
        private String eQty;
        private String purPrice;
    }

    @Data
    public class Org{
        private String item;
        private String orgNo;
        private String status;
    }

}
