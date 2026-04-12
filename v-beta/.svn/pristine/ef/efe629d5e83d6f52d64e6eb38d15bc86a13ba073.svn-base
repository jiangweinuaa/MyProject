package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_PrintTemplateDetailQueryRes extends JsonRes {

    private List<DCP_PrintTemplateDetailQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm {
        private String modularNo;
        private String modularName;
        private String proName;
        private String status;
        private String onSale;
        private String totCqty;
        private List<Detail> detail;
    }


    @Data
    public class Detail{

        private String printNo;
        private String printName;
        private String proName;
        private String paraMeter;
        private String isStandard;
        private String isDefault;
        private String printType;
        private String restrictOp;
        private List<RestrictOpList> restrictOpList;
        private String restrictOrg;
        private List<RestrictOrgList> restrictOrgList;
        private String restrictCust;
        private List<RestrictCustList> restrictCustList;
        private String status;
    }

    @Data
    public class RestrictOpList{
        private String opType;
        private String id;
        private String name;
    }

    @Data
    public class RestrictOrgList{

        private String orgNo;
        private String orgName;
    }

    @Data
    public class RestrictCustList{

        private String customerNo;
        private String customerName;
    }


}
