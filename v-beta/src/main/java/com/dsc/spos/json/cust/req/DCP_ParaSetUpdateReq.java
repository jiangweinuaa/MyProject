package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.req.DCP_ParaTemplateDeleteReq.levelReq;

/**
 * 服務函數：ParaDefineUpdate
 *    說明：参数定义修改
 * 服务说明：参数定义修改
 * @author jzma 
 * @since  2017-03-03
 */
public class DCP_ParaSetUpdateReq extends JsonBasicReq {
	/*
"serviceId": "ParaSetUpdateDCP",	必传且非空，服务名				
"token": "f14ee75ff5b220177ac0dc538bdea08c",	必传且非空，访问令牌				
"item": "IS_RECE",	必传且非空，参数编码				
"paraType": "1"	必传且非空，参数类型	1.中台参数 2.门店参数 3.POS参数 4.机台参数			
"itemValue","1"	必传且非空，参数值				
"paraShop": "",	可选，门店				
"paraMachine": "",	可选，机台				
	 */
	private levelReq request;

	public levelReq getRequest() {
		return request;
	}

	public void setRequest(levelReq request) {
		this.request = request;
	}
	
	public class levelReq{

		private String item;
		private String paraType;
		private String itemValue;
		private String paraShop;
		private String paraMachine;
		
		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public String getParaType() {
			return paraType;
		}
		public void setParaType(String paraType) {
			this.paraType = paraType;
		}
		public String getItemValue() {
			return itemValue;
		}
		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
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

	}
	






}
