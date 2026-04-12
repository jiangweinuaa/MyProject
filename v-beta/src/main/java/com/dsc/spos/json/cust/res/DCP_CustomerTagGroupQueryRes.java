package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 客户标签
 * @author yuanyy
 *
 */
public class DCP_CustomerTagGroupQueryRes extends JsonRes {
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
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
