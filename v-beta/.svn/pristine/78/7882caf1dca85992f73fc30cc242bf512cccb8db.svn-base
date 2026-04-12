package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ProdTemplateDetailQueryRes extends JsonRes {

    private List<DCP_ProdTemplateDetailQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm {
        private String templateId;
        private String templateName;
        private String status;
        private String restrictOrg;
        private String memo;
        private String createOpId;
        private String createOpName;
        private String createDeptId;
        private String createDeptName;
        private String createTime;
        private String lastModiOpId;
        private String lastModiOpName;
        private String lastModiTime;

        private List<Detail> detail;
        private List<OrgList> orgList;
    }

    @Data
    public class Detail{
        private String pluNo;
        private String pluName;
        private String category;
        private String categoryName;
        private String sourceType;
        private String baseUnit;
        private String baseUnitName;
        private String prodUnit;
        private String prodUnitName;
        private String prodMinQty;
        private String prodMulQty;
        private String remainType;
        private String dispType;
        private String procRate;
        private String semiWoType;
        private String semiWoDeptType;
        private String sdLaborTime;
        private String sdMachineTime;
        private String fixPreDays;
        private String status;
        private String oddValue;
        private String productExceed;
        private String standardHours;
        private String createOpId;
        private String createOpName;
        private String createTime;
        private String lastModiOpId;
        private String lastModiOpName;
        private String lastModiTime;
    }

    @Data
    public class OrgList{
        private String organizationNo;
        private String organizationName;
        private String status;
        private String createOpId;
        private String createOpName;
        private String createTime;
        private String lastModiOpId;
        private String lastModiOpName;
        private String lastModiTime;
    }
}

