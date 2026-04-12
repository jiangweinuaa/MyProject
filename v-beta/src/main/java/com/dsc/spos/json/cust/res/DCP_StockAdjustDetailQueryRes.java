package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_StockAdjustDetailQueryRes extends JsonRes
{
    private List<DCP_StockAdjustDetailQueryRes.level1Elm> datas;

    @Data
    public class level1Elm
    {
        private String adjustNo;
        private String bDate;
        private String memo;
        private String status;
        private String docType;
        private String oType;
        private String ofNo;
        private String loadDocType;
        private String loadDocNo;
        private String warehouse;
        private String warehouseName;
        private String createBy;
        private String createByName;
        private String createDate;
        private String createTime;
        private String createDateTime;
        private String modifyBy;
        private String modifyByName;
        private String modifyDate;
        private String modifyTime;
        private String modifyDateTime;
        private String submitBy;
        private String submitByName;
        private String submitDate;
        private String submitTime;
        private String submitDateTime;
        private String confirmBy;
        private String confirmByName;
        private String confirmDate;
        private String confirmTime;
        private String confirmDateTime;
        private String cancelBy;
        private String cancelByName;
        private String cancelDate;
        private String cancelTime;
        private String cancelDateTime;
        private String accountBy;
        private String accountByName;
        private String accountDate;
        private String accountTime;
        private String accountDateTime;
        private String update_time;
        private String process_status;
        private String totPqty;
        private String totAmt;
        private String totCqty;
        private String totDistriAmt;
        private String employeeId;
        private String employeeName;
        private String departId;
        private String departName;
        private String isLocation;
        private List<DCP_StockAdjustDetailQueryRes.level2Elm> datas;

    }

    @Data
    public class level2Elm
    {
        private String item;
        private String oItem;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String punit;
        private String punitName;
        private String pqty;
        private String price;
        private String amt;
        private String pluBarcode;
        private String warehouse;
        private String spec;
        private String listImage;
        private String distriAmt;
        private String distriPrice;
        private String baseQty;
        private String baseUnit;
        private String baseUnitName;
        private String unitRatio;
        private String batchNo;
        private String prodDate;

        private String category;
        private String categoryName;
        private String location;
        private String locationName;
        private String expDate;
        private String memo;

        private String isBatch;
        private String stockQty;




    }


}