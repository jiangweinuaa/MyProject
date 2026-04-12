package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;
/**
 * 服务函数：DCP_BatchPStockInQuery
 * 說明：分批出数单身查询
 * @author JZMA 
 * @since  2020-07-07
 */
public class DCP_BatchPStockInDetailRes  extends JsonRes {

	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm
	{
		private String item;
		private String pluNo;
		private String pluName;
		private String punit;
		private String punitName;
		private String price;
		private String distriPrice;
		private String amt;
		private String distriAmt;
		private String taskQty;
		private String pqty;
		private String scrapQty;
		private String mulQty;
		private String bsNo;
		private String bsName;
		private String unitRatio;
		private String listImage;
		private String spec;
		private String baseQty;
		private String baseUnit;
		private String baseUnitName;
		private String featureNo;
		private String featureName;		
		private List<level2Elm> material;

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
		public String getAmt() {
			return amt;
		}
		public void setAmt(String amt) {
			this.amt = amt;
		}
		public String getDistriAmt() {
			return distriAmt;
		}
		public void setDistriAmt(String distriAmt) {
			this.distriAmt = distriAmt;
		}
		public String getTaskQty() {
			return taskQty;
		}
		public void setTaskQty(String taskQty) {
			this.taskQty = taskQty;
		}
		public String getPqty() {
			return pqty;
		}
		public void setPqty(String pqty) {
			this.pqty = pqty;
		}
		public String getScrapQty() {
			return scrapQty;
		}
		public void setScrapQty(String scrapQty) {
			this.scrapQty = scrapQty;
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
		public String getBsName() {
			return bsName;
		}
		public void setBsName(String bsName) {
			this.bsName = bsName;
		}
		public String getUnitRatio() {
			return unitRatio;
		}
		public void setUnitRatio(String unitRatio) {
			this.unitRatio = unitRatio;
		}
		public String getListImage() {
			return listImage;
		}
		public void setListImage(String listImage) {
			this.listImage = listImage;
		}
		public String getSpec() {
			return spec;
		}
		public void setSpec(String spec) {
			this.spec = spec;
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
		public List<level2Elm> getMaterial() {
			return material;
		}
		public void setMaterial(List<level2Elm> material) {
			this.material = material;
		}
	}

	public class level2Elm
	{
		private String mItem;
		private String material_pluNo;
		private String material_pluName;
		private String material_punit;
		private String material_punitName;
		private String material_pqty;
		private String material_price;
		private String material_distriPrice;
		private String material_amt;
		private String material_distriAmt;
		private String material_finalProdBaseQty;
		private String material_rawMaterialBaseQty;
		private String isBuckle; 
		private String material_unitRatio;
		private String material_spec;
		private String material_listImage;
		private String material_baseUnit;
		private String material_baseUnitName;
		private String material_baseQty;
		private String material_isFeature;

		public String getmItem() {
			return mItem;
		}
		public void setmItem(String mItem) {
			this.mItem = mItem;
		}
		public String getMaterial_pluNo() {
			return material_pluNo;
		}
		public void setMaterial_pluNo(String material_pluNo) {
			this.material_pluNo = material_pluNo;
		}
		public String getMaterial_pluName() {
			return material_pluName;
		}
		public void setMaterial_pluName(String material_pluName) {
			this.material_pluName = material_pluName;
		}
		public String getMaterial_punit() {
			return material_punit;
		}
		public void setMaterial_punit(String material_punit) {
			this.material_punit = material_punit;
		}
		public String getMaterial_punitName() {
			return material_punitName;
		}
		public void setMaterial_punitName(String material_punitName) {
			this.material_punitName = material_punitName;
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
		public String getMaterial_distriPrice() {
			return material_distriPrice;
		}
		public void setMaterial_distriPrice(String material_distriPrice) {
			this.material_distriPrice = material_distriPrice;
		}
		public String getMaterial_amt() {
			return material_amt;
		}
		public void setMaterial_amt(String material_amt) {
			this.material_amt = material_amt;
		}
		public String getMaterial_distriAmt() {
			return material_distriAmt;
		}
		public void setMaterial_distriAmt(String material_distriAmt) {
			this.material_distriAmt = material_distriAmt;
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
		public String getIsBuckle() {
			return isBuckle;
		}
		public void setIsBuckle(String isBuckle) {
			this.isBuckle = isBuckle;
		}
		public String getMaterial_unitRatio() {
			return material_unitRatio;
		}
		public void setMaterial_unitRatio(String material_unitRatio) {
			this.material_unitRatio = material_unitRatio;
		}
		public String getMaterial_listImage() {
			return material_listImage;
		}
		public void setMaterial_listImage(String material_listImage) {
			this.material_listImage = material_listImage;
		}
		public String getMaterial_baseUnit() {
			return material_baseUnit;
		}
		public void setMaterial_baseUnit(String material_baseUnit) {
			this.material_baseUnit = material_baseUnit;
		}
		public String getMaterial_baseUnitName() {
			return material_baseUnitName;
		}
		public void setMaterial_baseUnitName(String material_baseUnitName) {
			this.material_baseUnitName = material_baseUnitName;
		}
		public String getMaterial_baseQty() {
			return material_baseQty;
		}
		public void setMaterial_baseQty(String material_baseQty) {
			this.material_baseQty = material_baseQty;
		}
		public String getMaterial_spec() {
			return material_spec;
		}
		public void setMaterial_spec(String material_spec) {
			this.material_spec = material_spec;
		}
		public String getMaterial_isFeature() {
			return material_isFeature;
		}
		public void setMaterial_isFeature(String material_isFeature) {
			this.material_isFeature = material_isFeature;
		}
	}


}
