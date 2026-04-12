package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_CostDataDetailQueryRes extends JsonBasicRes {


    private Datas datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private String accountID;
        private String account;
        private String costType;
        private String costNo;
        private String corp;
        private String bDate;
        private String cost_Calculation;
        private String year;
        private String period;
        private String employeeNo;
        private String employeeNamt;
        private String departNo;
        private String departName;
        private String glNo;
        private String totCostInAmt;
        private String totCostOutAmt;

        private String createOpId;
        private String createTime;
        private String lastmodiopID;
        private String lastmodiTime;
        private String confirmopID;
        private String confirmopTime;

        private List<CostinList> costinList;
        private List<CostoutList> costoutList;
    }

    @NoArgsConstructor
    @Data
    public class CostinList {
        private String costNo;
        private String item;
        private String billNo;
        private String billItem;
        private String bType;
        private String cType;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String subjectId;
        private String subjectName;
        private String memo;
        private String costDomainID;
        private String costDomainName;
        private String bsNo;
        private String bsName;
        private String costCenter;
        private String costCenterName;
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
        private String employeeName;
        private String employeeNo;
        private String departName;
        private String departId;
        private String freeChars1;
        private String freeChars2;
        private String freeChars3;
        private String freeChars4;
        private String freeChars5;
        private String category;
        private String categoryName;
    }

    @NoArgsConstructor
    @Data
    public class CostoutList {
        private String costNo;
        private String item;
        private String billNo;
        private String billItem;
        private String bType;
        private String cType;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String subjectId;
        private String subjectName;
        private String memo;
        private String costDomainID;
        private String costDomainName;
        private String bsNo;
        private String bsName;
        private String costCenter;
        private String costCenterName;
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
        private String employeeName;
        private String employeeNo;
        private String departName;
        private String departId;
        private String freeChars1;
        private String freeChars2;
        private String freeChars3;
        private String freeChars4;
        private String freeChars5;
        private String category;
        private String categoryName;
    }
}

