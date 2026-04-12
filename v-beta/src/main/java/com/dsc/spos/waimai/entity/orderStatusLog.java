package com.dsc.spos.waimai.entity;

public class orderStatusLog
{
	private String eId;
	private String shopNo;
	private String shopName;
	private String opNo; //用户编码
    private String opName; //用户名称
	private String orderNo;	
	private String loadDocType;
	private String channelId;
	private String loadDocBillType;//渠道单据类型
	private String loadDocOrderNo;
	private String statusType;//状态类型
	private String statusTypeName;//状态名称
	private String status;
	private String statusName;
	private String update_time;//操作时间
	private String need_notify;//是否通知云pos,N-不需要调用，Y-需要
	private String notify_status;//通知云pos状态返回，0-未通知，1-已通知
	private String need_callback;//是否调用第三方接口，N-不需要调用，Y-需要
	private String callback_status;//调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败		
	private String memo;
	private String machShopNo;
	private String machShopName;
	private String shippingShopNo;
	private String shippingShopName;
	private String display;//1:对外给买家看的 否则写0
	public String geteId()
	{
		return eId;
	}
	public void seteId(String eId)
	{
		this.eId = eId;
	}
	public String getShopNo()
	{
		return shopNo;
	}
	public void setShopNo(String shopNo)
	{
		this.shopNo = shopNo;
	}
	public String getShopName()
	{
		return shopName;
	}
	public void setShopName(String shopName)
	{
		this.shopName = shopName;
	}	
	public String getOpNo()
	{
		return opNo;
	}
	public void setOpNo(String opNo)
	{
		this.opNo = opNo;
	}
	public String getOpName()
	{
		return opName;
	}
	public void setOpName(String opName)
	{
		this.opName = opName;
	}	
	public String getOrderNo()
	{
		return orderNo;
	}
	public void setOrderNo(String orderNo)
	{
		this.orderNo = orderNo;
	}
	public String getLoadDocOrderNo()
	{
		return loadDocOrderNo;
	}
	public void setLoadDocOrderNo(String loadDocOrderNo)
	{
		this.loadDocOrderNo = loadDocOrderNo;
	}
	public String getLoadDocType()
	{
		return loadDocType;
	}
	public void setLoadDocType(String loadDocType)
	{
		this.loadDocType = loadDocType;
	}
	public String getChannelId()
	{
		return channelId;
	}
	public void setChannelId(String channelId)
	{
		this.channelId = channelId;
	}
	public String getStatusType()
	{
		return statusType;
	}
	public void setStatusType(String statusType)
	{
		this.statusType = statusType;
	}
	public String getStatusTypeName()
	{
		return statusTypeName;
	}
	public void setStatusTypeName(String statusTypeName)
	{
		this.statusTypeName = statusTypeName;
	}
	public String getStatus()
	{
		return status;
	}
	public void setStatus(String status)
	{
		this.status = status;
	}
	public String getStatusName()
	{
		return statusName;
	}
	public void setStatusName(String statusName)
	{
		this.statusName = statusName;
	}
	public String getUpdate_time()
	{
		return update_time;
	}
	public void setUpdate_time(String update_time)
	{
		this.update_time = update_time;
	}
	public String getNeed_notify()
	{
		return need_notify;
	}
	public void setNeed_notify(String need_notify)
	{
		this.need_notify = need_notify;
	}
	public String getNotify_status()
	{
		return notify_status;
	}
	public void setNotify_status(String notify_status)
	{
		this.notify_status = notify_status;
	}
	public String getNeed_callback()
	{
		return need_callback;
	}
	public void setNeed_callback(String need_callback)
	{
		this.need_callback = need_callback;
	}
	public String getCallback_status()
	{
		return callback_status;
	}
	public void setCallback_status(String callback_status)
	{
		this.callback_status = callback_status;
	}
	public String getMemo()
	{
		return memo;
	}
	public void setMemo(String memo)
	{
		this.memo = memo;
	}
	public String getMachShopNo()
	{
		return machShopNo;
	}
	public void setMachShopNo(String machShopNo)
	{
		this.machShopNo = machShopNo;
	}
	public String getMachShopName()
	{
		return machShopName;
	}
	public void setMachShopName(String machShopName)
	{
		this.machShopName = machShopName;
	}
	public String getShippingShopNo()
	{
		return shippingShopNo;
	}
	public void setShippingShopNo(String shippingShopNo)
	{
		this.shippingShopNo = shippingShopNo;
	}
	public String getShippingShopName()
	{
		return shippingShopName;
	}
	public void setShippingShopName(String shippingShopName)
	{
		this.shippingShopName = shippingShopName;
	}
	public String getDisplay()
	{
		return display;
	}
	public void setDisplay(String display)
	{
		this.display = display;
	}
	public String getLoadDocBillType()
	{
		return loadDocBillType;
	}
	public void setLoadDocBillType(String loadDocBillType)
	{
		this.loadDocBillType = loadDocBillType;
	}
	


}
