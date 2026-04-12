package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_PurchaseApplyQueryRes extends JsonRes {

    private List<DCP_PurchaseApplyQueryRes.Level1Elm> datas;


    @Data
    public class Level1Elm {
        private String billNo;
        private String bDate;
        private String rDate;
        private String totPqty;
        private String totAmt;
        private String totCqty;
        private String totPurAmt;
        private String employeeId;
        private String employeeName;
        private String departId;
        private String departName;
        private String memo;
        private String status;
        private String createOpId;
        private String createOpName;
        private String createTime;
        private String lastModiOpId;
        private String lastModiOpName;
        private String lastModiTime;
        private String submitOpId;
        private String submitOpName;
        private String submitTime;
        private String confirmOpId;
        private String confirmOpName;
        private String confirmTime;
        private String cancelOpId;
        private String cancelOpName;
        private String cancelTime;
        private String closeOpId;
        private String closeOpName;
        private String closeTime;
    }
}
