package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

/**
 * 服务函数：DCP_MinQtyTemplateCreate
 * 服务说明：商品起售量模板新增
 * @author wangzyc 
 * @since  2020-11-09
 */
public class DCP_MinQtyTemplateCreateReq extends JsonBasicReq{
	
	private level1Elm request;
	
	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}


	public class level1Elm{
		private String templateId; // 模板编码 必填
		private String templateName; // 模板名称
		private String memo; // 备注
		private String status; // 状态：-1未启用100已启用 0已禁用
		private String restrictShop; // 适用门店：0-所有门店1-指定门店2-排除门店
		private String restrictChannel; //适用渠道：0-所有渠道1-指定渠道2-排除渠道
		private String restrictPeriod; //适用时段：0-所有时段1-指定时段

		private List<level2Elm> shopList; // 适用门店
		private List<level3Elm> pluList; // 商品列表
		
		public String getRestrictShop() {
			return restrictShop;
		}
		public void setRestrictShop(String restrictShop) {
			this.restrictShop = restrictShop;
		}

		public String getRestrictChannel()
		{
			return restrictChannel;
		}

		public void setRestrictChannel(String restrictChannel)
		{
			this.restrictChannel = restrictChannel;
		}

		public String getRestrictPeriod()
		{
			return restrictPeriod;
		}

		public void setRestrictPeriod(String restrictPeriod)
		{
			this.restrictPeriod = restrictPeriod;
		}

		public List<level2Elm> getShopList() {
			return shopList;
		}
		public void setShopList(List<level2Elm> shopList) {
			this.shopList = shopList;
		}
		public String getTemplateId() {
			return templateId;
		}
		public void setTemplateId(String templateId) {
			this.templateId = templateId;
		}
		
		public String getTemplateName() {
			return templateName;
		}
		public void setTemplateName(String templateName) {
			this.templateName = templateName;
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
		public List<level3Elm> getPluList() {
			return pluList;
		}
		public void setPluList(List<level3Elm> pluList) {
			this.pluList = pluList;
		}
		
	}
	
	
	public class level2Elm{
		private String id; // 门店编号
		private String name;// 名称
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	public class level3Elm{
		private String pluNo; // 商品编码
		private String minQty; //起售量
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getMinQty() {
			return minQty;
		}
		public void setMinQty(String minQty) {
			this.minQty = minQty;
		}
		
	}
}
