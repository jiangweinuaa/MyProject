package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/04/29
 */
@Getter
@Setter
public class DCP_CustomerPOrderQueryRes extends JsonRes {

    private Datas datas;

    @Getter
    @Setter
    public class Datas {
        private List<DataList> dataList;
    }

    @Getter
    @Setter
    public class DataList {
        private String totDiscAmt;
        private String memo;
        private String totTaxAmt;
        private String deliverWarehouse;
        private String payType;
        private String billDateNo;
        private String orgNo;
        private String contact;
        private String closeByName;
        private String departName;
        private String lastModiOpName;
        private String employeeName;
        private String orgName;
        private String payOrgName;
        private String rDate;
        private String telephone;
        private String confirmTime;
        private String invoiceCode;
        private String customerName;
        private String discRate;
        private String deliverOrgName;
        private String deliverWarehouseName;
        private String payOrgNo;
        private String bDate;
        private String confirmOpId;
        private String status;
        private String deliverOrgNo;
        private String invoiceName;
        private String createOpName;
        private String salesManName;
        private String closeBy;
        private String payDateName;
        private String salesDepartName;
        private String totCqty;
        private String currencyName;
        private String pOrderNo;
        private String closeTime;
        private String totQty;
        private String departId;
        private String cancelBy;
        private String cancelByName;
        private String currency;
        private String confirmOpName;
        private String billDateName;
        private String lastModiOpId;
        private String templateNo;
        private String createOpId;
        private String address;
        private String salesManNo;
        private String employeeId;
        private String totAmt;
        private String totPrexTaxAmt;
        private String lastModiTime;
        private String salesDepartId;
        private String templateName;
        private String createTime;
        private String cancelTime;
        private String payDateNo;
        private String customerNo;
        private String payer;
        private String payerName;
    }

}