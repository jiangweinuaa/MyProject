package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_DifferenceDetailQueryRes extends JsonRes {

    private List<DCP_DifferenceDetailQueryRes.Datas> datas;
    private String differenceNo;
    private String bDate;
    private String memo;
    private String status;
    private String docType;
    private String oType;
    private String ofNo;
    private String loadDocType;
    private String loadDocNo;
    private String createByName;
    private String warehouse;
    private String warehouseName;
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

    private String totPqty;
    private String totAmt;
    private String totCqty;
    private String update_time;
    private String process_status;
    private String transferShop;
    private String transferShopName;
    private String totDistriAmt;

    private String transferWarehouse;
    private String transferWarehouseName;
    private String invWarehouse;
    private String invWarehouseName;
    private String createType;
    private String orgName;
    private String organizationName;
    private String orgNo;

    @Data
    public class Datas{
        private String item;
        private String oItem;
        private String pluNo;
        private String pluName;
        private String punit;
        private String punitName;
        private String reqQty;
        private String pqty;
        private String diffQty;
        private String bsNo;
        private String bsName;
        private String processMode;
        private String price;
        private String amt;
        private String stockInQty;
        private String stockOutQty;
        private String receiveDiffQty;
        private String baseUnit;
        private String baseUnitName;
        private String baseQty;
        private String unitRatio;
        private String warehouse;
        private String spec;
        private String listImage;
        private String batchNo;
        private String isBatch;
        private String prodDate;
        private String distriPrice;
        private String distriAmt;
        private String featureNo;
        private String featureName;
        private String transferBatchNo;
    }

}
