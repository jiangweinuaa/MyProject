package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;


/**
 * 服务函数：DCP_MinQtyTemplateQuery 
 * 服务说明：商品起售量模板查询
 * @author wangzyc
 * @since 2020-11-10
 */
public class DCP_MinQtyTemplateQueryRes extends JsonRes {

	private List<level1Elm> datas;

	public class level1Elm {
		private String templateId; // 模板编码
		private String templateName; // 模板名称
		private String memo; // 备注
		private String status; // 状态：-1未启用100已启用 0已禁用
		private String restrictShop; // 适用门店：0-所有门店1-指定门店
		private List<level2Elm> shopList; // 适用门店
		private List<level3Elm> pluList; // 商品列表
		private String createtime; // 创建时间yyyy-MM-dd HH:mm:ss，降序第一列
		private String createopid; // 创建人编号
		private String createopname; // 创建人编号
		private String updateTime; // 最后修改时间yyyyMMddHH:mm:ss
		private String lastmodiopid; // 最后修改人编号
		private String lastmodiname; // 最后修改人名称
		
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

		public List<level2Elm> getShopList() {
			return shopList;
		}

		public void setShopList(List<level2Elm> shopList) {
			this.shopList = shopList;
		}

		public String getRestrictShop() {
			return restrictShop;
		}

		public void setRestrictShop(String restrictShop) {
			this.restrictShop = restrictShop;
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

		public String getCreatetime() {
			return createtime;
		}

		public void setCreatetime(String createtime) {
			this.createtime = createtime;
		}

		public String getCreateopid() {
			return createopid;
		}

		public void setCreateopid(String createopid) {
			this.createopid = createopid;
		}

		public String getCreateopname() {
			return createopname;
		}

		public void setCreateopname(String createopname) {
			this.createopname = createopname;
		}

		public String getUpdateTime() {
			return updateTime;
		}

		public void setUpdateTime(String updateTime) {
			this.updateTime = updateTime;
		}

		public String getLastmodiopid() {
			return lastmodiopid;
		}

		public void setLastmodiopid(String lastmodiopid) {
			this.lastmodiopid = lastmodiopid;
		}

		public String getLastmodiname() {
			return lastmodiname;
		}

		public void setLastmodiname(String lastmodiname) {
			this.lastmodiname = lastmodiname;
		}
	}

	public class level2Elm {
		private String id; // 编号
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
		@Override
		public boolean equals(Object obj) {
			level2Elm level2Elm = (level2Elm) obj;
			return id.equals(level2Elm.id);
		}
	 
		@Override
		public int hashCode() {
			String in = id;
			return in.hashCode();
		}
	}

	public class level3Elm {
		private String pluNo; // 商品编码
		private String pluName;// 商品名称
		private String punitName; // 单位名称
		private String price; // 零售价
		private String minQty; // 起售量
		
		
		public String getPrice() {
			return price;
		}

		public void setPrice(String price) {
			this.price = price;
		}

		public String getPluName() {
			return pluName;
		}

		public void setPluName(String pluName) {
			this.pluName = pluName;
		}

		public String getPunitName() {
			return punitName;
		}

		public void setPunitName(String punitName) {
			this.punitName = punitName;
		}

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
		
		@Override
		public boolean equals(Object obj) {
			level3Elm level3Elm = (level3Elm) obj;
			return pluNo.equals(level3Elm.pluNo);
		}
	 
		@Override
		public int hashCode() {
			String in = pluNo;
			return in.hashCode();
		}

	}

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

}
