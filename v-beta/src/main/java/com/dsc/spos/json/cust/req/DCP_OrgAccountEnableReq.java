package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_OrgAccountEnableReq extends JsonBasicReq {
    private DCP_OrgAccountEnableReq.levelRequest request;


    @Getter
    @Setter
    public class levelRequest {
        private String oprType;//操作类型：1-启用 2-禁用
        private List<DCP_OrgAccountEnableReq.OrgAccount> orgAccountList;
    }

    @Getter
    @Setter
    public class OrgAccount {
        private String account;
        private String bankNo;
        private String shopID;
    }

}
