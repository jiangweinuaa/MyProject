package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 删除商品品牌 2018-10-19 
 * @author yuanyy
 *
 */
public class DCP_GoodsBrandDeleteReq extends JsonBasicReq{
	
private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		
	 private	List<level1Elm> brandNoList;

	 public List<level1Elm> getBrandNoList() {
		return brandNoList;
	}

	public void setBrandNoList(List<level1Elm> brandNoList) {
		this.brandNoList = brandNoList;
	}
	 
		
	}
	
	public class level1Elm
	{
		private String brandNo;

		public String getBrandNo() {
			return brandNo;
		}
	
		public void setBrandNo(String brandNo) {
			this.brandNo = brandNo;
		}
		
	}
	
	
}
