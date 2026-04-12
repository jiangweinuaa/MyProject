package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;


public class DCP_ClassGoodsUpdateReq  extends JsonBasicReq 
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
		private String classType;
		private String classNo;
		private String pluNo;
		//private String displayName;
		private String sortId;

		private String remind; // 是否开启点单提醒Y/N
        private String remindType; // 提醒类型，0.必须 1.提醒
		
		public String getClassType() {
			return classType;
		}
		public void setClassType(String classType) {
			this.classType = classType;
		}
		public String getClassNo() {
			return classNo;
		}
		public void setClassNo(String classNo) {
			this.classNo = classNo;
		}
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getSortId() {
			return sortId;
		}
		public void setSortId(String sortId) {
			this.sortId = sortId;
		}

        public String getRemind() {
            return remind;
        }

        public void setRemind(String remind) {
            this.remind = remind;
        }

        public String getRemindType() {
            return remindType;
        }

        public void setRemindType(String remindType) {
            this.remindType = remindType;
        }
    }

}
