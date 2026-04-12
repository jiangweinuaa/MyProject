package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_LStockOutDetailQueryRes extends JsonRes {

    private List<DCP_LStockOutDetailQueryRes.level1Elm> datas;

    @Data
    public class level1Elm {
        private String processERPNo;
        private String lStockOutNo;
        private String bDate;
        private String memo;
        private String status;
        private String warehouse;
        private String warehouseName;
        private String update_time;
        private String process_status;
        private String pTemplateNo;
        private String pTemplateName;
        private String createBy;
        private String createByName;
        private String createDate;
        private String createTime;
        private String modifyBy;
        private String modifyByName;
        private String modifyDate;
        private String modifyTime;
        private String submitBy;
        private String submitByName;
        private String submitDate;
        private String submitTime;
        private String confirmBy;
        private String confirmByName;
        private String confirmDate;
        private String confirmTime;
        private String cancelBy;
        private String cancelByName;
        private String cancelDate;
        private String cancelTime;
        private String accountBy;
        private String accountByName;
        private String accountDate;
        private String accountTime;
        private String totPqty;
        private String totAmt;
        private String totCqty;
        private String totDistriAmt;
        private String lStockoutNo_origin;
        private String lStockoutNo_refund;

        private String feeObjectType;
        private String feeObjectId;
        private String feeObjectName;
        private String fee;
        private String feeName;
        private String beeFeeNo;
        private String employeeId;
        private String employeeName;
        private String departId;
        private String departName;

        private List<DCP_LStockOutDetailQueryRes.level2Elm> datas;
        private List<DCP_LStockOutDetailQueryRes.level2ElmFileList> fileList;

    }

    @Data
    public class level2Elm{
        private String item;
        private String pluNo;
        private String pluName;
        private String punit;
        private String punitName;
        private String pqty;
        private String rqty;
        private String baseQty;
        private String price;
        private String amt;
        private String distriAmt;
        private String bsNo;
        private String bsName;
        private String baseUnit;
        private String baseUnitName;
        private String unitRatio;
        private String warehouse;
        private String warehouseName;
        private String spec;
        private String listImage;
        private String batchNo;
        private String isBatch;
        private String prodDate;
        private String distriPrice;
        private String punitUdLength;
        private String featureNo;
        private String featureName;
        private String baseUnitUdLength;
        private String pqty_refund;//原单对应的已退的数量
        private String item_origin;//
        private String pqty_origin;//
        private String location;
        private String locationName;
        private String expDate;
        private String stockQty;

    }

    @Data
    public class level2ElmFileList{
        private String fileName;
    }


}
