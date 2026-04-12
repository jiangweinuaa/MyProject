package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 客户标签
 * @author yuanyy
 * 
 */
public class DCP_CustomerTagGroupCreateReq extends JsonBasicReq {
//	private String timeStamp;
	private level1Elm request;
//	public String getTimeStamp() {
//		return timeStamp;
//	}
//	public void setTimeStamp(String timeStamp) {
//		this.timeStamp = timeStamp;
//	}
	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm{
		private String tagGroupNo;
		private String tagGroupName;
		private String mutualExclusion;
		private String status;
		private List<level2Elm> children;
		public String getTagGroupNo() {
			return tagGroupNo;
		}
		public void setTagGroupNo(String tagGroupNo) {
			this.tagGroupNo = tagGroupNo;
		}
		public String getTagGroupName() {
			return tagGroupName;
		}
		public void setTagGroupName(String tagGroupName) {
			this.tagGroupName = tagGroupName;
		}
		public String getMutualExclusion() {
			return mutualExclusion;
		}
		public void setMutualExclusion(String mutualExclusion) {
			this.mutualExclusion = mutualExclusion;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public List<level2Elm> getChildren() {
			return children;
		}
		public void setChildren(List<level2Elm> children) {
			this.children = children;
		}

	}

	public class level2Elm{
		private String tagNo;
		private String tagName;
		private String status;
		public String getTagNo() {
			return tagNo;
		}
		public void setTagNo(String tagNo) {
			this.tagNo = tagNo;
		}
		public String getTagName() {
			return tagName;
		}
		public void setTagName(String tagName) {
			this.tagName = tagName;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}

	}



}
