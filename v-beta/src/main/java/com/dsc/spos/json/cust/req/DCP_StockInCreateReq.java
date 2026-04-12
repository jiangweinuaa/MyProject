package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;


/**
 * 服務函數：StockInCreate
 *   說明：收货单保存
 * 服务说明：收货单保存
 * @author panjing 
 * @since  2016-10-09
 */
public class DCP_StockInCreateReq extends JsonBasicReq{

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
		private String stockInID;
		private String bsNo;
		private String warehouse;
		private String transferShop;
        private String transferWarehouse;
		private String receiptDate;	
		private String totPqty;
		private String totAmt;
		private String totCqty;
		private String totDistriAmt;
		private String packingNo;
		private String deliveryBy;

        private String invWarehouse;
        private String employeeId;
        private String departId;

		private String ooType;
		private String oofNo;

        private String corp;
        private String deliveryCorp;

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
		public String getLoadDocType() {
			return loadDocType;
		}
		public void setLoadDocType(String loadDocType) {
			this.loadDocType = loadDocType;
		}
		public String getStockInID() {
			return stockInID;
		}
		public void setStockInID(String stockInID) {
			this.stockInID = stockInID;
		}
		public String getWarehouse() {
			return warehouse;
		}
		public void setWarehouse(String warehouse) {
			this.warehouse = warehouse;
		}
		public String getTransferShop() {
			return transferShop;
		}
		public void setTransferShop(String transferShop) {
			this.transferShop = transferShop;
		}
		public String getReceiptDate() {
			return receiptDate;
		}
		public void setReceiptDate(String receiptDate) {
			this.receiptDate = receiptDate;
		}
		public List<level1Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level1Elm> datas) {
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
		public String getTotDistriAmt() {
			return totDistriAmt;
		}
		public void setTotDistriAmt(String totDistriAmt) {
			this.totDistriAmt = totDistriAmt;
		}
		
		public String getPackingNo()
		{
			return packingNo;
		}
		public void setPackingNo(String packingNo)
		{
			this.packingNo = packingNo;
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
		public String getBsNo() {
			return bsNo;
		}
		public void setBsNo(String bsNo) {
			this.bsNo = bsNo;
		}
		public String getDeliveryBy() {
			return deliveryBy;
		}
		public void setDeliveryBy(String deliveryBy) {
			this.deliveryBy = deliveryBy;
		}

        public String getInvWarehouse() {
            return invWarehouse;
        }

        public void setInvWarehouse(String invWarehouse) {
            this.invWarehouse = invWarehouse;
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
	public class level1Elm
	{
		private String item;
		private String oType;
		private String ofNo;
		private String oItem;
		private String ooType;
		private String oofNo;
		private String ooItem;
		private String pluNo;
		private String punit;
		private String pqty;
		private String poQty;
		private String receivingQty;
		private String price;
		private String amt;
		private String pluBarcode;
		private String warehouse;		
		private String pluMemo;
		private String batchNo;
		private String prodDate;
		private String distriPrice;
		private String distriAmt;
		private String punitUdLength;
		private String baseUnit;
		private String baseQty;
		private String unitRatio;
		private String featureNo;
		private String packingNo;
        private String expDate;
		private String originNo;//原始订单号
		private String originItem;
		private String transferBatchNo;
        private String location;
        private String bsNo;

		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public String getoType() {
			return oType;
		}
		public void setoType(String oType) {
			this.oType = oType;
		}
		public String getoItem() {
			return oItem;
		}
		public void setoItem(String oItem) {
			this.oItem = oItem;
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
		public String getPoQty() {
			return poQty;
		}
		public void setPoQty(String poQty) {
			this.poQty = poQty;
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
		public String getPluBarcode() {
			return pluBarcode;
		}
		public void setPluBarcode(String pluBarcode) {
			this.pluBarcode = pluBarcode;
		}
		public String getWarehouse() {
			return warehouse;
		}
		public void setWarehouse(String warehouse) {
			this.warehouse = warehouse;
		}
		public String getPluMemo() {
			return pluMemo;
		}
		public void setPluMemo(String pluMemo) {
			this.pluMemo = pluMemo;
		}
		public String getReceivingQty() {
			return receivingQty;
		}
		public void setReceivingQty(String receivingQty) {
			this.receivingQty = receivingQty;
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
		public String getFeatureNo() {
			return featureNo;
		}
		public void setFeatureNo(String featureNo) {
			this.featureNo = featureNo;
		}
        public String getPackingNo() {
            return packingNo;
        }
        public void setPackingNo(String packingNo) {
            this.packingNo = packingNo;
        }

        public String getExpDate() {
            return expDate;
        }

        public void setExpDate(String expDate) {
            this.expDate = expDate;
        }

		public String getOriginNo() {
			return originNo;
		}

		public void setOriginNo(String originNo) {
			this.originNo = originNo;
		}

		public String getOriginItem() {
			return originItem;
		}

		public void setOriginItem(String originItem) {
			this.originItem = originItem;
		}

		public String getTransferBatchNo() {
			return transferBatchNo;
		}

		public void setTransferBatchNo(String transferBatchNo) {
			this.transferBatchNo = transferBatchNo;
		}

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getBsNo() {
            return bsNo;
        }

        public void setBsNo(String bsNo) {
            this.bsNo = bsNo;
        }
    }


}
