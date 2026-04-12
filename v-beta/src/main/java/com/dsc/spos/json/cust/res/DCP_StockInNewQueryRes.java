package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_StockInNewQueryRes extends JsonRes {

    private List<DCP_StockInNewQueryRes.level1Elm> datas;

    @Data
    public class level1Elm {
        private String shopId;
        private String stockInNo;
        private String processERPNo;
        private String bDate;
        private String memo;
        private String status;
        private String docType;
        private String bsNo;
        private String bsName;
        private String transferShop;
        private String transferShopName;
        private String oType;
        private String ofNo;
        private String pTemplateNo;
        private String pTemplateName;
        // 2018-11-14 添加modifyBy 等参数
        private String createBy;
        private String createDate;
        private String createTime;
        private String submitBy;
        private String submitDate;
        private String submitTime;
        private String modifyBy;
        private String modifyDate;
        private String modifyTime;
        private String confirmBy;
        private String confirmDate;
        private String confirmTime;
        private String cancelBy;
        private String cancelDate;
        private String cancelTime;
        private String accountBy;
        private String accountDate;
        private String accountTime;
        private String modifyByName;
        private String cancelByName;
        private String confirmByName;
        private String submitByName;
        private String accountByName;
        private String loadDocType;
        private String loadDocNo;
        private String createByName;
        private String diffStatus;
        private String differenceNo;
        private String totPqty;
        private String totAmt;
        private String totCqty;
        private String warehouse;
        private String warehouseName;
        private String update_time;
        private String process_status;
        private String rDate;
        private String receiptDate;
        private String totDistriAmt;
        private String deliveryNo;
        private String packingNo;
        private String stockInNo_origin;
        private String stockInNo_refund;

        private String deliveryBy;
        private String deliveryName;
        private String deliveryTel;
        private String invWarehouse;
        private String invWarehouseName;
        private String employeeId;
        private String employeeName;
        private String departId;
        private String departName;
        private String isLocation;

        private String reason;
        private String ooType;
        private String oofNo;
        private String transferWarehouse;
        private String transferWarehouseName;

        private String corp;
        private String deliveryCorp;

    }

}
