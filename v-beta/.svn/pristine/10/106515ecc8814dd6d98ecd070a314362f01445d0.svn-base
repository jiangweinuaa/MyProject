package com.dsc.spos.json.cust.res;

import com.alibaba.fastjson.annotation.JSONField;
import com.dsc.spos.json.cust.JsonRes;
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
@Getter
@Setter
public class DCP_CostLevelDetailQueryRes extends JsonRes {

    private Datas datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private String status;
        private String corp;
        private String accountID;
        private String account;
        private String year;
        private String period;
        private List<LevelList> levelList;
    }

    @NoArgsConstructor
    @Data
    public class LevelList {
        private String item;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String baseUnit;
        private String baseUnitName;
        private String costLevel;
        @JSONField(name = "MaterialSource")
        private String materialSource;
        @JSONField(name = "MaterialBom")
        private String materialBom;
        @JSONField(name = "IsJointProd")
        private String isJointProd;
        private String costGroupingId;
        private String costGroupingId_Name;
        private String pluType;
        private String category;
        private String categoryName;

//            private String mcgCode;
    }
}

