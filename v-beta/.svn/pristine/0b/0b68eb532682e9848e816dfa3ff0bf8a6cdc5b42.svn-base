package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_PurTemplateDetailQueryRes extends JsonRes {

    private List<DCP_PurTemplateDetailQueryRes.DataList> datas;

    @Data
    public class DataList{
        private String purTemplateNo;
        private String supplier;
        private String supplierName;
        private String purType;
        private String purCenter;
        private String orgName;
        private String timeType;
        private String timeValue;
        private String preDays;
        private String memo;
        private String status;
        private String pluCnt;
        private String orgCnt;
        private String taxCode;
        private String taxName;
        private String taxRate;
        private String inclTax;
        private String creatorID;
        private String creatorName;
        private String creatorDeptID;
        private String creatorDeptName;
        private String create_datetime;
        private String lastmodifyID;
        private String lastmodifyName;
        private String lastmodify_datetime;
        private String beginDate;
        private String endDate;

        private List<DCP_PurTemplateDetailQueryRes.Name_lang> lang_list;

        private List<DCP_PurTemplateDetailQueryRes.Plu> pluList;

        private List<DCP_PurTemplateDetailQueryRes.Org> orgList;;
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
        private String pluName;
        private String spec;
        private String pluBarcode;
        private String category;
        private String categoryName;
        private String baseUnit;
        private String baseUnitName;
        private String wUnit;
        private String wUnitName;
        private String taxName;
        private String taxCode;
        private String taxRate;
        private String inclTax;
        private String purUnit;
        private String purUnitName;
        private String priceType;
        private String basePrice;
        private String minRate;
        private String maxRate;
        private String mulPqty;
        private String minPqty;
        private String status;

        private List<DCP_PurTemplateDetailQueryRes.Price> priceList;
    }

    @Data
    public class  Price{

        private String seq;
        private String bQty;
        private String eQty;
        private String price;
    }

    @Data
    public class Org{
        private String item;
        private String orgNo;
        private String orgName;
        private String status;
    }


}
