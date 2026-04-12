package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 服务函数：DingFuncGetDCP
 * 服务说明：钉钉功能审批查询
 * @author jinzma
 * @since  2019-10-31
 */
public class DCP_DingFuncQueryRes  extends JsonRes{

	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm
	{
		private String funcNo;
		private String funcName;
		private String templateNo;
		private String templateName;
		private String status;
		private List<level2Elm> datas;

		public String getFuncNo() {
			return funcNo;
		}
		public void setFuncNo(String funcNo) {
			this.funcNo = funcNo;
		}
		public String getTemplateNo() {
			return templateNo;
		}
		public void setTemplateNo(String templateNo) {
			this.templateNo = templateNo;
		}
		public String getFuncName() {
			return funcName;
		}
		public void setFuncName(String funcName) {
			this.funcName = funcName;
		}
		public String getTemplateName() {
			return templateName;
		}
		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public List<level2Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level2Elm> datas) {
			this.datas = datas;
		}

	}
	public class level2Elm
	{
		private String shopId;
		private String shopName;
		private String defUserID;
		private String defUserName;
		private String defDeptID;
		private List<level3Elm> datas;

		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getShopName() {
			return shopName;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}
		public String getDefUserID() {
			return defUserID;
		}
		public void setDefUserID(String defUserID) {
			this.defUserID = defUserID;
		}
		public String getDefUserName() {
			return defUserName;
		}
		public void setDefUserName(String defUserName) {
			this.defUserName = defUserName;
		}
		public String getDefDeptID() {
			return defDeptID;
		}
		public void setDefDeptID(String defDeptID) {
			this.defDeptID = defDeptID;
		}
		public List<level3Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level3Elm> datas) {
			this.datas = datas;
		}

	}
	public class level3Elm
	{
		private String approvedByid;
		private String approvedByName;
		private String approvedByDeptID;

		public String getApprovedByid() {
			return approvedByid;
		}
		public void setApprovedByid(String approvedByid) {
			this.approvedByid = approvedByid;
		}
		public String getApprovedByName() {
			return approvedByName;
		}
		public void setApprovedByName(String approvedByName) {
			this.approvedByName = approvedByName;
		}
		public String getApprovedByDeptID() {
			return approvedByDeptID;
		}
		public void setApprovedByDeptID(String approvedByDeptID) {
			this.approvedByDeptID = approvedByDeptID;
		}
	}

}
