package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;


public class DCP_OrderGoodsDeleteReq extends JsonBasicReq  
{

	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public  class level1Elm
	{
		private String pluNO;
		private String fileName;
		public String getPluNo() {
			return pluNO;
		}
		public void setPluNo(String pluNo) {
			this.pluNO = pluNo;
		}
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

	}




}
