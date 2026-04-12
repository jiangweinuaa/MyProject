package com.dsc.spos.json.cust.res;
import java.util.List;
import com.dsc.spos.json.cust.JsonRes;

/**
 * 服務函數：PTemplateGet
 *   說明：要货模板查询
 * 服务说明：要货模板查询
 * @author Jinzma
 * @since  2017-03-09
 */
public class DCP_PTemplateDetailRes extends JsonRes {

    private List<level1Elm> datas;
    public List<level1Elm> getDatas() {
        return datas;
    }
    public void setDatas(List<level1Elm> datas) {
        this.datas = datas;
    }

    public class level1Elm {
        private String pTemplateNo;
        private String pTemplateName;
        private String preDay;
        private String receiptOrg;
        private String receiptOrgName;
        private String isBTake;
        private String optionalTime;
        private String taskWay;
        private String goodsWarehouseNo;
        private String goodsWarehouseName;
        private String materialWarehouseNo;
        private String materialWarehouseName;
        private String supplier;
        private String supplierName;
        private String abbr;
        private String cal_type;
        private String materal_type;
        private String stockInAllowType;
        private String stockOutAllowType;
        private String isAdjustStock;
        private String rdate_Type;
        private String rdate_Add;
        private String rdate_Values;
        private String revoke_Day;
        private String revoke_Time;
        private String rdate_Times;
        private String isAddGoods;
        private String isShowHeadStockQty;
        private String docType;
        private List<level2Elm> datas;

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
        public String getPreDay() {
            return preDay;
        }
        public void setPreDay(String preDay) {
            this.preDay = preDay;
        }
        public String getReceiptOrg() {
            return receiptOrg;
        }
        public void setReceiptOrg(String receiptOrg) {
            this.receiptOrg = receiptOrg;
        }
        public String getReceiptOrgName() {
            return receiptOrgName;
        }
        public void setReceiptOrgName(String receiptOrgName) {
            this.receiptOrgName = receiptOrgName;
        }
        public String getIsBTake() {
            return isBTake;
        }
        public void setIsBTake(String isBTake) {
            this.isBTake = isBTake;
        }
        public String getOptionalTime() {
            return optionalTime;
        }
        public void setOptionalTime(String optionalTime) {
            this.optionalTime = optionalTime;
        }
        public String getTaskWay() {
            return taskWay;
        }
        public void setTaskWay(String taskWay) {
            this.taskWay = taskWay;
        }
        public String getGoodsWarehouseNo() {
            return goodsWarehouseNo;
        }
        public void setGoodsWarehouseNo(String goodsWarehouseNo) {
            this.goodsWarehouseNo = goodsWarehouseNo;
        }
        public String getGoodsWarehouseName() {
            return goodsWarehouseName;
        }
        public void setGoodsWarehouseName(String goodsWarehouseName) {
            this.goodsWarehouseName = goodsWarehouseName;
        }
        public String getMaterialWarehouseNo() {
            return materialWarehouseNo;
        }
        public void setMaterialWarehouseNo(String materialWarehouseNo) {
            this.materialWarehouseNo = materialWarehouseNo;
        }
        public String getMaterialWarehouseName() {
            return materialWarehouseName;
        }
        public void setMaterialWarehouseName(String materialWarehouseName) {
            this.materialWarehouseName = materialWarehouseName;
        }
        public String getSupplier() {
            return supplier;
        }
        public void setSupplier(String supplier) {
            this.supplier = supplier;
        }
        public String getSupplierName() {
            return supplierName;
        }
        public void setSupplierName(String supplierName) {
            this.supplierName = supplierName;
        }
        public String getAbbr() {
            return abbr;
        }
        public void setAbbr(String abbr) {
            this.abbr = abbr;
        }
        public String getCal_type() {
            return cal_type;
        }
        public void setCal_type(String cal_type) {
            this.cal_type = cal_type;
        }
        public String getMateral_type() {
            return materal_type;
        }
        public void setMateral_type(String materal_type) {
            this.materal_type = materal_type;
        }
        public String getStockInAllowType() {
            return stockInAllowType;
        }
        public void setStockInAllowType(String stockInAllowType) {
            this.stockInAllowType = stockInAllowType;
        }
        public String getStockOutAllowType() {
            return stockOutAllowType;
        }
        public void setStockOutAllowType(String stockOutAllowType) {
            this.stockOutAllowType = stockOutAllowType;
        }
        public String getIsAdjustStock() {
            return isAdjustStock;
        }
        public void setIsAdjustStock(String isAdjustStock) {
            this.isAdjustStock = isAdjustStock;
        }
        public String getRdate_Type() {
            return rdate_Type;
        }
        public void setRdate_Type(String rdate_Type) {
            this.rdate_Type = rdate_Type;
        }
        public String getRdate_Add() {
            return rdate_Add;
        }
        public void setRdate_Add(String rdate_Add) {
            this.rdate_Add = rdate_Add;
        }
        public String getRdate_Values() {
            return rdate_Values;
        }
        public void setRdate_Values(String rdate_Values) {
            this.rdate_Values = rdate_Values;
        }
        public String getRevoke_Day() {
            return revoke_Day;
        }
        public void setRevoke_Day(String revoke_Day) {
            this.revoke_Day = revoke_Day;
        }
        public String getRevoke_Time() {
            return revoke_Time;
        }
        public void setRevoke_Time(String revoke_Time) {
            this.revoke_Time = revoke_Time;
        }
        public String getRdate_Times() {
            return rdate_Times;
        }
        public void setRdate_Times(String rdate_Times) {
            this.rdate_Times = rdate_Times;
        }
        public String getIsAddGoods() {
            return isAddGoods;
        }
        public void setIsAddGoods(String isAddGoods) {
            this.isAddGoods = isAddGoods;
        }
        public String getIsShowHeadStockQty() {
            return isShowHeadStockQty;
        }
        public void setIsShowHeadStockQty(String isShowHeadStockQty) {
            this.isShowHeadStockQty = isShowHeadStockQty;
        }
        public List<level2Elm> getDatas() {
            return datas;
        }
        public void setDatas(List<level2Elm> datas) {
            this.datas = datas;
        }

        public String getDocType() {
            return docType;
        }

        public void setDocType(String docType) {
            this.docType = docType;
        }
    }

    public class level2Elm{
        private String item;
        private String pluNo;
        private String pluName;
        private String spec;

        private String category;
        private String categoryName;
        private String shortCutCode;
        private String price;
        private String standardPrice;
        private String distriPrice;
        private String unitRatio;
        private String baseUnit;
        private String baseUnitName;
        private String punit;
        private String punitName;
        private String minQty;
        private String maxQty;
        private String mulQty;
        private String defQty;
        private String pMulQty;
        private String pStockInQty;
        private String refBaseQty;
        private String isBatch;
        private String stockInValidDay;
        private String stockOutValidDay;
        private String shelfLife;
        private String batchNo;
        private String prodDate;
        private String punitUdLength;//punit对应的小数位数长度
        private String groupNo;//要货商品组别编号
        private String groupType;//组别类型
        private String groupReachCount;//达成条件
        private String listImage;
        private String featureNo;
        private String featureName;
        private String isHoliday;
        private String isNewGoods;
        private String isHotGoods;
        private List<level3Elm> unitList;
        private String stockManageType;
        /**
         * 特征码汇总库存量，根据前端传参取值,默认999999
         */
        private String Stockqty;
        private String baseUnitUdLength;
        private String warningQty;
        private String supplierType; //20250217 调整，增加返参  01029
        private String supplierId;
        private String supplierName;

        public String getStockqty()
        {
            return Stockqty;
        }
        public void setStockqty(String stockqty)
        {
            Stockqty = stockqty;
        }
        public String getStockManageType() {
            return stockManageType;
        }
        public void setStockManageType(String stockManageType) {
            this.stockManageType = stockManageType;
        }
        public String getItem() {
            return item;
        }
        public void setItem(String item) {
            this.item = item;
        }
        public String getPluNo() {
            return pluNo;
        }
        public void setPluNo(String pluNo) {
            this.pluNo = pluNo;
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
        public String getShortCutCode() {
            return shortCutCode;
        }
        public void setShortCutCode(String shortCutCode) {
            this.shortCutCode = shortCutCode;
        }
        public String getPrice() {
            return price;
        }
        public void setPrice(String price) {
            this.price = price;
        }
        public String getDistriPrice() {
            return distriPrice;
        }
        public void setDistriPrice(String distriPrice) {
            this.distriPrice = distriPrice;
        }
        public String getUnitRatio() {
            return unitRatio;
        }
        public void setUnitRatio(String unitRatio) {
            this.unitRatio = unitRatio;
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
        public String getMinQty() {
            return minQty;
        }
        public void setMinQty(String minQty) {
            this.minQty = minQty;
        }
        public String getMaxQty() {
            return maxQty;
        }
        public void setMaxQty(String maxQty) {
            this.maxQty = maxQty;
        }
        public String getMulQty() {
            return mulQty;
        }
        public void setMulQty(String mulQty) {
            this.mulQty = mulQty;
        }
        public String getDefQty() {
            return defQty;
        }
        public void setDefQty(String defQty) {
            this.defQty = defQty;
        }
        public String getpMulQty() {
            return pMulQty;
        }
        public void setpMulQty(String pMulQty) {
            this.pMulQty = pMulQty;
        }
        public String getpStockInQty() {
            return pStockInQty;
        }
        public void setpStockInQty(String pStockInQty) {
            this.pStockInQty = pStockInQty;
        }
        public String getRefBaseQty() {
            return refBaseQty;
        }
        public void setRefBaseQty(String refBaseQty) {
            this.refBaseQty = refBaseQty;
        }
        public String getIsBatch() {
            return isBatch;
        }
        public void setIsBatch(String isBatch) {
            this.isBatch = isBatch;
        }
        public String getStockInValidDay() {
            return stockInValidDay;
        }
        public void setStockInValidDay(String stockInValidDay) {
            this.stockInValidDay = stockInValidDay;
        }
        public String getStockOutValidDay() {
            return stockOutValidDay;
        }
        public void setStockOutValidDay(String stockOutValidDay) {
            this.stockOutValidDay = stockOutValidDay;
        }
        public String getShelfLife() {
            return shelfLife;
        }
        public void setShelfLife(String shelfLife) {
            this.shelfLife = shelfLife;
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
        public String getPunitUdLength() {
            return punitUdLength;
        }
        public void setPunitUdLength(String punitUdLength) {
            this.punitUdLength = punitUdLength;
        }
        public String getGroupNo() {
            return groupNo;
        }
        public void setGroupNo(String groupNo) {
            this.groupNo = groupNo;
        }
        public String getGroupType() {
            return groupType;
        }
        public void setGroupType(String groupType) {
            this.groupType = groupType;
        }
        public String getGroupReachCount() {
            return groupReachCount;
        }
        public void setGroupReachCount(String groupReachCount) {
            this.groupReachCount = groupReachCount;
        }
        public String getListImage() {
            return listImage;
        }
        public void setListImage(String listImage) {
            this.listImage = listImage;
        }
        public String getFeatureNo() {
            return featureNo;
        }
        public void setFeatureNo(String featureNo) {
            this.featureNo = featureNo;
        }
        public String getFeatureName() {
            return featureName;
        }
        public void setFeatureName(String featureName) {
            this.featureName = featureName;
        }
        public String getIsHoliday() {
            return isHoliday;
        }
        public void setIsHoliday(String isHoliday) {
            this.isHoliday = isHoliday;
        }
        public String getIsNewGoods() {
            return isNewGoods;
        }
        public void setIsNewGoods(String isNewGoods) {
            this.isNewGoods = isNewGoods;
        }
        public String getIsHotGoods() {
            return isHotGoods;
        }
        public void setIsHotGoods(String isHotGoods) {
            this.isHotGoods = isHotGoods;
        }
        public List<level3Elm> getUnitList() {
            return unitList;
        }
        public void setUnitList(List<level3Elm> unitList) {
            this.unitList = unitList;
        }
        public String getBaseUnitUdLength() {
            return baseUnitUdLength;
        }
        public void setBaseUnitUdLength(String baseUnitUdLength) {
            this.baseUnitUdLength = baseUnitUdLength;
        }
        public String getWarningQty() {
            return warningQty;
        }
        public void setWarningQty(String warningQty) {
            this.warningQty = warningQty;
        }
		public String getSupplierType() {
			return supplierType;
		}
		public void setSupplierType(String supplierType) {
			this.supplierType = supplierType;
		}
		public String getSupplierId() {
			return supplierId;
		}
		public void setSupplierId(String supplierId) {
			this.supplierId = supplierId;
		}
		public String getSupplierName() {
			return supplierName;
		}
		public void setSupplierName(String supplierName) {
			this.supplierName = supplierName;
		}

        public String getStandardPrice() {
            return standardPrice;
        }

        public void setStandardPrice(String standardPrice) {
            this.standardPrice = standardPrice;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }
    }

    public class level3Elm{
        private String punit;
        private String punitName;
        private String unitRatio;
        private String punitUDLength;

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
        public String getUnitRatio() {
            return unitRatio;
        }
        public void setUnitRatio(String unitRatio) {
            this.unitRatio = unitRatio;
        }
        public String getPunitUDLength() {
            return punitUDLength;
        }
        public void setPunitUDLength(String punitUDLength) {
            this.punitUDLength = punitUDLength;
        }
    }
}
