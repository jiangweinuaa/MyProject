package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ReconliationBillUpdateRes extends JsonBasicRes {

    private List<Datas> datas;

    @Data
    public class Datas{
        private String bizPartnerNo;
        private String bizType;
        private String orgNo;
        private String isCheck;
        private String reconNo;
    }

}
