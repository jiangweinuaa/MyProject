package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_PTemplateCalculateRes extends JsonRes
{
    private List<DCP_PTemplateCalculateRes.RDates> datas;

    @Data
    public class RDates{
        private String rDate;
        private List<DCP_PTemplateCalculateRes.Level1Elm> datas;
    }

    @Data
    public class Level1Elm{
        private String templateNo;
        private String templateName;
        private String optionalTime;
        private String receiptOrgNo;
        private String receiptOrgName;
        private String orgCqty;
        private String sumbitOrgCqty;
        private String unSumbitCqty;
        private List<UnSumbitOrgList> unSumbitOrgList;
    }

    @Data
    public class UnSumbitOrgList{
        private String orgNo;
        private String orgName;
        private String contact;
        private String phone;
    }

}
