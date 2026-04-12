package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

/**
 * 服务名：DCP_StockOutDetailQuery
 * 服务说明：出库单明细查询
 * @author jinzma
 * @since 2020-06-23
 */
public class DCP_StockOutDetailQueryRes extends JsonRes {
	private List<Datas> datas;
	
	public List<Datas> getDatas() {
		return datas;
	}
	public void setDatas(List<Datas> datas) {
		this.datas = datas;
	}

    @Data
    public class Datas{
        private String shopId;
        private String eId;
        private String stockOutNo;
        private String processERPNo;
        private String bDate;
        private String memo;
        private String status;
        private String docType;
        private String transferShop;
        private String transferShopName;
        private String oType;
        private String ofNo;
        private String bsNo;
        private String bsName;
        private String loadDocType;
        private String loadDocNo;
        private String createBy;
        private String createDate;
        private String createTime;
        private String createDateTime;
        private String createByName;
        private String confirmBy;
        private String confirmDate;
        private String confirmTime;
        private String confirmDateTime;
        private String confirmByName;
        private String receiptOrg;
        private String receiptOrgName;
        private String accountBy;
        private String accountDate;
        private String accountTime;
        private String accountDateTime;
        private String accountByName;
        private String cancelBy;
        private String cancelDate;
        private String cancelTime;
        private String cancelDateTime;
        private String cancelByName;
        private String submitBy;
        private String submitDate;
        private String submitTime;
        private String submitDateTime;
        private String submitByName;
        private String modifyBy;
        private String modifyDate;
        private String modifyTime;
        private String modifyDateTime;
        private String modifyByName;
        private String totPqty;
        private String totAmt;
        private String totCqty;
        private String totDistriAmt;
        private String deliveryNo;
        private String diffStatus;
        private String warehouse;
        private String warehouseName;
        private String transferWarehouse;
        private String transferWarehouseName;
        private String update_time;
        private String process_status;
        private String pTemplateNo;//模板编码
        private String pTemplateName;//模板名称
        private String sourceMenu;
        private String stockOutNo_refund;
        private String stockOutNo_origin;
        private String rejectReason;
        private String deliveryBy;
        private String deliveryName;
        private String deliveryTel;

        private String employeeId;
        private String employeeName;
        private String departId;
        private String departName;
        private String receiptDate;
        private String packingNo;
        private String invWarehouse;
        private String invWarehouseName;
        private String isLocation;
        private String isBatchManage;
        private String deliveryDate;
        private String ooType;
        private String oofNo;
        private String isTranInConfirm;
        //private String stockInNo;
        private String receiptWHIsLocation;

        private List<level1Elm> detail;

        private List<BatchList> batchList;
        private List<StockInInfo> stockInNo;

        private String corp;
        private String receiptCorp;

   }

   @Data
	public class level1Elm {
		private String item;
		private String oItem;
		private String pluNo;
		private String pluName;
		private String spec;
		private String punit;
		private String punitName;
		private String pqty;
		private String baseUnit;
		private String baseUnitName;
		private String baseQty;
		private String unitRatio;
		private String price;
		private String amt;
		private String distriAmt;
		private String listImage;
		private String pluBarcode;
		private String routqty;
		private String routunit;
		private String oqty;
		private String rqty;
		private String routunitName;
		private String bsNo;
		private String bsName;
		private String pluMemo;
		private String batchNo;
		private String prodDate;
		private String distriPrice;
		private String isBatch;
		private String punitUdLength;
		private String featureNo ;
		private String featureName;
		private String stockManageType;
		/**
		 * 基准单位库存量
		 */
		private String Stockqty;
		private String baseUnitUdLength;
        private String item_origin;
        private String pqty_origin;
        private String pqty_refund;
        private String warehouse;
		private String warehouseName;
        private String oType;

        private List<level2Elm> imageList;
        private List<TransInLocationList> transInLocationList;


       private String packingNo;
       private String location;
       private String locationName;
       private String expDate;
       private String ofNo;
       private String ooType;
       private String oofNo;
       private String ooItem;
       private String stoctOutNoQty;
       private String transferBatchNo;

    }

    @Data
    public class level2Elm
    {
        private String image;
        private String item;

    }

    @Data
    public class BatchList{
        private String item;
        private String item2;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String warehouse;
        private String warehouseName;
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
        private String spec;
    }

	@Data
    public class TransInLocationList{

        private String item;
        private String oItem;
        private String transInLocation;
        private String transInLocationName;
        private String transInQty;
        private String pUnit;
        private String pUnitName;
        private String baseUnit;
        private String baseUnitName;
        private String baseQty;
        private String unitRatio;
    }

    @Data
    public class StockInInfo{
        private String stockInNo;
        private String stockInNo_origin;
    }
}
