package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_ParaSetQueryReq extends JsonBasicReq {
	private String paraShop;
	private String paraMachine;
	private String paraType;

	private String keyTxt;
	private String modularId;
	
	public String getParaType() {
		return paraType;
	}

	public void setParaType(String paraType) {
		this.paraType = paraType;
	}

	public String getParaShop() {
		return paraShop;
	}

	public void setParaShop(String paraShop) {
		this.paraShop = paraShop;
	}

	public String getParaMachine() {
		return paraMachine;
	}

	public void setParaMachine(String paraMachine) {
		this.paraMachine = paraMachine;
	}

	public String getKeyTxt() {
		return keyTxt;
	}

	public void setKeyTxt(String keyTxt) {
		this.keyTxt = keyTxt;
	}

	public String getModularId() {
		return modularId;
	}

	public void setModularId(String modularId) {
		this.modularId = modularId;
	}

}
