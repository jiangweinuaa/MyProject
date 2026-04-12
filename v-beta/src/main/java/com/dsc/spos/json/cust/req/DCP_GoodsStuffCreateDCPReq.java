package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 新增统一加料信息
 * 2018-09-21	
 * @author yuanyy
 *
 */
public class DCP_GoodsStuffCreateDCPReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String stuffNo;
		private String stuffName; 
		private String priority ;
		private String status;
		private List<level1Elm> datas;

		public List<level1Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}
		public String getStuffNo() {
			return stuffNo;
		}
		public void setStuffNo(String stuffNo) {
			this.stuffNo = stuffNo;
		}
		public String getStuffName() {
			return stuffName;
		}
		public void setStuffName(String stuffName) {
			this.stuffName = stuffName;
		}
		public String getPriority() {
			return priority;
		}
		public void setPriority(String priority) {
			this.priority = priority;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
	}

	public  class level1Elm{
		private String stuffNo;
		private String stuffName;

		public String getStuffNo() {
			return stuffNo;
		}
		public void setStuffNo(String stuffNo) {
			this.stuffNo = stuffNo;
		}
		public String getStuffName() {
			return stuffName;
		}
		public void setStuffName(String stuffName) {
			this.stuffName = stuffName;
		} 

	}

}
