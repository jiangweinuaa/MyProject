package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

public class DCP_PStockInRefundUpdateReq  extends JsonBasicReq{


	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String pStockInNo;
		private String bDate;
		private String memo;
		private String pTemplateNo;
		private String warehouse;
		private String materialWarehouseNo;
		private String docType;
		private String totPqty;
		private String totAmt;
		private String totDistriAmt;
		private String totCqty;
		private String pStockInNo_origin;//原完工入库单号

        private String employeeId;
        private String departId;
        private String prodType;
        private String oOType;
        private String oOfNo;
		private List<level1Elm> datas;
		
		public String getpStockInNo() {
			return pStockInNo;
		}
		public void setpStockInNo(String pStockInNo) {
			this.pStockInNo = pStockInNo;
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
		public String getpStockInNo_origin() {
			return pStockInNo_origin;
		}
		public void setpStockInNo_origin(String pStockInNo_origin) {
			this.pStockInNo_origin = pStockInNo_origin;
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
		private String punit;
		private String pqty;
		private String price;
		private String amt;
		private String scrapQty;
		private String taskQty;
		private String mulQty;
		private String bsNo;
		private String warehouse;
		private String batchNo;
		private String prodDate;
		private String distriPrice;
		private String distriAmt;
		private String featureNo;
		private String baseUnit;
		private String baseQty;
		private String unitRatio;
		private String pqty_origin;        //红充的完工入库单时 ，原单的pqty
		private String pqty_refund;        //完工入单时，红充数
		private String scrapQty_origin;    //红充的完工入库单时 ，原单的scrapQty
		private String scrapQty_refund;    //完工入单时，红充数scrapQty
        private String memo;

        private String location;
        private String expDate;

        private String dispType;
        private String oOItem;
		//2018-08-10新增level2Elm
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
		public String getWarehouse() {
			return warehouse;
		}
		public void setWarehouse(String warehouse) {
			this.warehouse = warehouse;
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
		public String getPqty_origin() {
			return pqty_origin;
		}
		public void setPqty_origin(String pqty_origin) {
			this.pqty_origin = pqty_origin;
		}
		public String getPqty_refund() {
			return pqty_refund;
		}
		public void setPqty_refund(String pqty_refund) {
			this.pqty_refund = pqty_refund;
		}
		public String getScrapQty_origin() {
			return scrapQty_origin;
		}
		public void setScrapQty_origin(String scrapQty_origin) {
			this.scrapQty_origin = scrapQty_origin;
		}
		public String getScrapQty_refund() {
			return scrapQty_refund;
		}
		public void setScrapQty_refund(String scrapQty_refund) {
			this.scrapQty_refund = scrapQty_refund;
		}
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
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
		private String isBuckle;
		private String material_featureNo;
		private String material_baseUnit;
		private String material_baseQty;
		private String material_unitRatio;

        private List<DCP_PStockInRefundUpdateReq.materialBatchList> materialBatchList;


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

        public List<DCP_PStockInRefundUpdateReq.materialBatchList> getMaterialBatchList() {
            return materialBatchList;
        }

        public void setMaterialBatchList(List<DCP_PStockInRefundUpdateReq.materialBatchList> materialBatchList) {
            this.materialBatchList = materialBatchList;
        }
    }

    @Data
    public class materialBatchList{
        private String item;
        private String oitem;
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


}
