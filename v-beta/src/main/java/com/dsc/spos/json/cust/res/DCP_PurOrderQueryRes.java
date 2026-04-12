package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_PurOrderQueryRes extends JsonRes {

    private List<DCP_PurOrderQueryRes.level1Elm> datas;

    @Data
    public class level1Elm{

        private String status;
        private String receiveStatus;
        private String purOrderNo;
        private String purOrgNo;
        private String orgName;
        private String bDate;
        private String supplier;
        private String supplierName;
        private String purType;
        private String receiveOrgno;
        private String receiveOrgname;
        private String expireDate;
        private String purEmpNo;
        private String purEmpName;
        private String purDeptName;
        private String purDeptNo;
        private String totcQty;
        private String totpQty;
        private String totrQty;
        private String totsQty;
        private String totBookQty;
        private String rate;
        private String creatorID;
        private String creatorName;
        private String creatorDeptID;
        private String creatorDeptName;
        private String create_datetime;
        private String lastmodifyID;
        private String lastmodifyName;
        private String lastmodify_datetime;
        private String confirmID;
        private String confirmName;
        private String confirm_datetime;
        private String cancelBy;
        private String cancelByName;
        private String cancel_datetime;
        private String CloseBy;
        private String CloseByName;
        private String CloseBy_datetime;
        private String ownerID;
        private String ownerName;
        private String ownerDeptID;
        private String ownerDeptName;
        private String payee;
        private String payeeName;

        private String address;
        private String receiptOrgNo;
        private String receiptOrgName;

    }

    @Data
    public class purOrderInfo{
        private String purOrderNo;
        private String receiveStatus;
        private String sh;
        private String rk;
        private String dhl;
        private String bk;
    }
}
