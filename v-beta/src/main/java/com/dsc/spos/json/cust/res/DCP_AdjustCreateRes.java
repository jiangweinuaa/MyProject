package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

/**
 * AdjustCreate库存调整新增作业
 * @author kangzc
 */
public class DCP_AdjustCreateRes extends JsonBasicRes
{
  /**JSON RESPONSE
   * 
   * 
   */
	
	private String doc_no;
	private String org_no;
	/**
	 * @return the doc_no
	 */
	public String getDoc_no() {
		return doc_no;
	}
	/**
	 * @param doc_no the doc_no to set
	 */
	public void setDoc_no(String doc_no) {
		this.doc_no = doc_no;
	}
	/**
	 * @return the org_no
	 */
	public String getOrg_no() {
		return org_no;
	}
	/**
	 * @param org_no the org_no to set
	 */
	public void setOrg_no(String org_no) {
		this.org_no = org_no;
	}	
	
}
