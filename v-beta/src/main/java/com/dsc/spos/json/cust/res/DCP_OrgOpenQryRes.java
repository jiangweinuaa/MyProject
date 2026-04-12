package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_OrgOpenQryRes extends JsonRes {

    private List<DCP_OrgOpenQryRes.level1Elm> datas;

    @Data
    public class level1Elm {
        private String status;
        private String orgNo;
        private String sName;
        private String fullName;
        private String org_form;
        private String isCorp;
        private String corp;
        private String corpName;
        private String contact;
        private String contactName;
        private String address;
        private String phone;
    }
}
