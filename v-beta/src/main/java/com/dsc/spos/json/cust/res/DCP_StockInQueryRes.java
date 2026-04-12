package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 服務函數：StockInGet 說明：收货单查询 服务说明：收货单查询
 * 
 * @author panjing
 * @since 2016-09-22
 */
public class DCP_StockInQueryRes extends JsonRes {

	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

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
		private String isBatchManage;
		private String reason;
		private String ooType;
		private String oofNo;
        private String transferWarehouse;
        private String transferWarehouseName;
        private String corp;
        private String deliveryCorp;
		

		private List<level2Elm> datas;


        public String getStockInNo_origin()
        {
            return stockInNo_origin;
        }

        public void setStockInNo_origin(String stockInNo_origin)
        {
            this.stockInNo_origin = stockInNo_origin;
        }

        public String getStockInNo_refund()
        {
            return stockInNo_refund;
        }

        public void setStockInNo_refund(String stockInNo_refund)
        {
            this.stockInNo_refund = stockInNo_refund;
        }

        public String getStockInNo() {
			return stockInNo;
		}

		public void setStockInNo(String stockInNo) {
			this.stockInNo = stockInNo;
		}

		public String getpTemplateName() {
			return pTemplateName;
		}

		public void setpTemplateName(String pTemplateName) {
			this.pTemplateName = pTemplateName;
		}

		public String getBsNo() {
			return bsNo;
		}

		public void setBsNo(String bsNo) {
			this.bsNo = bsNo;
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

		public String getLoadDocNo() {
			return loadDocNo;
		}

		public void setLoadDocNo(String loadDocNo) {
			this.loadDocNo = loadDocNo;
		}

		public String getDifferenceNo() {
			return differenceNo;
		}

		public void setDifferenceNo(String differenceNo) {
			this.differenceNo = differenceNo;
		}

		public String getDeliveryNo() {
			return deliveryNo;
		}

		public void setDeliveryNo(String deliveryNo) {
			this.deliveryNo = deliveryNo;
		}	

		public String getPackingNo()
		{
			return packingNo;
		}

		public void setPackingNo(String packingNo)
		{
			this.packingNo = packingNo;
		}

		public String getModifyByName() {
			return modifyByName;
		}

		public void setModifyByName(String modifyByName) {
			this.modifyByName = modifyByName;
		}

		public String getCancelByName() {
			return cancelByName;
		}

		public void setCancelByName(String cancelByName) {
			this.cancelByName = cancelByName;
		}

		public String getConfirmByName() {
			return confirmByName;
		}

		public void setConfirmByName(String confirmByName) {
			this.confirmByName = confirmByName;
		}

		public String getSubmitByName() {
			return submitByName;
		}

		public void setSubmitByName(String submitByName) {
			this.submitByName = submitByName;
		}

		public String getAccountByName() {
			return accountByName;
		}

		public void setAccountByName(String accountByName) {
			this.accountByName = accountByName;
		}

		public String getAccountBy() {
			return accountBy;
		}

		public void setAccountBy(String accountBy) {
			this.accountBy = accountBy;
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

		public String getCreateBy() {
			return createBy;
		}

		public void setCreateBy(String createBy) {
			this.createBy = createBy;
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

		public String getSubmitBy() {
			return submitBy;
		}

		public void setSubmitBy(String submitBy) {
			this.submitBy = submitBy;
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

		public String getModifyBy() {
			return modifyBy;
		}

		public void setModifyBy(String modifyBy) {
			this.modifyBy = modifyBy;
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

		public String getConfirmBy() {
			return confirmBy;
		}

		public void setConfirmBy(String confirmBy) {
			this.confirmBy = confirmBy;
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

		public String getShopId() {
			return shopId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}

		public String getProcessERPNo() {
			return processERPNo;
		}

		public void setProcessERPNo(String processERPNo) {
			this.processERPNo = processERPNo;
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

		public String getBsName() {
			return bsName;
		}

		public void setBsName(String bsName) {
			this.bsName = bsName;
		}

		public String getTransferShop() {
			return transferShop;
		}

		public void setTransferShop(String transferShop) {
			this.transferShop = transferShop;
		}

		public String getTransferShopName() {
			return transferShopName;
		}

		public void setTransferShopName(String transferShopName) {
			this.transferShopName = transferShopName;
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

		public String getCreateByName() {
			return createByName;
		}

		public void setCreateByName(String createByName) {
			this.createByName = createByName;
		}

		public String getDiffStatus() {
			return diffStatus;
		}

		public void setDiffStatus(String diffStatus) {
			this.diffStatus = diffStatus;
		}

		public List<level2Elm> getDatas() {
			return datas;
		}

		public void setDatas(List<level2Elm> datas) {
			this.datas = datas;
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

		public String getTotCqty() {
			return totCqty;
		}

		public void setTotCqty(String totCqty) {
			this.totCqty = totCqty;
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

		public String getUpdate_time() {
			return update_time;
		}

		public void setUpdate_time(String update_time) {
			this.update_time = update_time;
		}

		public String getProcess_status() {
			return process_status;
		}

		public void setProcess_status(String process_status) {
			this.process_status = process_status;
		}

		public String getrDate() {
			return rDate;
		}

		public void setrDate(String rDate) {
			this.rDate = rDate;
		}

		public String getReceiptDate() {
			return receiptDate;
		}

		public void setReceiptDate(String receiptDate) {
			this.receiptDate = receiptDate;
		}

		public String getTotDistriAmt() {
			return totDistriAmt;
		}

		public void setTotDistriAmt(String totDistriAmt) {
			this.totDistriAmt = totDistriAmt;
		}
		
		public String getDeliveryBy() {
			return deliveryBy;
		}
		
		public void setDeliveryBy(String deliveryBy) {
			this.deliveryBy = deliveryBy;
		}
		
		public String getDeliveryName() {
			return deliveryName;
		}
		
		public void setDeliveryName(String deliveryName) {
			this.deliveryName = deliveryName;
		}
		
		public String getDeliveryTel() {
			return deliveryTel;
		}
		
		public void setDeliveryTel(String deliveryTel) {
			this.deliveryTel = deliveryTel;
		}

		public String getInvWarehouse() {
			return invWarehouse;
		}

		public void setInvWarehouse(String invWarehouse) {
			this.invWarehouse = invWarehouse;
		}

		public String getInvWarehouseName() {
			return invWarehouseName;
		}

		public void setInvWarehouseName(String invWarehouseName) {
			this.invWarehouseName = invWarehouseName;
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

		public String getIsLocation() {
			return isLocation;
		}

		public void setIsLocation(String isLocation) {
			this.isLocation = isLocation;
		}

		public String getIsBatchManage() {
			return isBatchManage;
		}

		public void setIsBatchManage(String isBatchManage) {
			this.isBatchManage = isBatchManage;
		}

		public String getReason() {
			return reason;
		}

		public void setReason(String reason) {
			this.reason = reason;
		}

		public String getOoType() {
			return ooType;
		}

		public void setOoType(String ooType) {
			this.ooType = ooType;
		}

		public String getOofNo() {
			return oofNo;
		}

		public void setOofNo(String oofNo) {
			this.oofNo = oofNo;
		}

        public String getTransferWarehouse() {
            return transferWarehouse;
        }

        public void setTransferWarehouse(String transferWarehouse) {
            this.transferWarehouse = transferWarehouse;
        }

        public String getTransferWarehouseName() {
            return transferWarehouseName;
        }

        public void setTransferWarehouseName(String transferWarehouseName) {
            this.transferWarehouseName = transferWarehouseName;
        }

        public String getCorp() {
            return corp;
        }

        public void setCorp(String corp) {
            this.corp = corp;
        }

        public String getDeliveryCorp() {
            return deliveryCorp;
        }

        public void setDeliveryCorp(String deliveryCorp) {
            this.deliveryCorp = deliveryCorp;
        }
    }

	public class level2Elm {

		private int item;
		private String oType;
		private String ofNo;
		private int oItem;
		private String ooType;
		private String oofNo;
		private String ooItem;
		private String pluNo;
		private String pluName;
		private String spec;
		private String punit;
		private String punitName;
		private float pqty;
		private float receivingQty;
		private float realQty;
		private float poQty;
		private float diffReqQty;
		private float diffQty;
		private float price;
		private String amt;
		private String pluBarcode;
		private String unitRatio;
		private String warehouse;
		private String warehouseName;
		private float routqty;
		private String routunit;
		private String routunitName;
		private String procRate;
		private String pluMemo;
		private String batchNo;
		private String isBatch;
		private String prodDate;
		private String distriPrice;
		private String distriAmt;
		private String punitUdLength;
		private String baseQty;
		private String baseUnit;
		private String baseUnitName;
		private String featureName;
		private String featureNo;
		private String listImage;
		private String packingNo;
        private String baseUnitUdLength;
        private String item_origin;
        private String pqty_origin;
        private String Pqty_refund;

        private String transferBatchNo;


        public String getItem_origin()
        {
            return item_origin;
        }

        public void setItem_origin(String item_origin)
        {
            this.item_origin = item_origin;
        }

        public String getPqty_origin()
        {
            return pqty_origin;
        }

        public void setPqty_origin(String pqty_origin)
        {
            this.pqty_origin = pqty_origin;
        }

        public String getPqty_refund()
        {
            return Pqty_refund;
        }

        public void setPqty_refund(String pqty_refund)
        {
            Pqty_refund = pqty_refund;
        }

        public String getBaseQty() {
			return baseQty;
		}

		public void setBaseQty(String baseQty) {
			this.baseQty = baseQty;
		}

		public String getBaseUnit() {
			return baseUnit;
		}

		public void setBaseUnit(String baseUnit) {
			this.baseUnit = baseUnit;
		}

		public String getBaseUnitName() {
			return baseUnitName;
		}

		public void setBaseUnitName(String baseUnitName) {
			this.baseUnitName = baseUnitName;
		}

		public String getFeatureName() {
			return featureName;
		}

		public void setFeatureName(String featureName) {
			this.featureName = featureName;
		}

		public String getFeatureNo() {
			return featureNo;
		}

		public void setFeatureNo(String featureNo) {
			this.featureNo = featureNo;
		}

		public String getListImage() {
			return listImage;
		}

		public void setListImage(String listImage) {
			this.listImage = listImage;
		}

		public float getRealQty() {
			return realQty;
		}

		public void setRealQty(float realQty) {
			this.realQty = realQty;
		}

		public int getItem() {
			return item;
		}

		public void setItem(int item) {
			this.item = item;
		}

		public int getoItem() {
			return oItem;
		}

		public void setoItem(int oItem) {
			this.oItem = oItem;
		}

		public String getOfNo() {
			return ofNo;
		}

		public void setOfNo(String ofNo) {
			this.ofNo = ofNo;
		}

		public String getOofNo() {
			return oofNo;
		}

		public void setOofNo(String oofNo) {
			this.oofNo = oofNo;
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

		public String getPluName() {
			return pluName;
		}

		public void setPluName(String pluName) {
			this.pluName = pluName;
		}

		public String getSpec() {
			return spec;
		}

		public void setSpec(String spec) {
			this.spec = spec;
		}

		public String getPunit() {
			return punit;
		}

		public void setPunit(String punit) {
			this.punit = punit;
		}

		public String getPunitName() {
			return punitName;
		}

		public void setPunitName(String punitName) {
			this.punitName = punitName;
		}

		public float getPqty() {
			return pqty;
		}

		public void setPqty(float pqty) {
			this.pqty = pqty;
		}

		public float getPoQty() {
			return poQty;
		}

		public void setPoQty(float poQty) {
			this.poQty = poQty;
		}

		public float getDiffReqQty() {
			return diffReqQty;
		}

		public void setDiffReqQty(float diffReqQty) {
			this.diffReqQty = diffReqQty;
		}

		public float getDiffQty() {
			return diffQty;
		}

		public void setDiffQty(float diffQty) {
			this.diffQty = diffQty;
		}

		public float getPrice() {
			return price;
		}

		public void setPrice(float price) {
			this.price = price;
		}

		public String getAmt() {
			return amt;
		}

		public void setAmt(String amt) {
			this.amt = amt;
		}

		public String getPluBarcode() {
			return pluBarcode;
		}

		public void setPluBarcode(String pluBarcode) {
			this.pluBarcode = pluBarcode;
		}

		public String getoType() {
			return oType;
		}

		public void setoType(String oType) {
			this.oType = oType;
		}

		public String getOoType() {
			return ooType;
		}

		public void setOoType(String ooType) {
			this.ooType = ooType;
		}

		public String getOoItem() {
			return ooItem;
		}

		public void setOoItem(String ooItem) {
			this.ooItem = ooItem;
		}

		public String getUnitRatio() {
			return unitRatio;
		}

		public void setUnitRatio(String unitRatio) {
			this.unitRatio = unitRatio;
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

		public float getRoutqty() {
			return routqty;
		}

		public void setRoutqty(float routqty) {
			this.routqty = routqty;
		}

		public String getRoutunit() {
			return routunit;
		}

		public void setRoutunit(String routunit) {
			this.routunit = routunit;
		}

		public String getRoutunitName() {
			return routunitName;
		}

		public void setRoutunitName(String routunitName) {
			this.routunitName = routunitName;
		}

		public String getProcRate() {
			return procRate;
		}

		public void setProcRate(String procRate) {
			this.procRate = procRate;
		}

		public String getPluMemo() {
			return pluMemo;
		}

		public void setPluMemo(String pluMemo) {
			this.pluMemo = pluMemo;
		}

		public float getReceivingQty() {
			return receivingQty;
		}

		public void setReceivingQty(float receivingQty) {
			this.receivingQty = receivingQty;
		}

		public String getIsBatch() {
			return isBatch;
		}

		public void setIsBatch(String isBatch) {
			this.isBatch = isBatch;
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

		public String getDistriAmt() {
			return distriAmt;
		}

		public void setDistriAmt(String distriAmt) {
			this.distriAmt = distriAmt;
		}

		public String getPunitUdLength() {
			return punitUdLength;
		}

		public void setPunitUdLength(String punitUdLength) {
			this.punitUdLength = punitUdLength;
		}

        public String getPackingNo() {
            return packingNo;
        }

        public void setPackingNo(String packingNo) {
            this.packingNo = packingNo;
        }
		
		public String getBaseUnitUdLength() {
			return baseUnitUdLength;
		}
		
		public void setBaseUnitUdLength(String baseUnitUdLength) {
			this.baseUnitUdLength = baseUnitUdLength;
		}

        public String getTransferBatchNo() {
            return transferBatchNo;
        }

        public void setTransferBatchNo(String transferBatchNo) {
            this.transferBatchNo = transferBatchNo;
        }
    }
}
