package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import java.util.List;

public class DCP_GoodsSetCreateReq extends JsonBasicReq {
	
	private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}
	public void setRequest(levelRequest request) {
		this.request = request;
	}
	
	public class levelRequest {
		private String pluNo;
		private String pluId;
		private List<pluName_lang> pluName_lang;
		private String pluType;        //类型：NORMAL-普通商品 FEATURE-多特征商品 PACKAGE-套餐商品
		private String attrGroupId;
		private List<attr> attrList;
		private List<barcode> barcodeList;
		private String description;
		private String shortcutCode;
		private String warmType;
		private String category;
		private String brandNo;
		private String seriesNo;
		private String taxCode;
		private String virtual;
		private String openPrice;
		private String isWeight;
		private String stockManageType;
		private String memo;
		private String status;
		private String price;//标准零售价，大于等于0【默认销售单位】
		private String supPrice;//标准进货价，大于等于0【默认要货单位】
		
		private String baseUnit;//基准单位编码
		private String pUnit;//要货单位编码
		private String purUnit;//采购单位编码
		private String sUnit;//默认销售单位编码
		private String cUnit;//盘点单位编码
		private String wUnit;//库存单位编码
		private String bomUnit;//配方单位编码
		private String prodUnit;//成品的生产单位
		private String rUnit;//退货单位
		private List<unit> unitList;
		private String isBatch;
		private String shelfLife;
		private String stockInValidDay;
		private String stockOutValidDay;
		private String checkValidDay;
		private String isHoliday;
		private String isHotGoods;
		private String isOwnGoods;
		private String isDoubleGoods;
		private String selfBuiltShopId;
        private String splitType;
        private String shortCutCode;
        private String mainBarcode;
        private String sourceType;
        private String spec;
        private String pickUnit;
        private String purPrice;
        private String supplier;
        private String procRate;
        private String isQualityCheck;

        private List<GoodsTemplateList> goodsTemplateList;
        private List<POrderTemplateList> pOrderTemplateList;
        private String testMarketSaleQty;
        private String testMarketSaleAmt;
        private String testMarketGrossAmt;
        private String minQty;
        private String maxQty;
        private String mulQty;
        private String batchRules;
        private String batchSortType;
        private String isShelfLifeCheck;
        private String prodShop;
        private String prodHQ;
        private String prodOEM;
        private String isSensitive;
        private String isCombineBatch;
        private String materialProperties;
        private String productTime;
        private String producer;
        private String storageCon;
        private String manufacturer;
        private String hotLine;
        private String ingreTable;
        private String netContent;
        private String foodProLicNum;
        private String eatingMethod;
        private String exStandard;
        private String propAddress;
        private String inputTaxCode;
        private List<TagList> tagList;
		
		public String getrUnit() {
			return rUnit;
		}
		public void setrUnit(String rUnit) {
			this.rUnit = rUnit;
		}
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getPluId() {
			return pluId;
		}
		public void setPluId(String pluId) {
			this.pluId = pluId;
		}
		public List<pluName_lang> getPluName_lang() {
			return pluName_lang;
		}
		public void setPluName_lang(List<pluName_lang> pluName_lang) {
			this.pluName_lang = pluName_lang;
		}
		public String getPluType() {
			return pluType;
		}
		public void setPluType(String pluType) {
			this.pluType = pluType;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getAttrGroupId() {
			return attrGroupId;
		}
		public String getSupPrice()
		{
			return supPrice;
		}
		public void setSupPrice(String supPrice)
		{
			this.supPrice = supPrice;
		}
		public void setAttrGroupId(String attrGroupId) {
			this.attrGroupId = attrGroupId;
		}
		public List<attr> getAttrList() {
			return attrList;
		}
		public void setAttrList(List<attr> attrList) {
			this.attrList = attrList;
		}
		public List<barcode> getBarcodeList() {
			return barcodeList;
		}
		public void setBarcodeList(List<barcode> barcodeList) {
			this.barcodeList = barcodeList;
		}
		public String getShortcutCode() {
			return shortcutCode;
		}
		public void setShortcutCode(String shortcutCode) {
			this.shortcutCode = shortcutCode;
		}
		public String getWarmType() {
			return warmType;
		}
		public void setWarmType(String warmType) {
			this.warmType = warmType;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public String getBrandNo() {
			return brandNo;
		}
		public void setBrandNo(String brandNo) {
			this.brandNo = brandNo;
		}
		public String getSeriesNo() {
			return seriesNo;
		}
		public void setSeriesNo(String seriesNo) {
			this.seriesNo = seriesNo;
		}
		public String getTaxCode() {
			return taxCode;
		}
		public void setTaxCode(String taxCode) {
			this.taxCode = taxCode;
		}
		public String getVirtual() {
			return virtual;
		}
		public void setVirtual(String virtual) {
			this.virtual = virtual;
		}
		public String getOpenPrice() {
			return openPrice;
		}
		public void setOpenPrice(String openPrice) {
			this.openPrice = openPrice;
		}
		public String getIsWeight() {
			return isWeight;
		}
		public void setIsWeight(String isWeight) {
			this.isWeight = isWeight;
		}
		public String getStockManageType() {
			return stockManageType;
		}
		public void setStockManageType(String stockManageType) {
			this.stockManageType = stockManageType;
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
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getBaseUnit() {
			return baseUnit;
		}
		public void setBaseUnit(String baseUnit) {
			this.baseUnit = baseUnit;
		}
		public String getpUnit() {
			return pUnit;
		}
		public void setpUnit(String pUnit) {
			this.pUnit = pUnit;
		}
		public String getPurUnit() {
			return purUnit;
		}
		public void setPurUnit(String purUnit) {
			this.purUnit = purUnit;
		}
		public String getsUnit() {
			return sUnit;
		}
		public void setsUnit(String sUnit) {
			this.sUnit = sUnit;
		}
		public String getcUnit() {
			return cUnit;
		}
		public void setcUnit(String cUnit) {
			this.cUnit = cUnit;
		}
		public String getwUnit() {
			return wUnit;
		}
		public void setwUnit(String wUnit) {
			this.wUnit = wUnit;
		}
		public String getBomUnit() {
			return bomUnit;
		}
		public void setBomUnit(String bomUnit) {
			this.bomUnit = bomUnit;
		}
		public String getProdUnit() {
			return prodUnit;
		}
		public void setProdUnit(String prodUnit) {
			this.prodUnit = prodUnit;
		}
		public List<unit> getUnitList() {
			return unitList;
		}
		public void setUnitList(List<unit> unitList) {
			this.unitList = unitList;
		}
		public String getIsBatch() {
			return isBatch;
		}
		public void setIsBatch(String isBatch) {
			this.isBatch = isBatch;
		}
		public String getShelfLife() {
			return shelfLife;
		}
		public void setShelfLife(String shelfLife) {
			this.shelfLife = shelfLife;
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
		public String getCheckValidDay() {
			return checkValidDay;
		}
		public void setCheckValidDay(String checkValidDay) {
			this.checkValidDay = checkValidDay;
		}
		public String getIsHoliday()
		{
			return isHoliday;
		}
		public void setIsHoliday(String isHoliday)
		{
			this.isHoliday = isHoliday;
		}
		public String getIsHotGoods() {
			return isHotGoods;
		}
		public void setIsHotGoods(String isHotGoods) {
			this.isHotGoods = isHotGoods;
		}
		public String getIsOwnGoods() {
			return isOwnGoods;
		}
		public void setIsOwnGoods(String isOwnGoods) {
			this.isOwnGoods = isOwnGoods;
		}
		public String getIsDoubleGoods() {
			return isDoubleGoods;
		}
		public void setIsDoubleGoods(String isDoubleGoods) {
			this.isDoubleGoods = isDoubleGoods;
		}
		public String getSelfBuiltShopId() {
			return selfBuiltShopId;
		}
		public void setSelfBuiltShopId(String selfBuiltShopId) {
			this.selfBuiltShopId = selfBuiltShopId;
		}

        public String getSplitType() {
            return splitType;
        }

        public void setSplitType(String splitType) {
            this.splitType = splitType;
        }

        public String getShortCutCode() {
            return shortCutCode;
        }

        public void setShortCutCode(String shortCutCode) {
            this.shortCutCode = shortCutCode;
        }

        public String getMainBarcode() {
            return mainBarcode;
        }

        public void setMainBarcode(String mainBarcode) {
            this.mainBarcode = mainBarcode;
        }

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        public String getSpec() {
            return spec;
        }

        public void setSpec(String spec) {
            this.spec = spec;
        }

        public String getPickUnit() {
            return pickUnit;
        }

        public void setPickUnit(String pickUnit) {
            this.pickUnit = pickUnit;
        }

        public String getSupplier() {
            return supplier;
        }

        public void setSupplier(String supplier) {
            this.supplier = supplier;
        }

        public String getProcRate() {
            return procRate;
        }

        public void setProcRate(String procRate) {
            this.procRate = procRate;
        }

        public String getIsQualityCheck() {
            return isQualityCheck;
        }

        public void setIsQualityCheck(String isQualityCheck) {
            this.isQualityCheck = isQualityCheck;
        }

        public List<GoodsTemplateList> getGoodsTemplateList() {
            return goodsTemplateList;
        }

        public void setGoodsTemplateList(List<GoodsTemplateList> goodsTemplateList) {
            this.goodsTemplateList = goodsTemplateList;
        }

        public List<POrderTemplateList> getpOrderTemplateList() {
            return pOrderTemplateList;
        }

        public void setpOrderTemplateList(List<POrderTemplateList> pOrderTemplateList) {
            this.pOrderTemplateList = pOrderTemplateList;
        }

        public String getTestMarketSaleQty() {
            return testMarketSaleQty;
        }

        public void setTestMarketSaleQty(String testMarketSaleQty) {
            this.testMarketSaleQty = testMarketSaleQty;
        }

        public String getTestMarketSaleAmt() {
            return testMarketSaleAmt;
        }

        public void setTestMarketSaleAmt(String testMarketSaleAmt) {
            this.testMarketSaleAmt = testMarketSaleAmt;
        }

        public String getTestMarketGrossAmt() {
            return testMarketGrossAmt;
        }

        public void setTestMarketGrossAmt(String testMarketGrossAmt) {
            this.testMarketGrossAmt = testMarketGrossAmt;
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

        public String getBatchRules() {
            return batchRules;
        }

        public void setBatchRules(String batchRules) {
            this.batchRules = batchRules;
        }

        public String getBatchSortType() {
            return batchSortType;
        }

        public void setBatchSortType(String batchSortType) {
            this.batchSortType = batchSortType;
        }

        public String getIsShelfLifeCheck() {
            return isShelfLifeCheck;
        }

        public void setIsShelfLifeCheck(String isShelfLifeCheck) {
            this.isShelfLifeCheck = isShelfLifeCheck;
        }

        public String getProdShop() {
            return prodShop;
        }

        public void setProdShop(String prodShop) {
            this.prodShop = prodShop;
        }

        public String getProdHQ() {
            return prodHQ;
        }

        public void setProdHQ(String prodHQ) {
            this.prodHQ = prodHQ;
        }

        public String getProdOEM() {
            return prodOEM;
        }

        public void setProdOEM(String prodOEM) {
            this.prodOEM = prodOEM;
        }

        public String getIsSensitive() {
            return isSensitive;
        }

        public void setIsSensitive(String isSensitive) {
            this.isSensitive = isSensitive;
        }

        public String getIsCombineBatch() {
            return isCombineBatch;
        }

        public void setIsCombineBatch(String isCombineBatch) {
            this.isCombineBatch = isCombineBatch;
        }

        public String getMaterialProperties() {
            return materialProperties;
        }

        public void setMaterialProperties(String materialProperties) {
            this.materialProperties = materialProperties;
        }

        public String getProductTime() {
            return productTime;
        }

        public void setProductTime(String productTime) {
            this.productTime = productTime;
        }

        public String getProducer() {
            return producer;
        }

        public void setProducer(String producer) {
            this.producer = producer;
        }

        public String getStorageCon() {
            return storageCon;
        }

        public void setStorageCon(String storageCon) {
            this.storageCon = storageCon;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
        }

        public String getHotLine() {
            return hotLine;
        }

        public void setHotLine(String hotLine) {
            this.hotLine = hotLine;
        }

        public String getIngreTable() {
            return ingreTable;
        }

        public void setIngreTable(String ingreTable) {
            this.ingreTable = ingreTable;
        }

        public String getNetContent() {
            return netContent;
        }

        public void setNetContent(String netContent) {
            this.netContent = netContent;
        }

        public String getFoodProLicNum() {
            return foodProLicNum;
        }

        public void setFoodProLicNum(String foodProLicNum) {
            this.foodProLicNum = foodProLicNum;
        }

        public String getEatingMethod() {
            return eatingMethod;
        }

        public void setEatingMethod(String eatingMethod) {
            this.eatingMethod = eatingMethod;
        }

        public String getExStandard() {
            return exStandard;
        }

        public void setExStandard(String exStandard) {
            this.exStandard = exStandard;
        }



        public List<TagList> getTagList() {
            return tagList;
        }

        public void setTagList(List<TagList> tagList) {
            this.tagList = tagList;
        }

		public String getPropAddress() {
			return propAddress;
		}

		public void setPropAddress(String propAddress) {
			this.propAddress = propAddress;
		}

		public String getPurPrice() {
			return purPrice;
		}

		public void setPurPrice(String purPrice) {
			this.purPrice = purPrice;
		}

        public String getInputTaxCode() {
            return inputTaxCode;
        }

        public void setInputTaxCode(String inputTaxCode) {
            this.inputTaxCode = inputTaxCode;
        }
    }
	
	public class pluName_lang {
		private String langType;
		private String name;
		
		public String getLangType() {
			return langType;
		}
		public void setLangType(String langType) {
			this.langType = langType;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
	}
	
	public class spec_lang {
		private String langType;
		private String name;
		
		public String getLangType() {
			return langType;
		}
		public void setLangType(String langType) {
			this.langType = langType;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
	}
	
	public class attr {
		private String attrId;
		private String attrName;
		private List<attrValue> attrValueList;
		public String getAttrId() {
			return attrId;
		}
		public void setAttrId(String attrId) {
			this.attrId = attrId;
		}
		public String getAttrName() {
			return attrName;
		}
		public void setAttrName(String attrName) {
			this.attrName = attrName;
		}
		public List<attrValue> getAttrValueList() {
			return attrValueList;
		}
		public void setAttrValueList(List<attrValue> attrValueList) {
			this.attrValueList = attrValueList;
		}
	}
	
	public class attrValue {
		private String attrValueId;
		private String attrValueName;
		public String getAttrValueId() {
			return attrValueId;
		}
		public void setAttrValueId(String attrValueId) {
			this.attrValueId = attrValueId;
		}
		public String getAttrValueName() {
			return attrValueName;
		}
		public void setAttrValueName(String attrValueName) {
			this.attrValueName = attrValueName;
		}
	}
	
	public class barcode {
		private String unit;//单位
		private String featureNo;//特征码 普通商品和套餐商品为一个空格，FEATURE商品：示例“RED-L”，使用短线连接规格的编码
		private String pluBarcode;
		private String status;
		private String len;
		private String width;
		private String height;
		private String volumeUnit;
		private String weight;
		private String weightUnit;
        private String barcodeType;
		
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public String getFeatureNo() {
			return featureNo;
		}
		public void setFeatureNo(String featureNo) {
			this.featureNo = featureNo;
		}
		public String getPluBarcode() {
			return pluBarcode;
		}
		public void setPluBarcode(String pluBarcode) {
			this.pluBarcode = pluBarcode;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getLen()
		{
			return len;
		}
		public void setLen(String len)
		{
			this.len = len;
		}
		public String getWidth()
		{
			return width;
		}
		public void setWidth(String width)
		{
			this.width = width;
		}
		public String getHeight()
		{
			return height;
		}
		public void setHeight(String height)
		{
			this.height = height;
		}
		public String getVolumeUnit()
		{
			return volumeUnit;
		}
		public void setVolumeUnit(String volumeUnit)
		{
			this.volumeUnit = volumeUnit;
		}
		public String getWeight()
		{
			return weight;
		}
		public void setWeight(String weight)
		{
			this.weight = weight;
		}
		public String getWeightUnit()
		{
			return weightUnit;
		}
		public void setWeightUnit(String weightUnit)
		{
			this.weightUnit = weightUnit;
		}

        public String getBarcodeType() {
            return barcodeType;
        }

        public void setBarcodeType(String barcodeType) {
            this.barcodeType = barcodeType;
        }
    }
	
	public class unit {
		private String oUnit;
		private String oQty;
		private String unit;
		private String qty;
		
		private List<spec_lang> spec_lang;
		private String weight;//重量(KG)
		private String volume;//体积(立方米)
		
		private String pUnitUse;//要货单位编码
		private String purUnitUse;//采购单位编码
		private String sUnitUse;//默认销售单位编码
		private String cUnitUse;//盘点单位编码
		private String bomUnitUse;//配方单位编码
		private String prodUnitUse;//成品的生产单位
		private String rUnitUse="N";
		
		public String getrUnitUse() {
			return rUnitUse;
		}
		public void setrUnitUse(String rUnitUse) {
			this.rUnitUse = rUnitUse;
		}
		public String getoUnit() {
			return oUnit;
		}
		public void setoUnit(String oUnit) {
			this.oUnit = oUnit;
		}
		public String getoQty() {
			return oQty;
		}
		public void setoQty(String oQty) {
			this.oQty = oQty;
		}
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public String getQty() {
			return qty;
		}
		public void setQty(String qty) {
			this.qty = qty;
		}
		public String getpUnitUse() {
			return pUnitUse;
		}
		public void setpUnitUse(String pUnitUse) {
			this.pUnitUse = pUnitUse;
		}
		public String getPurUnitUse() {
			return purUnitUse;
		}
		public void setPurUnitUse(String purUnitUse) {
			this.purUnitUse = purUnitUse;
		}
		public String getsUnitUse() {
			return sUnitUse;
		}
		public void setsUnitUse(String sUnitUse) {
			this.sUnitUse = sUnitUse;
		}
		public String getcUnitUse() {
			return cUnitUse;
		}
		public void setcUnitUse(String cUnitUse) {
			this.cUnitUse = cUnitUse;
		}
		public String getBomUnitUse() {
			return bomUnitUse;
		}
		public void setBomUnitUse(String bomUnitUse) {
			this.bomUnitUse = bomUnitUse;
		}
		public String getProdUnitUse() {
			return prodUnitUse;
		}
		public void setProdUnitUse(String prodUnitUse) {
			this.prodUnitUse = prodUnitUse;
		}
		public List<spec_lang> getSpec_lang() {
			return spec_lang;
		}
		public void setSpec_lang(List<spec_lang> spec_lang) {
			this.spec_lang = spec_lang;
		}
		public String getWeight() {
			return weight;
		}
		public void setWeight(String weight) {
			this.weight = weight;
		}
		public String getVolume() {
			return volume;
		}
		public void setVolume(String volume) {
			this.volume = volume;
		}
		
	}

    public class TagList{
        private String tagNo;

        public String getTagNo() {
            return tagNo;
        }

        public void setTagNo(String tagNo) {
            this.tagNo = tagNo;
        }
    }

    public class GoodsTemplateList{
        private String templateId;

        public String getTemplateId() {
            return templateId;
        }

        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }
    }

    public class POrderTemplateList{
        private String templateNo;

        public String getTemplateNo() {
            return templateNo;
        }

        public void setTemplateNo(String templateNo) {
            this.templateNo = templateNo;
        }
    }
}
