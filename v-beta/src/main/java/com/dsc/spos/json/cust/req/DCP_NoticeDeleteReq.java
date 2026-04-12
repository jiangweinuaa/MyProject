package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_NoticeDeleteReq extends JsonBasicReq
{
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String noticeID;

		public String getNoticeID() {
			return noticeID;
		}

		public void setNoticeID(String noticeID) {
			this.noticeID = noticeID;
		}
	}
}
