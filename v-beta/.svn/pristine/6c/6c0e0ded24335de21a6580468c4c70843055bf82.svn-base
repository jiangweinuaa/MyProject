package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * V3参数定义批次更新 
 * @author 2020-06-02
 *
 */
public class DCP_ParaDefineBatchUpdateReq extends JsonBasicReq {
	private levelReq request;
	
	public levelReq getRequest() {
		return request;
	}

	public void setRequest(levelReq request) {
		this.request = request;
	}

	public class levelReq{
 		private String modularId;
 		private String modularName;
 		
 		private List<ParaList> itemList;
 		
 		//2020-07-14 增加 paraType， classNO 
 		private String paraType;
 		private String classNO;
 		
		public String getParaType() {
			return paraType;
		}

		public String getClassNO() {
			return classNO;
		}

		public void setParaType(String paraType) {
			this.paraType = paraType;
		}

		public void setClassNO(String classNO) {
			this.classNO = classNO;
		}

		public String getModularId() {
			return modularId;
		}

		public String getModularName() {
			return modularName;
		}

		public List<ParaList> getItemList() {
			return itemList;
		}

		public void setModularId(String modularId) {
			this.modularId = modularId;
		}

		public void setModularName(String modularName) {
			this.modularName = modularName;
		}

		public void setItemList(List<ParaList> itemList) {
			this.itemList = itemList;
		}

 	}
	
	public class ParaList{
		private String item;

		public String getItem() {
			return item;
		}

		public void setItem(String item) {
			this.item = item;
		}
		
	}
	
}
