package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 商品品牌查询	2018-10-17	
 * @author yuanyy
 *
 */
public class DCP_GoodsBrandQueryRes extends JsonRes {
	private List<level1Elm> datas;
	
	
	public List<level1Elm> getDatas() {
		return datas;
	}


	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}


	public class level1Elm{
		private String brandNo;
		private String brandName;
		private String status;
		private List<level2Elm> brandName_lang;
		
		public String getBrandName() {
			return brandName;
		}
		public void setBrandName(String brandName) {
			this.brandName = brandName;
		}						
		public List<level2Elm> getBrandName_lang() {
		return brandName_lang;
		}
		public void setBrandName_lang(List<level2Elm> brandName_lang) {
			this.brandName_lang = brandName_lang;
		}
		public String getStatus() {
						return status;
			}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getBrandNo() {
			return brandNo;
		}
		public void setBrandNo(String brandNo) {
			this.brandNo = brandNo;
		}
		
	}
	
	public class level2Elm{
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
	
}
