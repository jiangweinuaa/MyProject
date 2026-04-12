package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/04/21
 */
@NoArgsConstructor
@Data
@Getter
@Setter
public class DCP_IniInvCostOpnCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;


    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "期别")
        private String period;
        @JSONFieldRequired(display = "账套编号")
        private String accountID;
        @JSONFieldRequired(display = "")
        private List<InvList> invList;
        @JSONFieldRequired(display = "年度 ")
        private String year;
        @JSONFieldRequired(display = "成本计算方式")
        private String cost_Calculation;
        @JSONFieldRequired(display = "账套 ")
        private String account;
        @JSONFieldRequired(display = "")
        private String status;
    }

    @NoArgsConstructor
    @Data
    public class InvList {
        private String item;
        private String costDomainId;
        private String costDomainDis;
        private String pulNo;
        private String pluName;
        private String featureNo;
        private String qty;
        private String totAmt;
        private String material;
        private String labor;
        private String oem;
        private String exp1;
        private String exp2;
        private String exp3;
        private String exp4;
        private String exp5;
        private String avg_Price;
        private String avg_Price_Material;
        private String avg_Price_Man;
        private String avg_Price_Oem;
        private String avg_Price_Exp1;
        private String avg_Price_Exp2;
        private String avg_Price_Exp3;
        private String avg_Price_Exp4;
        private String avg_Price_Exp5;
    }


}