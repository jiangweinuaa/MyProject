package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_KeyQueryRes extends JsonRes{

	private List<level1Elm> datas;

	public List<level1Elm> getdatas()
	{
		return datas;
	}
	public void setdatas(List<level1Elm> datas)
	{
		this.datas=datas;
	}

	public class level1Elm 
	{
		private String keyID;
		private String shopId;
		private String shopName;
		private String kbType;
		private String kbName;
		private String status;
		public String getkeyID()
		{
			return keyID;
		}
		public void setkeyID(String keyID)
		{
			this.keyID=keyID;
		}
		public String getKeyID() {
			return keyID;
		}
		public void setKeyID(String keyID) {
			this.keyID = keyID;
		}
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getShopName() {
			return shopName;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}
		public String getKbType() {
			return kbType;
		}
		public void setKbType(String kbType) {
			this.kbType = kbType;
		}
		public String getKbName() {
			return kbName;
		}
		public void setKbName(String kbName) {
			this.kbName = kbName;
		}
		public String getshopName()
		{
			return shopName;
		}
		public void setshopName(String shopName)
		{
			this.shopName=shopName;
		}
		public String getkbType()
		{
			return kbType;
		}
		public void setkbType(String kbType)
		{
			this.kbType=kbType;
		}
		public String getkbName()
		{
			return kbName;
		}
		public void setkbName(String kbName)
		{
			this.kbName=kbName;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		
	}

}
