package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

/**
 * 订单查询
 * @author 24480
 *
 */
public class DCP_OrderQueryReq extends JsonBasicReq 
{
	private levelRequest request;

	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String eId;
		private String orgNo;
		private String orgType;		
		private String machShopNo;
		private String shippingShopNo;
		private String shopNo;
		private String sortMode;//0下单时间 1配送时间
		private String sortType;//0升序 1降序
		private String beginDate;//开始日期  格式yyyyMMdd
		private String endDate;//截止日期  格式yyyyMMdd
		private String isInvoice;//需开发票
		private String invOperateType;//发票操作（0开立1作废2折让）
		private String sortStatus;//状态排序
		private String keyTxt;
		private String proName;
		private String pickUpDocPrint;
		private String orderPrint;
        private String beginTime;//开始时间 格式HHMMSS
        private String endTime;//结束时间 格式HHMMSS
		/**
		 * 配送员ID
		 */
		private String delId;
		
		/**
		 * 配送地址模糊查询（支持PROVINCE、CITY、COUNTY、STREET、ADDRESS）
		 */
		private String address;
        /**
         * 区县
         */
        private String county;
		
		private String[] loadDocType;
		private String[] channelId;
		private String[] shipType;
		private String[] status;
		private String[] refundStatus;
		private String[] deliveryStatus;
		private String[] deliveryType;
		private String[] payStatus;
		private String[] productStatus;
        /**
         * 是否查询厨打设置 Y/N,默认N
         */
        private String isQueryKitchenPrintSet;
        private String orderNo;
        private String sn;
        private String memberId;
        private String localStoreProduction;
        private String lineNo;
        /**
         * 下单人opNo，以及关联获取opName下单人名称，完全匹配
         */
        private String opNo;

        private List<levelCategory> categoryList;
        private List<levelOrder> orderList;

		
		public String getSortStatus() {
			return sortStatus;
		}
		public void setSortStatus(String sortStatus) {
			this.sortStatus = sortStatus;
		}
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
		public String getSortMode()
		{
			return sortMode;
		}
		public void setSortMode(String sortMode)
		{
			this.sortMode = sortMode;
		}
		public String getSortType()
		{
			return sortType;
		}
		public void setSortType(String sortType)
		{
			this.sortType = sortType;
		}
		public String getBeginDate()
		{
			return beginDate;
		}
		public void setBeginDate(String beginDate)
		{
			this.beginDate = beginDate;
		}
		public String getEndDate()
		{
			return endDate;
		}
		public void setEndDate(String endDate)
		{
			this.endDate = endDate;
		}
		public String getIsInvoice()
		{
			return isInvoice;
		}
		public void setIsInvoice(String isInvoice)
		{
			this.isInvoice = isInvoice;
		}
		public String getInvOperateType()
		{
			return invOperateType;
		}
		public void setInvOperateType(String invOperateType)
		{
			this.invOperateType = invOperateType;
		}
		public String getKeyTxt()
		{
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt)
		{
			this.keyTxt = keyTxt;
		}
		public String getProName()
		{
			return proName;
		}
		public void setProName(String proName)
		{
			this.proName = proName;
		}
		public String[] getLoadDocType()
		{
			return loadDocType;
		}
		public void setLoadDocType(String[] loadDocType)
		{
			this.loadDocType = loadDocType;
		}
		public String[] getChannelId()
		{
			return channelId;
		}
		public void setChannelId(String[] channelId)
		{
			this.channelId = channelId;
		}
		public String[] getShipType()
		{
			return shipType;
		}
		public void setShipType(String[] shipType)
		{
			this.shipType = shipType;
		}
		public String[] getStatus()
		{
			return status;
		}
		public void setStatus(String[] status)
		{
			this.status = status;
		}
		public String[] getRefundStatus()
		{
			return refundStatus;
		}
		public void setRefundStatus(String[] refundStatus)
		{
			this.refundStatus = refundStatus;
		}
		public String[] getDeliveryStatus()
		{
			return deliveryStatus;
		}
		public void setDeliveryStatus(String[] deliveryStatus)
		{
			this.deliveryStatus = deliveryStatus;
		}
		public String[] getDeliveryType()
		{
			return deliveryType;
		}
		public void setDeliveryType(String[] deliveryType)
		{
			this.deliveryType = deliveryType;
		}
		public String[] getPayStatus()
		{
			return payStatus;
		}
		public void setPayStatus(String[] payStatus)
		{
			this.payStatus = payStatus;
		}
		public String getPickUpDocPrint()
		{
			return pickUpDocPrint;
		}
		public void setPickUpDocPrint(String pickUpDocPrint)
		{
			this.pickUpDocPrint = pickUpDocPrint;
		}
		public String[] getProductStatus()
		{
			return productStatus;
		}
		public void setProductStatus(String[] productStatus)
		{
			this.productStatus = productStatus;
		}
		public String getOrgNo()
		{
			return orgNo;
		}
		public void setOrgNo(String orgNo)
		{
			this.orgNo = orgNo;
		}
		public String getOrgType()
		{
			return orgType;
		}
		public void setOrgType(String orgType)
		{
			this.orgType = orgType;
		}
		public String getMachShopNo()
		{
			return machShopNo;
		}
		public void setMachShopNo(String machShopNo)
		{
			this.machShopNo = machShopNo;
		}
		public String getShippingShopNo()
		{
			return shippingShopNo;
		}
		public void setShippingShopNo(String shippingShopNo)
		{
			this.shippingShopNo = shippingShopNo;
		}
		public String getOrderPrint()
		{
			return orderPrint;
		}
		public void setOrderPrint(String orderPrint)
		{
			this.orderPrint = orderPrint;
		}
		public String getDelId()
		{
			return delId;
		}
		public void setDelId(String delId)
		{
			this.delId = delId;
		}
		public String getAddress()
		{
			return address;
		}
		public void setAddress(String address)
		{
			this.address = address;
		}
        public String getIsQueryKitchenPrintSet()
        {
            return isQueryKitchenPrintSet;
        }
        public void setIsQueryKitchenPrintSet(String isQueryKitchenPrintSet) {
            this.isQueryKitchenPrintSet = isQueryKitchenPrintSet;
        }
        public String getOrderNo() {
            return orderNo;
        }
        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }
        public String getSn() {
            return sn;
        }
        public void setSn(String sn) {
            this.sn = sn;
        }
        public List<levelCategory> getCategoryList() {
            return categoryList;
        }
        public void setCategoryList(List<levelCategory> categoryList) {
            this.categoryList = categoryList;
        }
        public List<levelOrder> getOrderList() {
            return orderList;
        }
        public void setOrderList(List<levelOrder> orderList) {
            this.orderList = orderList;
        }
        public String getBeginTime() {
            return beginTime;
        }
        public void setBeginTime(String beginTime) {
            this.beginTime = beginTime;
        }
        public String getEndTime() {
            return endTime;
        }
        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
        public String getCounty() {
            return county;
        }
        public void setCounty(String county) {
            this.county = county;
        }
        public String getMemberId() {
            return memberId;
        }
        public void setMemberId(String memberId) {
            this.memberId = memberId;
        }
        public String getLocalStoreProduction() {
            return localStoreProduction;
        }
        public void setLocalStoreProduction(String localStoreProduction) {
            this.localStoreProduction = localStoreProduction;
        }
		public String getLineNo() {
			return lineNo;
		}
		public void setLineNo(String lineNo) {
			this.lineNo = lineNo;
		}

        public String getOpNo() {
            return opNo;
        }

        public void setOpNo(String opNo) {
            this.opNo = opNo;
        }
    }

    public class levelCategory
    {
        private String categoryId;
        public String getCategoryId() {
            return categoryId;
        }
        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }
    }
    public class levelOrder
    {
        private String orderNo;

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }
    }
}
