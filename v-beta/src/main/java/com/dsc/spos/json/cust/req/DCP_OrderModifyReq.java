package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderModifyReq extends JsonBasicReq
{

	private levelRequest request;

	public levelRequest getRequest()
	{
		return request;
	}

	public void setRequest(levelRequest request)
	{
		this.request = request;
	}

	public class levelRequest
	{
		private String eId;
		private String orderNo;
		private String loadDocType;
		private String shippingShopNo;
		private String shippingShopName;
		private String machShopNo;
		private String machShopName;
		private String address;
		private String shipDate;//配送日期（格式 yyyyMMdd）
		private String shipStartTime;//格式HHmmss
		private String shipEndTime;//格式HHmmss
		private String shipType;//1.外卖平台配送 2.配送 3.顾客自提 5总部配送
		private String getMan;
		private String getManTel;
		private String longitude;//配送地址经度(高德系坐标)
		private String latitude;//配送地址纬度(高德系坐标)
		private String proMemo;
		private String delMemo;
		private String opId;
		private String opName;
		private String memo;
		private String isDelete;
		private String province;
		private String city;
		private String county;
		private String street;
		private String contMan;
		private String contTel;
		private String deliveryBusinessType;
		private String isUrgentOrder;
		private String deliveryType;
		private String delId;
		private String delName;
		private String delTelephone;
		private String packerId;
		private String packerName;
		private String packerTelephone;
		private String canModify;
		private String shopId;
		private String deliveryMoney;   //配送费
		private String superZoneMoney;  //超区费
		private String urgentMoney;     //加急费
		private String isHaveCard;      //是否含贺卡  Y/N
		private String isCardPrint;     //贺卡是否已打印  Y/N
		private String lineNo;          //路线编码
		private String lineName;        //路线名称
		private List<level1ELM> goodsList;


		public String geteId()
		{
			return eId;
		}
		public void seteId(String eId)
		{
			this.eId = eId;
		}
		public String getOrderNo()
		{
			return orderNo;
		}
		public void setOrderNo(String orderNo)
		{
			this.orderNo = orderNo;
		}
		public String getLoadDocType()
		{
			return loadDocType;
		}
		public void setLoadDocType(String loadDocType)
		{
			this.loadDocType = loadDocType;
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
		public String getAddress()
		{
			return address;
		}
		public void setAddress(String address)
		{
			this.address = address;
		}
		public String getShipDate()
		{
			return shipDate;
		}
		public void setShipDate(String shipDate)
		{
			this.shipDate = shipDate;
		}
		public String getShipStartTime()
		{
			return shipStartTime;
		}
		public void setShipStartTime(String shipStartTime)
		{
			this.shipStartTime = shipStartTime;
		}
		public String getShipEndTime()
		{
			return shipEndTime;
		}
		public void setShipEndTime(String shipEndTime)
		{
			this.shipEndTime = shipEndTime;
		}
		public String getShipType()
		{
			return shipType;
		}
		public void setShipType(String shipType)
		{
			this.shipType = shipType;
		}
		public String getGetMan()
		{
			return getMan;
		}
		public void setGetMan(String getMan)
		{
			this.getMan = getMan;
		}
		public String getGetManTel()
		{
			return getManTel;
		}
		public void setGetManTel(String getManTel)
		{
			this.getManTel = getManTel;
		}
		public String getLongitude()
		{
			return longitude;
		}
		public void setLongitude(String longitude)
		{
			this.longitude = longitude;
		}
		public String getLatitude()
		{
			return latitude;
		}
		public void setLatitude(String latitude)
		{
			this.latitude = latitude;
		}
		public String getProMemo()
		{
			return proMemo;
		}
		public void setProMemo(String proMemo)
		{
			this.proMemo = proMemo;
		}
		public String getDelMemo()
		{
			return delMemo;
		}
		public void setDelMemo(String delMemo)
		{
			this.delMemo = delMemo;
		}
		public String getOpId()
		{
			return opId;
		}
		public void setOpId(String opId)
		{
			this.opId = opId;
		}
		public String getOpName()
		{
			return opName;
		}
		public void setOpName(String opName)
		{
			this.opName = opName;
		}
		public String getMemo()
		{
			return memo;
		}
		public void setMemo(String memo)
		{
			this.memo = memo;
		}
		public String getIsDelete()
		{
			return isDelete;
		}
		public String getProvince()
		{
			return province;
		}
		public void setProvince(String province)
		{
			this.province = province;
		}
		public String getCity()
		{
			return city;
		}
		public void setCity(String city)
		{
			this.city = city;
		}
		public String getCounty()
		{
			return county;
		}
		public void setCounty(String county)
		{
			this.county = county;
		}
		public String getStreet()
		{
			return street;
		}
		public void setStreet(String street)
		{
			this.street = street;
		}
		public String getContMan()
		{
			return contMan;
		}
		public void setContMan(String contMan)
		{
			this.contMan = contMan;
		}
		public String getContTel()
		{
			return contTel;
		}
		public void setContTel(String contTel)
		{
			this.contTel = contTel;
		}
		public String getDeliveryBusinessType()
		{
			return deliveryBusinessType;
		}
		public void setDeliveryBusinessType(String deliveryBusinessType)
		{
			this.deliveryBusinessType = deliveryBusinessType;
		}
		public String getIsUrgentOrder()
		{
			return isUrgentOrder;
		}
		public void setIsUrgentOrder(String isUrgentOrder)
		{
			this.isUrgentOrder = isUrgentOrder;
		}
		public String getDeliveryType()
		{
			return deliveryType;
		}
		public void setDeliveryType(String deliveryType)
		{
			this.deliveryType = deliveryType;
		}
		public void setIsDelete(String isDelete)
		{
			this.isDelete = isDelete;
		}
		public List<level1ELM> getGoodsList()
		{
			return goodsList;
		}
		public void setGoodsList(List<level1ELM> goodsList)
		{
			this.goodsList = goodsList;
		}
		public String getDelId() {
			return delId;
		}
		public void setDelId(String delId) {
			this.delId = delId;
		}
		public String getDelName() {
			return delName;
		}
		public void setDelName(String delName) {
			this.delName = delName;
		}
		public String getDelTelephone() {
			return delTelephone;
		}
		public void setDelTelephone(String delTelephone) {
			this.delTelephone = delTelephone;
		}
		public String getPackerId() {
			return packerId;
		}
		public void setPackerId(String packerId) {
			this.packerId = packerId;
		}
		public String getPackerName() {
			return packerName;
		}
		public void setPackerName(String packerName) {
			this.packerName = packerName;
		}
		public String getPackerTelephone() {
			return packerTelephone;
		}
		public void setPackerTelephone(String packerTelephone) {
			this.packerTelephone = packerTelephone;
		}
		public String getCanModify() {
			return canModify;
		}
		public void setCanModify(String canModify) {
			this.canModify = canModify;
		}
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getDeliveryMoney() {
			return deliveryMoney;
		}
		public void setDeliveryMoney(String deliveryMoney) {
			this.deliveryMoney = deliveryMoney;
		}
		public String getSuperZoneMoney() {
			return superZoneMoney;
		}
		public void setSuperZoneMoney(String superZoneMoney) {
			this.superZoneMoney = superZoneMoney;
		}
		public String getUrgentMoney() {
			return urgentMoney;
		}
		public void setUrgentMoney(String urgentMoney) {
			this.urgentMoney = urgentMoney;
		}
		public String getIsHaveCard() {
			return isHaveCard;
		}
		public void setIsHaveCard(String isHaveCard) {
			this.isHaveCard = isHaveCard;
		}
		public String getIsCardPrint() {
			return isCardPrint;
		}
		public void setIsCardPrint(String isCardPrint) {
			this.isCardPrint = isCardPrint;
		}
		public String getLineNo() {
			return lineNo;
		}
		public void setLineNo(String lineNo) {
			this.lineNo = lineNo;
		}
		public String getLineName() {
			return lineName;
		}
		public void setLineName(String lineName) {
			this.lineName = lineName;
		}
	}

	public class level1ELM
	{
		private String item;
		private List<level2ELM> messages;
		public String getItem()
		{
			return item;
		}
		public void setItem(String item)
		{
			this.item = item;
		}
		public List<level2ELM> getMessages()
		{
			return messages;
		}
		public void setMessages(List<level2ELM> messages)
		{
			this.messages = messages;
		}

	}

	public class level2ELM
	{
		private String msgType;
		private String msgName;
		private String message;
		public String getMsgType()
		{
			return msgType;
		}
		public void setMsgType(String msgType)
		{
			this.msgType = msgType;
		}
		public String getMsgName()
		{
			return msgName;
		}
		public void setMsgName(String msgName)
		{
			this.msgName = msgName;
		}
		public String getMessage()
		{
			return message;
		}
		public void setMessage(String message)
		{
			this.message = message;
		}
	}
}
