package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_TagGroupQueryReq extends JsonBasicReq
{
	
	private level1Elm request;
	
	
	
	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}



	public class level1Elm
	{
		private String keyTxt;
		private String tagGroupType;

		public String getKeyTxt() {
		return keyTxt;
		}

		public void setKeyTxt(String keyTxt) {
		this.keyTxt = keyTxt;
		}

		public String getTagGroupType() {
			return tagGroupType;
		}

		public void setTagGroupType(String tagGroupType) {
			this.tagGroupType = tagGroupType;
		}
	}
}

