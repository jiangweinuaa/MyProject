package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_BankAccountCreateReq extends JsonBasicReq {

    private levelRequest request;

    public levelRequest getRequest() {
        return request;
    }

    public void setRequest(levelRequest request) {
        this.request = request;
    }

    @Getter
    @Setter
    public class levelRequest {
        private String accountCode;//银行账户编码

        private String organizationNo;//开户人(组织)

        private String bankCode;//网点编号
        private String bankAccount;//银行账户
        private String accountName;//户名
        private String status;//状态码

        private List<level1Elm> accountName_lang;

    }

    @Getter
    @Setter
    public class level1Elm {
        private String langType;
        private String name;

    }

}
