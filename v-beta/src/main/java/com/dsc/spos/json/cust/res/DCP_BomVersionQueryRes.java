package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_BomVersionQueryRes extends JsonRes {

    private List<DCP_BomVersionQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm{

        private String bomNo;
        private String bomType;
        private String versionNum;
        private String pluNo;
        private String pluName;
        private String unit;
        private String unitName;
        private String mulQty;
        private String effDate;
        private String memo;
        private String batchQty;
        private String spec;
        private String category;
        private String categoryName;
        private String baseUnit;
        private String baseUnitName;
        private String prodType;
        private String restrictShop;
        private String fixedLossQty;
        private String isProcessEnable;
        private String inWGroupNo;
        private String inWGroupName;
        private String createOpId;
        private String createOpName;
        private String createTime;
        private String lastModiTime;
        private String lastModiOpId;
        private String lastModiOpName;
        private String createDeptId;
        private String createDeptName;
        private String remainType;
        private String containType;
        private String minQty;
        private String oddValue;
        private String productExceed;
        private String procRate;
        private String dispType;
        private String semiwoType;
        private String semiwoDeptType;
        private String fixPreDays;
        private String sdlaborTime;
        private String sdmachineTime;
        private String standardHours;
        private String changeOpId;
        private String changeOpName;
        private String changeTime;

    }
}
