package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：DingFuncUpdateDCP 服务说明：钉钉功能审批修改
 * 
 * @author jinzma
 * @since 2019-10-31
 */
public class DCP_DingFuncUpdateReq extends JsonBasicReq {

	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String funcNo;
		private String templateNo;
		private String status;
		private List<level1Elm> datas;

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

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public List<level1Elm> getDatas() {
			return datas;
		}

		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}
	}
	public class level1Elm {
		private String shopId;
		private String defUserID;
		private String defDeptID;
		private List<level2Elm> datas;

		public String getShopId() {
			return shopId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}

		public String getDefUserID() {
			return defUserID;
		}

		public void setDefUserID(String defUserID) {
			this.defUserID = defUserID;
		}

		public String getDefDeptID() {
			return defDeptID;
		}

		public void setDefDeptID(String defDeptID) {
			this.defDeptID = defDeptID;
		}

		public List<level2Elm> getDatas() {
			return datas;
		}

		public void setDatas(List<level2Elm> datas) {
			this.datas = datas;
		}

	}

	public class level2Elm {
		private String approvedByid;
		private String approvedByDeptID;

		public String getApprovedByid() {
			return approvedByid;
		}

		public void setApprovedByid(String approvedByid) {
			this.approvedByid = approvedByid;
		}

		public String getApprovedByDeptID() {
			return approvedByDeptID;
		}

		public void setApprovedByDeptID(String approvedByDeptID) {
			this.approvedByDeptID = approvedByDeptID;
		}

	}

}
