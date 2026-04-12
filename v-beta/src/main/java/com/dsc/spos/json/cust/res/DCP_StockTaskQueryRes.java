package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_StockTaskQueryRes  extends JsonRes
{
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm
	{
		private String shopId;	
		private String stockTaskNo;
        private String stockTaskID;
        private String sDate;
		private String bDate;
		private String memo;
		private String status;
		private String docType;
        private String pTemplateNo;
        private String pTemplateName;
		private String loadDocType;
		private String loadDocNo;			
		private String isBTake;
		private String warehouse;
		private String warehouseName;
		private String taskWay;
        private String isAdjustStock;
		private String notGoodsMode;
		private String stockTakeNo;
		private String submitStatus;

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

		private String createType;
		private String totSubTaskQty;
		private String totCqty;
		private String warehouseType;
		private String employeeId;
		private String employeeName;
		private String departId;
		private String departName;


		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getStockTaskNo() {
			return stockTaskNo;
		}
		public void setStockTaskNo(String stockTaskNo) {
			this.stockTaskNo = stockTaskNo;
		}
		public String getLoadDocNo() {
			return loadDocNo;
		}
		public void setLoadDocNo(String loadDocNo) {
			this.loadDocNo = loadDocNo;
		}
		public String getStockTakeNo() {
			return stockTakeNo;
		}
		public void setStockTakeNo(String stockTakeNo) {
			this.stockTakeNo = stockTakeNo;
		}
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
		public String getLoadDocType() {
			return loadDocType;
		}
		public void setLoadDocType(String loadDocType) {
			this.loadDocType = loadDocType;
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
		public String getWarehouseName() {
			return warehouseName;
		}
		public void setWarehouseName(String warehouseName) {
			this.warehouseName = warehouseName;
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
		public String getSubmitStatus() {
			return submitStatus;
		}
		public void setSubmitStatus(String submitStatus) {
			this.submitStatus = submitStatus;
		}
		public String getCreateBy() {
			return createBy;
		}
		public void setCreateBy(String createBy) {
			this.createBy = createBy;
		}
		public String getCreateByName() {
			return createByName;
		}
		public void setCreateByName(String createByName) {
			this.createByName = createByName;
		}
		public String getCreateDate() {
			return createDate;
		}
		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}
		public String getCreateTime() {
			return createTime;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		public String getModifyBy() {
			return modifyBy;
		}
		public void setModifyBy(String modifyBy) {
			this.modifyBy = modifyBy;
		}
		public String getModifyByName() {
			return modifyByName;
		}
		public void setModifyByName(String modifyByName) {
			this.modifyByName = modifyByName;
		}
		public String getModifyDate() {
			return modifyDate;
		}
		public void setModifyDate(String modifyDate) {
			this.modifyDate = modifyDate;
		}
		public String getModifyTime() {
			return modifyTime;
		}
		public void setModifyTime(String modifyTime) {
			this.modifyTime = modifyTime;
		}
		public String getSubmitBy() {
			return submitBy;
		}
		public void setSubmitBy(String submitBy) {
			this.submitBy = submitBy;
		}
		public String getSubmitByName() {
			return submitByName;
		}
		public void setSubmitByName(String submitByName) {
			this.submitByName = submitByName;
		}
		public String getSubmitDate() {
			return submitDate;
		}
		public void setSubmitDate(String submitDate) {
			this.submitDate = submitDate;
		}
		public String getSubmitTime() {
			return submitTime;
		}
		public void setSubmitTime(String submitTime) {
			this.submitTime = submitTime;
		}
		public String getConfirmBy() {
			return confirmBy;
		}
		public void setConfirmBy(String confirmBy) {
			this.confirmBy = confirmBy;
		}
		public String getConfirmByName() {
			return confirmByName;
		}
		public void setConfirmByName(String confirmByName) {
			this.confirmByName = confirmByName;
		}
		public String getConfirmDate() {
			return confirmDate;
		}
		public void setConfirmDate(String confirmDate) {
			this.confirmDate = confirmDate;
		}
		public String getConfirmTime() {
			return confirmTime;
		}
		public void setConfirmTime(String confirmTime) {
			this.confirmTime = confirmTime;
		}
		public String getCancelBy() {
			return cancelBy;
		}
		public void setCancelBy(String cancelBy) {
			this.cancelBy = cancelBy;
		}
		public String getCancelByName() {
			return cancelByName;
		}
		public void setCancelByName(String cancelByName) {
			this.cancelByName = cancelByName;
		}
		public String getCancelDate() {
			return cancelDate;
		}
		public void setCancelDate(String cancelDate) {
			this.cancelDate = cancelDate;
		}
		public String getCancelTime() {
			return cancelTime;
		}
		public void setCancelTime(String cancelTime) {
			this.cancelTime = cancelTime;
		}
		public String getAccountBy() {
			return accountBy;
		}
		public void setAccountBy(String accountBy) {
			this.accountBy = accountBy;
		}
		public String getAccountByName() {
			return accountByName;
		}
		public void setAccountByName(String accountByName) {
			this.accountByName = accountByName;
		}
		public String getAccountDate() {
			return accountDate;
		}
		public void setAccountDate(String accountDate) {
			this.accountDate = accountDate;
		}
		public String getAccountTime() {
			return accountTime;
		}
		public void setAccountTime(String accountTime) {
			this.accountTime = accountTime;
		}

        public String getStockTaskID() {
            return stockTaskID;
        }

        public void setStockTaskID(String stockTaskID) {
            this.stockTaskID = stockTaskID;
        }

        public String getsDate() {
            return sDate;
        }

        public void setsDate(String sDate) {
            this.sDate = sDate;
        }

        public String getpTemplateNo() {
            return pTemplateNo;
        }

        public void setpTemplateNo(String pTemplateNo) {
            this.pTemplateNo = pTemplateNo;
        }

        public String getpTemplateName() {
            return pTemplateName;
        }

        public void setpTemplateName(String pTemplateName) {
            this.pTemplateName = pTemplateName;
        }

		public String getCreateType() {
			return createType;
		}

		public void setCreateType(String createType) {
			this.createType = createType;
		}

		public String getTotSubTaskQty() {
			return totSubTaskQty;
		}

		public void setTotSubTaskQty(String totSubTaskQty) {
			this.totSubTaskQty = totSubTaskQty;
		}

		public String getTotCqty() {
			return totCqty;
		}

		public void setTotCqty(String totCqty) {
			this.totCqty = totCqty;
		}

		public String getWarehouseType() {
			return warehouseType;
		}

		public void setWarehouseType(String warehouseType) {
			this.warehouseType = warehouseType;
		}

		public String getEmployeeId() {
			return employeeId;
		}

		public void setEmployeeId(String employeeId) {
			this.employeeId = employeeId;
		}

		public String getEmployeeName() {
			return employeeName;
		}

		public void setEmployeeName(String employeeName) {
			this.employeeName = employeeName;
		}

		public String getDepartId() {
			return departId;
		}

		public void setDepartId(String departId) {
			this.departId = departId;
		}

		public String getDepartName() {
			return departName;
		}

		public void setDepartName(String departName) {
			this.departName = departName;
		}

        public String getIsAdjustStock() {
            return isAdjustStock;
        }

        public void setIsAdjustStock(String isAdjustStock) {
            this.isAdjustStock = isAdjustStock;
        }
    }

}
