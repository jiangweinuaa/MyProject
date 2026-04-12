package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_TopupSetQueryRes extends JsonRes {

    private List<Request> request;

    @NoArgsConstructor
    @Data
    public class Request {
        private String corp;
        private String corpName;
        private String status;
        private String topupOrg;
        private String topupOrgName;
        private List<SetList> setList;
    }

    @NoArgsConstructor
    @Data
    public class SetList {
        private String topupOrg;
        private String topupProdID;
        private String topupProdName;
        private String topupPayType;
        private String topupPayName;
        private String consPayType;
        private String consPayName;
        private String consProdID;
        private String consProdName;
    }
}

