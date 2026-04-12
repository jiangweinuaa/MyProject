package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服務函數：PStockInCreate
 *    說明：完工入库新增
 * 服务说明：完工入库新增
 * @author luoln 
 * @since  2017-03-31
 */
public class DCP_PStockInCreateReq extends JsonBasicReq{

	private levelElm request;
	
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm{
		private String bDate;
		private String memo;
		private String status;
		private String ofNo;
		private String oType;
		private String loadDocType;
		private String loadDocNo;
		private String pStockInID;
		private String pTemplateNo;
		private String warehouse;
		private String materialWarehouseNo;
		//2018-08-07新增docType
		private String docType; //0-完工入库  1-组合单   2-拆解单
		private String totPqty;
		private String totAmt;
		private String totDistriAmt;
		private String totCqty;
        private String processPlanNo;
        private String task0No;

        private String employeeId;
        private String departId;

        private String prodType;
        private String oOType;
        private String oOfNo;

        private List<level1Elm> datas;
		
		public String getbDate() {
			return bDate;
		}
		public void setbDate(String bDate) {
			this.bDate = bDate;
		}
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getOfNo() {
			return ofNo;
		}
		public void setOfNo(String ofNo) {
			this.ofNo = ofNo;
		}
		public String getoType() {
			return oType;
		}
		public void setoType(String oType) {
			this.oType = oType;
		}
		public String getLoadDocType() {
			return loadDocType;
		}
		public void setLoadDocType(String loadDocType) {
			this.loadDocType = loadDocType;
		}
		public String getLoadDocNo() {
			return loadDocNo;
		}
		public void setLoadDocNo(String loadDocNo) {
			this.loadDocNo = loadDocNo;
		}
		public String getpStockInID() {
			return pStockInID;
		}
		public void setpStockInID(String pStockInID) {
			this.pStockInID = pStockInID;
		}
		public String getpTemplateNo() {
			return pTemplateNo;
		}
		public void setpTemplateNo(String pTemplateNo) {
			this.pTemplateNo = pTemplateNo;
		}
		public String getWarehouse() {
			return warehouse;
		}
		public void setWarehouse(String warehouse) {
			this.warehouse = warehouse;
		}
		public String getMaterialWarehouseNo() {
			return materialWarehouseNo;
		}
		public void setMaterialWarehouseNo(String materialWarehouseNo) {
			this.materialWarehouseNo = materialWarehouseNo;
		}
		public String getDocType() {
			return docType;
		}
		public void setDocType(String docType) {
			this.docType = docType;
		}
		public String getTotPqty() {
			return totPqty;
		}
		public void setTotPqty(String totPqty) {
			this.totPqty = totPqty;
		}
		public String getTotAmt() {
			return totAmt;
		}
		public void setTotAmt(String totAmt) {
			this.totAmt = totAmt;
		}
		public String getTotDistriAmt() {
			return totDistriAmt;
		}
		public void setTotDistriAmt(String totDistriAmt) {
			this.totDistriAmt = totDistriAmt;
		}
		public String getTotCqty() {
			return totCqty;
		}
		public void setTotCqty(String totCqty) {
			this.totCqty = totCqty;
		}

        public String getProcessPlanNo()
        {
            return processPlanNo;
        }

        public void setProcessPlanNo(String processPlanNo)
        {
            this.processPlanNo = processPlanNo;
        }

        public String getTask0No()
        {
            return task0No;
        }

        public void setTask0No(String task0No)
        {
            this.task0No = task0No;
        }

        public List<level1Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}

        public String getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(String employeeId) {
            this.employeeId = employeeId;
        }

        public String getDepartId() {
            return departId;
        }

        public void setDepartId(String departId) {
            this.departId = departId;
        }

        public String getProdType() {
            return prodType;
        }

        public void setProdType(String prodType) {
            this.prodType = prodType;
        }

        public String getoOType() {
            return oOType;
        }

        public void setoOType(String oOType) {
            this.oOType = oOType;
        }

        public String getoOfNo() {
            return oOfNo;
        }

        public void setoOfNo(String oOfNo) {
            this.oOfNo = oOfNo;
        }
    }

	public  class level1Elm
	{
		private String item;
		private String oItem;
		private String pluNo;
		private String batchNo;
		private String prodDate;
		private String distriPrice;
		private String punit;
		private String pqty;
		private String price;
		private String amt;
		private String scrapQty;  //报废数
		private String taskQty;   //任务数
		private String mulQty;    //倍量
        private String minQty;
		private String bsNo;
		private String memo;
		private String gDate;     //蛋糕需求日期
		private String gTime;     //蛋糕需求时间
		private String warehouse;
		private String distriAmt;
		private String featureNo;
		private String baseUnit;
		private String baseQty;
		private String unitRatio;
        private String location;
        private String expDate;
        private String dispType;
        private String oOItem;

        //prodType，bomNo，versionNum
        private String prodType;
        private String bomNo;
        private String versionNum;

		private List<level2Elm> material;
		
		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public String getoItem() {
			return oItem;
		}
		public void setoItem(String oItem) {
			this.oItem = oItem;
		}
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getBatchNo() {
			return batchNo;
		}
		public void setBatchNo(String batchNo) {
			this.batchNo = batchNo;
		}
		public String getProdDate() {
			return prodDate;
		}
		public void setProdDate(String prodDate) {
			this.prodDate = prodDate;
		}
		public String getDistriPrice() {
			return distriPrice;
		}
		public void setDistriPrice(String distriPrice) {
			this.distriPrice = distriPrice;
		}
		public String getPunit() {
			return punit;
		}
		public void setPunit(String punit) {
			this.punit = punit;
		}
		public String getPqty() {
			return pqty;
		}
		public void setPqty(String pqty) {
			this.pqty = pqty;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getAmt() {
			return amt;
		}
		public void setAmt(String amt) {
			this.amt = amt;
		}
		public String getScrapQty() {
			return scrapQty;
		}
		public void setScrapQty(String scrapQty) {
			this.scrapQty = scrapQty;
		}
		public String getTaskQty() {
			return taskQty;
		}
		public void setTaskQty(String taskQty) {
			this.taskQty = taskQty;
		}
		public String getMulQty() {
			return mulQty;
		}
		public void setMulQty(String mulQty) {
			this.mulQty = mulQty;
		}
		public String getBsNo() {
			return bsNo;
		}
		public void setBsNo(String bsNo) {
			this.bsNo = bsNo;
		}
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public String getgDate() {
			return gDate;
		}
		public void setgDate(String gDate) {
			this.gDate = gDate;
		}
		public String getgTime() {
			return gTime;
		}
		public void setgTime(String gTime) {
			this.gTime = gTime;
		}
		public String getWarehouse() {
			return warehouse;
		}
		public void setWarehouse(String warehouse) {
			this.warehouse = warehouse;
		}
		public String getDistriAmt() {
			return distriAmt;
		}
		public void setDistriAmt(String distriAmt) {
			this.distriAmt = distriAmt;
		}
		public String getFeatureNo() {
			return featureNo;
		}
		public void setFeatureNo(String featureNo) {
			this.featureNo = featureNo;
		}
		public String getBaseUnit() {
			return baseUnit;
		}
		public void setBaseUnit(String baseUnit) {
			this.baseUnit = baseUnit;
		}
		public String getBaseQty() {
			return baseQty;
		}
		public void setBaseQty(String baseQty) {
			this.baseQty = baseQty;
		}
		public String getUnitRatio() {
			return unitRatio;
		}
		public void setUnitRatio(String unitRatio) {
			this.unitRatio = unitRatio;
		}
		public List<level2Elm> getMaterial() {
			return material;
		}
		public void setMaterial(List<level2Elm> material) {
			this.material = material;
		}

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getExpDate() {
            return expDate;
        }

        public void setExpDate(String expDate) {
            this.expDate = expDate;
        }

        public String getDispType() {
            return dispType;
        }

        public void setDispType(String dispType) {
            this.dispType = dispType;
        }

        public String getoOItem() {
            return oOItem;
        }

        public void setoOItem(String oOItem) {
            this.oOItem = oOItem;
        }

        public String getMinQty() {
            return minQty;
        }

        public void setMinQty(String minQty) {
            this.minQty = minQty;
        }

        public String getProdType() {
            return prodType;
        }

        public void setProdType(String prodType) {
            this.prodType = prodType;
        }

        public String getBomNo() {
            return bomNo;
        }

        public void setBomNo(String bomNo) {
            this.bomNo = bomNo;
        }

        public String getVersionNum() {
            return versionNum;
        }

        public void setVersionNum(String versionNum) {
            this.versionNum = versionNum;
        }
    }

	public  class level2Elm{
		private String mItem;
		private String material_item;
		private String material_warehouse;
		private String material_pluNo;
		private String material_punit;
		private String material_pqty;
		private String material_price;
		private String material_amt;
		private String material_finalProdBaseQty;    //成品基础量       从BOM服务中获取 
		private String material_rawMaterialBaseQty;  //原料基础用量
		private String material_batchNo;
		private String material_prodDate;
		private String material_distriPrice;
		private String material_distriAmt;
		private String isBuckle;                     //是否扣料件		
		private String material_featureNo;
		private String material_baseUnit;
		private String material_baseQty;
		private String material_unitRatio;

        private String material_location;
        private String material_expDate;

        private String costRate;

		private List<level3Elm> materialBatchList;

		public String getmItem() {
			return mItem;
		}
		public void setmItem(String mItem) {
			this.mItem = mItem;
		}
		public String getMaterial_item() {
			return material_item;
		}
		public void setMaterial_item(String material_item) {
			this.material_item = material_item;
		}
		public String getMaterial_warehouse() {
			return material_warehouse;
		}
		public void setMaterial_warehouse(String material_warehouse) {
			this.material_warehouse = material_warehouse;
		}
		public String getMaterial_pluNo() {
			return material_pluNo;
		}
		public void setMaterial_pluNo(String material_pluNo) {
			this.material_pluNo = material_pluNo;
		}
		public String getMaterial_punit() {
			return material_punit;
		}
		public void setMaterial_punit(String material_punit) {
			this.material_punit = material_punit;
		}
		public String getMaterial_pqty() {
			return material_pqty;
		}
		public void setMaterial_pqty(String material_pqty) {
			this.material_pqty = material_pqty;
		}
		public String getMaterial_price() {
			return material_price;
		}
		public void setMaterial_price(String material_price) {
			this.material_price = material_price;
		}
		public String getMaterial_amt() {
			return material_amt;
		}
		public void setMaterial_amt(String material_amt) {
			this.material_amt = material_amt;
		}
		public String getMaterial_finalProdBaseQty() {
			return material_finalProdBaseQty;
		}
		public void setMaterial_finalProdBaseQty(String material_finalProdBaseQty) {
			this.material_finalProdBaseQty = material_finalProdBaseQty;
		}
		public String getMaterial_rawMaterialBaseQty() {
			return material_rawMaterialBaseQty;
		}
		public void setMaterial_rawMaterialBaseQty(String material_rawMaterialBaseQty) {
			this.material_rawMaterialBaseQty = material_rawMaterialBaseQty;
		}
		public String getMaterial_batchNo() {
			return material_batchNo;
		}
		public void setMaterial_batchNo(String material_batchNo) {
			this.material_batchNo = material_batchNo;
		}
		public String getMaterial_prodDate() {
			return material_prodDate;
		}
		public void setMaterial_prodDate(String material_prodDate) {
			this.material_prodDate = material_prodDate;
		}
		public String getMaterial_distriPrice() {
			return material_distriPrice;
		}
		public void setMaterial_distriPrice(String material_distriPrice) {
			this.material_distriPrice = material_distriPrice;
		}
		public String getMaterial_distriAmt() {
			return material_distriAmt;
		}
		public void setMaterial_distriAmt(String material_distriAmt) {
			this.material_distriAmt = material_distriAmt;
		}
		public String getIsBuckle() {
			return isBuckle;
		}
		public void setIsBuckle(String isBuckle) {
			this.isBuckle = isBuckle;
		}
		public String getMaterial_featureNo() {
			return material_featureNo;
		}
		public void setMaterial_featureNo(String material_featureNo) {
			this.material_featureNo = material_featureNo;
		}
		public String getMaterial_baseUnit() {
			return material_baseUnit;
		}
		public void setMaterial_baseUnit(String material_baseUnit) {
			this.material_baseUnit = material_baseUnit;
		}
		public String getMaterial_baseQty() {
			return material_baseQty;
		}
		public void setMaterial_baseQty(String material_baseQty) {
			this.material_baseQty = material_baseQty;
		}
		public String getMaterial_unitRatio() {
			return material_unitRatio;
		}
		public void setMaterial_unitRatio(String material_unitRatio) {
			this.material_unitRatio = material_unitRatio;
		}

		public String getMaterial_location() {
			return material_location;
		}

		public void setMaterial_location(String material_location) {
			this.material_location = material_location;
		}

		public String getMaterial_expDate() {
			return material_expDate;
		}

		public void setMaterial_expDate(String material_expDate) {
			this.material_expDate = material_expDate;
		}

		public List<level3Elm> getMaterialBatchList() {
			return materialBatchList;
		}

		public void setMaterialBatchList(List<level3Elm> materialBatchList) {
			this.materialBatchList = materialBatchList;
		}

		public String getCostRate() {
			return costRate;
		}

		public void setCostRate(String costRate) {
			this.costRate = costRate;
		}
	}

	@Data
	public class level3Elm{
		private String item;
		private String oItem;
		private String location;
		private String batchNo;
		private String prodDate;
		private String expDate;
		private String pUnit;
		private String pQty;
		private String baseUnit;
		private String baseQty;
		private String unitRatio;
	}

	@Data
	public class StockInfo{
		private String pluNo;
		private String featureNo;
		private String warehouse;
		private String batchNo;
		private String location;
		private String baseUnit;
		private String qty;
		private String lockQty;
		private String prodDate;
		private String validDate;
	}

}
