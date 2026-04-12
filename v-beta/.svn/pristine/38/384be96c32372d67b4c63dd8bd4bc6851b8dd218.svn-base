package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_WOReportDetailQueryRes extends JsonRes {


    private List<LevelElm> datas;

    @NoArgsConstructor
    @Data
    public  class LevelElm {
        private String reportNo;
        private String bDate;
        private String accountDate;
        private String memo;
        private String status;
        private String createBy;
        private String createByName;
        private String createTime;
        private String modifyBy;
        private String modifyByName;
        private String modifyTime;
        private String accountBy;
        private String accountByName;
        private String accountTime;
        private String cancelBy;
        private String cancelByName;
        private String cancelTime;
        private String processStatus;
        private String processErpNo;
        private String processErpOrg;
        private String employeeId;
        private String employeeName;
        private String departId;
        private String departName;
        private List<Datas> datas;


    }

    @NoArgsConstructor
    @Data
    public  class Datas {
        private String item;
        private String ofNo;
        private String oItem;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String pQty;
        private String pUnit;
        private String pUName;
        private String equipNo;
        private String equipName;
        private String eQty;
        private String laborTime;
        private String machineTime;
        private String memo;
    }
}
