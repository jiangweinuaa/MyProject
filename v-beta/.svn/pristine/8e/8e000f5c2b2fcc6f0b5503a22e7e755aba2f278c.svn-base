package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_PStockInDetailQueryRes extends JsonRes {
    private List<DCP_PStockInDetailQueryRes.level1Elm> datas;

    @Data
    public class level1Elm {
        private String pStockInNo;
        private String bDate;
        private String memo;
        private String status;
        private String oType;
        private String ofNo;
        private String loadDocType;
        private String loadDocNo;
        private String createByName;
        private String pTemplateNo;
        private String pTemplateName;
        private String warehouse;
        private String warehouseName;
        private String materialWarehouseNo;
        private String materialWarehouseName;
        private String createBy;
        private String createDate;
        private String createTime;
        private String createDateTime;
        private String submitBy;
        private String submitDate;
        private String submitTime;
        private String submitDateTime;
        private String modifyBy;
        private String modifyDate;
        private String modifyTime;
        private String modifyDateTime;
        private String confirmBy;
        private String confirmDate;
        private String confirmTime;
        private String confirmDateTime;
        private String cancelBy;
        private String cancelDate;
        private String cancelTime;
        private String cancelDateTime;
        private String accountBy;
        private String accountDate;
        private String accountTime;
        private String accountDateTime;
        private String modifyByName;
        private String cancelByName;
        private String confirmByName;
        private String submitByName;
        private String accountByName;
        private String totPqty;
        private String totAmt;
        private String totCqty;
        private String totDistriAmt;
        private String update_time;
        private String process_status;
        private String processERPNo;
        private String docType; // 0-完工入库  1-组合单   2-拆解单
        private String pStockInNo_origin;//原完工入库单号
        private String pStockInNo_refund;//关联的红冲完工入库单号，不想改查询语句了，直接加个字段吧
        private String refundStatus;//关联红冲的完工入库单状态 ，

        private String processPlanNo;
        private String task0No;
        private String dtNo;
        private String dtName;
        private String dtBeginTime;
        private String dtEndTime;
        private String employeeId;
        private String employeeName;
        private String departId;
        private String departName;
        private String createDeptId;
        private String createDeptName;
        private String isLocation;
        private String material_IsLocation;

        private String prodType;
        private String oOType;
        private String oOfNo;

        private List<DCP_PStockInDetailQueryRes.level2Elm> datas;

    }

    @Data
    public class level2Elm {
        private String item;
        private String pluNo;
        private String pluName;
        private String punit;
        private String punitName;
        private String pqty;
        private String price;
        private String amt;
        private String taskQty;
        private String scrapQty;
        private String mulQty;
        private String bsNo;
        private String bsName;
        private String pStockInQty;		//加工任务单单身的完工入库数量
        private String warehouse;
        private String warehouseName;
        private String listImage ;
        private String spec;
        private String batchNo;
        private String isBatch;
        private String prodDate;
        private String distriPrice;
        private String baseUnit;
        private String baseUnitName;
        private String baseQty;
        private String unitRatio;
        private String distriAmt;
        private String punitUdLength;
        private String pqty_origin;      //红充的完工入库单时 ，原单的合格数pqty
        private String pqty_refund;      //完工入单时，红充合格数
        private String scrapQty_origin;  //红充的完工入库单时 ，原单的报废数scrapQty
        private String scrapQty_refund;  //完工入单时，红充报废数数
        private String oItem;            //用于红冲
        private String featureNo;
        private String featureName;
        private String memo;
        private String baseUnitUdLength;
        private String location;
        private String locationName;
        private String expDate;
        private String isLocation;
        private String shelfLife;
        private String dispType;
        private String oOItem;
        private String minQty;

        private String prodType;
        private String bomNo;
        private String versionNum;

        private List<DCP_PStockInDetailQueryRes.level3Elm> material;

    }

    @Data
    public class level3Elm{
        private String mItem;
        private String material_item;
        private String material_warehouse;
        private String material_warehouseName;
        private String material_pluNo;
        private String material_pluName;
        private String material_punit;
        private String material_punitName;
        private String material_pqty;
        private String material_price;
        private String material_amt;
        private String material_finalProdBaseQty;    //成品基础量       从BOM服务中获取
        private String material_rawMaterialBaseQty;  //原料基础用量
        private String material_listImage ;
        private String material_spec;
        private String material_batchNo;
        private String material_isBatch;
        private String material_prodDate;
        private String material_distriPrice;
        private String material_baseQty;
        private String material_baseUnit;
        private String material_baseUnitName;
        private String material_unitRatio;
        private String material_distriAmt;
        private String material_punitUdLength;
        private String isBuckle;
        private String material_featureNo;
        private String material_featureName;
        private String material_baseUnitUdLength;
        private String material_location;
        private String material_locationName;
        private String material_isLocation;
        private String material_stockQty;
        private String material_totBatchQty;
        private String material_expDate;
        private String costRate;

        private List<MaterialBatchList> materialBatchList;
    }

    @Data
    public class MaterialBatchList{

        private String item;
        private String oItem;
        private String location;
        private String locationName;
        private String batchNo;
        private String prodDate;
        private String expDate;
        private String pUnit;
        private String pUnitName;
        private String pQty;
        private String baseUnit;
        private String baseUnitName;
        private String baseQty;
        private String stockQty;

    }

}
