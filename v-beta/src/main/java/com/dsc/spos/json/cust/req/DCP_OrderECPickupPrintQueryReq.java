package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 电商订单拣货查询
 * @author yuanyy 2019-04-23
 *
 */
public class DCP_OrderECPickupPrintQueryReq extends JsonBasicReq {
	
    private String startDate;
    private String endDate;
    private String[] ecPlatformNo;
    private String printStatus; // 列印状态    1:未列印 2:已列印   空是全部
    private String pickupWay; // 取货方式         1:超取 2:宅配    空是全部
    
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	
	
	public String[] getEcPlatformNo() {
		return ecPlatformNo;
	}
	public void setEcPlatformNo(String[] ecPlatformNo) {
		this.ecPlatformNo = ecPlatformNo;
	}
	public String getPrintStatus() {
		return printStatus;
	}
	public void setPrintStatus(String printStatus) {
		this.printStatus = printStatus;
	}
	public String getPickupWay() {
		return pickupWay;
	}
	public void setPickupWay(String pickupWay) {
		this.pickupWay = pickupWay;
	}
    
    

}
