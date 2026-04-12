package com.dsc.spos.json.cust.res;

import java.util.List;
import java.util.Map;
import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.JsonRes;

/**
 * BarcodeInforGet 專用的 response json
 * @author panjing 
 * @since  2016-09-22
 */

public class DCP_OrderBarcodeInforQueryRes extends JsonBasicRes {
/**
* {	
	"success": true,	成功否
	"serviceStatus": "000",	服務狀態代碼
	"serviceDescription": "服務執行成功",	服務狀態說明
	 "datas": [	
		{	
		"pluBarcode":
		"pluNO": "SP001",	商品编码
		"pluName": "海鲜鱿鱼丝",	商品名称
		"category": "01",	小类编号
		"categoryName": "真皮系列",	小类名称
		"spec": "大包装",	商品规格
		"picture": "",	商品图片
		"wunit": "1001",	库存单位编码
		"wunitName": "包",	库存单位名称
		"price": "12.00",	单价
		"punit": "1002",	要货单位编码
		"punitName": "箱",	要货单位名称
		"unitRatio": "24.00",	单位换算率
		"isFeature": "Y",	是否启用特征
		}]
    "illegaldatas": [
			{
			"pluBarcode": "100001",
 	 }]
 }	
	
 */
	
	private List<level1Elm> datas;
	private List<levelBElm> illegaldatas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	public class level1Elm
	{
	/**JSON Response 商品明细
	    "pluBarcode":
		"pluNO": "SP001",	商品编码
		"pluName": "海鲜鱿鱼丝",	商品名称
		"category": "01",	小类编号
		"categoryName": "真皮系列",	小类名称
		"spec": "大包装",	商品规格
		"picture": "",	商品图片
		"wunit": "1001",	库存单位编码
		"wunitName": "包",	库存单位名称
		"price": "12.00",	单价
		"punit": "1002",	要货单位编码
		"punitName": "箱",	要货单位名称
		"unitRatio": "24.00",	单位换算率
		"isFeature": "Y",	是否启用特征
	 */		
		private String pluBarcode;
		private String pluNO;
		private String pluName;	
		private String category;	
		private String categoryName;	
		private String fpso;
		private String spec;
		private String specNanme;
		private String fileName;
		
		private String wunit;		
		private String wunitName;
		private String price;
		
		private String punit;		
		private String punitName;
		private String unitRatio;
		
		//private String pqty;//EXCEL导入进来的盘点数量
		
		public String getPluNO() {
			return pluNO;
		}
		public void setPluNO(String pluNO) {
			this.pluNO = pluNO;
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
		public String getWunit() {
			return wunit;
		}
		public void setWunit(String wunit) {
			this.wunit = wunit;
		}
		public String getWunitName() {
			return wunitName;
		}
		public void setWunitName(String wunitName) {
			this.wunitName = wunitName;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
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
		public String getUnitRatio() {
			return unitRatio;
		}
		public void setUnitRatio(String unitRatio) {
			this.unitRatio = unitRatio;
		}
			
		public String getPluBarcode() {
			return pluBarcode;
		}
		public void setPluBarcode(String pluBarcode) {
			this.pluBarcode = pluBarcode;
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
	public String getFpso() {
	return fpso;
	}
	public void setFpso(String fpso) {
	this.fpso = fpso;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getSpecNanme() {
	return specNanme;
	}
	public void setSpecNanme(String specNanme) {
	this.specNanme = specNanme;
	}
	}

	public class levelBElm{

		private String pluBarcode;

		public String getPluBarcode() {
			return pluBarcode;
		}

		public void setPluBarcode(String pluBarcode) {
			this.pluBarcode = pluBarcode;
		}	
	
	}

	public List<levelBElm> getIllegaldatas() {
		return illegaldatas;
	}
	public void setIllegaldatas(List<levelBElm> illegaldatas) {
		this.illegaldatas = illegaldatas;
	}

	
}


	