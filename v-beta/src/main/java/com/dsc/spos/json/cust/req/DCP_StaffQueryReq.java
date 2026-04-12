package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

/**
 * 用户信息查询 2018-09-26
 * @author yuanyy
 *
 */
public class DCP_StaffQueryReq extends JsonBasicReq {
		
	private levelRequest request;

	public levelRequest getRequest()
	{
		return request;
	}

	public void setRequest(levelRequest request)
	{
		this.request = request;
	}

	public class levelRequest
	{
		private String status; 
		private String keyTxt;
		private String opGroup;
		private String org;
		private String getType;


		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		public String getOpGroup() {
			return opGroup;
		}
		public void setOpGroup(String opGroup) {
			this.opGroup = opGroup;
		}
		public String getOrg() {
			return org;
		}
		public void setOrg(String org) {
			this.org = org;
		}
		public String getGetType() {
			return getType;
		}
		public void setGetType(String getType) {
			this.getType = getType;
		}

    }

    public class levelOrgElm
    {
        private String org;

        public String getOrg()
        {
            return org;
        }

        public void setOrg(String org)
        {
            this.org = org;
        }
    }

}
