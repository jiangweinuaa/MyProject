package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/03/04
 */
@Getter
@Setter
public class DCP_CostDomainUpdateReq extends JsonBasicReq {
    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "法人组织")
        private String corp;

        @JSONFieldRequired
        private List<Datas> datas;
        @JSONFieldRequired(display = "成本域类型")
        private String costDomainType;
        @JSONFieldRequired(display = "成本计算方式")
        private String cost_Calculation;

    }

    @Getter
    @Setter
    public class Datas {
        @JSONFieldRequired(display = "成本域")
        private String costDomain;
        @JSONFieldRequired(display = "成本域编码")
        private String costDomainId;
        private String langType;
        @JSONFieldRequired(display = "状态")
        private String status;
        private String memo;
    }

}