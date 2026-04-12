package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;
/**
 * 服務函數：OrderCategoryUpdateDCP
 * 服务说明：外卖分类修改
 * @author jinzma 
 * @since  2019-03-11
 */
public class DCP_OrderCategoryUpdateReq extends JsonBasicReq {

	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public  class level1Elm
	{
		private String categoryNO;
		private String categoryName;
		private String priority ;
		public String getCategoryNO() {
			return categoryNO;
		}
		public void setCategoryNO(String categoryNO) {
			this.categoryNO = categoryNO;
		}
		public String getCategoryName() {
			return categoryName;
		}
		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}
		public String getPriority() {
			return priority;
		}
		public void setPriority(String priority) {
			this.priority = priority;
		}
	}




}
