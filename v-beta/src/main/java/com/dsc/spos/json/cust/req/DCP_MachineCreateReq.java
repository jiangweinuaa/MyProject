package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
/**
 * 服務函數：MachineCreateDCP
 * 服务说明：机台信息创建
 * @author jzma 
 * @since  2018-11-01
 */
public class DCP_MachineCreateReq  extends JsonBasicReq {

	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{


		private String shopId;
		private String machineId;
		private String machineName;
		private String status;
		private String businessType;//点单类型：0-触屏先结1-扫码先结2-触屏后结桌台管理3-触屏先结桌台管理
		private String apiUserCode;//接口帐号编码
		private String appNo;//
		private String channelId;
		
		public String getMachineId() {
			return machineId;
		}
		public void setMachineId(String machineId) {
			this.machineId = machineId;
		}
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getMachineName() {
			return machineName;
		}
		public void setMachineName(String machineName) {
			this.machineName = machineName;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getBusinessType()
		{
			return businessType;
		}
		public void setBusinessType(String businessType)
		{
			this.businessType = businessType;
		}
		public String getApiUserCode()
		{
			return apiUserCode;
		}
		public void setApiUserCode(String apiUserCode)
		{
			this.apiUserCode = apiUserCode;
		}
		public String getAppNo()
		{
			return appNo;
		}
		public void setAppNo(String appNo)
		{
			this.appNo = appNo;
		}
		public String getChannelId()
		{
			return channelId;
		}
		public void setChannelId(String channelId)
		{
			this.channelId = channelId;
		}
		
		
		
	}
}
