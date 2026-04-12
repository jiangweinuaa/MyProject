package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：StockTakeCreatDCP
 *   說明：库存盘点新建
 * 服务说明：库存盘点新建
 * @author JZMA
 * @since  2018-11-21
 */
public class DCP_StockTakeCreateReq extends JsonBasicReq{
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
		private String docType;
		private String oType;
		private String ofNo;
		private String pTemplateNo;
		private String loadDocType;
		private String loadDocNo;
		private String stockTakeID;
		private String isBTake;
		private String warehouse;
		private String taskWay;
		private String notGoodsMode;
		private String isAdjustStock;
		private String totPqty;
		private String totAmt;
		private String totDistriAmt;
		private String totCqty;
        private String employeeId;
        private String departId;
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
		public String getDocType() {
			return docType;
		}
		public void setDocType(String docType) {
			this.docType = docType;
		}
		public String getoType() {
			return oType;
		}
		public void setoType(String oType) {
			this.oType = oType;
		}
		public String getOfNo() {
			return ofNo;
		}
		public void setOfNo(String ofNo) {
			this.ofNo = ofNo;
		}
		public String getpTemplateNo() {
			return pTemplateNo;
		}
		public void setpTemplateNo(String pTemplateNo) {
			this.pTemplateNo = pTemplateNo;
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
		public String getStockTakeID() {
			return stockTakeID;
		}
		public void setStockTakeID(String stockTakeID) {
			this.stockTakeID = stockTakeID;
		}
		public String getIsBTake() {
			return isBTake;
		}
		public void setIsBTake(String isBTake) {
			this.isBTake = isBTake;
		}
		public String getWarehouse() {
			return warehouse;
		}
		public void setWarehouse(String warehouse) {
			this.warehouse = warehouse;
		}
		public String getTaskWay() {
			return taskWay;
		}
		public void setTaskWay(String taskWay) {
			this.taskWay = taskWay;
		}
		public String getNotGoodsMode() {
			return notGoodsMode;
		}
		public void setNotGoodsMode(String notGoodsMode) {
			this.notGoodsMode = notGoodsMode;
		}
		public String getIsAdjustStock() {
			return isAdjustStock;
		}
		public void setIsAdjustStock(String isAdjustStock) {
			this.isAdjustStock = isAdjustStock;
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
    }

	public class level1Elm {
		private String item;
		private String oItem;
		private String pluNo;
		private String featureNo;
		private String punit;
		private String pqty;
		private String distriPrice;
		private String distriAmt;
		private String price;
		private String amt;
		private String refBaseQty;
		private String warehouse;
		private String memo;
		private String batchNo;
		private String prodDate;
		private String baseUnit;
		private String baseQty;
		private String unitRatio;
        private String location;
        private String expDate;
        private String isBatchAdd;
		private List<level2Elm> unitList;

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
		public String getFeatureNo() {
			return featureNo;
		}
		public void setFeatureNo(String featureNo) {
			this.featureNo = featureNo;
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
		public String getDistriPrice() {
			return distriPrice;
		}
		public void setDistriPrice(String distriPrice) {
			this.distriPrice = distriPrice;
		}
		public String getDistriAmt() {
			return distriAmt;
		}
		public void setDistriAmt(String distriAmt) {
			this.distriAmt = distriAmt;
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
		public String getRefBaseQty() {
			return refBaseQty;
		}
		public void setRefBaseQty(String refBaseQty) {
			this.refBaseQty = refBaseQty;
		}
		public String getWarehouse() {
			return warehouse;
		}
		public void setWarehouse(String warehouse) {
			this.warehouse = warehouse;
		}
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
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
		public List<level2Elm> getUnitList() {
			return unitList;
		}
		public void setUnitList(List<level2Elm> unitList) {
			this.unitList = unitList;
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

        public String getIsBatchAdd() {
            return isBatchAdd;
        }

        public void setIsBatchAdd(String isBatchAdd) {
            this.isBatchAdd = isBatchAdd;
        }
    }

	public class level2Elm {
		private String pqty;
		private String punit;
        private String unitRatio;

		public String getPqty() {
			return pqty;
		}
		public void setPqty(String pqty) {
			this.pqty = pqty;
		}
		public String getPunit() {
			return punit;
		}
		public void setPunit(String punit) {
			this.punit = punit;
		}

        public String getUnitRatio() {
            return unitRatio;
        }

        public void setUnitRatio(String unitRatio) {
            this.unitRatio = unitRatio;
        }
    }

}

