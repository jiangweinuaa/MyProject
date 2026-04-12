package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 参数分类新增
 * @author yuanyy 2020-05-29
 *
 */
public class DCP_ParaClassCreateReq extends JsonBasicReq {

	private level1Elm request;

	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm{
		
		private String classNo;
		private String className;
		private String className_TW;
		private String className_EN;
		
		public String getClassNo() {
			return classNo;
		}
		public String getClassName() {
			return className;
		}
		public void setClassNo(String classNo) {
			this.classNo = classNo;
		}
		public void setClassName(String className) {
			this.className = className;
		}
		public String getClassName_TW() {
			return className_TW;
		}
		public String getClassName_EN() {
			return className_EN;
		}
		public void setClassName_TW(String className_TW) {
			this.className_TW = className_TW;
		}
		public void setClassName_EN(String className_EN) {
			this.className_EN = className_EN;
		}
		
		
	} 
	
	
	
}
