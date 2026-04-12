package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 修改商品品牌 2018-10-18
 * 
 * @author yuanyy
 *
 */
public class DCP_GoodsBrandUpdateReq extends JsonBasicReq {

private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String brandNo;
		private String status;

		private List<level1Elm> brandName_lang;

		public String getBrandNo() {
			return brandNo;
		}
	
		public void setBrandNo(String brandNo) {
			this.brandNo = brandNo;
		}
	
		public String getStatus() {
			return status;
		}
	
		public void setStatus(String status) {
			this.status = status;
		}

		public List<level1Elm> getBrandName_lang() {
			return brandName_lang;
		}
	
		public void setBrandName_lang(List<level1Elm> brandName_lang) {
			this.brandName_lang = brandName_lang;
		}

			
	
			

		
		
	}
	

	public class level1Elm {
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
