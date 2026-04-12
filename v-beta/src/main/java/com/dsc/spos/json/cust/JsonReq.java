package com.dsc.spos.json.cust;

import java.beans.Transient;
import java.util.Map;

import com.dsc.spos.json.JsonBasicReq;

public class JsonReq extends JsonBasicReq {

    private String checkDate;    //对账日期，可为空
    private String inputDate;    //录入日期，可为空
    private String confirmDate;  //审核日期，可为空
    private String confirmCode;  //审核状态，可为空

	public String getCheckDate() {
		return checkDate;
	}
	public void setCheckDate(String checkDate) {
		this.checkDate = checkDate;
	}
	public String getInputDate() {
		return inputDate;
	}
	public void setInputDate(String inputDate) {
		this.inputDate = inputDate;
	}
	public String getConfirmDate() {
		return confirmDate;
	}
	public void setConfirmDate(String confirmDate) {
		this.confirmDate = confirmDate;
	}
	public String getConfirmCode() {
		return confirmCode;
	}
	public void setConfirmCode(String confirmCode) {
		this.confirmCode = confirmCode;
	}

}
