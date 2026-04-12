package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/04/21
 */
@Getter
@Setter
public class DCP_CostLevelDetailUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "账套编码")
        private String accountID;
        @JSONFieldRequired(display = "期别")
        private String period;
        @JSONFieldRequired(display = "年度")
        private String year;
        @JSONFieldRequired(display = "")
        private List<LevelList> levelList;
        @JSONFieldRequired(display = "成本计算方式")
        private String cost_Calculation;
//        @JSONFieldRequired(display = "分摊类型")
//        private String allocType;
        private String account;
    }

    @Getter
    @Setter
    public class LevelList {
        private String item;
        private String baseUnit;
        private String MaterialSource;
        private String pluNo;
        private String pluName;
        private String pluType;
        private String costLevel;
        private String IsJointProd;
        private String mcgCode;
        private String featureNo;
        private String MaterialBom;
        private String costGroupingId;
    }



}