package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;
/**
 * 服務函數：MachineGetDCP
 * 服务说明：机台信息查询
 * @author jzma 
 * @since  2018-11-1
 */
public class DCP_MachineQueryRes  extends JsonRes {

	private List<level1Elm> datas;

	public class level1Elm
	{
		private String shopId;
		private String shopName;
		private String machineId;
		private String machineName;
		private String hardwareInfo;
		private String sNumber;
		private String regflg;
		private String status;
		private String businessType;//点单类型：0-触屏先结1-扫码先结2-触屏后结桌台管理3-触屏先结桌台管理
		private String apiUserCode;//接口帐号编码
		private String appNo;//
		private String appName;//
		private String channelId;
		private String channelName;
		
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
		
		public String getMachineId() {
			return machineId;
		}
		public void setMachineId(String machineId) {
			this.machineId = machineId;
		}
		public String getMachineName() {
			return machineName;
		}
		public void setMachineName(String machineName) {
			this.machineName = machineName;
		}
		public String getHardwareInfo() {
			return hardwareInfo;
		}
		public void setHardwareInfo(String hardwareInfo) {
			this.hardwareInfo = hardwareInfo;
		}
		public String getsNumber() {
			return sNumber;
		}
		public void setsNumber(String sNumber) {
			this.sNumber = sNumber;
		}
		public String getRegflg() {
			return regflg;
		}
		public void setRegflg(String regflg) {
			this.regflg = regflg;
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
		
		public String getAppName()
		{
			return appName;
		}
		public void setAppName(String appName)
		{
			this.appName = appName;
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
		public String getChannelName()
		{
			return channelName;
		}
		public void setChannelName(String channelName)
		{
			this.channelName = channelName;
		}
		
		

		
	}

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

}
