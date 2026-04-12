package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_AccountCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private levelRequest request;


    @Getter
    @Setter
    public class levelRequest {
        @JSONFieldRequired(display = "银行账户编码")
        private String accountCode;//银行账户编码
        @JSONFieldRequired(display = "开户人(组织)")
        private String organizationNo;//开户人(组织)
        @JSONFieldRequired(display = "网点编号")
        private String bankCode;//网点编号
        @JSONFieldRequired(display = "银行账户")
        private String bankAccount;//银行账户
        @JSONFieldRequired(display = "户名")
        private String accountName;//户名
        @JSONFieldRequired(display = "状态")
        private String status;//状态码
        @JSONFieldRequired(display = "币种")
        private String currency;
        @JSONFieldRequired(display = "默认币种")
        private String subjectId;

        @JSONFieldRequired(display = "多语言资料")
        private List<level1Elm> accountName_lang;

    }

    @Getter
    @Setter
    public class level1Elm {
        @JSONFieldRequired(display = "多语言类型")
        private String langType;
        private String name;

    }

}
