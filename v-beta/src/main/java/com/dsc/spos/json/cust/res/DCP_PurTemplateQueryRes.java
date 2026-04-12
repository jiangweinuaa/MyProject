package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_PurTemplateQueryRes extends JsonRes {

    private List<DCP_PurTemplateQueryRes.DataList> datas;

    @Data
    public class DataList{
        private String purTemplateNo;
        private String purTemplateName;
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
        private String plucnt;
        private String orgcnt;
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

        private List<DCP_PurTemplateQueryRes.Lang> lang_list;
    }

    @Data
    public class Lang{
        private String langType;
        private String name;
    }

}
