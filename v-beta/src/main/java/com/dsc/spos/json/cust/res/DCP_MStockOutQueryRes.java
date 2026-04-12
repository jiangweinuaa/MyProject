package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_MStockOutQueryRes extends JsonRes {

    private List<DCP_MStockOutQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm {
        private String mStockOutNo;
        private String docType;
        private String warehouse;
        private String warehouseName;
        private String bDate;
        private String accountDate;
        private String totPQty;
        private String totCQty;
        private String totAmt;
        private String totDistriAmt;
        private String memo;
        private String status;
        private String adjustStatus;
        private String oMStockOutNo;
        private String loadDocType;
        private String loadDocNo;
        private String autoProcess;
        private String createBy;
        private String createByName;
        private String createTime;
        private String modifyBy;
        private String modifyByName;
        private String modifyTime;
        private String confirmBy;
        private String confirmByName;
        private String confirmTime;
        private String accountBy;
        private String accountByName;
        private String accountTime;
        private String processStatus;
        private String processErpNo;
        private String processErpOrg;
        private String employeeId;
        private String employeeName;
        private String departId;
        private String departName;
        private String oType;
        private String ofNo;
        private String oOType;
        private String oOfNo;

    }
}
