package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.req.DCP_AttrGroupCreateReq.levelRequest;

public class DCP_DepartmentCreateReq extends JsonBasicReq
{
private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String departNo;
		private String departName;
		private String memo;
		private String status;
		private String upDepartNo;
		private List<level2Elm> depart_Lang;
		
		public String getDepartNo() {
			return departNo;
		}
		public void setDepartNo(String departNo) {
			this.departNo = departNo;
		}
		public String getDepartName() {
			return departName;
		}
		public void setDepartName(String departName) {
			this.departName = departName;
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
		public String getUpDepartNo() {
			return upDepartNo;
		}
		public void setUpDepartNo(String upDepartNo) {
			this.upDepartNo = upDepartNo;
		}
		public List<level2Elm> getDepart_Lang() {
			return depart_Lang;
		}
		public void setDepart_Lang(List<level2Elm> depart_Lang) {
			this.depart_Lang = depart_Lang;
		}
		
	}

	public  class level2Elm
	{
		private String lang_Type;
		private String departName;
		public String getLang_Type() {
			return lang_Type;
		}
		public void setLang_Type(String lang_Type) {
			this.lang_Type = lang_Type;
		}
		public String getDepartName() {
			return departName;
		}
		public void setDepartName(String departName) {
			this.departName = departName;
		}
	}


}
