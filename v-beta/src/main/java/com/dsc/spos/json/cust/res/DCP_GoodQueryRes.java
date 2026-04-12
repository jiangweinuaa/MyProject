package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

/**
 * GoodGet 專用的 response json
 * @author panjing 
 * @since  2016-09-22
 */
public class DCP_GoodQueryRes extends JsonBasicRes{
	/**
	 *{	
		"success": true,	成功否
		"serviceStatus": "000",	服務狀態代碼
		"serviceDescription": "服務執行成功",	服務狀態說明
		"datas": [	
		  {	
			"pluNO": "SP001",	商品编码
			"pluName": "海鲜鱿鱼丝",	商品名称
			"featureNO": "11_12",	商品特征码
			"featureName": "红_S",	商品特征名称
			"spec": "大包装",	商品规格
			"picture": "",	商品图片
			"wunit": "1001",	库存单位编码
			"wunitName": "包",	库存单位名称
			"price": "12.00",	单价
			"punit": "1002",	要货单位编码
			"punitName": "箱",	要货单位名称
			"unitRatio": "24.00",	单位换算率
			"pluBarcode": "1111111"	商品条码
			"isFeature": "Y",	是否启用特征
		    "datas": [	
			  {	
			     "classify": "A",	特征分类
			     "classifyName": "箱",	分类名称
			     "item": "1",	项次
			     "isHorizontal": "Y",	是否二维显示
			     "datas": [	
			     {	
			      "featureNO": "11",	特征值编码
			      "featureName": "红色",	特征值名称
			     }]	
	    	  }
	    	]	
		  }
		]	
	* }	
	**/
	private List<level1Elm> datas;
		
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm{
		/**JSON Response 商品明细
			"pluNO": "SP001",	商品编码
			"pluName": "海鲜鱿鱼丝",	商品名称
			"featureNO": "11_12",	商品特征码
			"featureName": "红_S",	商品特征名称
			"spec": "大包装",	商品规格
			"picture": "",	商品图片
			"wunit": "1001",	库存单位编码
			"wunitName": "包",	库存单位名称
			"price": "12.00",	单价
			"punit": "1002",	要货单位编码
			"punitName": "箱",	要货单位名称
			"unitRatio": "24.00",	单位换算率
			"pluBarcode": "1111111"	商品条码
			"isFeature": "Y",	是否启用特征
		**/
		
		private String pluNO;
		private String pluName;	
		private String featureNO;
		private String featureName;
		private String receiptOrg;	
		private String receiptOrgName;		
		private String spec;
		private String picture;
		private String wunit;
		private String wunitName;		
		private String price;	
		private String punit;
		private String punitName;
		private String unitRatio;		
		private String pluBarcode;
		private String isFeature;
		private String langType;
		private String fpsp;
		private String fpso;
		private List<level2Elm> datas;

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
		
		public String getFeatureNO() {
			return featureNO;
		}
		public void setFeatureNO(String featureNO) {
			this.featureNO = featureNO;
		}

		public String getFeatureName() {
			return featureName;
		}
		public void setFeatureName(String featureName) {
			this.featureName = featureName;
		}

		public String getSpec() {
			return spec;
		}
		public void setSpec(String spec) {
			this.spec = spec;
		}

		public String getPicture() {
			return picture;
		}
		public void setPicture(String picture) {
			this.picture = picture;
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

		public String getIsFeature() {
			return isFeature;
		}
		public void setIsFeature(String isFeature) {
			this.isFeature = isFeature;
		}
		
		public String getLangType() {
			return langType;
		}
		public void setLangType(String langType) {
			this.langType = langType;
		}
		public String getFpso() {
			return fpso;
		}
		public void setFpso(String fpso) {
			this.fpso = fpso;
		}	
		public String getFpsp() {
			return fpsp;
		}
		public void setFpsp(String fpsp) {
			this.fpsp = fpsp;
		}

		public List<level2Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level2Elm> datas) {
			this.datas = datas;
		}
	}	
	
	public class level2Elm{
		/**JSON Response 特征分类
	   	   "classify": "A",	特征分类
           "classifyName": "箱",	分类名称
           "item": "1",	项次
           "isHorizontal": "Y",	是否二维显示
		 */
		private String classify;			
		private String classifyName;			
		private String item;			
		private String isHorizontal;
		private String featureNO;
		
		private List<level3Elm> datas;
		
		public String getClassify() {
			return classify;
		}
		public void setClassify(String classify) {
			this.classify = classify;
		}
		
		public String getClassifyName() {
			return classifyName;
		}
		public void setClassifyName(String classifyName) {
			this.classifyName = classifyName;
		}
		
		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		
		public String getIsHorizontal() {
			return isHorizontal;
		}
		public void setIsHorizontal(String isHorizontal) {
			this.isHorizontal = isHorizontal;
		}
		
		public String getFeatureNO() {
			return featureNO;
		}
		public void setFeatureNO(String featureNO) {
			this.featureNO = featureNO;
		}
		
		public List<level3Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level3Elm> datas) {
			this.datas = datas;
		}
	}
	
	public class level3Elm{
		/**JSON Response 特征值
            "featureNO": "11",	特征值编码
            "featureName": "红色",	特征值名称
		 */
		private String featureNO;	
		private String featureName;
		
		public String getFeatureNO() {
			return featureNO;
		}
		public void setFeatureNO(String featureNO) {
			this.featureNO = featureNO;
		}
		
		public String getFeatureName() {
			return featureName;
		}
		public void setFeatureName(String featureName) {
			this.featureName = featureName;
		}
	}
}
